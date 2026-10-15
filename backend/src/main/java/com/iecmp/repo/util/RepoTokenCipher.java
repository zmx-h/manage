package com.iecmp.repo.util;

import com.iecmp.repo.properties.RepoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * {@code repo_account.access_token} 的加解密器（架构文档 4.5 节「第三方平台 AccessToken：数据库 AES 加密存储」）。
 *
 * <p>实现要点：
 * <ol>
 *   <li>算法 {@code AES/GCM/NoPadding}：GCM 自带完整性校验，密文被篡改时解密直接失败，
 *       不会像 CBC 那样悄悄返回一段乱码；</li>
 *   <li>每次加密随机生成 12 字节 IV，与密文拼接后一并 Base64 落库，同一令牌两次加密的密文不同；</li>
 *   <li>密钥取自 {@code repo.crypto.aes-key}，长度不足 16 字节时先做 SHA-256 再取 32 字节，
 *       避免因为配置写错长度导致应用启动失败。</li>
 * </ol>
 *
 * <p><b>不做的一步</b>：不实现密钥轮换。更换 {@code repo.crypto.aes-key} 会让存量密文全部解不开，
 * 届时需要成员重新绑定账户（接口 2-04）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RepoTokenCipher {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    /** GCM 推荐的 IV 长度（字节）。 */
    private static final int IV_LENGTH = 12;

    /** GCM 认证标签长度（位）。 */
    private static final int TAG_LENGTH_BIT = 128;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RepoProperties repoProperties;

    /** 惰性初始化的密钥，避免每次加解密都做一次 SHA-256。 */
    private volatile SecretKeySpec secretKey;

    /**
     * 加密明文令牌。
     *
     * @param plainText 明文令牌，为空时原样返回
     * @return Base64(IV ‖ 密文 ‖ 认证标签)
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        try {
            byte[] iv = new byte[IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key(), new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] payload = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(encrypted, 0, payload, iv.length, encrypted.length);
            return Base64.getEncoder().encodeToString(payload);
        } catch (Exception e) {
            // 密钥或算法不可用时属于部署配置问题，直接抛出而不是降级存明文
            throw new IllegalStateException("访问令牌加密失败：" + e.getMessage(), e);
        }
    }

    /**
     * 解密数据库中的令牌密文。
     *
     * @param cipherText {@link #encrypt(String)} 产出的 Base64 串
     * @return 明文令牌；解密失败返回 {@code null}，由调用方按「令牌无效」处理
     */
    public String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            return null;
        }
        try {
            byte[] payload = Base64.getDecoder().decode(cipherText);
            if (payload.length <= IV_LENGTH) {
                log.warn("访问令牌密文长度异常，无法解密");
                return null;
            }
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(payload, 0, iv, 0, IV_LENGTH);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key(), new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            byte[] plain = cipher.doFinal(payload, IV_LENGTH, payload.length - IV_LENGTH);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 换过密钥、密文被改动都会走到这里，按「令牌无效」处理而不是抛 500
            log.warn("访问令牌解密失败，按令牌失效处理：{}", e.getMessage());
            return null;
        }
    }

    private SecretKeySpec key() throws Exception {
        SecretKeySpec cached = this.secretKey;
        if (cached == null) {
            synchronized (this) {
                cached = this.secretKey;
                if (cached == null) {
                    cached = buildKey(repoProperties.getCrypto().getAesKey());
                    this.secretKey = cached;
                }
            }
        }
        return cached;
    }

    private SecretKeySpec buildKey(String configured) {
        if (configured == null || configured.isBlank()) {
            throw new IllegalStateException("未配置 repo.crypto.aes-key，无法加解密代码托管平台访问令牌");
        }
        byte[] raw = configured.getBytes(StandardCharsets.UTF_8);
        int length = raw.length;
        if (length != 16 && length != 24 && length != 32) {
            try {
                raw = MessageDigest.getInstance("SHA-256").digest(raw);
            } catch (Exception e) {
                throw new IllegalStateException("构造 AES 密钥失败：" + e.getMessage(), e);
            }
        }
        return new SecretKeySpec(raw, "AES");
    }
}
