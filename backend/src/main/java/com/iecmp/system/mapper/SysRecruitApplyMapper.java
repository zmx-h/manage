package com.iecmp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.system.entity.SysRecruitApply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 招新报名表（{@code sys_recruit_apply}）数据访问接口。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_email (email, deleted)}</li>
 *   <li>{@code UNIQUE KEY uk_student_no (student_no, deleted)}</li>
 *   <li>{@code UNIQUE KEY uk_code_hash (code_hash, deleted)}</li>
 *   <li>{@code KEY idx_status_apply_time (status, apply_time)}</li>
 *   <li>{@code KEY idx_email_status (email, status)}</li>
 *   <li>{@code KEY idx_ip_apply_time (ip, apply_time)}</li>
 * </ul>
 */
@Mapper
public interface SysRecruitApplyMapper extends BaseMapper<SysRecruitApply> {
}
