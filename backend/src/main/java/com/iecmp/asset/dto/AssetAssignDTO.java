package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 分配保管人与存放位置 请求体。
 *
 * <p>对应接口：4-14（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetAssignDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 保管人 ID；不传表示不变更 */
    @Schema(description = "保管人 ID；不传表示不变更")
    private Long keeperId;

    /** 存放位置描述，最长 128 字 */
    @Schema(description = "存放位置描述，最长 128 字")
    private String location;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
