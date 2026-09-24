// 模块一 · 用户与权限 接口封装（骨架）
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
//   1-01 POST   /admin/login                              后台登录
//   1-02 POST   /user/login                               前台登录
//   1-03 POST   /user/logout                              退出登录
//   1-04 POST   /user/token/refresh                       刷新令牌
//   1-05 GET    /user/info                                当前用户信息
//   1-06 PUT    /user/info                                修改个人资料
//   1-07 PUT    /user/password                            修改密码
//   1-08 GET    /admin/user/page                          用户分页查询
//   1-09 GET    /admin/user/{id}                          用户详情
//   1-10 POST   /admin/user                               新增用户
//   1-11 PUT    /admin/user                               修改用户
//   1-12 PUT    /admin/user/{id}/status                   启用 / 禁用用户
//   1-13 PUT    /admin/user/{id}/password/reset           重置密码
//   1-14 DELETE /admin/user/{id}                          删除用户
//   1-15 GET    /admin/role/list                          角色下拉列表
//   1-16 GET    /admin/role/page                          角色分页查询
//   1-17 POST   /admin/role                               新增角色
//   1-18 PUT    /admin/role                               修改角色
//   1-19 DELETE /admin/role/{id}                          删除角色
//   1-20 PUT    /admin/role/{id}/permission               角色授权
//   1-21 GET    /admin/permission/tree                    权限码树
//   1-22 GET    /common/register/config                   注册方式配置查询
//   1-23 POST   /user/register                            邀请码注册
//   1-24 POST   /common/register/apply                    提交注册申请
//   1-25 GET    /admin/register/apply/page                注册申请分页
//   1-26 PUT    /admin/register/apply/{id}/audit          注册申请审核
//   1-27 GET    /admin/invite-code/page                   邀请码分页
//   1-28 POST   /admin/invite-code                        生成邀请码
//   1-29 DELETE /admin/invite-code/{id}                   作废邀请码

export default {}
