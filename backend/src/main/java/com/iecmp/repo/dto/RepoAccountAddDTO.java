package com.iecmp.repo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/**
 * 绑定代码托管账户 请求体。
 *
 * <p>对应接口：2-02（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author WanZiyu
 */
@Data
public class RepoAccountAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 平台标识：gitee / github */
    @Schema(description = "平台标识：gitee / github")
    @NotBlank(message = "平台标识：gitee / github不能为空")
    private String platform;

    /** 托管平台账号名 */
    @Schema(description = "托管平台账号名")
    @NotBlank(message = "托管平台账号名不能为空")
    private String accountName;

    /** 个人访问令牌，需具备读取仓库与提交的权限 */
    @Schema(description = "个人访问令牌，需具备读取仓库与提交的权限")
    @NotBlank(message = "个人访问令牌，需具备读取仓库与提交的权限不能为空")
    private String accessToken;
}
