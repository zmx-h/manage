// 模块五 · 部门工位 接口封装（骨架）
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
//   5-01 GET    /user/seat/plan                           平面图数据
//   5-02 GET    /user/seat/list                           工位列表
//   5-03 GET    /user/seat/my                             我的工位
//   5-04 POST   /user/seat/apply                          提交工位申请
//   5-05 GET    /user/seat/apply/my/page                  我的工位申请分页
//   5-06 PUT    /user/seat/apply/{id}/cancel              撤销工位申请
//   5-07 GET    /admin/seat/area/list                     区域列表（后台）
//   5-08 POST   /admin/seat/area                          新增区域（上传平面图）
//   5-09 PUT    /admin/seat/area                          修改区域
//   5-10 DELETE /admin/seat/area/{id}                     删除区域
//   5-11 POST   /admin/seat                               新增工位
//   5-12 PUT    /admin/seat                               修改工位
//   5-13 PUT    /admin/seat/batch                         批量保存工位布局坐标
//   5-14 DELETE /admin/seat/{id}                          删除工位
//   5-15 PUT    /admin/seat/{id}/assign                   分配工位
//   5-16 PUT    /admin/seat/{id}/release                  释放工位
//   5-17 PUT    /admin/seat/{id}/status                   工位状态变更
//   5-18 GET    /admin/seat/apply/page                    工位申请分页（后台）
//   5-19 PUT    /admin/seat/apply/{id}/audit              工位申请审核
//   5-20 POST   /user/seat/swap                           成员发起座位调换
//   5-21 GET    /user/seat/swap/my/page                   我的调换申请分页
//   5-22 PUT    /user/seat/swap/{id}/handle               调换对方确认 / 拒绝
//   5-23 GET    /admin/seat/swap/page                     调换申请分页（后台）
//   5-24 PUT    /admin/seat/swap/{id}/audit               调换申请审核（负责人）
//   5-25 GET    /admin/seat/plan                          平面图数据（后台）
//   5-26 POST   /admin/seat/plan/drag                     平面图拖拽调整（移动 / 互换）

export default {}
