package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 归还登记（本人） 请求体。
 *
 * <p>对应接口：4-07（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserAssetBorrowReturnUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 归还备注，如「外壳有轻微磨损」 */
    @Schema(description = "归还备注，如「外壳有轻微磨损」")
    private String remark;
}
