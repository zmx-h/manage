package com.iecmp.commonm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.commonm.entity.SysNotice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告表（{@code sys_notice}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/commonm/SysNoticeMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_status_top_time (status, is_top, publish_time)}</li>
 *   <li>{@code KEY idx_notice_type (notice_type)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface SysNoticeMapper extends BaseMapper<SysNotice> {
}
