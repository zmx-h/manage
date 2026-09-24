package com.iecmp.commonm.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 前台展示视频列表 查询条件。
 *
 * <p>对应接口：8-15（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class VideoListQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 1 首页展示，2 学长学姐展板，3 其他 */
    @Schema(description = "1 首页展示，2 学长学姐展板，3 其他")
    private Integer bizType;

    /** 返回条数上限，范围 1~50 */
    @Schema(description = "返回条数上限，范围 1~50")
    private Integer limit;
}
