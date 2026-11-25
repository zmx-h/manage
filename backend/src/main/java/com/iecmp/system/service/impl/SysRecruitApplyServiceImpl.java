package com.iecmp.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.system.entity.SysRecruitApply;
import com.iecmp.system.mapper.SysRecruitApplyMapper;
import com.iecmp.system.service.SysRecruitApplyService;
import org.springframework.stereotype.Service;

/** 招新报名通用 CRUD 实现。 */
@Service
public class SysRecruitApplyServiceImpl extends ServiceImpl<SysRecruitApplyMapper, SysRecruitApply>
        implements SysRecruitApplyService {
}
