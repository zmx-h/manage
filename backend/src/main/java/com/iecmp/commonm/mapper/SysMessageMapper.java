package com.iecmp.commonm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.commonm.entity.SysMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站内消息表（补充表）（{@code sys_message}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/commonm/SysMessageMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_receiver_read_time (receiver_id, is_read, create_time)}</li>
 *   <li>{@code KEY idx_biz (biz_type, biz_id)}</li>
 *   <li>{@code KEY idx_create_time (create_time)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface SysMessageMapper extends BaseMapper<SysMessage> {
}
