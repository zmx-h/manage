package com.iecmp.common.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 客户端 IP 工具，用于注册限流、操作日志与登录风控。
 */
public final class IpUtil {

    private static final String UNKNOWN = "unknown";

    private static final String[] HEADERS = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    private IpUtil() {
    }

    /** 取客户端真实 IP；经反向代理时取 X-Forwarded-For 的第一段。 */
    public static String getIp(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }
        for (String header : HEADERS) {
            String value = request.getHeader(header);
            if (value != null && !value.isBlank() && !UNKNOWN.equalsIgnoreCase(value)) {
                int comma = value.indexOf(',');
                return comma > 0 ? value.substring(0, comma).trim() : value.trim();
            }
        }
        String remoteAddr = request.getRemoteAddr();
        return remoteAddr == null ? UNKNOWN : remoteAddr;
    }
}
