// Generated from docs/IECMP-接口开发文档-v1.6.md; each method accepts documented field names.
import { invoke } from './client'
export default {
  /** 7-01 GET /user/duty/schedule/page · 值日安排分页（公示） */
  '7-01': (values, options) => invoke('7-01', values, options),
  /** 7-02 GET /user/duty/schedule/my · 我的值日（含待办） */
  '7-02': (values, options) => invoke('7-02', values, options),
  /** 7-03 GET /user/duty/schedule/calendar · 值日日历（月视图） */
  '7-03': (values, options) => invoke('7-03', values, options),
  /** 7-04 PUT /user/duty/schedule/{id}/confirm · 值日完成确认 */
  '7-04': (values, options) => invoke('7-04', values, options),
  /** 7-05 POST /user/duty/swap · 发起换班申请 */
  '7-05': (values, options) => invoke('7-05', values, options),
  /** 7-06 GET /user/duty/swap/my/page · 我的换班申请分页 */
  '7-06': (values, options) => invoke('7-06', values, options),
  /** 7-07 PUT /user/duty/swap/{id}/handle · 换班处理（同意 / 拒绝） */
  '7-07': (values, options) => invoke('7-07', values, options),
  /** 7-08 GET /admin/duty/plan/page · 排班计划分页（后台） */
  '7-08': (values, options) => invoke('7-08', values, options),
  /** 7-09 POST /admin/duty/plan · 新增排班计划 */
  '7-09': (values, options) => invoke('7-09', values, options),
  /** 7-10 PUT /admin/duty/plan · 修改排班计划 */
  '7-10': (values, options) => invoke('7-10', values, options),
  /** 7-11 DELETE /admin/duty/plan/{id} · 删除排班计划 */
  '7-11': (values, options) => invoke('7-11', values, options),
  /** 7-12 POST /admin/duty/plan/{id}/generate · 自动生成排班 */
  '7-12': (values, options) => invoke('7-12', values, options),
  /** 7-13 PUT /admin/duty/plan/{id}/publish · 发布 / 取消发布 */
  '7-13': (values, options) => invoke('7-13', values, options),
  /** 7-14 POST /admin/duty/schedule · 新增值日条目 */
  '7-14': (values, options) => invoke('7-14', values, options),
  /** 7-15 PUT /admin/duty/schedule · 修改值日条目 */
  '7-15': (values, options) => invoke('7-15', values, options),
  /** 7-16 DELETE /admin/duty/schedule/{id} · 删除值日条目 */
  '7-16': (values, options) => invoke('7-16', values, options),
  /** 7-17 GET /admin/duty/schedule/page · 值日安排分页（后台） */
  '7-17': (values, options) => invoke('7-17', values, options),
  /** 7-18 PUT /admin/duty/schedule/{id}/status · 值日状态变更 */
  '7-18': (values, options) => invoke('7-18', values, options),
  /** 7-19 GET /admin/duty/stat/overview · 值日统计看板 */
  '7-19': (values, options) => invoke('7-19', values, options),
  /** 7-20 GET /admin/duty/reminder · 提醒配置查询 */
  '7-20': (values, options) => invoke('7-20', values, options),
  /** 7-21 PUT /admin/duty/reminder · 提醒配置保存 */
  '7-21': (values, options) => invoke('7-21', values, options),
}
