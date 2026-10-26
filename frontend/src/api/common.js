// Generated from docs/IECMP-接口开发文档-v1.6.md; each method accepts documented field names.
import { invoke } from './client'
export default {
  /** 8-01 POST /user/file/upload · 单文件上传 */
  '8-01': (values, options) => invoke('8-01', values, options),
  /** 8-02 POST /user/file/upload/batch · 多文件上传 */
  '8-02': (values, options) => invoke('8-02', values, options),
  /** 8-03 DELETE /user/file/{id} · 文件删除 */
  '8-03': (values, options) => invoke('8-03', values, options),
  /** 8-04 GET /common/dict/{type} · 数据字典查询 */
  '8-04': (values, options) => invoke('8-04', values, options),
  /** 8-13 GET /common/captcha · 图形验证码 */
  '8-13': (values, options) => invoke('8-13', values, options),
  /** 8-14 GET /common/health · 健康检查 */
  '8-14': (values, options) => invoke('8-14', values, options),
  /** 8-05 GET /user/notice/page · 公告分页（前台） */
  '8-05': (values, options) => invoke('8-05', values, options),
  /** 8-06 GET /admin/notice/page · 公告分页（后台） */
  '8-06': (values, options) => invoke('8-06', values, options),
  /** 8-07 POST /admin/notice · 公告新增 */
  '8-07': (values, options) => invoke('8-07', values, options),
  /** 8-08 PUT /admin/notice · 公告修改 */
  '8-08': (values, options) => invoke('8-08', values, options),
  /** 8-09 DELETE /admin/notice/{id} · 公告删除 */
  '8-09': (values, options) => invoke('8-09', values, options),
  /** 8-10 GET /user/home/overview · 前台首页看板 */
  '8-10': (values, options) => invoke('8-10', values, options),
  /** 8-11 GET /admin/stat/dashboard · 后台统计看板 */
  '8-11': (values, options) => invoke('8-11', values, options),
  /** 8-12 GET /admin/log/page · 操作日志分页 */
  '8-12': (values, options) => invoke('8-12', values, options),
  /** 8-15 GET /user/video/list · 前台展示视频列表 */
  '8-15': (values, options) => invoke('8-15', values, options),
  /** 8-16 GET /admin/video/page · 视频分页（后台） */
  '8-16': (values, options) => invoke('8-16', values, options),
  /** 8-17 POST /admin/video · 新增视频 */
  '8-17': (values, options) => invoke('8-17', values, options),
  /** 8-18 PUT /admin/video · 视频修改 */
  '8-18': (values, options) => invoke('8-18', values, options),
  /** 8-19 DELETE /admin/video/{id} · 视频删除 */
  '8-19': (values, options) => invoke('8-19', values, options),
  /** 8-20 GET /user/message/page · 我的消息分页 */
  '8-20': (values, options) => invoke('8-20', values, options),
  /** 8-21 GET /user/message/unread/count · 未读消息数 */
  '8-21': (values, options) => invoke('8-21', values, options),
  /** 8-22 PUT /user/message/read · 标记消息已读 */
  '8-22': (values, options) => invoke('8-22', values, options),
}
