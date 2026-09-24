package com.iecmp.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.context.LoginUser;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.util.IpUtil;
import com.iecmp.commonm.entity.SysOperLog;
import com.iecmp.commonm.service.SysOperLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * 操作日志切面：把标注了 {@link OperLog} 的接口调用写入 {@code sys_oper_log}（接口文档 8-12）。
 *
 * <p>三条约束：
 * <ol>
 *   <li><b>绝不影响主流程</b>：落库异常只记日志，不向上抛；</li>
 *   <li><b>隔离失败</b>：落库失败只打日志，绝不因日志问题让业务接口报错；</li>
 *   <li><b>参数脱敏</b>：密码、令牌类字段不落库（见 {@link #serializeArgs}）。</li>
 * </ol>
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {

    /** 请求参数中不落库的字段关键字。 */
    private static final String[] SENSITIVE_KEYS = {"password", "oldPassword", "newPassword", "token", "accessToken", "refreshToken", "secret"};

    /** 请求参数字符串上限，与 {@code sys_oper_log.request_param} 字段长度一致。 */
    private static final int PARAM_MAX_LENGTH = 2000;

    private final SysOperLogService sysOperLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(operLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperLog operLog) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = null;
        Throwable error = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            try {
                SysOperLog entity = buildEntity(joinPoint, operLog, System.currentTimeMillis() - start, error, result);
                saveQuietly(entity);
            } catch (Exception e) {
                log.warn("操作日志采集失败：{}", e.getMessage());
            }
        }
    }

    private SysOperLog buildEntity(ProceedingJoinPoint joinPoint, OperLog operLog,
                                   long costMs, Throwable error, Object result) {
        SysOperLog entity = new SysOperLog();
        entity.setModule(operLog.module());
        entity.setOperation(operLog.value());
        entity.setCostMs(costMs);
        entity.setStatus(error == null ? 1 : 0);

        LoginUser loginUser = UserContext.get();
        if (loginUser != null) {
            entity.setOperatorId(loginUser.getUserId());
            entity.setOperatorName(loginUser.getUsername());
        }

        HttpServletRequest request = currentRequest();
        if (request != null) {
            entity.setRequestUri(request.getRequestURI());
            entity.setRequestMethod(request.getMethod());
            entity.setIp(IpUtil.getIp(request));
        }
        if (operLog.saveParam()) {
            entity.setRequestParam(serializeArgs(joinPoint.getArgs()));
        }
        entity.setResponseMsg(error == null ? "success" : error.getMessage());
        return entity;
    }

    /**
     * 序列化请求参数：过滤文件、请求 / 响应对象与敏感字段，并截断到字段长度上限。
     */
    private String serializeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        String json;
        try {
            Object[] printable = Arrays.stream(args)
                    .filter(arg -> !(arg instanceof MultipartFile)
                            && !(arg instanceof HttpServletRequest)
                            && !(arg instanceof jakarta.servlet.http.HttpServletResponse))
                    .toArray();
            json = objectMapper.writeValueAsString(printable);
        } catch (Exception e) {
            json = "参数序列化失败";
        }
        for (String key : SENSITIVE_KEYS) {
            json = json.replaceAll("\"" + key + "\"\\s*:\\s*\"[^\"]*\"", "\"" + key + "\":\"***\"");
        }
        return json.length() > PARAM_MAX_LENGTH ? json.substring(0, PARAM_MAX_LENGTH) : json;
    }

    private void saveQuietly(SysOperLog entity) {
        try {
            sysOperLogService.save(entity);
        } catch (Exception e) {
            log.warn("操作日志落库失败：{}", e.getMessage());
        }
    }

    private HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest();
        }
        return null;
    }
}
