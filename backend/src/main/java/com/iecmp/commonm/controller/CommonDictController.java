package com.iecmp.commonm.controller;

import com.iecmp.common.result.Result;
import com.iecmp.commonm.service.SysDictService;
import com.iecmp.commonm.vo.DictDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共-数据字典（commonm 模块）。
 *
 * <p>覆盖接口：8-04，共 1 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "公共-数据字典")
@RestController
@RequiredArgsConstructor
public class CommonDictController {

    /** SysDict 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysDictService sysDictService;

    /**
     * 8-04 数据字典查询。
     *
     * <p>{@code GET /common/dict/{type}} —— 按字典类型返回枚举列表，前端下拉统一走此接口，避免在各页面硬编码枚举
     *
     * <p>权限：无（公开接口）；令牌：否。
     */
    @Operation(summary = "8-04 数据字典查询", description = "按字典类型返回枚举列表，前端下拉统一走此接口，避免在各页面硬编码枚举")
    @GetMapping("/common/dict/{type}")
    public Result<DictDetailVO> getDict(@PathVariable("type") String type) {
        // TODO 待实现（接口 8-04）：见《IECMP-接口开发文档-v1.6.md》接口 8-04
        return Result.todo("8-04");
    }
}
