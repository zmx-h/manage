package com.iecmp.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.asset.dto.AssetAddDTO;
import com.iecmp.asset.dto.AssetAssignDTO;
import com.iecmp.asset.dto.AssetStatusDTO;
import com.iecmp.asset.dto.AssetUpdateDTO;
import com.iecmp.asset.entity.AssetInfo;
import com.iecmp.asset.query.AdminAssetPageQuery;
import com.iecmp.asset.query.AssetExportQuery;
import com.iecmp.asset.query.UserAssetPageQuery;
import com.iecmp.asset.vo.AdminAssetDetailVO;
import com.iecmp.asset.vo.AdminAssetPageVO;
import com.iecmp.asset.vo.AssetCreateVO;
import com.iecmp.asset.vo.AssetStatOverviewVO;
import com.iecmp.asset.vo.UserAssetDetailVO;
import com.iecmp.asset.vo.UserAssetPageVO;
import com.iecmp.common.result.PageResult;
import jakarta.servlet.http.HttpServletResponse;


/**
 * 资产台账业务接口（{@code asset_info}）。
 *
 * <p>骨架阶段只继承 {@code IService} 获得通用 CRUD 能力；
 * 实现具体接口时，把该表的专有业务方法声明在这里，并在 Impl 中实现。
 *
 * @author IECMP 后端组
 */
public interface AssetInfoService extends IService<AssetInfo> {
    PageResult<AdminAssetPageVO> page(AdminAssetPageQuery query);
    AdminAssetDetailVO detail(Long id);

    /** 4-10 新增资产。 */
    AssetCreateVO add(AssetAddDTO dto);

    /** 4-11 修改资产。 */
    void update(AssetUpdateDTO dto);

    /** 4-12 删除资产（逻辑删除）。 */
    void remove(Long id);

    /** 4-13 资产状态变更。 */
    void changeStatus(Long id, AssetStatusDTO dto);

    /** 4-14 分配保管人与存放位置。 */
    void assign(Long id, AssetAssignDTO dto);

    /** 4-01 资产分页（前台）。 */
    PageResult<UserAssetPageVO> userPage(UserAssetPageQuery query);

    /** 4-02 资产详情（前台）。 */
    UserAssetDetailVO userDetail(Long id);

    /** 4-04 资产总览统计。 */
    AssetStatOverviewVO statOverview();

    /** 4-27 资产台账导出。 */
    void export(AssetExportQuery query, HttpServletResponse response);
}
