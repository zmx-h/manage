// 模块三 · 部门预算 接口封装（骨架）
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
//   3-01 GET    /user/budget/period/page                  公示预算周期分页
//   3-02 GET    /user/budget/period/{id}                  预算周期详情
//   3-03 GET    /user/budget/period/{id}/item/list        预算科目明细
//   3-04 GET    /user/budget/expense/page                 支出明细公示
//   3-05 POST   /user/budget/apply                        提交预算申请
//   3-06 GET    /user/budget/apply/my/page                我的预算申请分页
//   3-07 GET    /user/budget/apply/{id}                   预算申请详情
//   3-08 PUT    /user/budget/apply/{id}/cancel            撤销预算申请
//   3-09 GET    /user/budget/stat/my                      我的经费汇总
//   3-10 GET    /admin/budget/period/page                 预算周期分页（后台）
//   3-11 POST   /admin/budget/period                      新增预算周期（起草）
//   3-12 PUT    /admin/budget/period                      修改预算周期
//   3-13 DELETE /admin/budget/period/{id}                 删除预算周期
//   3-14 PUT    /admin/budget/period/{id}/status          周期状态流转
//   3-15 PUT    /admin/budget/period/{id}/public          公示 / 取消公示
//   3-16 POST   /admin/budget/period/{id}/collect         发起预算征集（v1.1 预留）
//   3-17 POST   /admin/budget/item                        新增预算科目
//   3-18 PUT    /admin/budget/item                        修改预算科目
//   3-19 DELETE /admin/budget/item/{id}                   删除预算科目
//   3-20 GET    /admin/budget/apply/page                  申请审核分页（后台）
//   3-21 PUT    /admin/budget/apply/{id}/audit            申请审核（通过 / 驳回）
//   3-22 POST   /admin/budget/expense                     登记支出
//   3-23 GET    /admin/budget/expense/page                支出分页（后台）
//   3-24 DELETE /admin/budget/expense/{id}                删除支出记录
//   3-25 GET    /admin/budget/stat/overview               预算执行看板

export default {}
