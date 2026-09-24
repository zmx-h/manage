package com.iecmp.common.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体公共父类：主键 + 审计字段。
 *
 * <p>除 {@code sys_oper_log} 外，全库 36 张表中的 35 张都包含这 6 个字段，
 * 因此统一由本类承载，各实体只声明自己的业务字段。{@code sys_oper_log}
 * 没有审计字段（见《数据库设计》），单独实现，不继承本类。
 *
 * <p><b>逻辑删除说明</b>：全库 {@code deleted} 为 {@code BIGINT}，删除时写入本行
 * {@code id}（不是 1），并参与全部唯一索引，使「删除后同名数据可重新插入」。
 * 详见《IECMP-数据库设计.md》1.5 节。
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键，数据库自增 BIGINT。 */
    @Schema(description = "主键 ID", example = "1024")
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 创建人 ID，插入时自动填充。 */
    @Schema(description = "创建人 ID")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Long createBy;

    /** 创建时间，插入时自动填充。 */
    @Schema(description = "创建时间", example = "2026-09-21 14:30:00")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新人 ID，插入与更新时自动填充。 */
    @Schema(description = "更新人 ID")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /** 更新时间，插入与更新时自动填充。 */
    @Schema(description = "更新时间", example = "2026-09-21 14:30:00")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记：0 未删除；已删除时写入本行 id。
     *
     * <p>该字段不返回给前端，避免暴露内部实现。
     */
    @JsonIgnore
    @Schema(description = "逻辑删除标记，0 未删除", hidden = true)
    @TableLogic(value = "0", delval = "id")
    private Long deleted;
}
