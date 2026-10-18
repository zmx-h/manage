package com.iecmp.repo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

/**
 * 绑定代码托管账户 请求体。
 *
 * <p>对应接口：2-02（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoAccountAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 平台标识：gitee / github */
    @Schema(description = "平台标识：gitee / github")
    @NotBlank(message = "平台标识不能为空")
    @Size(max = 16, message = "平台标识长度不能超过 16 个字符")
    private String platform;

    /** 托管平台账号名 */
    @Schema(description = "托管平台账号名")
    @NotBlank(message = "托管平台账号名不能为空")
    @Size(max = 64, message = "托管平台账号名长度不能超过 64 个字符")
    private String accountName;

    /** 个人访问令牌，需具备读取仓库与提交的权限 */
    @Schema(description = "个人访问令牌，需具备读取仓库与提交的权限")
    @NotBlank(message = "个人访问令牌不能为空")
    @Size(max = 256, message = "个人访问令牌长度不能超过 256 个字符")
    @Pattern(regexp = "^[\\x21-\\x7E]+$", message = "个人访问令牌只能包含可打印 ASCII 字符")
    private String accessToken;
}
