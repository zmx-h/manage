package com.iecmp.honor.service;

import com.iecmp.common.result.PageResult;
import com.iecmp.honor.dto.AdminHonorAlumniAddDTO;
import com.iecmp.honor.dto.AdminHonorAlumniUpdateDTO;
import com.iecmp.honor.dto.HonorAlumniAuditDTO;
import com.iecmp.honor.dto.HonorAlumniSortDTO;
import com.iecmp.honor.dto.HonorAlumniStatusDTO;
import com.iecmp.honor.query.AdminHonorAlumniPageQuery;
import com.iecmp.honor.query.HonorAlumniAuditPageQuery;
import com.iecmp.honor.vo.AdminHonorAlumniCreateVO;
import com.iecmp.honor.vo.AdminHonorAlumniPageVO;
import com.iecmp.honor.vo.HonorAlumniAuditPageVO;

/**
 * 管理端学长学姐信息维护与审核服务，接口 6-05 ~ 6-10、6-17 ~ 6-18。
 */
public interface HonorAlumniAdminService {

    PageResult<AdminHonorAlumniPageVO> page(AdminHonorAlumniPageQuery query);

    AdminHonorAlumniCreateVO add(AdminHonorAlumniAddDTO dto);

    void update(AdminHonorAlumniUpdateDTO dto);

    void delete(Long id);

    void updateShowStatus(Long id, HonorAlumniStatusDTO dto);

    void updateSort(Long id, HonorAlumniSortDTO dto);

    PageResult<HonorAlumniAuditPageVO> auditPage(HonorAlumniAuditPageQuery query);

    void audit(Long id, HonorAlumniAuditDTO dto);
}