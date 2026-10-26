// Generated from docs/IECMP-接口开发文档-v1.6.md; each method accepts documented field names.
import { invoke } from './client'
export default {
  /** 3-01 GET /user/budget/period/page · 公示预算周期分页 */
  '3-01': (values, options) => invoke('3-01', values, options),
  /** 3-02 GET /user/budget/period/{id} · 预算周期详情 */
  '3-02': (values, options) => invoke('3-02', values, options),
  /** 3-03 GET /user/budget/period/{id}/item/list · 预算科目明细 */
  '3-03': (values, options) => invoke('3-03', values, options),
  /** 3-04 GET /user/budget/expense/page · 支出明细公示 */
  '3-04': (values, options) => invoke('3-04', values, options),
  /** 3-05 POST /user/budget/apply · 提交预算申请 */
  '3-05': (values, options) => invoke('3-05', values, options),
  /** 3-06 GET /user/budget/apply/my/page · 我的预算申请分页 */
  '3-06': (values, options) => invoke('3-06', values, options),
  /** 3-07 GET /user/budget/apply/{id} · 预算申请详情 */
  '3-07': (values, options) => invoke('3-07', values, options),
  /** 3-08 PUT /user/budget/apply/{id}/cancel · 撤销预算申请 */
  '3-08': (values, options) => invoke('3-08', values, options),
  /** 3-09 GET /user/budget/stat/my · 我的经费汇总 */
  '3-09': (values, options) => invoke('3-09', values, options),
  /** 3-10 GET /admin/budget/period/page · 预算周期分页（后台） */
  '3-10': (values, options) => invoke('3-10', values, options),
  /** 3-11 POST /admin/budget/period · 新增预算周期（起草） */
  '3-11': (values, options) => invoke('3-11', values, options),
  /** 3-12 PUT /admin/budget/period · 修改预算周期 */
  '3-12': (values, options) => invoke('3-12', values, options),
  /** 3-13 DELETE /admin/budget/period/{id} · 删除预算周期 */
  '3-13': (values, options) => invoke('3-13', values, options),
  /** 3-14 PUT /admin/budget/period/{id}/status · 周期状态流转 */
  '3-14': (values, options) => invoke('3-14', values, options),
  /** 3-15 PUT /admin/budget/period/{id}/public · 公示 / 取消公示 */
  '3-15': (values, options) => invoke('3-15', values, options),
  /** 3-16 POST /admin/budget/period/{id}/collect · 发起预算征集（v1.1 预留） */
  '3-16': (values, options) => invoke('3-16', values, options),
  /** 3-17 POST /admin/budget/item · 新增预算科目 */
  '3-17': (values, options) => invoke('3-17', values, options),
  /** 3-18 PUT /admin/budget/item · 修改预算科目 */
  '3-18': (values, options) => invoke('3-18', values, options),
  /** 3-19 DELETE /admin/budget/item/{id} · 删除预算科目 */
  '3-19': (values, options) => invoke('3-19', values, options),
  /** 3-20 GET /admin/budget/apply/page · 申请审核分页（后台） */
  '3-20': (values, options) => invoke('3-20', values, options),
  /** 3-21 PUT /admin/budget/apply/{id}/audit · 申请审核（通过 / 驳回） */
  '3-21': (values, options) => invoke('3-21', values, options),
  /** 3-22 POST /admin/budget/expense · 登记支出 */
  '3-22': (values, options) => invoke('3-22', values, options),
  /** 3-23 GET /admin/budget/expense/page · 支出分页（后台） */
  '3-23': (values, options) => invoke('3-23', values, options),
  /** 3-24 DELETE /admin/budget/expense/{id} · 删除支出记录 */
  '3-24': (values, options) => invoke('3-24', values, options),
  /** 3-25 GET /admin/budget/stat/overview · 预算执行看板 */
  '3-25': (values, options) => invoke('3-25', values, options),
}
