package com.iecmp.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具：统一使用 BCrypt 加盐哈希（{@code sys_user.password} 存 60 位哈希）。
 *
 * <p>初始数据中的 {@code admin / 123456} 即为本算法生成的哈希，
 * 校验方式见《项目介绍与技术架构》4.2 节。
 */
public final class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordUtil() {
    }

    /** 生成密码哈希。 */
    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /** 校验明文密码与哈希是否匹配。 */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return ENCODER.matches(rawPassword, encodedPassword);
    }
}
