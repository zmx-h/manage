package com.iecmp.commonm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.commonm.entity.SysDict;
import com.iecmp.commonm.vo.DictDetailVO;

import java.util.List;

/** 数据字典业务接口（{@code sys_dict}）。 */
public interface SysDictService extends IService<SysDict> {

    /** 8-04 按字典类型查询启用项。 */
    List<DictDetailVO> listByType(String type);
}