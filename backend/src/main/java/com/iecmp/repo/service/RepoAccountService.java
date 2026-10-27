package com.iecmp.repo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.repo.dto.RepoAccountAddDTO;
import com.iecmp.repo.dto.RepoAccountUpdateDTO;
import com.iecmp.repo.entity.RepoAccount;
import com.iecmp.repo.query.RepoAccountPageQuery;
import com.iecmp.repo.vo.RepoAccountCreateVO;
import com.iecmp.repo.vo.RepoAccountListVO;
import com.iecmp.repo.vo.RepoAccountPageVO;
import com.iecmp.repo.vo.RepoPlatformListVO;
import com.iecmp.repo.vo.RepoSyncStatusVO;

import java.util.List;

/**
 * 托管账户业务接口（{@code repo_account}），覆盖接口 2-01 ~ 2-06 与后台的 2-13。
 *
 * <p>除 2-13 外都是「当前登录用户自己的账户」，用户 ID 一律由实现类通过
 * {@code LoginUserUtil} 取，不从入参传入——避免越权访问别人的账户。
 *
 * <p><b>令牌边界</b>：{@code access_token} 明文只在绑定 / 换令牌的校验过程中短暂存在，
 * 落库前即 AES 加密；任何返回结构（包括 {@link RepoAccount} 实体，其字段已标 {@code @JsonIgnore}）
 * 都不带出密文或明文。
 */
public interface RepoAccountService extends IService<RepoAccount> {

    /**
     * 2-01 支持的平台列表。
     *
     * <p>直接由配置 {@code repo.platform} 下发，不在代码里写死——换平台或改令牌入口地址不用改代码。
     */
    List<RepoPlatformListVO> listPlatforms();

    /**
     * 2-02 绑定代码托管账户。
     *
     * <p>先校验令牌有效性，再加密落库，然后<b>同步</b>拉一次仓库列表（接口要返回
     * {@code projectCount}，异步拿不到），最后异步触发提交采集。
     *
     * @throws com.iecmp.common.exception.BusinessException 20002 重复绑定；20003 令牌无效；20007 平台调用失败
     */
    RepoAccountCreateVO bind(RepoAccountAddDTO dto);

    /** 2-03 我的绑定账户列表。 */
    List<RepoAccountListVO> listMyAccounts();

    /**
     * 2-04 更新绑定账户令牌。
     *
     * @throws com.iecmp.common.exception.BusinessException 20001 账户不存在；20003 令牌无效；20005 不属于当前用户
     */
    void updateToken(RepoAccountUpdateDTO dto);

    /**
     * 2-05 解绑账户：连带逻辑删除该账户下的仓库与提交。
     *
     * @throws com.iecmp.common.exception.BusinessException 20001 账户不存在；20005 不属于当前用户
     */
    void unbind(Long accountId);

    /** 2-06 我的同步状态。 */
    List<RepoSyncStatusVO> listSyncStatus();

    /** 2-13 账户与仓库分页（后台）。 */
    PageResult<RepoAccountPageVO> pageAccounts(RepoAccountPageQuery query);
}
