// Generated from docs/IECMP-接口开发文档-v1.6.md; each method accepts documented field names.
import { invoke } from './client'
export default {
  /** 6-01 GET /user/honor/alumni/page · 展板学长分页 */
  '6-01': (values, options) => invoke('6-01', values, options),
  /** 6-02 GET /user/honor/alumni/{id} · 学长详情（荣誉时间轴） */
  '6-02': (values, options) => invoke('6-02', values, options),
  /** 6-03 GET /user/honor/alumni/highlight · 精选轮播 */
  '6-03': (values, options) => invoke('6-03', values, options),
  /** 6-04 GET /user/honor/stat · 展板统计 */
  '6-04': (values, options) => invoke('6-04', values, options),
  /** 6-05 GET /admin/honor/alumni/page · 学长分页（后台） */
  '6-05': (values, options) => invoke('6-05', values, options),
  /** 6-06 POST /admin/honor/alumni · 新增学长 */
  '6-06': (values, options) => invoke('6-06', values, options),
  /** 6-07 PUT /admin/honor/alumni · 修改学长 */
  '6-07': (values, options) => invoke('6-07', values, options),
  /** 6-08 DELETE /admin/honor/alumni/{id} · 删除学长 */
  '6-08': (values, options) => invoke('6-08', values, options),
  /** 6-09 PUT /admin/honor/alumni/{id}/status · 上板 / 下板 */
  '6-09': (values, options) => invoke('6-09', values, options),
  /** 6-10 PUT /admin/honor/alumni/{id}/sort · 调整展示排序 */
  '6-10': (values, options) => invoke('6-10', values, options),
  /** 6-11 POST /admin/honor/record · 新增荣誉记录 */
  '6-11': (values, options) => invoke('6-11', values, options),
  /** 6-12 PUT /admin/honor/record · 修改荣誉记录 */
  '6-12': (values, options) => invoke('6-12', values, options),
  /** 6-13 DELETE /admin/honor/record/{id} · 删除荣誉记录 */
  '6-13': (values, options) => invoke('6-13', values, options),
  /** 6-14 POST /user/honor/alumni · 成员提交学长信息 */
  '6-14': (values, options) => invoke('6-14', values, options),
  /** 6-15 GET /user/honor/alumni/my/page · 我的提交记录分页 */
  '6-15': (values, options) => invoke('6-15', values, options),
  /** 6-16 PUT /user/honor/alumni/{id}/withdraw · 撤回提交 */
  '6-16': (values, options) => invoke('6-16', values, options),
  /** 6-19 PUT /user/honor/alumni/{id} · 重新提交我的信息 */
  '6-19': (values, options) => invoke('6-19', values, options),
  /** 6-17 GET /admin/honor/alumni/audit/page · 待审核信息分页（后台） */
  '6-17': (values, options) => invoke('6-17', values, options),
  /** 6-18 PUT /admin/honor/alumni/{id}/audit · 审核通过 / 驳回 */
  '6-18': (values, options) => invoke('6-18', values, options),
}
