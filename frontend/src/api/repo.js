// 模块二 · 代码仓库关联 接口封装（骨架）
//
// 本文件只列出该模块的接口清单与调用写法模板，**尚未实现任何方法**。
// 统一返回：code === 200 时 resolve 出 data 字段（见 src/api/request.js）。
//
// 写法模板（示例，取消注释并按接口文档补齐参数即可）：
//
// import request from '@/api/request'
//
// /** 1-08 用户分页查询 · GET /admin/user/page */
// export function pageUser(query) {
//   return request.get('/admin/user/page', { params: query })
// }
//
// /** 1-10 新增用户 · POST /admin/user */
// export function addUser(data) {
//   return request.post('/admin/user', data)
// }
//
// 接口清单：
//   2-01 GET    /user/repo/platform/list                  支持的平台列表
//   2-02 POST   /user/repo/account                        绑定代码托管账户
//   2-03 GET    /user/repo/account/list                   我的绑定账户列表
//   2-04 PUT    /user/repo/account                        更新绑定账户令牌
//   2-05 DELETE /user/repo/account/{id}                   解绑账户
//   2-06 GET    /user/repo/sync/status                    我的同步状态
//   2-07 GET    /user/repo/project/page                   我的仓库分页
//   2-08 PUT    /user/repo/project/{id}/track             开启 / 关闭仓库统计
//   2-09 GET    /user/repo/commit/page                    提交记录分页
//   2-10 GET    /user/repo/commit/chart                   提交-日期趋势图
//   2-11 GET    /user/repo/commit/heatmap                 贡献热力图
//   2-12 GET    /user/repo/stat/overview                  个人贡献概览
//   2-13 GET    /admin/repo/account/page                  账户与仓库分页（后台）
//   2-14 GET    /admin/repo/stat/rank                     成员贡献排行（后台）
//   2-15 GET    /admin/repo/stat/trend                    中心提交趋势（后台）
//   2-16 GET    /admin/repo/sync/log/page                 同步日志分页（后台）
//   2-17 POST   /admin/repo/sync                          手动补采（后台）

export default {}
