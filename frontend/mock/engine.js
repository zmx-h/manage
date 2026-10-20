import { randomUUID } from 'node:crypto'
import { stamp, dateAt, seedDatabase, dictionaries } from './seed.js'
export class MockError extends Error { constructor(code, message) { super(message); this.code = code } }
const fail=(code,message)=>{throw new MockError(code,message)}
const clone=x=>JSON.parse(JSON.stringify(x))
const clean=row=>{if(!row)return row;const {password,accessToken,...data}=row;void password;void accessToken;return data}
const pageOf=(rows,q)=>{const pageNum=Math.max(1,+q.pageNum||1),pageSize=Math.min(100,Math.max(1,+q.pageSize||10));return {total:rows.length,pageNum,pageSize,pages:Math.ceil(rows.length/pageSize),list:rows.slice((pageNum-1)*pageSize,pageNum*pageSize)}}
const bindings={
 '1-08':['users','userId'],'1-09':['users','userId'],'1-10':['users','userId'],'1-11':['users','userId'],'1-14':['users','userId'],'1-15':['roles','roleId'],'1-16':['roles','roleId'],'1-17':['roles','roleId'],'1-18':['roles','roleId'],'1-19':['roles','roleId'],'1-25':['registerApplies','applyId'],'1-27':['invites','id'],
 '2-03':['accounts','accountId','userId'],'2-07':['projects','projectId'],'2-13':['accounts','accountId'],'2-16':['syncLogs','logId'],
 '3-01':['periods','periodId'],'3-02':['periods','periodId'],'3-04':['expenses','expenseId'],'3-06':['budgetApplies','applyId','applyUserId'],'3-07':['budgetApplies','applyId','applyUserId'],'3-10':['periods','periodId'],'3-11':['periods','periodId'],'3-12':['periods','periodId'],'3-13':['periods','periodId'],'3-17':['items','itemId'],'3-18':['items','itemId'],'3-19':['items','itemId'],'3-20':['budgetApplies','applyId'],'3-23':['expenses','expenseId'],'3-24':['expenses','expenseId'],
 '4-01':['assets','assetId'],'4-06':['borrows','borrowId','userId'],'4-08':['assets','assetId'],'4-10':['assets','assetId'],'4-11':['assets','assetId'],'4-12':['assets','assetId'],'4-15':['assetRecords','recordId'],'4-16':['assetRecords','recordId'],'4-17':['categories','categoryId'],'4-18':['categories','categoryId'],'4-19':['categories','categoryId'],'4-20':['borrows','borrowId'],'4-24':['inventories','inventoryId'],
 '5-05':['seatApplies','applyId','userId'],'5-08':['areas','areaId'],'5-09':['areas','areaId'],'5-10':['areas','areaId'],'5-11':['seats','seatId'],'5-12':['seats','seatId'],'5-14':['seats','seatId'],'5-18':['seatApplies','applyId'],'5-21':['seatSwaps','swapId'],'5-23':['seatSwaps','swapId'],
 '6-01':['alumni','alumniId'],'6-03':['alumni','alumniId'],'6-05':['alumni','alumniId'],'6-06':['alumni','alumniId'],'6-07':['alumni','alumniId'],'6-08':['alumni','alumniId'],'6-11':['honors','recordId'],'6-12':['honors','recordId'],'6-13':['honors','recordId'],'6-15':['alumni','alumniId','submitUserId'],'6-17':['alumni','alumniId'],
 '7-01':['schedules','scheduleId'],'7-06':['swaps','swapId'],'7-08':['dutyPlans','planId'],'7-09':['dutyPlans','planId'],'7-10':['dutyPlans','planId'],'7-11':['dutyPlans','planId'],'7-14':['schedules','scheduleId'],'7-15':['schedules','scheduleId'],'7-16':['schedules','scheduleId'],'7-17':['schedules','scheduleId'],
 '8-05':['notices','noticeId'],'8-06':['notices','noticeId'],'8-07':['notices','noticeId'],'8-08':['notices','noticeId'],'8-09':['notices','noticeId'],'8-12':['logs','logId'],'8-15':['videos','videoId'],'8-16':['videos','videoId'],'8-17':['videos','videoId'],'8-18':['videos','videoId'],'8-19':['videos','videoId'],'8-20':['messages','messageId','receiverId']
}
export function createMockEngine(contracts,initial,persist=()=>{}) {
 let db=initial||seedDatabase(contracts)
 const save=()=>persist(db)
 const find=(table,key,id)=>db[table].find(r=>String(r[key])===String(id))||fail(404,'本地模拟记录不存在')
 const next=(table,key)=>Math.max(0,...db[table].map(r=>+r[key]||0))+1
 const insert=(table,key,values)=>{const row={...values,[key]:next(table,key),createTime:stamp(),updateTime:stamp()};db[table].unshift(row);return row}
 const remove=(table,key,id)=>{find(table,key,id);db[table]=db[table].filter(r=>String(r[key])!==String(id))}
 const own=(row,u,key='userId')=>{if(row[key]!==u.userId)fail(403,'只能操作本人模拟数据');return row}
 const pending=(row,status=0)=>{if(row.status!==status)fail(400,'当前状态不允许此操作');return row}
 const name=id=>db.users.find(u=>u.userId===+id)?.realName||'—'
 function info(u){const roles=u.roleIds.map(id=>db.roles.find(r=>r.roleId===id)).filter(Boolean);return {...clean(u),roles:roles.map(r=>r.roleCode),permissions:[...new Set(roles.flatMap(r=>r.permCodes||[]))],roleCodes:roles.map(r=>r.roleCode),roleNames:roles.map(r=>r.roleName)}}
 function login(u){const accessToken=`mock-access-${randomUUID()}`,refreshToken=`mock-refresh-${randomUUID()}`;db.sessions[accessToken]=u.userId;db.refreshSessions[refreshToken]=u.userId;return {accessToken,refreshToken,expiresIn:7200,tokenType:'Bearer',userInfo:info(u)}}
 function identity(token){return db.users.find(u=>u.userId===db.sessions[token]&&u.status===1)||fail(401,'模拟登录已过期，请重新登录')}
 function notify(receiverId,title,bizType,bizId){insert('messages','messageId',{receiverId,title,content:'本地测试操作已完成，可到对应页面查看。',msgType:2,bizType,bizId,isRead:0})}
 function aggregate(){for(const i of db.items)i.usedAmount=db.expenses.filter(e=>e.itemId===i.itemId).reduce((s,e)=>s+e.amount,0);for(const p of db.periods){p.usedAmount=db.expenses.filter(e=>e.periodId===p.periodId).reduce((s,e)=>s+e.amount,0);p.remainAmount=p.totalAmount-p.usedAmount;p.executionRate=p.totalAmount?+(p.usedAmount/p.totalAmount*100).toFixed(1):0;p.itemCount=db.items.filter(i=>i.periodId===p.periodId).length;p.applyCount=db.budgetApplies.filter(a=>a.periodId===p.periodId).length}}
 function decorate(table,row){if(!row)return null;if(table==='users')return info(row);const r={...clean(row)};
  for(const [key,label]of [['userId','userName'],['applyUserId','applyUserName'],['targetUserId','targetUserName'],['keeperId','keeperName'],['handlerId','handlerName'],['submitUserId','submitUserName'],['operatorId','operatorName'],['auditUserId','auditUserName']])if(row[key])r[label]=name(row[key])
  for(const [id,t,label,labelKey]of [['periodId','periods','periodTitle','title'],['itemId','items','itemName','name'],['categoryId','categories','categoryName','name'],['areaId','areas','areaName','name'],['seatId','seats','seatNo','seatNo'],['assetId','assets','assetName','name']])if(row[id])r[label]=db[t].find(x=>x[id]===row[id])?.[labelKey]
  if(row.assetId)r.assetNo=db.assets.find(a=>a.assetId===row.assetId)?.assetNo
  if(table==='roles')Object.assign(r,{permissionCount:row.permCodes?.length||0,userCount:db.users.filter(u=>u.roleIds.includes(row.roleId)).length})
  if(table==='accounts')Object.assign(r,{realName:name(row.userId),grade:db.users.find(u=>u.userId===row.userId)?.grade,platformName:row.platform==='github'?'GitHub':'Gitee',projectCount:db.projects.filter(p=>p.accountId===row.accountId).length,commitCount:commits(row.userId).length})
  if(table==='projects')r.commitCount=db.commits.filter(c=>c.projectId===row.projectId).length
  if(table==='commits')r.projectName=db.projects.find(p=>p.projectId===row.projectId)?.repoName
  if(table==='alumni')Object.assign(r,{honorCount:db.honors.filter(h=>h.alumniId===row.alumniId).length,canWithdraw:row.status===0?1:0})
  if(table==='dutyPlans'){const entries=db.schedules.filter(s=>s.planId===row.planId);Object.assign(r,{memberCount:row.memberIds?.length||0,groupCount:row.groupConfig?.length||0,scheduleCount:entries.length,completedCount:entries.filter(e=>e.status===1).length})}
  if(table==='inventories'){const entries=db.inventoryItems.filter(i=>i.inventoryId===row.inventoryId);Object.assign(r,{items:entries.map(i=>decorate('inventoryItems',i)),totalCount:entries.length,checkedCount:entries.filter(i=>i.checkStatus!==0).length,normalCount:entries.filter(i=>i.checkStatus===1).length,abnormalCount:entries.filter(i=>i.checkStatus>1).length,progressRate:entries.length?Math.round(entries.filter(i=>i.checkStatus!==0).length/entries.length*100):0})}return r
 }
 function filter(table,q,rows=db[table]){return rows.filter(row=>{if(q.keyword&&!Object.values(clean(row)).join(' ').toLowerCase().includes(String(q.keyword).toLowerCase()))return false;for(const key of ['status','periodId','itemId','categoryId','userId','applyUserId','accountId','platform','tracked','assetId','borrowType','recordType','planId','planType','areaId','submitUserId','isShow','isPublic','noticeType','bizType','msgType','isRead','syncType','syncResult','grade','year'])if(q[key]!==undefined&&q[key]!==''&&String(row[key])!==String(q[key]))return false;if(q.roleCode&&!info(row).roles.includes(q.roleCode))return false;const date=row.commitTime||row.expenseDate||row.dutyDate||row.purchaseDate||row.createTime||'';if((q.beginTime&&date<q.beginTime)||(q.endTime&&date>q.endTime)||(q.beginDate&&date.slice(0,10)<q.beginDate)||(q.endDate&&date.slice(0,10)>q.endDate))return false;return true})}
 function commits(userId,q={}){const accounts=db.accounts.filter(a=>(!userId||a.userId===userId)&&a.status===1);const projects=db.projects.filter(p=>p.tracked===1&&accounts.some(a=>a.accountId===p.accountId));return filter('commits',q,db.commits.filter(c=>projects.some(p=>p.projectId===c.projectId)&&(!q.projectId||c.projectId===+q.projectId)&&(q.includeMerge===1||!c.isMerge)))}
 function contribution(userId,q={}){const rows=commits(userId,q),activeDays=new Set(rows.map(c=>c.commitTime.slice(0,10))).size,addLines=rows.reduce((s,c)=>s+c.addLines,0);return {commitCount:rows.length,activeDays,addLines,delLines:rows.reduce((s,c)=>s+c.delLines,0),projectCount:new Set(rows.map(c=>c.projectId)).size,branchCount:new Set(rows.map(c=>c.branch)).size,rank:1,contributionScore:+(rows.length+activeDays*2+addLines*.01).toFixed(2),lastSyncTime:stamp()}}
 function trend(userId,q={}){const rows=commits(userId,q),result=[];let day=q.beginDate||dateAt(-29);const end=q.endDate||dateAt();for(let i=0;day<=end&&i<366;i++){const entries=rows.filter(c=>c.commitTime.slice(0,10)===day);result.push({date:day,commitCount:entries.length,memberCount:new Set(entries.map(c=>c.authorName)).size,addLines:entries.reduce((s,c)=>s+c.addLines,0),delLines:entries.reduce((s,c)=>s+c.delLines,0)});const date=new Date(`${day}T12:00:00Z`);date.setUTCDate(date.getUTCDate()+1);day=date.toISOString().slice(0,10)}return result}
 function seat(s,u,admin){const owner=db.users.find(x=>x.userId===s.userId),r={...s,occupantName:owner?.realName||null,isMine:s.userId===u.userId?1:0};delete r.userId;if(admin)Object.assign(r,{occupantUserId:owner?.userId||null,occupantGrade:owner?.grade,occupantMajor:owner?.major});return r}
 function areas(q,u,admin){return db.areas.filter(a=>!q.areaId||a.areaId===+q.areaId).map(a=>{const rows=db.seats.filter(s=>s.areaId===a.areaId);return {...a,seats:rows.map(s=>seat(s,u,admin)),seatCount:rows.length,seatStat:{total:rows.length,free:rows.filter(s=>s.status===1).length,used:rows.filter(s=>s.status===2).length,reserved:rows.filter(s=>s.status===3).length,repairing:rows.filter(s=>s.status===4).length}}})}
 function assign(s,userId){if(s.status!==1&&s.userId!==userId)fail(50005,'工位当前不可分配');find('users','userId',userId);const old=db.seats.find(x=>x.userId===userId&&x!==s);if(old)Object.assign(old,{userId:null,status:1,assignTime:null});Object.assign(s,{userId,status:2,assignTime:stamp()});return old?.seatId||null}
 function assetStat(){return {total:db.assets.length,totalValue:db.assets.reduce((s,a)=>s+a.price,0),...Object.fromEntries(Object.entries({inUse:1,idle:2,repair:3,borrowed:4,scrapped:5}).map(([k,v])=>[k,db.assets.filter(a=>a.status===v).length])),categoryStats:db.categories.map(c=>({categoryId:c.categoryId,categoryName:c.name,count:db.assets.filter(a=>a.categoryId===c.categoryId).length}))}}
 function audit(table,key,id,q,u,owner,biz){const r=pending(find(table,key,id));if(![1,2].includes(q.auditStatus))fail(400,'审核动作须为 1 通过 / 2 驳回');Object.assign(r,{status:q.auditStatus,auditRemark:q.auditRemark||'',auditTime:stamp(),auditUserId:u.userId});if(r[owner])notify(r[owner],q.auditStatus===1?'申请已通过':'申请已驳回',biz,id);return r}
 function returnAsset(b,u){if(![1,4].includes(b.status)||b.borrowType!==1)fail(400,'当前不可归还');Object.assign(b,{status:3,actualReturnTime:stamp()});find('assets','assetId',b.assetId).status=1;insert('assetRecords','recordId',{assetId:b.assetId,recordType:3,operatorId:u.userId,recordTime:stamp(),fromUserId:b.userId});return null}
 function api(id,q,u,token,upload){const now=stamp(),today=dateAt();switch(id){
 case '1-01':case '1-02':{const user=db.users.find(x=>x.username===q.username);if(!user||user.password!==q.password)fail(10003,'模拟账号或密码错误，请查看模拟控制台');if(!user.status)fail(10002,'账号已禁用');if(id==='1-01'&&!info(user).permissions.length)fail(403,'成员账号请使用成员登录');return login(user)}
 case '1-03':{const uid=db.sessions[token];for(const[k,v]of Object.entries(db.sessions))if(v===uid)delete db.sessions[k];for(const[k,v]of Object.entries(db.refreshSessions))if(v===uid)delete db.refreshSessions[k];return null}
 case '1-04':{const user=db.users.find(x=>x.userId===db.refreshSessions[q.refreshToken]&&x.status===1);if(!user)fail(10007,'刷新令牌已失效');return login(user)}
 case '1-05':return info(u)
 case '1-06':Object.assign(u,q);return null
 case '1-07':if(u.password!==q.oldPassword)fail(10010,'原密码错误');if(q.newPassword!==q.confirmPassword)fail(10011,'两次新密码不一致');u.password=q.newPassword;for(const[k,v]of Object.entries(db.sessions))if(v===u.userId)delete db.sessions[k];return null
 case '1-12':{const user=find('users','userId',q.id);if(user.roleIds.includes(1)&&q.status===0)fail(10016,'不能禁用超级管理员');user.status=q.status;return null}
 case '1-13':find('users','userId',q.id).password=q.newPassword||'demo1234';return null
 case '1-20':find('roles','roleId',q.id).permCodes=q.permCodes;return null
 case '1-21':return [...new Set(Object.values(contracts).map(c=>c.permission).filter(p=>/^[a-z]+:/.test(p)))].reduce((groups,code)=>{const module=code.split(':')[0];let group=groups.find(g=>g.module===module);if(!group){group={label:{system:'用户与权限',repo:'代码贡献',budget:'预算管理',asset:'资产管理',seat:'工位管理',honor:'学长展板',duty:'值日排班',common:'通用支撑'}[module]||module,module,children:[]};groups.push(group)}group.children.push({code,label:Object.values(contracts).find(c=>c.permission===code)?.name||code,module});return groups},[])
 case '1-22':return {inviteEnabled:1,applyEnabled:1,captchaEnabled:0,tips:'本地模拟：邀请码 DEMO2026，验证码 2468'}
 case '1-23':{const invite=db.invites.find(x=>x.code===q.inviteCode&&x.status===1&&x.usedCount<x.maxUseCount);if(!invite)fail(10026,'邀请码无效或已用完');if(db.users.some(x=>x.username===q.username||x.studentNo===q.studentNo))fail(10013,'账号或学号已存在');const row=insert('users','userId',{...q,roleIds:[4],status:1});invite.usedCount++;return {userId:row.userId,username:row.username}}
 case '1-24':{if(q.captchaCode!=='2468')fail(10004,'模拟验证码为 2468');const old=db.registerApplies.find(a=>a.studentNo===q.studentNo);if(old){Object.assign(old,q,{status:0});return {applyId:old.applyId}}return {applyId:insert('registerApplies','applyId',{...q,status:0,ip:'127.0.0.1',applyTime:now}).applyId}}
 case '1-26':{const a=pending(find('registerApplies','applyId',q.id));if(q.auditStatus===2){audit('registerApplies','applyId',q.id,q,u,'createUserId','register');return null}const username=q.username||`student${a.studentNo}`;if(db.users.some(x=>x.username===username))fail(10013,'账号已存在');const user=insert('users','userId',{username,realName:a.realName,studentNo:a.studentNo,major:a.major,phone:a.phone,roleIds:q.roleIds||[4],status:1,password:'demo1234'});audit('registerApplies','applyId',q.id,q,u,'createUserId','register');a.createUserId=user.userId;return {userId:user.userId,username,initialPassword:'demo1234'}}
 case '1-28':{const codes=[];for(let i=0;i<Math.min(50,q.count||1);i++){const code=randomUUID().slice(0,8).toUpperCase();insert('invites','id',{...q,code,maxUseCount:q.maxUseCount||1,usedCount:0,status:1});codes.push(code)}return {codes}}
 case '1-29':find('invites','id',q.id).status=0;return null
 case '2-01':return [{platform:'github',platformName:'GitHub',tokenUrl:'https://github.com/settings/tokens',tokenTip:'模拟模式请勿输入真实令牌，不会访问 GitHub'},{platform:'gitee',platformName:'Gitee',tokenUrl:'https://gitee.com/profile/personal_access_tokens',tokenTip:'任意测试令牌仅在本地使用'}]
 case '2-02':{if(db.accounts.some(a=>a.userId===u.userId&&a.platform===q.platform&&a.accountName===q.accountName))fail(20002,'已绑定该账号');const row=insert('accounts','accountId',{platform:q.platform,accountName:q.accountName,userId:u.userId,status:1,syncStatus:2,lastSyncTime:now,bindTime:now});insert('projects','projectId',{accountId:row.accountId,repoName:'local-test-project',ownerName:row.accountName,repoUrl:'https://example.test/local',language:'Vue',defaultBranch:'main',tracked:1,syncStatus:2});return {accountId:row.accountId,projectCount:1}}
 case '2-04':Object.assign(own(find('accounts','accountId',q.accountId),u),{status:1,syncStatus:2,lastSyncTime:now});return null
 case '2-05':{const a=own(find('accounts','accountId',q.id),u);const ids=db.projects.filter(p=>p.accountId===a.accountId).map(p=>p.projectId);db.projects=db.projects.filter(p=>!ids.includes(p.projectId));db.commits=db.commits.filter(c=>!ids.includes(c.projectId));remove('accounts','accountId',q.id);return null}
 case '2-06':return db.accounts.filter(a=>a.userId===u.userId).map(a=>({...decorate('accounts',a),lastSyncResult:1,newCommitCount:16,errorMsg:null,nextSyncTime:`${dateAt(1)} 02:00:00`}))
 case '2-08':{const p=find('projects','projectId',q.id);own(find('accounts','accountId',p.accountId),u);p.tracked=q.tracked;return null}
 case '2-09':return pageOf(commits(u.userId,q).map(c=>decorate('commits',c)),q)
 case '2-10':return trend(u.userId,q)
 case '2-11':{const year=String(q.year||today.slice(0,4)),points=trend(q.userId||u.userId,{beginDate:`${year}-01-01`,endDate:`${year}-12-31`});return {year,totalCommits:points.reduce((s,p)=>s+p.commitCount,0),activeDays:points.filter(p=>p.commitCount).length,maxDaily:Math.max(0,...points.map(p=>p.commitCount)),cells:points.map(p=>({date:p.date,count:p.commitCount,level:Math.min(4,p.commitCount)}))}}
 case '2-12':return contribution(u.userId,q)
 case '2-14':return db.users.map(user=>({userId:user.userId,realName:user.realName,grade:user.grade,avatarUrl:user.avatarUrl,...contribution(user.userId,q)})).sort((a,b)=>b.commitCount-a.commitCount).slice(0,q.limit||20).map((r,i)=>({...r,rank:i+1}))
 case '2-15':return trend(null,q)
 case '2-17':{const rows=q.accountIds?.length?db.accounts.filter(a=>q.accountIds.includes(a.accountId)):db.accounts;for(const a of rows){a.lastSyncTime=now;a.syncStatus=2;insert('syncLogs','logId',{accountId:a.accountId,userId:a.userId,syncType:2,syncResult:1,newCommitCount:0,startTime:now,endTime:now,costMs:20})}return {taskId:`local-${randomUUID()}`,accountCount:rows.length,message:'本地模拟采集完成；未访问任何外部托管平台'}}
 case '3-03':return db.items.filter(i=>i.periodId===+q.id).map(i=>({...i,remainAmount:i.budgetAmount-i.usedAmount}))
 case '3-05':{const p=find('periods','periodId',q.periodId),i=find('items','itemId',q.itemId);if(p.status!==1||i.periodId!==p.periodId)fail(30001,'请选择执行中周期与所属科目');if(q.amount<=0||q.amount>i.budgetAmount-i.usedAmount)fail(30002,'金额不合法或超过剩余额度');return {applyId:insert('budgetApplies','applyId',{...q,applyUserId:u.userId,status:0}).applyId}}
 case '3-08':pending(own(find('budgetApplies','applyId',q.id),u,'applyUserId')).status=3;return null
 case '3-09':{const rows=db.budgetApplies.filter(a=>a.applyUserId===u.userId);return {applyCount:rows.length,pendingCount:rows.filter(a=>a.status===0).length,approvedCount:rows.filter(a=>a.status===1).length,approvedAmount:rows.filter(a=>a.status===1).reduce((s,a)=>s+(a.approvedAmount??a.amount),0)}}
 case '3-14':{const p=find('periods','periodId',q.id);if(![0,1,2].includes(q.status)||q.status<p.status)fail(400,'周期状态不可回退');p.status=q.status;return null}
 case '3-15':Object.assign(find('periods','periodId',q.id),{isPublic:q.isPublic,publicTime:now});return null
 case '3-16':return fail(30020,'文档约定：v1 不开放集中征集')
 case '3-21':{const row=pending(find('budgetApplies','applyId',q.id));if(q.approvedAmount>row.amount)fail(400,'批准金额不得高于申请金额');audit('budgetApplies','applyId',q.id,q,u,'applyUserId','budget');if(q.auditStatus===1)row.approvedAmount=q.approvedAmount??row.amount;return null}
 case '3-22':{const p=find('periods','periodId',q.periodId),i=find('items','itemId',q.itemId);if(p.status!==1||i.periodId!==p.periodId)fail(400,'周期或科目不匹配');if(q.amount<=0||q.amount>p.totalAmount-p.usedAmount||q.amount>i.budgetAmount-i.usedAmount)fail(400,'超过预算剩余额度');if(q.applyId){const a=find('budgetApplies','applyId',q.applyId),spent=db.expenses.filter(e=>e.applyId===a.applyId).reduce((s,e)=>s+e.amount,0);if(a.status!==1||a.periodId!==p.periodId||a.itemId!==i.itemId||spent+q.amount>(a.approvedAmount??a.amount))fail(400,'关联申请未通过、科目不匹配或超过批准金额')}const row=insert('expenses','expenseId',{...q,handlerId:u.userId});aggregate();return {expenseId:row.expenseId,periodUsedAmount:p.usedAmount,itemUsedAmount:i.usedAmount}}
 case '3-25':{const p=q.periodId?find('periods','periodId',q.periodId):db.periods.find(p=>p.status===1)||db.periods[0],rows=db.items.filter(i=>i.periodId===p?.periodId).map(i=>({...i,remainAmount:i.budgetAmount-i.usedAmount,executionRate:i.budgetAmount?+(i.usedAmount/i.budgetAmount*100).toFixed(1):0}));return {...p,periodTitle:p?.title,itemStats:rows,overBudgetItems:rows.reduce((s,i)=>s+i.budgetAmount,0)>(p?.totalAmount||0)?rows:[]}}
 case '4-02':case '4-09':{const row=decorate('assets',find('assets','assetId',q.id));return {...row,records:db.assetRecords.filter(r=>r.assetId===row.assetId).map(r=>decorate('assetRecords',r)),borrowHistory:db.borrows.filter(b=>b.assetId===row.assetId).map(b=>decorate('borrows',b))}}
 case '4-03':{const tree=parent=>db.categories.filter(c=>c.parentId===parent).map(c=>({...c,assetCount:db.assets.filter(a=>a.categoryId===c.categoryId).length,children:tree(c.categoryId)}));return tree(0)}
 case '4-04':return assetStat()
 case '4-05':{const a=find('assets','assetId',q.assetId);if(![1,2].includes(a.status))fail(40006,'资产当前不可借用');return {borrowId:insert('borrows','borrowId',{...q,userId:u.userId,status:0,applyTime:now}).borrowId}}
 case '4-07':return returnAsset(own(find('borrows','borrowId',q.id),u),u)
 case '4-13':if(q.status===4)fail(400,'已借出由借用流程维护');find('assets','assetId',q.id).status=q.status;insert('assetRecords','recordId',{assetId:+q.id,recordType:q.status===5?5:4,remark:q.remark,operatorId:u.userId,recordTime:now});return null
 case '4-14':{const a=find('assets','assetId',q.id);insert('assetRecords','recordId',{assetId:a.assetId,recordType:6,fromUserId:a.keeperId,toUserId:q.keeperId,fromLocation:a.location,toLocation:q.location,operatorId:u.userId,recordTime:now});if(q.keeperId)a.keeperId=q.keeperId;if(q.location)a.location=q.location;return null}
 case '4-21':{const b=pending(find('borrows','borrowId',q.id)),a=find('assets','assetId',b.assetId);if(q.auditStatus===1&&![1,2].includes(a.status))fail(40006,'资产已被借出');audit('borrows','borrowId',q.id,q,u,'userId','asset');if(b.status===1){a.status=b.borrowType===1?4:1;if(b.borrowType===2)a.keeperId=b.userId;insert('assetRecords','recordId',{assetId:a.assetId,recordType:b.borrowType===1?2:1,toUserId:b.userId,operatorId:u.userId,recordTime:now})}return null}
 case '4-22':return returnAsset(find('borrows','borrowId',q.id),u)
 case '4-23':{if(db.inventories.some(i=>i.status===0))fail(40013,'请先结束已有进行中盘点');const row=insert('inventories','inventoryId',{...q,status:0,beginTime:now});for(const a of db.assets.filter(a=>(!q.categoryId||a.categoryId===q.categoryId)&&(q.excludeScrapped===0||a.status!==5)))insert('inventoryItems','id',{inventoryId:row.inventoryId,assetId:a.assetId,checkStatus:0});return {inventoryId:row.inventoryId,totalCount:db.inventoryItems.filter(i=>i.inventoryId===row.inventoryId).length}}
 case '4-25':{const r=find('inventoryItems','id',q.id);pending(find('inventories','inventoryId',r.inventoryId));Object.assign(r,{checkStatus:q.checkStatus,remark:q.remark});return null}
 case '4-26':{const row=pending(find('inventories','inventoryId',q.id));Object.assign(row,{status:1,endTime:now});return {...decorate('inventories',row),uncheckedCount:db.inventoryItems.filter(i=>i.inventoryId===row.inventoryId&&i.checkStatus===0).length}}
 case '4-27':return {__export:filter('assets',q).map(a=>decorate('assets',a))}
 case '5-01':case '5-25':case '5-07':return areas(q,u,id!=='5-01')
 case '5-02':return filter('seats',q).map(s=>seat(s,u,false))
 case '5-03':{const s=db.seats.find(s=>s.userId===u.userId);return s?{...seat(s,u,false),areaName:db.areas.find(a=>a.areaId===s.areaId)?.name}:null}
 case '5-04':if(db.seatApplies.some(a=>a.userId===u.userId&&a.status===0))fail(50008,'已有待审核申请');find('areas','areaId',q.areaId);return {applyId:insert('seatApplies','applyId',{...q,userId:u.userId,status:0}).applyId}
 case '5-06':pending(own(find('seatApplies','applyId',q.id),u)).status=3;return null
 case '5-13':{if(q.seats.length>200)fail(400,'单次最多 200 项');const rows=q.seats.map(s=>[find('seats','seatId',s.seatId),s]);if(rows.some(([s])=>s.areaId!==q.areaId))fail(400,'工位不属于该区域');for(const[s,v]of rows)for(const key of ['xRatio','yRatio','widthRatio','heightRatio','rotation'])if(v[key]!==undefined)s[key]=v[key];return {updatedCount:rows.length}}
 case '5-15':{const s=find('seats','seatId',q.id);return {seatId:s.seatId,releasedSeatId:assign(s,q.userId)}}
 case '5-16':{const s=find('seats','seatId',q.id);if(s.status!==2)fail(400,'仅已占用工位可以释放');Object.assign(s,{status:1,userId:null,assignTime:null});return null}
 case '5-17':if(q.status===2)fail(400,'已占用状态需通过分配工位维护');find('seats','seatId',q.id).status=q.status;return null
 case '5-19':{const a=pending(find('seatApplies','applyId',q.id));if(q.auditStatus===1){const s=find('seats','seatId',q.assignSeatId||a.seatId);assign(s,a.userId);a.assignSeatId=s.seatId}audit('seatApplies','applyId',q.id,q,u,'userId','seat');return null}
 case '5-20':{const a=db.seats.find(s=>s.userId===u.userId),b=db.seats.find(s=>s.userId===q.targetUserId);if(!a||!b||a===b)fail(50011,'双方须分别占有不同工位');return {swapId:insert('seatSwaps','swapId',{...q,applyUserId:u.userId,applySeatId:a.seatId,targetSeatId:b.seatId,status:0}).swapId}}
 case '5-22':{const s=pending(own(find('seatSwaps','swapId',q.id),u,'targetUserId'));s.status=q.handleStatus===1?1:3;return null}
 case '5-24':{const s=pending(find('seatSwaps','swapId',q.id),1);if(![1,2].includes(q.auditStatus))fail(400,'无效审核动作');if(q.auditStatus===1){const a=find('seats','seatId',s.applySeatId),b=find('seats','seatId',s.targetSeatId);if(a.userId!==s.applyUserId||b.userId!==s.targetUserId)fail(50011,'工位归属已经变化');[a.userId,b.userId]=[b.userId,a.userId];s.status=2}else s.status=3;Object.assign(s,{auditRemark:q.auditRemark,auditTime:now});notify(s.applyUserId,'座位调换审核完成','seat',s.swapId);return null}
 case '5-26':{const a=find('seats','seatId',q.fromSeatId),b=find('seats','seatId',q.toSeatId);if(a===b)fail(50016,'源工位与目标不能相同');if(!a.userId)fail(50015,'源工位无人');if([3,4].includes(b.status))fail(50017,'目标为预留或维修工位');const action=b.userId?'swap':'move';[a.userId,b.userId]=[b.userId,a.userId];a.status=a.userId?2:1;b.status=2;a.assignTime=a.userId?now:null;b.assignTime=now;return {action,fromSeat:seat(a,u,true),toSeat:seat(b,u,true)}}
 case '6-02':{const a=find('alumni','alumniId',q.id);if(a.status!==1||a.isShow!==1)fail(60001,'该记录未公开');return {...decorate('alumni',a),honors:db.honors.filter(h=>h.alumniId===a.alumniId)}}
 case '6-04':return {alumniCount:db.alumni.filter(a=>a.isShow===1&&a.status===1).length,honorCount:db.honors.length,companyCount:new Set(db.alumni.map(a=>a.company)).size}
 case '6-09':{const a=find('alumni','alumniId',q.id);if(q.isShow&&a.status!==1)fail(60006,'须审核通过后才能上板');a.isShow=q.isShow;return null}
 case '6-10':find('alumni','alumniId',q.id).sortNo=q.sortNo;return null
 case '6-14':return {alumniId:insert('alumni','alumniId',{...q,status:0,isShow:0,submitUserId:u.userId}).alumniId}
 case '6-16':pending(own(find('alumni','alumniId',q.id),u,'submitUserId'));remove('alumni','alumniId',q.id);return null
 case '6-18':{const a=audit('alumni','alumniId',q.id,q,u,'submitUserId','honor');a.isShow=q.auditStatus===1?(q.isShow||0):0;return null}
 case '6-19':{const a=own(find('alumni','alumniId',q.id),u,'submitUserId');if(![1,2].includes(a.status))fail(60008,'当前不可重新提交');Object.assign(a,q,{status:0,isShow:0});return null}
 case '7-02':{const rows=db.schedules.filter(s=>s.userId===u.userId&&db.dutyPlans.some(p=>p.planId===s.planId&&p.status===1));return {todayDuty:decorate('schedules',rows.find(s=>s.dutyDate===today)),nextDuty:decorate('schedules',rows.filter(s=>s.dutyDate>=today&&s.status===0).sort((a,b)=>a.dutyDate.localeCompare(b.dutyDate))[0]),pendingCount:rows.filter(s=>s.status===0).length,completedCount:rows.filter(s=>s.status===1).length,swappedCount:rows.filter(s=>s.status===3).length,pendingSwapCount:db.swaps.filter(s=>s.targetUserId===u.userId&&s.status===0).length}}
 case '7-03':{const month=q.month||today.slice(0,7),count=new Date(+month.slice(0,4),+month.slice(5,7),0).getDate();return {month,planId:+q.planId||1,planTitle:db.dutyPlans[0]?.title,days:Array.from({length:count},(_,i)=>{const date=`${month}-${String(i+1).padStart(2,'0')}`,duties=db.schedules.filter(s=>s.dutyDate===date&&(!q.planId||s.planId===+q.planId)&&db.dutyPlans.some(p=>p.planId===s.planId&&p.status===1)).map(s=>decorate('schedules',s));return {date,weekday:new Date(`${date}T12:00:00Z`).getUTCDay()||7,isMine:duties.some(s=>s.userId===u.userId)?1:0,duties}})}}
 case '7-04':{const s=pending(own(find('schedules','scheduleId',q.id),u));Object.assign(s,{status:1,confirmTime:now,remark:q.remark});return null}
 case '7-05':{const s=pending(own(find('schedules','scheduleId',q.scheduleId),u));find('users','userId',q.targetUserId);return {swapId:insert('swaps','swapId',{...q,applyUserId:u.userId,dutyDate:s.dutyDate,status:0}).swapId}}
 case '7-07':{const swap=pending(own(find('swaps','swapId',q.id),u,'targetUserId')),s=pending(find('schedules','scheduleId',swap.scheduleId));if(q.handleStatus===1){if(swap.targetScheduleId){const target=pending(own(find('schedules','scheduleId',swap.targetScheduleId),u));[s.userId,target.userId]=[target.userId,s.userId]}else{s.status=3;insert('schedules','scheduleId',{planId:s.planId,userId:u.userId,dutyDate:s.dutyDate,groupName:s.groupName,taskContent:s.taskContent,status:0})}swap.status=1}else swap.status=2;swap.handleRemark=q.handleRemark;return null}
 case '7-12':{const p=find('dutyPlans','planId',q.id);if(!p.memberIds?.length)fail(70001,'计划没有参与成员');if(q.overwrite===1)db.schedules=db.schedules.filter(s=>s.planId!==p.planId);let date=p.startDate,index=0,generatedCount=0;for(let day=0;date<=p.endDate&&day<366;day++){const d=new Date(`${date}T12:00:00Z`);if(!Number.isFinite(d.getTime()))fail(400,'排班日期无效');if(!(q.skipWeekend===1&&[0,6].includes(d.getUTCDay())))for(let n=0;n<Math.min(20,q.perDayCount||1);n++){const userId=p.memberIds[index++%p.memberIds.length];if(!db.schedules.some(s=>s.planId===p.planId&&s.userId===userId&&s.dutyDate===date)){insert('schedules','scheduleId',{planId:p.planId,userId,dutyDate:date,groupName:p.groupConfig?.find(g=>g.memberIds.includes(userId))?.name||'轮值组',taskContent:p.taskContent,status:0});generatedCount++}}d.setUTCDate(d.getUTCDate()+1);date=d.toISOString().slice(0,10)}return {generatedCount,totalCount:db.schedules.filter(s=>s.planId===p.planId).length}}
 case '7-13':{const p=find('dutyPlans','planId',q.id);if(q.publish&&!db.schedules.some(s=>s.planId===p.planId))fail(70005,'请先生成排班');p.status=q.publish?1:0;if(q.publish)p.publishTime=now;return null}
 case '7-18':if(q.status===3)fail(400,'已换班状态由换班流程维护');Object.assign(find('schedules','scheduleId',q.id),{status:q.status,remark:q.remark,confirmTime:q.status===1?now:null});return null
 case '7-19':{const rows=db.schedules.filter(s=>!q.planId||s.planId===+q.planId),completedCount=rows.filter(s=>s.status===1).length;return {planId:+q.planId||1,planTitle:db.dutyPlans[0]?.title,totalCount:rows.length,completedCount,unfinishedCount:rows.filter(s=>s.status===0).length,leaveCount:rows.filter(s=>s.status===2).length,swappedCount:rows.filter(s=>s.status===3).length,completionRate:rows.length?+(completedCount/rows.length*100).toFixed(1):0,overdueCount:rows.filter(s=>s.dutyDate<today&&s.status===0).length,memberStats:db.users.map(user=>({userId:user.userId,userName:user.realName,totalCount:rows.filter(s=>s.userId===user.userId).length,completedCount:rows.filter(s=>s.userId===user.userId&&s.status===1).length})),groupStats:[]}}
 case '7-20':return {...(db.reminders.find(r=>r.planId===+q.planId)||db.reminders[0]),isGlobal:q.planId?0:1}
 case '7-21':{const planId=+q.planId||0,row=db.reminders.find(r=>r.planId===planId);if(row)Object.assign(row,q);else db.reminders.push({planId,...q});return null}
 case '8-01':case '8-02':{if(!upload?.length)fail(80001,'请选择文件');const rows=upload.map(f=>{if(f.size>(f.mime.startsWith('image/')?5:20)*1024*1024)fail(80002,'文件超过大小限制');const fileId=next('files','fileId'),row={fileId,name:f.name,originalName:f.name,url:`/__mock/files/${fileId}`,size:f.size,fileSize:f.size,suffix:f.name.split('.').pop(),fileType:f.mime,bizType:q.bizType||'common',uploadUserId:u.userId,createTime:now,base64:f.base64};db.files.push(row);const{base64,...publicFile}=row;void base64;return publicFile});return id==='8-01'?rows[0]:rows}
 case '8-03':{const f=own(find('files','fileId',q.id),u,'uploadUserId');if(db.budgetApplies.some(a=>a.attachFileId===f.fileId)||db.expenses.some(e=>e.voucherFileId===f.fileId))fail(80005,'文件仍被业务引用');remove('files','fileId',q.id);return null}
 case '8-04':return dictionaries[q.type]||fail(404,'模拟字典不存在')
 case '8-13':{const captchaKey=randomUUID();db.captchas[captchaKey]='2468';return {captchaKey,captchaImage:db.captchaImage||''}}
 case '8-14':return {status:'UP',service:'IECMP LOCAL MOCK',timestamp:now,mode:'mock',backendConnected:false}
 case '8-10':{const s=db.seats.find(s=>s.userId===u.userId),duty=db.schedules.filter(s=>s.userId===u.userId&&s.dutyDate>=today&&s.status===0).sort((a,b)=>a.dutyDate.localeCompare(b.dutyDate))[0];return {mySeat:s?{seatId:s.seatId,seatNo:s.seatNo,areaName:db.areas.find(a=>a.areaId===s.areaId)?.name}:null,nextDuty:decorate('schedules',duty),borrowingCount:db.borrows.filter(b=>b.userId===u.userId&&[1,4].includes(b.status)).length,budgetApplyPending:db.budgetApplies.filter(a=>a.applyUserId===u.userId&&a.status===0).length,budgetApprovedAmount:db.budgetApplies.filter(a=>a.applyUserId===u.userId&&a.status===1).reduce((s,a)=>s+(a.approvedAmount??a.amount),0),commitCountThisMonth:commits(u.userId,{beginDate:`${today.slice(0,7)}-01`}).length,unreadMsgCount:db.messages.filter(m=>m.receiverId===u.userId&&!m.isRead).length,topNotices:db.notices.filter(n=>n.status===1&&n.isTop===1)}}
 case '8-11':{const p=db.periods.find(p=>p.status===1);return {userCount:db.users.filter(u=>u.status===1).length,assetStat:assetStat(),budgetStat:p?{periodTitle:p.title,totalAmount:p.totalAmount,usedAmount:p.usedAmount,executionRate:p.executionRate}:null,todayDuty:db.schedules.filter(s=>s.dutyDate===today).map(s=>({...decorate('schedules',s),realName:name(s.userId)})),repoCommitCountThisMonth:commits(null,{beginDate:`${today.slice(0,7)}-01`}).length,pendingTodo:{budgetApply:db.budgetApplies.filter(a=>a.status===0).length,assetBorrow:db.borrows.filter(a=>a.status===0).length,seatApply:db.seatApplies.filter(a=>a.status===0).length,seatSwap:db.seatSwaps.filter(a=>a.status===1).length,honorAudit:db.alumni.filter(a=>a.status===0).length,registerApply:db.registerApplies.filter(a=>a.status===0).length}}}
 case '8-21':{const rows=db.messages.filter(m=>m.receiverId===u.userId&&!m.isRead);return {total:rows.length,dutyCount:rows.filter(m=>m.msgType===1).length,auditCount:rows.filter(m=>m.msgType===2).length,systemCount:rows.filter(m=>m.msgType===3).length}}
 case '8-22':{let rows;if(q.all===1)rows=db.messages.filter(m=>m.receiverId===u.userId&&(!q.msgType||m.msgType===q.msgType));else{if(!q.messageIds?.length)fail(80012,'请选择消息');rows=q.messageIds.map(id=>own(find('messages','messageId',id),u,'receiverId'))}let updatedCount=0;for(const m of rows)if(!m.isRead){m.isRead=1;m.readTime=now;updatedCount++}return {updatedCount}}
 default:return generic(id,q,u)
 }}
 function generic(id,q,u){
  const bind=bindings[id];if(!bind)fail(501,`本地模拟未支持 ${id}，不会转发真实后端`)
  const[table,key,owner]=bind,c=contracts[id]
  if(c.method==='get'){
   if(c.path.includes('{id}')){const row=find(table,key,q.id);if(owner)own(row,u,owner);return decorate(table,row)}
   let rows=db[table];if(owner)rows=rows.filter(r=>r[owner]===u.userId)
   if(['3-01'].includes(id))rows=rows.filter(r=>r.status!==0&&r.isPublic===1)
   if(id==='3-04')rows=rows.filter(r=>db.periods.some(p=>p.periodId===r.periodId&&p.status!==0&&p.isPublic===1))
   if(['6-01','6-03'].includes(id))rows=rows.filter(r=>r.status===1&&r.isShow===1)
   if(id==='8-05')rows=rows.filter(r=>r.status===1&&r.publishTime<=stamp())
   if(id==='8-15')rows=rows.filter(r=>r.status===1)
   if(id==='7-01')rows=rows.filter(s=>db.dutyPlans.some(p=>p.planId===s.planId&&p.status===1))
   if(['5-21','7-06'].includes(id))rows=rows.filter(s=>q.role==='target'?s.targetUserId===u.userId:q.role==='apply'?s.applyUserId===u.userId:s.targetUserId===u.userId||s.applyUserId===u.userId)
   if(id==='2-07')rows=rows.filter(p=>db.accounts.some(a=>a.accountId===p.accountId&&a.userId===u.userId))
   rows=filter(table,q,rows).map(r=>decorate(table,r))
   if(id==='6-03')rows.sort((a,b)=>(a.sortNo||0)-(b.sortNo||0))
   if(q.limit)rows=rows.slice(0,+q.limit)
   return c.path.endsWith('/page')?pageOf(rows,q):rows
  }
  if(c.method==='post'){
   const defaults={users:{roleIds:[4],status:1,password:'demo1234'},roles:{dataScope:2,permCodes:[],status:1},periods:{status:0,isPublic:0,usedAmount:0,version:0},items:{usedAmount:0},assets:{status:1,version:0,imageUrls:[]},categories:{parentId:0},seats:{status:1,userId:null,version:0},areas:{status:1},alumni:{status:1,isShow:0,submitUserId:u.userId},dutyPlans:{status:0},schedules:{status:0},notices:{status:0,isTop:0,publishTime:stamp()},videos:{status:1},assetRecords:{operatorId:u.userId,recordTime:stamp()}}
   if(table==='users'&&db.users.some(x=>x.username===q.username||(q.studentNo&&x.studentNo===q.studentNo)))fail(10013,'账号或学号已存在')
   if(table==='roles'&&db.roles.some(x=>x.roleCode===q.roleCode))fail(400,'角色编码已存在')
   if(table==='assets'&&db.assets.some(x=>x.assetNo===q.assetNo))fail(400,'资产编号已存在')
   if(table==='categories'&&db.categories.some(x=>x.code===q.code))fail(400,'分类编码已存在')
   if(table==='seats'&&db.seats.some(x=>x.areaId===q.areaId&&x.seatNo===q.seatNo))fail(400,'区域内工位编号已存在')
   if(table==='items')find('periods','periodId',q.periodId)
   if(table==='honors')find('alumni','alumniId',q.alumniId)
   if(table==='schedules'){find('dutyPlans','planId',q.planId);find('users','userId',q.userId);if(db.schedules.some(s=>s.planId===q.planId&&s.userId===q.userId&&s.dutyDate===q.dutyDate))fail(400,'同日同计划不能重复排班')}
   const row=insert(table,key,{...defaults[table],...q});return {[key]:row[key]}
  }
  if(c.method==='put'){
   const row=find(table,key,q[key]||q.id)
   if(table==='periods'&&q.totalAmount<row.usedAmount||table==='items'&&q.budgetAmount<row.usedAmount)fail(400,'额度不得低于已用金额')
   if(table==='categories'&&q.parentId){let parent=q.parentId;const seen=new Set([row.categoryId]);while(parent){if(seen.has(parent))fail(40003,'分类父级形成循环');seen.add(parent);parent=db.categories.find(c=>c.categoryId===parent)?.parentId||0}}
   Object.assign(row,q,{updateTime:stamp()});return null
  }
  if(c.method==='delete'){
   const row=find(table,key,q.id)
   if(table==='users'&&row.roleIds.includes(1))fail(10019,'不能删除超级管理员')
   if(table==='users'&&db.borrows.some(b=>b.userId===row.userId&&[0,1,4].includes(b.status)))fail(10017,'该用户存在待处理借用')
   if(table==='roles'&&db.users.some(x=>x.roleIds.includes(row.roleId)))fail(400,'角色仍被用户引用')
   if(table==='periods'&&(db.items.some(i=>i.periodId===row.periodId)||db.budgetApplies.some(a=>a.periodId===row.periodId)))fail(400,'周期内有记录，不能删除')
   if(table==='items'&&(db.expenses.some(e=>e.itemId===row.itemId)||db.budgetApplies.some(a=>a.itemId===row.itemId)))fail(400,'科目已被引用')
   if(table==='assets'&&db.borrows.some(b=>b.assetId===row.assetId&&[0,1,4].includes(b.status)))fail(400,'资产存在未完成借用')
   if(table==='categories'&&(db.assets.some(a=>a.categoryId===row.categoryId)||db.categories.some(c=>c.parentId===row.categoryId)))fail(400,'分类仍被引用')
   if(table==='areas'&&db.seats.some(s=>s.areaId===row.areaId))fail(400,'区域内仍有工位')
   if(table==='seats'&&row.userId)fail(50005,'请先释放占用工位')
   if(table==='alumni')db.honors=db.honors.filter(h=>h.alumniId!==row.alumniId)
   if(table==='dutyPlans')db.schedules=db.schedules.filter(s=>s.planId!==row.planId)
   remove(table,key,q.id);return null
  }
  fail(501,`本地模拟未支持 ${id}`)
 }
 function dispatch(id,values={},token='',upload=[]){
  const c=contracts[id];if(!c)fail(404,'接口不存在')
  const u=c.auth?identity(token):null
  if(u&&/^[a-z]+:/.test(c.permission)&&!info(u).roles.includes('SUPER_ADMIN')&&!info(u).permissions.includes(c.permission))fail(403,'当前模拟角色无权限')
  const q={...values}
  for(const f of c.fields.filter(f=>!f.parent)){
   const v=q[f.key]
   if(f.required&&(v===undefined||v===null||v==='')&&!(['8-01','8-02'].includes(id)&&['file','files'].includes(f.key)))fail(400,`缺少必填字段 ${f.key}`)
   if(v!==undefined&&v!==null&&['integer','number'].includes(f.type)){if(!Number.isFinite(+v))fail(400,`${f.key} 必须为数字`);q[f.key]=+v}
   if(v!==undefined&&f.type==='array'&&!Array.isArray(v))fail(400,`${f.key} 必须为数组`)
  }
  const snapshot=clone(db)
  try{aggregate();const data=api(id,q,u,token,upload);aggregate();if(c.method!=='get'){if(u)insert('logs','logId',{module:`本地模拟 ${id.split('-')[0]}`,operation:c.name,operatorId:u.userId,operatorName:u.realName,requestUri:c.path,requestParam:'测试参数（敏感字段已省略）',status:1,costMs:12,ip:'127.0.0.1'});save()}return data===undefined?null:clone(data)}catch(e){db=snapshot;throw e}
 }
 aggregate()
 return {dispatch,get database(){return db},reset(){db=seedDatabase(contracts);save();return {reset:true}},save,stats(){return {users:db.users.length,assets:db.assets.length,budgetApplies:db.budgetApplies.length,notices:db.notices.length,logs:db.logs.length,seedDate:db.seedDate}}}
}
