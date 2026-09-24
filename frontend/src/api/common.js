// 模块八 · 通用支撑 接口封装（骨架）
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
//   8-01 POST   /user/file/upload                         单文件上传
//   8-02 POST   /user/file/upload/batch                   多文件上传
//   8-03 DELETE /user/file/{id}                           文件删除
//   8-04 GET    /common/dict/{type}                       数据字典查询
//   8-05 GET    /user/notice/page                         公告分页（前台）
//   8-06 GET    /admin/notice/page                        公告分页（后台）
//   8-07 POST   /admin/notice                             公告新增
//   8-08 PUT    /admin/notice                             公告修改
//   8-09 DELETE /admin/notice/{id}                        公告删除
//   8-10 GET    /user/home/overview                       前台首页看板
//   8-11 GET    /admin/stat/dashboard                     后台统计看板
//   8-12 GET    /admin/log/page                           操作日志分页
//   8-13 GET    /common/captcha                           图形验证码
//   8-14 GET    /common/health                            健康检查
//   8-15 GET    /user/video/list                          前台展示视频列表
//   8-16 GET    /admin/video/page                         视频分页（后台）
//   8-17 POST   /admin/video                              新增视频
//   8-18 PUT    /admin/video                              视频修改
//   8-19 DELETE /admin/video/{id}                         视频删除
//   8-20 GET    /user/message/page                        我的消息分页
//   8-21 GET    /user/message/unread/count                未读消息数
//   8-22 PUT    /user/message/read                        标记消息已读

export default {}
