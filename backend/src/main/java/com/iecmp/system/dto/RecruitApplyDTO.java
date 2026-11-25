package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

/**
 * 招新报名 请求体（接口 1-30）。
 *
 * <p>由外部招新网页提交，邮箱是后续注册的唯一凭据。
 */
@Data
public class RecruitApplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 姓名 */
    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    @Size(max = 32, message = "姓名最长 32 字")
    private String realName;

    /** 年级，如 2025 */
    @Schema(description = "年级，如 2025")
    @NotBlank(message = "年级不能为空")
    @Size(max = 10, message = "年级最长 10 字")
    private String grade;

    /** 班级 */
    @Schema(description = "班级")
    @NotBlank(message = "班级不能为空")
    @Size(max = 50, message = "班级最长 50 字")
    private String className;

    /** 邮箱，注册码收件地址 */
    @Schema(description = "邮箱，注册码收件地址")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 64, message = "邮箱最长 64 位")
    private String email;

    /** 学号，可选 */
    @Schema(description = "学号，可选")
    @Size(max = 20, message = "学号最长 20 位")
    private String studentNo;

    /** 手机号，可选 */
    @Schema(description = "手机号，可选")
    @Pattern(regexp = "^$|^1\\d{10}$", message = "手机号格式不正确")
    private String phone;

    /** 个人简介，可选 */
    @Schema(description = "个人简介，可选")
    @Size(max = 500, message = "个人简介最长 500 字")
    private String intro;
}
