package com.iecmp.honor.service;

import com.iecmp.common.result.PageResult;
import com.iecmp.honor.query.HonorAlumniHighlightQuery;
import com.iecmp.honor.query.UserHonorAlumniPageQuery;
import com.iecmp.honor.vo.HonorAlumniDetailVO;
import com.iecmp.honor.vo.HonorAlumniHighlightVO;
import com.iecmp.honor.vo.HonorStatVO;
import com.iecmp.honor.vo.UserHonorAlumniPageVO;
import java.util.List;

/**
 * 学长学姐展板前台查询服务。
 *
 * <p>只暴露「审核通过且已上板」的数据，接口 6-01 ~ 6-04。
 */
public interface HonorBoardService {

    PageResult<UserHonorAlumniPageVO> page(UserHonorAlumniPageQuery query);

    HonorAlumniDetailVO detail(Long id);

    List<HonorAlumniHighlightVO> highlight(HonorAlumniHighlightQuery query);

    HonorStatVO stat();
}