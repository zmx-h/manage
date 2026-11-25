package com.iecmp.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 招新注册码生成与摘要工具。
 *
 * <p>注册码为 8 位、去除易混淆字符；数据库只保存 {@code HMAC-SHA256(code + pepper)}，
 * 即使库被读取也无法还原明文。
 */
public final class RecruitCodeUtil {

    private static final char[] CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final SecureRandom RANDOM = new SecureRandom();

    private RecruitCodeUtil() {
    }

    /** 生成 8 位注册码。 */
    public static String generate() {
        StringBuilder code = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            code.append(CODE_CHARS[RANDOM.nextInt(CODE_CHARS.length)]);
        }
        return code.toString();
    }

    /** 计算注册码摘要，输出 64 位小写十六进制。 */
    public static String hash(String code, String pepper) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec((pepper == null ? "" : pepper).getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM));
            return HexFormat.of().formatHex(mac.doFinal(code.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("注册码摘要计算失败", e);
        }
    }

    /** 恒定时间比较，避免通过响应耗时探测注册码。 */
    public static boolean matches(String code, String expectedHash, String pepper) {
        if (code == null || expectedHash == null) {
            return false;
        }
        byte[] actual = hash(code, pepper).getBytes(StandardCharsets.US_ASCII);
        byte[] expected = expectedHash.getBytes(StandardCharsets.US_ASCII);
        return MessageDigest.isEqual(actual, expected);
    }
}
