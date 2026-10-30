package com.iecmp.commonm.controller;

import com.iecmp.common.result.Result;
import com.iecmp.commonm.service.SysDictService;
import com.iecmp.commonm.vo.DictDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** 公共-数据字典：8-04。 */
@Tag(name = "公共-数据字典")
@RestController
@RequiredArgsConstructor
public class CommonDictController {

    private final SysDictService sysDictService;

    @Operation(summary = "8-04 数据字典查询", description = "按字典类型返回启用枚举列表")
    @GetMapping("/common/dict/{type}")
    public Result<List<DictDetailVO>> getDict(@PathVariable("type") String type) {
        return Result.success(sysDictService.listByType(type));
    }
}