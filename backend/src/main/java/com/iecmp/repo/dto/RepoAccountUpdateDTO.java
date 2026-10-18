package com.iecmp.repo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

/**
 * 更新绑定账户令牌 请求体。
 *
 * <p>对应接口：2-04（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoAccountUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 托管账户 ID */
    @Schema(description = "托管账户 ID")
    @NotNull(message = "托管账户 ID不能为空")
    private Long accountId;

    /** 新的个人访问令牌 */
    @Schema(description = "新的个人访问令牌")
    @NotBlank(message = "新的个人访问令牌不能为空")
    @Size(max = 256, message = "个人访问令牌长度不能超过 256 个字符")
    @Pattern(regexp = "^[\\x21-\\x7E]+$", message = "个人访问令牌只能包含可打印 ASCII 字符")
    private String accessToken;
}
