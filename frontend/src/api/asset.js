// Generated from docs/IECMP-接口开发文档-v1.6.md; each method accepts documented field names.
import { invoke } from './client'
export default {
  /** 4-01 GET /user/asset/page · 资产分页（前台） */
  '4-01': (values, options) => invoke('4-01', values, options),
  /** 4-02 GET /user/asset/{id} · 资产详情（前台） */
  '4-02': (values, options) => invoke('4-02', values, options),
  /** 4-03 GET /user/asset/category/tree · 资产分类树 */
  '4-03': (values, options) => invoke('4-03', values, options),
  /** 4-04 GET /user/asset/stat/overview · 资产总览统计 */
  '4-04': (values, options) => invoke('4-04', values, options),
  /** 4-05 POST /user/asset/borrow · 借用 / 领用申请 */
  '4-05': (values, options) => invoke('4-05', values, options),
  /** 4-06 GET /user/asset/borrow/my/page · 我的借用记录 */
  '4-06': (values, options) => invoke('4-06', values, options),
  /** 4-07 PUT /user/asset/borrow/{id}/return · 归还登记（本人） */
  '4-07': (values, options) => invoke('4-07', values, options),
  /** 4-08 GET /admin/asset/page · 资产分页（后台） */
  '4-08': (values, options) => invoke('4-08', values, options),
  /** 4-09 GET /admin/asset/{id} · 资产详情（后台） */
  '4-09': (values, options) => invoke('4-09', values, options),
  /** 4-10 POST /admin/asset · 新增资产 */
  '4-10': (values, options) => invoke('4-10', values, options),
  /** 4-11 PUT /admin/asset · 修改资产 */
  '4-11': (values, options) => invoke('4-11', values, options),
  /** 4-12 DELETE /admin/asset/{id} · 删除资产 */
  '4-12': (values, options) => invoke('4-12', values, options),
  /** 4-13 PUT /admin/asset/{id}/status · 资产状态变更 */
  '4-13': (values, options) => invoke('4-13', values, options),
  /** 4-14 PUT /admin/asset/{id}/assign · 分配保管人与存放位置 */
  '4-14': (values, options) => invoke('4-14', values, options),
  /** 4-15 POST /admin/asset/record · 变动记录登记 */
  '4-15': (values, options) => invoke('4-15', values, options),
  /** 4-16 GET /admin/asset/record/page · 变动流水分页 */
  '4-16': (values, options) => invoke('4-16', values, options),
  /** 4-17 POST /admin/asset/category · 新增资产分类 */
  '4-17': (values, options) => invoke('4-17', values, options),
  /** 4-18 PUT /admin/asset/category · 修改资产分类 */
  '4-18': (values, options) => invoke('4-18', values, options),
  /** 4-19 DELETE /admin/asset/category/{id} · 删除资产分类 */
  '4-19': (values, options) => invoke('4-19', values, options),
  /** 4-20 GET /admin/asset/borrow/page · 借用审核分页 */
  '4-20': (values, options) => invoke('4-20', values, options),
  /** 4-21 PUT /admin/asset/borrow/{id}/audit · 借用审核 */
  '4-21': (values, options) => invoke('4-21', values, options),
  /** 4-22 PUT /admin/asset/borrow/{id}/return · 归还登记（后台） */
  '4-22': (values, options) => invoke('4-22', values, options),
  /** 4-23 POST /admin/asset/inventory · 发起盘点 */
  '4-23': (values, options) => invoke('4-23', values, options),
  /** 4-24 GET /admin/asset/inventory/page · 盘点任务分页 */
  '4-24': (values, options) => invoke('4-24', values, options),
  /** 4-25 PUT /admin/asset/inventory/item/{id} · 盘点明细登记 */
  '4-25': (values, options) => invoke('4-25', values, options),
  /** 4-26 PUT /admin/asset/inventory/{id}/finish · 结束盘点 */
  '4-26': (values, options) => invoke('4-26', values, options),
  /** 4-27 GET /admin/asset/export · 资产台账导出 */
  '4-27': (values, options) => invoke('4-27', values, options),
}
