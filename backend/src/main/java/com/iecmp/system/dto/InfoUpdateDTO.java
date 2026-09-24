package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 修改个人资料 请求体。
 *
 * <p>对应接口：1-06（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class InfoUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 姓名，最长 32 字 */
    @Schema(description = "姓名，最长 32 字")
    private String realName;

    /** 手机号，11 位 */
    @Schema(description = "手机号，11 位")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /** 头像地址，来源于文件上传接口 8-01 */
    @Schema(description = "头像地址，来源于文件上传接口 8-01")
    private String avatarUrl;
}
