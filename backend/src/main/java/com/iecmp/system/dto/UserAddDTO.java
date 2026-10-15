package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 新增用户 请求体。
 *
 * <p>对应接口：1-10（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 登录账号，4~32 位，全局唯一 */
    @Schema(description = "登录账号，4~32 位，全局唯一")
    @NotBlank(message = "登录账号，4~32 位，全局唯一不能为空")
    @Size(min = 4, max = 32, message = "登录账号，4~32 位，全局唯一长度需在 4~32 之间")
    private String username;

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
