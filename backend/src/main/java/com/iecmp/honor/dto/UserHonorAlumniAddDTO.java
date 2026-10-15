package com.iecmp.honor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/**
 * 成员提交学长信息 请求体。
 *
 * <p>对应接口：6-14（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserHonorAlumniAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 姓名，最长 32 字 */
    @Schema(description = "姓名，最长 32 字")
    @NotBlank(message = "姓名，最长 32 字不能为空")
    private String name;

    /** 0 未知，1 男，2 女 */
    @Schema(description = "0 未知，1 男，2 女")
    private Integer gender;

    /** 毕业年份，如 2024 */
    @Schema(description = "毕业年份，如 2024")
    private String graduationYear;

    /** 专业 */
    @Schema(description = "专业")
    private String major;

    /** 班级 */
    @Schema(description = "班级")
    private String className;

    /** 就职单位 */
    @Schema(description = "就职单位")
    private String company;

    /** 职位 */
    @Schema(description = "职位")
    private String position;

    /** 头像地址，先调接口 8-01 上传（`bizType = honor`）后填入 */
    @Schema(description = "头像地址，先调接口 8-01 上传（`bizType = honor`）后填入")
    private String avatarUrl;

    /** 个人简介 */
    @Schema(description = "个人简介")
    private String brief;

    /** 标签，逗号分隔 */
    @Schema(description = "标签，逗号分隔")
    private String tags;
}
