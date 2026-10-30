package com.iecmp.honor.service;

import com.iecmp.honor.dto.HonorRecordAddDTO;
import com.iecmp.honor.dto.HonorRecordUpdateDTO;
import com.iecmp.honor.vo.HonorRecordCreateVO;

/**
 * 管理端荣誉记录维护服务，接口 6-11 ~ 6-13。
 */
public interface HonorRecordAdminService {

    HonorRecordCreateVO add(HonorRecordAddDTO dto);

    void update(HonorRecordUpdateDTO dto);

    void delete(Long id);
}