package com.iecmp.honor.service;

import com.iecmp.common.result.PageResult;
import com.iecmp.honor.dto.UserHonorAlumniAddDTO;
import com.iecmp.honor.dto.UserHonorAlumniUpdateDTO;
import com.iecmp.honor.query.HonorAlumniMyPageQuery;
import com.iecmp.honor.vo.HonorAlumniMyPageVO;
import com.iecmp.honor.vo.HonorAlumniUpdateVO;
import com.iecmp.honor.vo.UserHonorAlumniCreateVO;

/**
 * 成员端学长学姐信息提交服务，接口 6-14 ~ 6-16、6-19。
 */
public interface HonorSubmitService {

    UserHonorAlumniCreateVO submit(UserHonorAlumniAddDTO dto);

    PageResult<HonorAlumniMyPageVO> myPage(HonorAlumniMyPageQuery query);

    void withdraw(Long id);

    HonorAlumniUpdateVO update(Long id, UserHonorAlumniUpdateDTO dto);
}