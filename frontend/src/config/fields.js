import labels from './field-labels.json'
export const zhLabels = { ...labels, id:'记录 ID', userId:'用户 ID', roleId:'角色 ID', roleIds:'角色', roleName:'角色名称', roleCode:'角色编码', permissions:'权限', permCodes:'权限码', dataScope:'数据范围', sortNo:'排序', status:'状态', title:'标题', name:'名称', username:'登录账号', realName:'姓名', studentNo:'学号', grade:'年级', major:'专业', phone:'手机号', email:'邮箱', remark:'备注', description:'说明', content:'正文', createTime:'创建时间', updateTime:'更新时间', periodId:'预算周期', itemId:'预算科目', amount:'金额', approvedAmount:'批准金额', totalAmount:'总额度', budgetAmount:'科目额度', usedAmount:'已用金额', auditStatus:'审核动作', auditRemark:'审核意见', assetId:'资产', assetNo:'资产编号', categoryId:'资产分类', areaId:'区域', seatId:'工位', seatNo:'工位编号', planId:'排班计划', scheduleId:'值日安排', alumniId:'学长学姐', noticeId:'公告', videoId:'视频', accountId:'托管账户', accountName:'托管用户名', projectId:'代码仓库', repositoryName:'仓库名称', repoName:'仓库名称', platform:'托管平台', keyword:'关键字', beginTime:'开始时间', endTime:'结束时间', beginDate:'开始日期', endDate:'结束日期', startDate:'开始日期', reason:'申请理由', inviteCode:'邀请码', initialPassword:'初始密码', accessToken:'访问令牌', oldPassword:'原密码', newPassword:'新密码', confirmPassword:'确认密码', password:'密码', captchaCode:'验证码', expectReturnTime:'预计归还时间', isPublic:'是否公示', isShow:'是否上板', isTop:'是否置顶', publish:'发布状态', borrowerName:'借用人', applyUserName:'申请人', operatorName:'操作人', publishTime:'发布时间', taskContent:'值日任务', memberIds:'参与成员', groupConfig:'分组配置', groupName:'分组名称', dutyDate:'值日日期', planType:'排班方式', seats:'工位坐标', parentId:'父分类', sourceType:'资产来源', borrowType:'申请类型', planImageUrl:'平面图底图', avatarUrl:'头像', imageUrls:'资产图片', voucherFileId:'支出凭证', attachFileId:'申请附件', certificateUrl:'荣誉证书', coverUrl:'视频封面', playUrl:'播放地址', isRead:'阅读状态', msgType:'消息类型', year:'年份', month:'月份', count:'数量', maxUseCount:'最多使用次数', expireTime:'过期时间', tracked:'纳入统计', fullSync:'全量采集', role:'查看范围', handleStatus:'处理动作', handleRemark:'处理说明' }
export function labelFor(key, isEn) { return isEn ? key.replace(/([A-Z])/g, ' $1').replace(/^./, s => s.toUpperCase()) : zhLabels[key] || key }
export const fieldDictionaries = { noticeType:'noticeType', bizType:'videoBizType', msgType:'msgType', planType:'dutyPlanType', sourceType:'assetSourceType', seatType:'seatType', level:'honorLevel', category:'budgetCategory', checkStatus:'assetCheckStatus' }
export function typeFor(field) {
 if (/^(file|files)$/.test(field.key)) return 'file'
 if (field.type === 'array' || field.type === 'object') return 'json'
 if (/Time$/.test(field.key)) return 'datetime'
 if (/Date$/.test(field.key)) return 'date'
 if (field.type === 'integer' || field.type === 'number') return 'number'
 if (/password|accessToken/i.test(field.key)) return 'password'
 if (/content|description|reason|remark|brief/i.test(field.key)) return 'textarea'
 return 'text'
}
