// 模块四 · 部门财产 接口封装（骨架）
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
//   4-01 GET    /user/asset/page                          资产分页（前台）
//   4-02 GET    /user/asset/{id}                          资产详情（前台）
//   4-03 GET    /user/asset/category/tree                 资产分类树
//   4-04 GET    /user/asset/stat/overview                 资产总览统计
//   4-05 POST   /user/asset/borrow                        借用 / 领用申请
//   4-06 GET    /user/asset/borrow/my/page                我的借用记录
//   4-07 PUT    /user/asset/borrow/{id}/return            归还登记（本人）
//   4-08 GET    /admin/asset/page                         资产分页（后台）
//   4-09 GET    /admin/asset/{id}                         资产详情（后台）
//   4-10 POST   /admin/asset                              新增资产
//   4-11 PUT    /admin/asset                              修改资产
//   4-12 DELETE /admin/asset/{id}                         删除资产
//   4-13 PUT    /admin/asset/{id}/status                  资产状态变更
//   4-14 PUT    /admin/asset/{id}/assign                  分配保管人与存放位置
//   4-15 POST   /admin/asset/record                       变动记录登记
//   4-16 GET    /admin/asset/record/page                  变动流水分页
//   4-17 POST   /admin/asset/category                     新增资产分类
//   4-18 PUT    /admin/asset/category                     修改资产分类
//   4-19 DELETE /admin/asset/category/{id}                删除资产分类
//   4-20 GET    /admin/asset/borrow/page                  借用审核分页
//   4-21 PUT    /admin/asset/borrow/{id}/audit            借用审核
//   4-22 PUT    /admin/asset/borrow/{id}/return           归还登记（后台）
//   4-23 POST   /admin/asset/inventory                    发起盘点
//   4-24 GET    /admin/asset/inventory/page               盘点任务分页
//   4-25 PUT    /admin/asset/inventory/item/{id}          盘点明细登记
//   4-26 PUT    /admin/asset/inventory/{id}/finish        结束盘点
//   4-27 GET    /admin/asset/export                       资产台账导出

export default {}
