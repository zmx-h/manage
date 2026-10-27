import { test } from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs'
import http from 'node:http'
import { Buffer } from 'node:buffer'
import { createMockEngine } from './engine.js'
import { createMockMiddleware } from './server.js'
const contracts=JSON.parse(fs.readFileSync(new URL('../src/config/contracts.json',import.meta.url),'utf8'))
const engine=()=>createMockEngine(contracts)
const token=(e,username='admin')=>e.dispatch(username==='admin'?'1-01':'1-02',{username,password:'demo1234'}).accessToken
const errorCode=(fn,code)=>assert.throws(fn,error=>error.code===code)

test('all 186 contracts have real local mock handlers, not a generic success fallback',()=>{
 const source=fs.readFileSync(new URL('./engine.js',import.meta.url),'utf8');const supported=new Set([...source.matchAll(/(?:case\s+)?'([1-8]-\d{2})'\s*:/g)].map(m=>m[1]));for(const id of Object.keys(contracts))assert.ok(supported.has(id),id)
})
test('all GET contracts respond with valid structures and pagination, without backend',()=>{
 const e=engine(),t=token(e),member=token(e,'member')
 for(const c of Object.values(contracts).filter(c=>c.method==='get')){
  const q={pageNum:1,pageSize:3,type:'assetStatus',month:new Date().toISOString().slice(0,7)}
  for(const f of c.fields)if(f.required)q[f.key]=f.key==='type'?'assetStatus':1
  const result=e.dispatch(c.id,q,c.path.startsWith('/user/')?member:t);assert.notEqual(result,undefined,c.id)
  if(c.path.endsWith('/page')){assert.equal(result.pageSize,3,c.id);assert.ok(Array.isArray(result.list),c.id);assert.ok(result.list.length<=3,c.id)}
 }
})
test('login, roles, invalid passwords, ownership and permission isolation remain active',()=>{
 const e=engine(),admin=token(e),member=token(e,'member');assert.equal(e.dispatch('1-05',{},member).username,'member');errorCode(()=>e.dispatch('1-01',{username:'admin',password:'bad'}),10003);errorCode(()=>e.dispatch('1-08',{},member),403);errorCode(()=>e.dispatch('3-07',{id:2},member),403);assert.ok(e.dispatch('1-08',{},admin).total>0)
})
test('budget create -> partial approval -> expense -> delete keeps totals consistent',()=>{
 const e=engine(),a=token(e),m=token(e,'member'),before=e.dispatch('3-02',{id:1},a).usedAmount
 const {applyId}=e.dispatch('3-05',{periodId:1,itemId:1,title:'测试申请',amount:500},m)
 e.dispatch('3-21',{id:applyId,auditStatus:1,approvedAmount:300},a)
 assert.equal(e.dispatch('3-07',{id:applyId},m).approvedAmount,300)
 errorCode(()=>e.dispatch('3-22',{periodId:1,itemId:1,applyId,title:'超批金额',amount:301,expenseDate:'2026-10-20'},a),400)
 const {expenseId}=e.dispatch('3-22',{periodId:1,itemId:1,applyId,title:'测试支出',amount:200,expenseDate:'2026-10-20'},a)
 assert.equal(e.dispatch('3-02',{id:1},a).usedAmount,before+200);e.dispatch('3-24',{id:expenseId},a);assert.equal(e.dispatch('3-02',{id:1},a).usedAmount,before)
})
test('asset borrowing, approval, return and audit notification are linked',()=>{
 const e=engine(),a=token(e),m=token(e,'member');const {borrowId}=e.dispatch('4-05',{assetId:5,borrowType:1,purpose:'测试',expectReturnTime:'2026-10-30 18:00:00'},m)
 e.dispatch('4-21',{id:borrowId,auditStatus:1},a);assert.equal(e.dispatch('4-09',{id:5},a).status,4)
 e.dispatch('4-07',{id:borrowId},m);assert.equal(e.dispatch('4-09',{id:5},a).status,1);assert.ok(e.dispatch('8-20',{},m).list.some(x=>x.bizType==='asset'))
})
test('seat swaps and layout saves are atomic and modify different fields',()=>{
 const e=engine(),a=token(e);e.dispatch('5-26',{fromSeatId:2,toSeatId:3},a)
 const before=e.dispatch('5-25',{},a)[0].seats.find(s=>s.seatId===2);assert.equal(before.occupantUserId,3)
 e.dispatch('5-13',{areaId:1,seats:[{seatId:2,xRatio:24,yRatio:31}]},a)
 const after=e.dispatch('5-25',{},a)[0].seats.find(s=>s.seatId===2);assert.equal(after.occupantUserId,3);assert.equal(after.xRatio,24)
 errorCode(()=>e.dispatch('5-26',{fromSeatId:2,toSeatId:11},a),50017);assert.equal(e.dispatch('5-25',{},a)[0].seats.find(s=>s.seatId===2).occupantUserId,3)
})
test('CRUD persists through engine reload and reset restores seeded data',()=>{
 let saved;const e=createMockEngine(contracts,undefined,data=>saved=JSON.parse(JSON.stringify(data))),t=token(e)
 const {noticeId}=e.dispatch('8-07',{title:'本地公告',content:'本地内容',status:1},t);e.dispatch('8-08',{noticeId,title:'编辑公告',content:'新内容',status:1},t)
 const restored=createMockEngine(contracts,saved),rt=token(restored);assert.ok(restored.dispatch('8-06',{},rt).list.some(n=>n.title==='编辑公告'))
 restored.dispatch('8-09',{id:noticeId},rt);assert.ok(!restored.dispatch('8-06',{},rt).list.some(n=>n.noticeId===noticeId));restored.reset();assert.equal(restored.database.notices.length,4);errorCode(()=>restored.dispatch('1-05',{},rt),401)
})
test('messages reject mixed-owner batches without partially marking records',()=>{
 const e=engine(),m=token(e,'member');errorCode(()=>e.dispatch('8-22',{messageIds:[1,4]},m),403);assert.equal(e.database.messages.find(x=>x.messageId===1).isRead,0)
})
test('HTTP middleware serves mock API, multipart upload, local video and one-shot errors',async()=>{
 const middleware=createMockMiddleware({persist:false});const server=http.createServer((req,res)=>middleware(req,res,()=>{res.statusCode=404;res.end()}));await new Promise(resolve=>server.listen(0,'127.0.0.1',resolve));const base=`http://127.0.0.1:${server.address().port}`
 try{
  const call=async(path,data,access)=>{const r=await fetch(base+path,{method:data?'POST':'GET',headers:{...(data?{'Content-Type':'application/json'}:{}),...(access?{Authorization:`Bearer ${access}`}:{})},body:data?JSON.stringify(data):undefined});return r.json()}
  assert.equal((await call('/__mock/status')).backendConnected,false)
  const login=await call('/api/admin/login',{username:'admin',password:'demo1234'}),t=login.data.accessToken
  assert.ok((await call('/api/admin/stat/dashboard',null,t)).data.userCount>0)
  const form=new FormData();form.append('bizType','avatar');form.append('file',new Blob([Buffer.from([137,80,78,71])],{type:'image/png'}),'test.png')
  const result=await(await fetch(base+'/api/user/file/upload',{method:'POST',headers:{Authorization:`Bearer ${t}`},body:form})).json();assert.equal(result.code,200);assert.ok(result.data.url.startsWith('/__mock/files/'));assert.equal((await fetch(base+result.data.url)).status,200)
  const video=await fetch(base+'/__mock/media/welcome.mp4',{headers:{Range:'bytes=0-30'}});assert.equal(video.status,206);assert.equal((await video.arrayBuffer()).byteLength,31)
  assert.ok((await call('/api/common/captcha')).data.captchaImage.startsWith('data:image/png;base64,'))
  await call('/__mock/scenario',{id:'1-08',type:'500'});assert.equal((await call('/api/admin/user/page',null,t)).code,500);assert.equal((await call('/api/admin/user/page',null,t)).code,200)
  assert.equal((await call('/api/unknown')).code,404)
 }finally{server.closeAllConnections();await new Promise(resolve=>server.close(resolve))}
})
