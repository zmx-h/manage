package com.iecmp.system.service;

import com.iecmp.common.properties.IecmpProperties;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * 招新注册码邮件发送。
 *
 * <p>发信失败不抛异常，由调用方决定是否保留「待发码」状态并提示管理员补发。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitMailService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final JavaMailSender javaMailSender;
    private final IecmpProperties iecmpProperties;

    @Value("${spring.mail.username:}")
    private String from;

    /** 发送注册码邮件。 */
    public MailResult sendCode(String to, String realName, String code, LocalDateTime expireTime) {
        IecmpProperties.Recruit cfg = iecmpProperties.getRecruit();
        if (!Integer.valueOf(1).equals(cfg.getMailEnabled())) {
            log.info("招新邮件发送未启用，注册码仅记录日志：email={}, code={}", to, code);
            return new MailResult(false, "邮件发送未启用，请管理员使用补发接口获取注册码并线下告知");
        }
        if (!StringUtils.hasText(from)) {
            log.warn("招新邮件发送失败：未配置 spring.mail.username");
            return new MailResult(false, "未配置发件邮箱");
        }
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(from, cfg.getMailFromName());
            helper.setTo(to);
            helper.setSubject("IECMP 创业中心招新注册码");
            helper.setText(buildHtml(realName, code, expireTime), true);
            javaMailSender.send(message);
            return new MailResult(true, null);
        } catch (MailException | jakarta.mail.MessagingException
                | java.io.UnsupportedEncodingException e) {
            log.warn("招新注册码邮件发送失败：email={}, reason={}", to, e.getMessage());
            return new MailResult(false, "邮件发送失败，请稍后重试或由管理员补发");
        }
    }

    private String buildHtml(String realName, String code, LocalDateTime expireTime) {
        String name = HtmlUtils.htmlEscape(StringUtils.hasText(realName) ? realName : "同学");
        String expire = expireTime == null ? "" : expireTime.format(DATE_TIME_FORMATTER);
        return """
                <div style="font-family:Microsoft YaHei,Arial,sans-serif;line-height:1.8;color:#222">
                  <p>%s，你好：</p>
                  <p>恭喜你通过 IECMP 创业中心招新考核，请使用以下注册码完成账号注册。</p>
                  <p style="font-size:24px;font-weight:bold;letter-spacing:4px;color:#1f6feb">%s</p>
                  <p>注册码有效期至：%s（%d 小时）。</p>
                  <p>注册时请使用本邮件接收地址作为报名邮箱，注册码仅可使用一次。</p>
                  <p style="color:#888">如非本人操作，请忽略本邮件。</p>
                </div>
                """.formatted(name, code, expire,
                iecmpProperties.getRecruit().getCodeExpireHours());
    }

    /** 邮件发送结果。 */
    public record MailResult(boolean sent, String error) {
    }
}
