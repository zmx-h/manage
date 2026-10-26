// Generated from docs/IECMP-接口开发文档-v1.6.md; each method accepts documented field names.
import { invoke } from './client'
export default {
  /** 5-01 GET /user/seat/plan · 平面图数据 */
  '5-01': (values, options) => invoke('5-01', values, options),
  /** 5-02 GET /user/seat/list · 工位列表 */
  '5-02': (values, options) => invoke('5-02', values, options),
  /** 5-03 GET /user/seat/my · 我的工位 */
  '5-03': (values, options) => invoke('5-03', values, options),
  /** 5-04 POST /user/seat/apply · 提交工位申请 */
  '5-04': (values, options) => invoke('5-04', values, options),
  /** 5-05 GET /user/seat/apply/my/page · 我的工位申请分页 */
  '5-05': (values, options) => invoke('5-05', values, options),
  /** 5-06 PUT /user/seat/apply/{id}/cancel · 撤销工位申请 */
  '5-06': (values, options) => invoke('5-06', values, options),
  /** 5-07 GET /admin/seat/area/list · 区域列表（后台） */
  '5-07': (values, options) => invoke('5-07', values, options),
  /** 5-08 POST /admin/seat/area · 新增区域（上传平面图） */
  '5-08': (values, options) => invoke('5-08', values, options),
  /** 5-09 PUT /admin/seat/area · 修改区域 */
  '5-09': (values, options) => invoke('5-09', values, options),
  /** 5-10 DELETE /admin/seat/area/{id} · 删除区域 */
  '5-10': (values, options) => invoke('5-10', values, options),
  /** 5-11 POST /admin/seat · 新增工位 */
  '5-11': (values, options) => invoke('5-11', values, options),
  /** 5-12 PUT /admin/seat · 修改工位 */
  '5-12': (values, options) => invoke('5-12', values, options),
  /** 5-13 PUT /admin/seat/batch · 批量保存工位布局坐标 */
  '5-13': (values, options) => invoke('5-13', values, options),
  /** 5-14 DELETE /admin/seat/{id} · 删除工位 */
  '5-14': (values, options) => invoke('5-14', values, options),
  /** 5-15 PUT /admin/seat/{id}/assign · 分配工位 */
  '5-15': (values, options) => invoke('5-15', values, options),
  /** 5-16 PUT /admin/seat/{id}/release · 释放工位 */
  '5-16': (values, options) => invoke('5-16', values, options),
  /** 5-17 PUT /admin/seat/{id}/status · 工位状态变更 */
  '5-17': (values, options) => invoke('5-17', values, options),
  /** 5-18 GET /admin/seat/apply/page · 工位申请分页（后台） */
  '5-18': (values, options) => invoke('5-18', values, options),
  /** 5-19 PUT /admin/seat/apply/{id}/audit · 工位申请审核 */
  '5-19': (values, options) => invoke('5-19', values, options),
  /** 5-20 POST /user/seat/swap · 成员发起座位调换 */
  '5-20': (values, options) => invoke('5-20', values, options),
  /** 5-21 GET /user/seat/swap/my/page · 我的调换申请分页 */
  '5-21': (values, options) => invoke('5-21', values, options),
  /** 5-22 PUT /user/seat/swap/{id}/handle · 调换对方确认 / 拒绝 */
  '5-22': (values, options) => invoke('5-22', values, options),
  /** 5-23 GET /admin/seat/swap/page · 调换申请分页（后台） */
  '5-23': (values, options) => invoke('5-23', values, options),
  /** 5-24 PUT /admin/seat/swap/{id}/audit · 调换申请审核（负责人） */
  '5-24': (values, options) => invoke('5-24', values, options),
  /** 5-25 GET /admin/seat/plan · 平面图数据（后台） */
  '5-25': (values, options) => invoke('5-25', values, options),
  /** 5-26 POST /admin/seat/plan/drag · 平面图拖拽调整（移动 / 互换） */
  '5-26': (values, options) => invoke('5-26', values, options),
}
