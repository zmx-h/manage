// Generated from docs/IECMP-接口开发文档-v1.6.md; each method accepts documented field names.
import { invoke } from './client'
export default {
  /** 2-01 GET /user/repo/platform/list · 支持的平台列表 */
  '2-01': (values, options) => invoke('2-01', values, options),
  /** 2-02 POST /user/repo/account · 绑定代码托管账户 */
  '2-02': (values, options) => invoke('2-02', values, options),
  /** 2-03 GET /user/repo/account/list · 我的绑定账户列表 */
  '2-03': (values, options) => invoke('2-03', values, options),
  /** 2-04 PUT /user/repo/account · 更新绑定账户令牌 */
  '2-04': (values, options) => invoke('2-04', values, options),
  /** 2-05 DELETE /user/repo/account/{id} · 解绑账户 */
  '2-05': (values, options) => invoke('2-05', values, options),
  /** 2-06 GET /user/repo/sync/status · 我的同步状态 */
  '2-06': (values, options) => invoke('2-06', values, options),
  /** 2-07 GET /user/repo/project/page · 我的仓库分页 */
  '2-07': (values, options) => invoke('2-07', values, options),
  /** 2-08 PUT /user/repo/project/{id}/track · 开启 / 关闭仓库统计 */
  '2-08': (values, options) => invoke('2-08', values, options),
  /** 2-09 GET /user/repo/commit/page · 提交记录分页 */
  '2-09': (values, options) => invoke('2-09', values, options),
  /** 2-10 GET /user/repo/commit/chart · 提交-日期趋势图 */
  '2-10': (values, options) => invoke('2-10', values, options),
  /** 2-11 GET /user/repo/commit/heatmap · 贡献热力图 */
  '2-11': (values, options) => invoke('2-11', values, options),
  /** 2-12 GET /user/repo/stat/overview · 个人贡献概览 */
  '2-12': (values, options) => invoke('2-12', values, options),
  /** 2-13 GET /admin/repo/account/page · 账户与仓库分页（后台） */
  '2-13': (values, options) => invoke('2-13', values, options),
  /** 2-14 GET /admin/repo/stat/rank · 成员贡献排行（后台） */
  '2-14': (values, options) => invoke('2-14', values, options),
  /** 2-15 GET /admin/repo/stat/trend · 中心提交趋势（后台） */
  '2-15': (values, options) => invoke('2-15', values, options),
  /** 2-16 GET /admin/repo/sync/log/page · 同步日志分页（后台） */
  '2-16': (values, options) => invoke('2-16', values, options),
  /** 2-17 POST /admin/repo/sync · 手动补采（后台） */
  '2-17': (values, options) => invoke('2-17', values, options),
}
