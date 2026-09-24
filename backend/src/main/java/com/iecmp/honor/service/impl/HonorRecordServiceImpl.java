package com.iecmp.honor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.honor.entity.HonorRecord;
import com.iecmp.honor.mapper.HonorRecordMapper;
import com.iecmp.honor.service.HonorRecordService;
import org.springframework.stereotype.Service;

/**
 * 荣誉记录业务实现（{@code honor_record}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link HonorRecordService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class HonorRecordServiceImpl extends ServiceImpl<HonorRecordMapper, HonorRecord>
        implements HonorRecordService {
}
