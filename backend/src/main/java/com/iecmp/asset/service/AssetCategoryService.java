package com.iecmp.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.asset.dto.AssetCategoryAddDTO;
import com.iecmp.asset.dto.AssetCategoryUpdateDTO;
import com.iecmp.asset.entity.AssetCategory;
import com.iecmp.asset.vo.AssetCategoryCreateVO;
import com.iecmp.asset.vo.AssetCategoryTreeVO;

import java.util.List;


/**
 * 资产分类业务接口（{@code asset_category}）。
 *
 * @author IECMP 后端组
 */
public interface AssetCategoryService extends IService<AssetCategory> {

    /** 4-03 / 4-17 资产分类树（含各分类资产数量）。 */
    List<AssetCategoryTreeVO> tree();

    /** 4-17 新增分类。 */
    AssetCategoryCreateVO add(AssetCategoryAddDTO dto);

    /** 4-18 修改分类。 */
    void update(AssetCategoryUpdateDTO dto);

    /** 4-19 删除分类（逻辑删除）。 */
    void remove(Long id);
}
