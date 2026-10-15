package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 用户分页查询 响应体。
 *
 * <p>对应接口：1-08（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户 ID */
    @Schema(description = "用户 ID")
    private Long userId;

    /** 登录账号 */
    @Schema(description = "登录账号")
    private String username;

    /** 姓名 */
    @Schema(description = "姓名")
    private String realName;

    /** 学号 */
    @Schema(description = "学号")
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

    /** 头像地址 */
    @Schema(description = "头像地址")
    private String avatarUrl;

    /** 0 禁用，1 启用 */
    @Schema(description = "0 禁用，1 启用")
    private Integer status;

    /** 角色编码列表 */
    @Schema(description = "角色编码列表")
    private List<String> roleCodes;

    /** 角色名称列表，便于前端直接展示 */
    @Schema(description = "角色名称列表，便于前端直接展示")
    private List<String> roleNames;

    /** 最近登录时间 */
    @Schema(description = "最近登录时间")
    private String lastLoginTime;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private String createTime;
}
