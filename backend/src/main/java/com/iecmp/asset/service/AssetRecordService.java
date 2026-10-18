package com.iecmp.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.asset.dto.AssetRecordAddDTO;
import com.iecmp.asset.entity.AssetRecord;
import com.iecmp.asset.query.AssetRecordPageQuery;
import com.iecmp.asset.vo.AssetRecordCreateVO;
import com.iecmp.asset.vo.AssetRecordPageVO;
import com.iecmp.common.result.PageResult;

/**
 * 资产变动流水业务接口（{@code asset_record}）。
 *
 * @author IECMP 后端组
 */
public interface AssetRecordService extends IService<AssetRecord> {

    /** 4-15 手动登记一条资产变动流水。 */
    AssetRecordCreateVO createRecord(AssetRecordAddDTO dto);

    /** 4-16 资产变动流水分页。 */
    PageResult<AssetRecordPageVO> pageRecords(AssetRecordPageQuery query);
}
