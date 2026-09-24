package com.iecmp.common.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.imageio.ImageIO;

/**
 * 图形验证码生成（接口文档 8-13）。
 *
 * <p>不引入第三方验证码库：直接用 {@code java.awt} 绘制 4 位字符，
 * 返回 Base64 图片字符串与答案，答案由调用方写入 Redis（TTL 300 秒、一次性使用）。
 *
 * <p>注意：容器需要以 headless 模式运行（{@code java.awt.headless=true}，
 * 已在 application.yml 中设置），否则 AWT 初始化会失败。
 */
public final class CaptchaUtil {

    /** 验证码字符集：去掉 0/O/1/I/l 等易混淆字符。 */
    private static final char[] CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;

    private static final SecureRandom RANDOM = new SecureRandom();

    private CaptchaUtil() {
    }

    /** 验证码图片与答案。 */
    public record Captcha(String code, String imageBase64) {
    }

    /**
     * 生成验证码。
     *
     * @return 4 位答案与 {@code data:image/png;base64,...} 形式的图片字符串
     */
    public static Captcha generate() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(245, 247, 250));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // 干扰线
            g.setColor(new Color(200, 206, 214));
            for (int i = 0; i < 6; i++) {
                int x1 = RANDOM.nextInt(WIDTH);
                int y1 = RANDOM.nextInt(HEIGHT);
                int x2 = RANDOM.nextInt(WIDTH);
                int y2 = RANDOM.nextInt(HEIGHT);
                g.drawLine(x1, y1, x2, y2);
            }

            // 字符
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
            int step = (WIDTH - 20) / CODE_LENGTH;
            for (int i = 0; i < CODE_LENGTH; i++) {
                char c = CHARS[RANDOM.nextInt(CHARS.length)];
                code.append(c);
                g.setColor(new Color(30 + RANDOM.nextInt(80), 60 + RANDOM.nextInt(80), 120 + RANDOM.nextInt(100)));
                int x = 12 + i * step + RANDOM.nextInt(6);
                int y = 30 + RANDOM.nextInt(6);
                g.drawString(String.valueOf(c), x, y);
            }
        } finally {
            g.dispose();
        }
        return new Captcha(code.toString(), toBase64(image));
    }

    private static String toBase64(BufferedImage image) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", out);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("验证码图片生成失败", e);
        }
    }
}
