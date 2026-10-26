import fs from 'node:fs'
import { fileURLToPath } from 'node:url'

export const dictionaries = JSON.parse(fs.readFileSync(new URL('./dictionaries.json', import.meta.url), 'utf8'))
export const mockDataDirectory = fileURLToPath(new URL('../.mock-data/', import.meta.url))
export const stamp = () => new Intl.DateTimeFormat('sv-SE', { timeZone: 'Asia/Shanghai', dateStyle: 'short', timeStyle: 'medium' }).format(new Date())
export function dateAt(offset = 0) { const d = new Date(); d.setUTCDate(d.getUTCDate() + offset); return new Intl.DateTimeFormat('en-CA', { timeZone: 'Asia/Shanghai', year: 'numeric', month: '2-digit', day: '2-digit' }).format(d) }
const media = name => `/__mock/media/${name}.svg`
export function seedDatabase(contracts) {
  const now = stamp(); const today = dateAt(); const month = today.slice(0, 7); const year = Number(today.slice(0, 4))
  const allPermissions = [...new Set(Object.values(contracts).map(c => c.permission).filter(p => /^[a-z]+:/.test(p)))]
  const roles = [
    { roleId: 1, roleName: '超级管理员', roleCode: 'SUPER_ADMIN', dataScope: 1, permCodes: allPermissions },
    { roleId: 2, roleName: '中心负责人', roleCode: 'CENTER_HEAD', dataScope: 1, permCodes: allPermissions.filter(p => !p.startsWith('system:role:')) },
    { roleId: 3, roleName: '部长', roleCode: 'MINISTER', dataScope: 1, permCodes: allPermissions.filter(p => /^(asset|honor|duty|common):/.test(p)) },
    { roleId: 4, roleName: '成员', roleCode: 'MEMBER', dataScope: 2, permCodes: [] }
  ].map((r, i) => ({ ...r, status: 1, sortNo: i + 1, createTime: now, remark: '本地模拟角色' }))
  const users = [
    ['admin', '林知远', 1, '软件工程'], ['member', '陈予安', 4, '软件工程'], ['member2', '周可宁', 4, '计算机科学'], ['minister', '许思源', 3, '人工智能'],
    ['leader', '沈以辰', 2, '软件工程'], ['member3', '陆星禾', 4, '数据科学'], ['member4', '江时雨', 4, '数字媒体'], ['member5', '宋景行', 4, '计算机科学']
  ].map(([username, realName, roleId, major], i) => ({ userId: i + 1, username, realName, roleIds: [roleId], studentNo: `2024${String(i + 1).padStart(6, '0')}`, grade: '2024', major, phone: `1380000${String(i + 1).padStart(4, '0')}`, email: `${username}@example.test`, avatarUrl: media(`avatar-${i + 1}`), status: 1, password: 'demo1234', createTime: now, lastLoginTime: now, remark: '本地测试人物 / 非真实成员' }))
  const periods = [
    { periodId: 1, title: `${year} 秋季创新项目预算`, year, totalAmount: 50000, status: 1, isPublic: 1, startDate: `${year}-09-01`, endDate: `${year}-12-31` },
    { periodId: 2, title: `${year} 开源社区专项`, year, totalAmount: 18000, status: 1, isPublic: 1, startDate: `${year}-01-01`, endDate: `${year}-12-31` },
    { periodId: 3, title: `${year + 1} 春季预算草案`, year: year + 1, totalAmount: 60000, status: 0, isPublic: 0, startDate: `${year + 1}-01-01`, endDate: `${year + 1}-06-30` }
  ].map(p => ({ ...p, usedAmount: 0, description: '用于验证预算流转的模拟周期', createTime: now, publicTime: now, version: 0 }))
  const items = [
    { itemId: 1, periodId: 1, name: '设备采购', category: 1, budgetAmount: 28000 }, { itemId: 2, periodId: 1, name: '活动与竞赛', category: 3, budgetAmount: 15000 },
    { itemId: 3, periodId: 1, name: '日常耗材', category: 2, budgetAmount: 7000 }, { itemId: 4, periodId: 2, name: '开源协作活动', category: 3, budgetAmount: 18000 }
  ].map(x => ({ ...x, usedAmount: 0, sortNo: x.itemId, createTime: now, remark: '模拟科目' }))
  const budgetApplies = [
    { applyId: 1, applyUserId: 2, periodId: 1, itemId: 1, title: '嵌入式开发板与传感器', amount: 1800, status: 0 },
    { applyId: 2, applyUserId: 3, periodId: 1, itemId: 2, title: '创新项目交流沙龙', amount: 1200, status: 0 },
    { applyId: 3, applyUserId: 2, periodId: 1, itemId: 1, title: '工作站升级配件', amount: 6400, approvedAmount: 6000, status: 1 },
    { applyId: 4, applyUserId: 6, periodId: 1, itemId: 3, title: '办公与展示耗材', amount: 900, approvedAmount: 900, status: 1 },
    { applyId: 5, applyUserId: 2, periodId: 2, itemId: 4, title: '社区活动差旅', amount: 2800, status: 2, auditRemark: '请补充日程后重新申请' }
  ].map(x => ({ ...x, reason: '本地模拟申请，可测试审核与撤回', expectDate: dateAt(7), createTime: now, auditTime: x.status ? now : null, auditUserId: x.status ? 1 : null }))
  const expenses = [
    { expenseId: 1, periodId: 1, itemId: 1, applyId: 3, title: '工作站配件采购', amount: 5800, expenseDate: dateAt(-3), handlerId: 1 },
    { expenseId: 2, periodId: 1, itemId: 3, applyId: 4, title: '展示区与办公耗材', amount: 860, expenseDate: dateAt(-2), handlerId: 1 },
    { expenseId: 3, periodId: 2, itemId: 4, title: '开源共创活动物料', amount: 2400, expenseDate: dateAt(-8), handlerId: 5 }
  ].map(x => ({ ...x, createTime: now, voucherFileId: null, remark: '本地测试支出' }))
  const categories = [{ categoryId: 1, name: '计算机与外设', code: 'COMPUTER', parentId: 0 }, { categoryId: 2, name: '开发与实验设备', code: 'DEVKIT', parentId: 0 }, { categoryId: 3, name: '办公家具', code: 'FURNITURE', parentId: 0 }].map(x => ({ ...x, sortNo: x.categoryId, createTime: now }))
  const assets = [
    ['高性能开发工作站',1,'Lenovo','ThinkStation',12800,1,2], ['27 英寸显示器',1,'Dell','U2724D',2400,4,3], ['ESP32-S3 开发套件',2,'Espressif','ESP32-S3',320,2,2],
    ['逻辑分析仪',2,'Saleae','Logic 8',1600,1,4], ['移动投影仪',1,'EPSON','EF-12',4200,2,1], ['人体工学椅',3,'SIHOO','C300',1200,1,6],
    ['树莓派实验套装',2,'Raspberry Pi','5',980,3,4], ['会议显示屏',1,'Huawei','IdeaHub',8600,1,5], ['旧款笔记本',1,'Lenovo','T480',4500,5,1]
  ].map(([name,categoryId,brand,model,price,status,keeperId],i)=>({ assetId:i+1,assetNo:`IEC-${String(i+1).padStart(4,'0')}`,name,categoryId,brand,model,price,status,keeperId,sourceType:1,location:'创新中心 · A 区',purchaseDate:dateAt(-90),supplier:'模拟供应商',budgetExpenseId:i===0?1:null,createTime:now,imageUrls:[media('asset')],remark:'本地模拟资产',version:0 }))
  const borrows = [
    { borrowId:1,assetId:3,userId:2,borrowType:1,status:0,purpose:'嵌入式项目实验',expectReturnTime:`${dateAt(7)} 18:00:00` },
    { borrowId:2,assetId:2,userId:3,borrowType:1,status:1,purpose:'前端开发调试',expectReturnTime:`${dateAt(3)} 18:00:00` },
    { borrowId:3,assetId:5,userId:2,borrowType:1,status:3,purpose:'沙龙投影',expectReturnTime:`${dateAt(-1)} 18:00:00`,actualReturnTime:`${dateAt(-2)} 17:00:00` }
  ].map(x=>({...x,createTime:now,applyTime:now,auditTime:x.status?now:null,auditUserId:x.status?1:null}))
  const areas=[{areaId:1,name:'A 区 · 开发协作空间',width:1200,height:700,planImageUrl:media('floor'),description:'拖拽成员调整工位，切换布局模式编辑坐标。',sortNo:1,status:1,createTime:now},{areaId:2,name:'B 区 · 共创实验室',width:1200,height:700,planImageUrl:media('floor'),sortNo:2,status:1,createTime:now}]
  const seats=Array.from({length:18},(_,i)=>{const local=i%12;const areaId=i<12?1:2;const occupants={0:1,1:2,2:3,3:4,6:5,7:6,12:7,13:8};const userId=occupants[i]||null;return {seatId:i+1,areaId,seatNo:`${areaId===1?'A':'B'}-${String(local+1).padStart(2,'0')}`,seatType:1,xRatio:9+(local%4)*22,yRatio:13+Math.floor(local/4)*27,widthRatio:14,heightRatio:18,rotation:0,status:userId?2:i===10?3:i===11?4:1,userId,assignTime:userId?now:null,remark:'模拟工位',version:0,createTime:now}})
  const seatApplies=[{applyId:1,userId:2,areaId:1,seatId:5,status:0,reason:'希望与项目小组集中办公',createTime:now},{applyId:2,userId:3,areaId:2,seatId:15,status:0,reason:'需要实验区工位',createTime:now}]
  const seatSwaps=[{swapId:1,applyUserId:2,targetUserId:3,applySeatId:2,targetSeatId:3,status:0,reason:'便于项目协作',createTime:now},{swapId:2,applyUserId:4,targetUserId:5,applySeatId:4,targetSeatId:7,status:1,reason:'按小组重新排座',createTime:now}]
  const alumni=[['顾清和','2023','字节跳动','前端工程师'],['苏望舒','2022','华为','研发工程师'],['程亦舟','2024','浙江大学','硕士研究生'],['林晚晴','2021','腾讯','产品设计师'],['叶知秋','2023','阿里巴巴','后端工程师'],['赵予初','2024','待完善','创新项目负责人']].map(([name,graduationYear,company,position],i)=>({alumniId:i+1,name,graduationYear,company,position,major:'软件工程',className:'软件工程创新班',gender:i%2+1,avatarUrl:media(`avatar-${i+1}`),brief:'此信息为前端展示测试而创建，不对应真实人物。',tags:'创新,开源,协作',status:i===5?0:1,isShow:i<4?1:0,sortNo:i*10,submitUserId:i===5?2:1,createTime:now,auditTime:i===5?null:now}))
  const honors=alumni.slice(0,5).map((x,i)=>({recordId:i+1,alumniId:x.alumniId,title:['创新创业大赛一等奖','优秀毕业生','国家奖学金','开源社区贡献奖','软件设计大赛二等奖'][i],level:i%3+1,awardDate:dateAt(-300-i*30),description:'前端测试荣誉记录',sortNo:i,createTime:now}))
  const dutyPlans=[{planId:1,title:`${month} 共创空间值日`,planType:2,startDate:`${month}-01`,endDate:`${month}-31`,memberIds:[1,2,3,4,5,6,7,8],groupConfig:[{name:'开发一组',memberIds:[1,2,3,4]},{name:'开发二组',memberIds:[5,6,7,8]}],taskContent:'整理工位、清洁公共区域、检查设备电源',status:1,publishTime:now,remark:'可测试生成、发布和换班',createTime:now}]
  const schedules=Array.from({length:28},(_,i)=>({scheduleId:i+1,planId:1,userId:i===14?2:i%8+1,dutyDate:dateAt(i-14),groupName:i%2?'开发一组':'开发二组',taskContent:'清洁公共区域、整理设备',status:i<12?1:0,confirmTime:i<12?now:null,createTime:now}))
  const swaps=[{swapId:1,applyUserId:3,targetUserId:2,scheduleId:16,targetScheduleId:null,status:0,reason:'课程安排冲突，申请代值',createTime:now}]
  const notices=[{noticeId:1,title:'欢迎来到 IECMP 创新创业中心',content:'<p>这里是本地模拟测试环境。所有成员、金额与成果均为演示数据。</p><p>你可以放心创建、修改、审核与删除，不会访问真实后端。</p>',noticeType:1,isTop:1,status:1,publishTime:now},{noticeId:2,title:'本周共创沙龙 · 从想法到原型',content:'<p>一起分享项目进展，交流设计与开发经验。请提前准备你的原型演示。</p>',noticeType:3,isTop:1,status:1,publishTime:now},{noticeId:3,title:'公共设备借用与归还须知',content:'<p>提交借用申请后由管理员审核。归还时请确认配件齐全并登记状态。</p>',noticeType:2,isTop:0,status:1,publishTime:now},{noticeId:4,title:'下月活动预告草稿',content:'<p>仅管理端可见的草稿内容。</p>',noticeType:3,isTop:0,status:0,publishTime:now}].map(x=>({...x,createTime:now,createBy:1}))
  const messages=[{messageId:1,receiverId:2,title:'今天轮到你值日啦',content:'清洁公共区域、整理设备。完成后记得在我的值日中确认。',msgType:1,bizType:'duty',bizId:15,isRead:0},{messageId:2,receiverId:2,title:'预算申请已通过',content:'工作站升级配件已部分批准 6000 元。',msgType:2,bizType:'budget',bizId:3,isRead:0},{messageId:3,receiverId:2,title:'欢迎加入共创空间',content:'完善个人资料，绑定代码托管账号，开始你的中心生活。',msgType:3,bizType:'system',bizId:2,isRead:1},{messageId:4,receiverId:1,title:'有新的成员入驻申请',content:'入驻审核列表有一条待处理申请。',msgType:3,bizType:'register',bizId:1,isRead:0}].map(x=>({...x,createTime:now}))
  const accounts=[{accountId:1,userId:2,platform:'github',accountName:'demo-chen',status:1,syncStatus:2,lastSyncTime:now,bindTime:now},{accountId:2,userId:3,platform:'gitee',accountName:'demo-zhou',status:1,syncStatus:2,lastSyncTime:now,bindTime:now},{accountId:3,userId:1,platform:'github',accountName:'demo-admin',status:1,syncStatus:2,lastSyncTime:now,bindTime:now}]
  const projects=accounts.flatMap((a,i)=>[{projectId:i*2+1,accountId:a.accountId,repoName:'iecmp-workspace',repoUrl:`https://example.test/${a.accountName}/iecmp`,ownerName:a.accountName,language:'Vue',defaultBranch:'main',starCount:12+i*4,forkCount:3,tracked:1,syncStatus:2,lastPushTime:now},{projectId:i*2+2,accountId:a.accountId,repoName:'innovation-lab',repoUrl:`https://example.test/${a.accountName}/lab`,ownerName:a.accountName,language:'Java',defaultBranch:'main',starCount:8,forkCount:1,tracked:1,syncStatus:2,lastPushTime:now}])
  const commits=Array.from({length:140},(_,i)=>({commitId:i+1,projectId:i%6+1,commitSha:(i+1000).toString(16).padStart(40,'0'),shortSha:(i+1000).toString(16).padStart(7,'0'),branch:i%5?'main':'feature/ui',authorName:users[[1,2,0][Math.floor((i%6)/2)]].realName,message:['feat: 完善工作台布局','fix: 优化表单校验','docs: 更新项目说明','refactor: 统一请求逻辑'][i%4],commitTime:`${dateAt(-(i%60))} ${String(9+i%10).padStart(2,'0')}:30:00`,addLines:15+i%120,delLines:2+i%20,isMerge:i%13===0?1:0,createTime:now}))
  return {version:1,seedDate:today,roles,users,periods,items,budgetApplies,expenses,categories,assets,borrows,assetRecords:[{recordId:1,assetId:2,recordType:2,fromUserId:1,toUserId:3,fromLocation:'设备柜',toLocation:'A-03',recordTime:now,operatorId:1,remark:'模拟借出记录',createTime:now}],inventories:[{inventoryId:1,title:'秋季设备例行盘点',status:0,beginTime:now,createTime:now}],inventoryItems:assets.map((a,i)=>({id:i+1,inventoryId:1,assetId:a.assetId,checkStatus:i<3?1:0,remark:'模拟盘点明细'})),areas,seats,seatApplies,seatSwaps,alumni,honors,dutyPlans,schedules,swaps,reminders:[{planId:0,aheadMinutes:30,channel:1,enabled:1}],notices,messages,accounts,projects,commits,syncLogs:accounts.map((a,i)=>({logId:i+1,accountId:a.accountId,userId:a.userId,syncType:1,syncResult:1,newCommitCount:16,startTime:now,endTime:now,costMs:620,errorMsg:null})),registerApplies:[{applyId:1,realName:'温书言',studentNo:'2024999901',major:'软件工程',phone:'13800009901',reason:'希望加入创新项目开发',status:0,ip:'127.0.0.1',applyTime:now,createTime:now}],invites:[{id:1,code:'DEMO2026',maxUseCount:100,usedCount:0,expireTime:`${dateAt(365)} 23:59:59`,status:1,createTime:now}],videos:[{videoId:1,title:'IECMP · 本地视频播放测试',playUrl:'/__mock/media/welcome.mp4',coverUrl:media('video-cover'),bizType:1,duration:6,sortNo:0,status:1,remark:'离线生成的视频，仅用于播放器测试',createTime:now}],files:[],logs:[{logId:1,module:'本地模拟',operation:'initialize',operatorId:1,operatorName:'林知远',requestUri:'/__mock/',requestParam:'演示数据初始化',status:1,costMs:18,ip:'127.0.0.1',createTime:now}],sessions:{},refreshSessions:{},captchas:{},scenarios:{}}
}
