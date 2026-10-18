package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

/**
 * 变动记录登记 请求体。
 *
 * <p>对应接口：4-15（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetRecordAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 资产 ID */
    @Schema(description = "资产 ID")
    @NotNull(message = "资产 ID不能为空")
    private Long assetId;

    /** 1 领用，2 借用，3 归还，4 维修，5 报废，6 调拨，7 盘点 */
    @Schema(description = "1 领用，2 借用，3 归还，4 维修，5 报废，6 调拨，7 盘点")
    @NotNull(message = "变动类型不能为空")
    @Min(value = 1, message = "变动类型只能为 1~7 之间的有效值")
    @Max(value = 7, message = "变动类型只能为 1~7 之间的有效值")
    private Integer recordType;

    /** 变动前使用人 ID */
    @Schema(description = "变动前使用人 ID")
    private Long fromUserId;

    /** 变动后使用人 ID */
    @Schema(description = "变动后使用人 ID")
    private Long toUserId;

    /** 变动前存放位置 */
    @Schema(description = "变动前存放位置")
    @Size(max = 128, message = "变动前存放位置长度不能超过 128 个字符")
    private String fromLocation;

    /** 变动后存放位置 */
    @Schema(description = "变动后存放位置")
    @Size(max = 128, message = "变动后存放位置长度不能超过 128 个字符")
    private String toLocation;

    /** 变动时间，格式 yyyy-MM-dd HH:mm:ss */
    @Schema(description = "变动时间，格式 yyyy-MM-dd HH:mm:ss")
    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2}:\\d{2})?$",
            message = "变动时间格式应为 yyyy-MM-dd HH:mm:ss")
    private String recordTime;

    /** 备注 */
    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;
}
