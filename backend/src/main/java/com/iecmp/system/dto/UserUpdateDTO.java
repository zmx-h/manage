package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 修改用户 请求体。
 *
 * <p>对应接口：1-11（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户 ID */
    @Schema(description = "用户 ID")
    @NotNull(message = "用户 ID不能为空")
    private Long userId;

    /** 姓名 */
    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    private String realName;

    /** 学号，唯一 */
    @Schema(description = "学号，唯一")
    private String studentNo;

    /** 年级 */
    @Schema(description = "年级")
    private String grade;

    /** 专业 */
    @Schema(description = "专业")
    private String major;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /** 角色 ID 列表，至少一个 */
    @Schema(description = "角色 ID 列表，至少一个")
    @NotEmpty(message = "角色 ID 列表，至少一个不能为空")
    private List<Long> roleIds;

    /** 0 禁用，1 启用 */
    @Schema(description = "0 禁用，1 启用")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
