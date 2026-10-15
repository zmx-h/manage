package com.iecmp.commonm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.commonm.entity.SysFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件表（{@code sys_file}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/commonm/SysFileMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_object_key (object_key(191))}</li>
 *   <li>{@code KEY idx_upload_user (upload_user_id)}</li>
 *   <li>{@code KEY idx_biz_type (biz_type)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface SysFileMapper extends BaseMapper<SysFile> {
}
