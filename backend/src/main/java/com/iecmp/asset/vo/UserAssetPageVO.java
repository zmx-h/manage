package com.iecmp.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 资产分页（前台） 响应体。
 *
 * <p>对应接口：4-01（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserAssetPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 资产 ID */
    @Schema(description = "资产 ID")
    private Long assetId;

    /** 资产编号（一物一码） */
    @Schema(description = "资产编号（一物一码）")
    private String assetNo;

    /** 资产名称 */
    @Schema(description = "资产名称")
    private String name;

    /** 分类 ID */
    @Schema(description = "分类 ID")
    private Long categoryId;

    /** 分类名称 */
    @Schema(description = "分类名称")
    private String categoryName;

    /** 品牌 */
    @Schema(description = "品牌")
    private String brand;

    /** 型号 */
    @Schema(description = "型号")
    private String model;

    /** 1 在用，2 闲置，3 维修中，4 已借出，5 已报废 */
    @Schema(description = "1 在用，2 闲置，3 维修中，4 已借出，5 已报废")
    private Integer status;

    /** 保管人姓名 */
    @Schema(description = "保管人姓名")
    private String keeperName;

    /** 存放位置 */
    @Schema(description = "存放位置")
    private String location;

    /** 资产照片地址数组（OSS） */
    @Schema(description = "资产照片地址数组（OSS）")
    private List<String> imageUrls;
}
