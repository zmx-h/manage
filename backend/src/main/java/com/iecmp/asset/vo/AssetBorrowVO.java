package com.iecmp.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 借用 / 领用申请 响应体。
 *
 * <p>对应接口：4-05（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetBorrowVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 借用记录 ID */
    @Schema(description = "借用记录 ID")
    private Long borrowId;
}
