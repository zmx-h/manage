package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 盘点明细登记 请求体。
 *
 * <p>对应接口：4-25（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetInventoryItemUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 盘点结果：1 正常，2 盘亏，3 位置不符 */
    @Schema(description = "盘点结果：1 正常，2 盘亏，3 位置不符")
    @NotNull(message = "盘点结果：1 正常，2 盘亏，3 位置不符不能为空")
    private Integer checkStatus;

    /** 差异说明，结果非「正常」时建议填写 */
    @Schema(description = "差异说明，结果非「正常」时建议填写")
    private String remark;
}
