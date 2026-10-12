package com.iecmp.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.asset.dto.AdminAssetBorrowReturnUpdateDTO;
import com.iecmp.asset.dto.AssetBorrowAuditDTO;
import com.iecmp.asset.dto.AssetBorrowDTO;
import com.iecmp.asset.dto.UserAssetBorrowReturnUpdateDTO;
import com.iecmp.asset.entity.AssetBorrow;
import com.iecmp.asset.query.AssetBorrowMyPageQuery;
import com.iecmp.asset.query.AssetBorrowPageQuery;
import com.iecmp.asset.vo.AssetBorrowMyPageVO;
import com.iecmp.asset.vo.AssetBorrowPageVO;
import com.iecmp.asset.vo.AssetBorrowVO;
import com.iecmp.common.result.PageResult;

/**
 * 借用/领用业务接口（{@code asset_borrow}）。
 *
 * @author IECMP 后端组
 */
public interface AssetBorrowService extends IService<AssetBorrow> {

    /** 4-20 借用审核分页。 */
    PageResult<AssetBorrowPageVO> page(AssetBorrowPageQuery query);

    /** 4-21 借用审核。 */
    void audit(Long id, AssetBorrowAuditDTO dto);

    /** 4-22 管理员代归还。 */
    void adminReturn(Long id, AdminAssetBorrowReturnUpdateDTO dto);

    /** 4-05 借用/领用申请。 */
    AssetBorrowVO apply(AssetBorrowDTO dto);

    /** 4-06 我的借用记录。 */
    PageResult<AssetBorrowMyPageVO> myPage(AssetBorrowMyPageQuery query);

    /** 4-07 本人归还登记。 */
    void userReturn(Long id, UserAssetBorrowReturnUpdateDTO dto);
}
