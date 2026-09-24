package com.iecmp.seat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/**
 * 新增区域（上传平面图） 请求体。
 *
 * <p>对应接口：5-08（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatAreaAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 区域名称，最长 64 字 */
    @Schema(description = "区域名称，最长 64 字")
    @NotBlank(message = "区域名称，最长 64 字不能为空")
    private String name;

    /** 平面图底图地址，先调接口 8-01 上传（`bizType = seat`）后填入 */
    @Schema(description = "平面图底图地址，先调接口 8-01 上传（`bizType = seat`）后填入")
    private String planImageUrl;

    /** 底图宽度（像素） */
    @Schema(description = "底图宽度（像素）")
    private Integer width;

    /** 底图高度（像素） */
    @Schema(description = "底图高度（像素）")
    private Integer height;

    /** 区域说明 */
    @Schema(description = "区域说明")
    private String description;

    /** 排序号，升序 */
    @Schema(description = "排序号，升序")
    private Integer sortNo;
}
