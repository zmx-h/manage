package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 归还登记（后台） 请求体。
 *
 * <p>对应接口：4-22（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminAssetBorrowReturnUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 归还备注 */
    @Schema(description = "归还备注")
    private String remark;
}
