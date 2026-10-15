package com.iecmp.repo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 开启 / 关闭仓库统计 请求体。
 *
 * <p>对应接口：2-08（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoProjectTrackDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 不统计，1 统计 */
    @Schema(description = "0 不统计，1 统计")
    @NotNull(message = "0 不统计，1 统计不能为空")
    private Integer tracked;
}
