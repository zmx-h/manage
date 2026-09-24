package com.iecmp.commonm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.commonm.entity.SysVideo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 视频资源表（{@code sys_video}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/commonm/SysVideoMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_biz_status_sort (biz_type, status, sort_no)}</li>
 *   <li>{@code KEY idx_jellyfin_item_id (jellyfin_item_id)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface SysVideoMapper extends BaseMapper<SysVideo> {
}
