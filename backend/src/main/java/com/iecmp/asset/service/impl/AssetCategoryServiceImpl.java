package com.iecmp.asset.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.asset.dto.AssetCategoryAddDTO;
import com.iecmp.asset.dto.AssetCategoryUpdateDTO;
import com.iecmp.asset.entity.AssetCategory;
import com.iecmp.asset.entity.AssetInfo;
import com.iecmp.asset.enums.AssetErrorCode;
import com.iecmp.asset.mapper.AssetCategoryMapper;
import com.iecmp.asset.mapper.AssetInfoMapper;
import com.iecmp.asset.service.AssetCategoryService;
import com.iecmp.asset.vo.AssetCategoryCreateVO;
import com.iecmp.asset.vo.AssetCategoryTreeVO;
import com.iecmp.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 资产分类业务实现（{@code asset_category}）。
 *
 * @author IECMP 后端组
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetCategoryServiceImpl extends ServiceImpl<AssetCategoryMapper, AssetCategory>
        implements AssetCategoryService {

    /** 直接注入 Mapper 做资产计数，避免与 AssetInfoService 循环依赖。 */
    private final AssetInfoMapper assetInfoMapper;

    // ============================== 4-03 / 4-17 分类树 ==============================

    @Override
    public List<AssetCategoryTreeVO> tree() {
        List<AssetCategory> all = this.list();
        if (all.isEmpty()) return new ArrayList<>();

        // 父级 -> 子级映射
        Map<Long, List<AssetCategory>> parentMap = all.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() == null ? 0L : c.getParentId()));

        // 各分类资产数量（含子分类）：一次分组查询拿到直接资产数，再递归累加
        Map<Long, Integer> directCount = new HashMap<>();
        List<Map<String, Object>> countRows = assetInfoMapper.selectMaps(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AssetInfo>()
                        .select("category_id", "COUNT(*) AS cnt")
                        .isNotNull("category_id")
                        .groupBy("category_id"));
        for (Map<String, Object> countRow : countRows) {
            Object categoryId = countRow.get("category_id");
            Object count = countRow.get("cnt");
            if (categoryId instanceof Number id && count instanceof Number value) {
                directCount.put(id.longValue(), value.intValue());
            }
        }

        // 根节点：parentId 为 null 或 0
        List<AssetCategory> roots = parentMap.getOrDefault(0L, Collections.emptyList());

        Set<Long> visited = new HashSet<>();
        return roots.stream()
                .map(c -> buildNode(c, parentMap, directCount, visited))
                .filter(c -> c != null)
                .toList();
    }

    /** 递归构建树节点，assetCount 为自身 + 所有子孙资产数之和；visited 防止脏数据成环。 */
    private AssetCategoryTreeVO buildNode(AssetCategory category,
                                          Map<Long, List<AssetCategory>> parentMap,
                                          Map<Long, Integer> directCount,
                                          Set<Long> visited) {
        if (!visited.add(category.getId())) {
            return null;
        }
        AssetCategoryTreeVO vo = new AssetCategoryTreeVO();
        vo.setCategoryId(category.getId());
        vo.setName(category.getName());
        vo.setParentId(category.getParentId());

        List<AssetCategory> children = parentMap.get(category.getId());
        int assetCount = directCount.getOrDefault(category.getId(), 0);
        if (children != null && !children.isEmpty()) {
            List<AssetCategoryTreeVO> childVOs = children.stream()
                    .map(c -> buildNode(c, parentMap, directCount, visited))
                    .filter(c -> c != null)
                    .toList();
            vo.setChildren(childVOs);
            for (AssetCategoryTreeVO child : childVOs) {
                assetCount += child.getAssetCount() == null ? 0 : child.getAssetCount();
            }
        } else {
            vo.setChildren(new ArrayList<>());
        }
        vo.setAssetCount(assetCount);
        return vo;
    }

    // ============================== 4-18 新增分类 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssetCategoryCreateVO add(AssetCategoryAddDTO dto) {
        // 1. 父级存在性校验
        if (dto.getParentId() != null && dto.getParentId() != 0) {
            AssetCategory parent = this.getById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException(AssetErrorCode.E40003.getCode(), AssetErrorCode.E40003.getMsg());
            }
        }

        // 2. 分类编码唯一性校验（code 可空，仅非空时校验）
        String code = dto.getCode();
        if (code != null && !code.isBlank()) {
            Long exists = this.count(Wrappers.lambdaQuery(AssetCategory.class)
                    .eq(AssetCategory::getCode, code));
            if (exists != null && exists > 0) {
                throw new BusinessException(AssetErrorCode.E40018.getCode(), AssetErrorCode.E40018.getMsg());
            }
        }

        AssetCategory entity = new AssetCategory();
        entity.setName(dto.getName());
        entity.setParentId(dto.getParentId());
        entity.setCode(code);
        entity.setSortNo(dto.getSortNo());
        try {
            this.save(entity);
        } catch (DuplicateKeyException e) {
            // 并发下唯一键兜底：转业务异常，避免抛 500
            log.warn("新增资产分类触发唯一键冲突, code={}", code, e);
            throw new BusinessException(AssetErrorCode.E40018.getCode(), AssetErrorCode.E40018.getMsg());
        }

        AssetCategoryCreateVO vo = new AssetCategoryCreateVO();
        vo.setCategoryId(entity.getId());
        return vo;
    }

    // ============================== 4-18 修改分类 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AssetCategoryUpdateDTO dto) {
        // 1. 分类存在性校验
        AssetCategory entity = this.getById(dto.getCategoryId());
        if (entity == null) {
            throw new BusinessException(AssetErrorCode.E40003.getCode(), AssetErrorCode.E40003.getMsg());
        }

        Long parentId = dto.getParentId();

        // 2. 父级存在性校验
        if (parentId != null && parentId != 0) {
            AssetCategory parent = this.getById(parentId);
            if (parent == null) {
                throw new BusinessException(AssetErrorCode.E40003.getCode(), AssetErrorCode.E40003.getMsg());
            }
        }

        // 3. 不允许挂到自己或自己的子分类下（防止成环）
        if (parentId != null && parentId != 0) {
            Set<Long> descendants = collectDescendants(dto.getCategoryId());
            if (parentId.equals(dto.getCategoryId()) || descendants.contains(parentId)) {
                throw new BusinessException(AssetErrorCode.E40003.getCode(), AssetErrorCode.E40003.getMsg());
            }
        }

        // 4. 分类编码唯一性校验（排除当前 id，仅非空时校验）
        String code = dto.getCode();
        if (code != null && !code.isBlank()) {
            Long exists = this.count(Wrappers.lambdaQuery(AssetCategory.class)
                    .eq(AssetCategory::getCode, code)
                    .ne(AssetCategory::getId, dto.getCategoryId()));
            if (exists != null && exists > 0) {
                throw new BusinessException(AssetErrorCode.E40018.getCode(), AssetErrorCode.E40018.getMsg());
            }
        }

        // 5. 更新
        entity.setName(dto.getName());
        entity.setParentId(parentId);
        entity.setCode(code);
        entity.setSortNo(dto.getSortNo());
        try {
            this.updateById(entity);
        } catch (DuplicateKeyException e) {
            // 并发下唯一键兜底：转业务异常，避免抛 500
            log.warn("修改资产分类触发唯一键冲突, categoryId={}, code={}", dto.getCategoryId(), code, e);
            throw new BusinessException(AssetErrorCode.E40018.getCode(), AssetErrorCode.E40018.getMsg());
        }
    }

    // ============================== 4-19 删除分类 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        // 1. 分类存在性校验
        AssetCategory entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(AssetErrorCode.E40003.getCode(), AssetErrorCode.E40003.getMsg());
        }

        // 2. 存在子分类时禁止删除
        Long childCount = this.count(Wrappers.lambdaQuery(AssetCategory.class)
                .eq(AssetCategory::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException(AssetErrorCode.E40005.getCode(), AssetErrorCode.E40005.getMsg());
        }

        // 3. 该分类下存在资产时禁止删除
        Long assetCount = assetInfoMapper.selectCount(
                Wrappers.lambdaQuery(AssetInfo.class).eq(AssetInfo::getCategoryId, id));
        if (assetCount != null && assetCount > 0) {
            throw new BusinessException(AssetErrorCode.E40004.getCode(), AssetErrorCode.E40004.getMsg());
        }

        // 4. 逻辑删除
        this.removeById(id);
    }

    /** 收集某分类的所有子孙分类 ID（不含自身）。 */
    private Set<Long> collectDescendants(Long categoryId) {
        List<AssetCategory> all = this.list();
        Map<Long, List<AssetCategory>> parentMap = all.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() == null ? 0L : c.getParentId()));
        Set<Long> result = new HashSet<>();
        java.util.Deque<Long> stack = new java.util.ArrayDeque<>();
        List<AssetCategory> direct = parentMap.get(categoryId);
        if (direct != null) {
            for (AssetCategory c : direct) stack.push(c.getId());
        }
        while (!stack.isEmpty()) {
            Long cur = stack.pop();
            if (!result.add(cur)) continue;
            List<AssetCategory> children = parentMap.get(cur);
            if (children != null) {
                for (AssetCategory c : children) stack.push(c.getId());
            }
        }
        return result;
    }
}
