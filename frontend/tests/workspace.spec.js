import { test, expect } from '@playwright/test'
import fs from 'node:fs'
import { Buffer } from 'node:buffer'
const pages = JSON.parse(fs.readFileSync(new URL('../src/config/pages.json', import.meta.url)))
const contracts = JSON.parse(fs.readFileSync(new URL('../src/config/contracts.json', import.meta.url)))
const account = { userId: 7, username: 'tester', realName: '测试成员', roles: ['SUPER_ADMIN'], permissions: [] }
const ok = data => ({ code: 200, data, msg: 'success' })
async function mock(page, handlers = {}) {
  await page.route('http://127.0.0.1:5173/api/**', async route => {
    const url = new URL(route.request().url()); const path = url.pathname.replace(/^\/api/,'')
    if (handlers[path]) return handlers[path](route)
    const defaults = {'/user/info':account,'/common/register/config':{inviteEnabled:1,applyEnabled:1,captchaEnabled:0},'/user/message/unread/count':{total:0},'/admin/stat/dashboard':{userCount:0,assetStat:{total:0},budgetStat:{totalAmount:100,usedAmount:0,executionRate:0},pendingTodo:{},todayDuty:[],repoCommitCountThisMonth:0}}
    const data = path in defaults ? defaults[path] : /\/dict\//.test(path) ? [{label:'待审核',value:0},{label:'已通过',value:1},{label:'已驳回',value:2}] : path.endsWith('/page') ? {total:0,pageNum:1,pageSize:10,pages:0,list:[]} : []
    return route.fulfill({json:ok(data)})
  })
}
async function authenticate(page) { await page.addInitScript(() => { localStorage.setItem('iecmp_access_token','test-access'); localStorage.setItem('iecmp_refresh_token','test-refresh') }) }

test('contracts match all 186 documented endpoints without nested fields at root', async () => {
  expect(Object.keys(contracts)).toHaveLength(186)
  expect(pages).toHaveLength(43)
  for (const page of pages) for (const id of [page.api,...page.create,...page.actions]) expect(contracts[id],`${page.key}: ${id}`).toBeTruthy()
  expect(contracts['5-13'].fields.find(f=>f.key==='seatId').parent).toBe('seats')
  expect(contracts['5-24'].fields.find(f=>f.key==='auditStatus').description).toContain('1 通过')
  const java = fs.readdirSync('../backend/src/main/java/com/iecmp', {recursive:true}).filter(x=>x.endsWith('Controller.java'))
  const sources=java.map(p=>fs.readFileSync(`../backend/src/main/java/com/iecmp/${p}`,'utf8')).join('\n')
  for(const contract of Object.values(contracts)) expect(sources).toContain(`"${contract.path}"`)
})

test('every preview page renders without business requests or runtime errors', async ({page}) => {
  const errors=[]; const requests=[]; page.on('pageerror',e=>errors.push(e.message)); page.on('request',r=>{if(new URL(r.url()).pathname.startsWith('/api/'))requests.push(r.url())})
  for(const config of pages){ await page.goto(`/preview/${config.side}/${config.path.replace(config.side==='admin'?'/admin/':'/','')}`); await expect(page.locator('.page-heading h1')).toBeVisible(); await expect(page.locator('.preview-banner')).toBeVisible() }
  expect(errors).toEqual([]); expect(requests).toEqual([])
})

test('system theme follows OS changes; manual preference and language persist', async ({page}) => {
  await page.emulateMedia({colorScheme:'dark'}); await page.goto('/preview/admin/dashboard'); await expect(page.locator('html')).toHaveClass(/dark/)
  await page.emulateMedia({colorScheme:'light'}); await expect(page.locator('html')).not.toHaveClass(/dark/)
  await page.getByRole('button',{name:'外观',exact:true}).click(); await page.getByRole('menuitem',{name:/深色/}).click(); await expect(page.locator('html')).toHaveClass(/dark/)
  await page.getByRole('button',{name:'语言',exact:true}).click(); await page.getByRole('menuitem',{name:/English/}).click(); await expect(page.locator('html')).toHaveAttribute('lang','en-US'); await expect(page.locator('.page-heading h1')).toContainText('Welcome back')
  await page.reload(); await expect(page.locator('html')).toHaveClass(/dark/); await expect(page.locator('.breadcrumb-current')).toHaveText('Overview')
  await page.screenshot({path:'test-results/dashboard-dark-en.png',fullPage:true})
})

test('desktop and mobile layouts do not overflow',async({page})=>{
 await page.goto('/preview/admin/dashboard'); await page.screenshot({path:'test-results/dashboard-light.png',fullPage:true})
 for(const path of ['/preview/admin/dashboard','/preview/admin/system/user','/preview/user/home','/login']){await page.setViewportSize({width:390,height:844}); await mock(page); await page.goto(path); expect(await page.evaluate(()=>document.documentElement.scrollWidth<=window.innerWidth)).toBeTruthy()}
 await page.goto('/preview/admin/dashboard'); await page.getByRole('button',{name:'工作空间',exact:true}).click(); await expect(page.locator('.sidebar')).toHaveCSS('transform','matrix(1, 0, 0, 1, 0, 0)'); await page.screenshot({path:'test-results/mobile.png',fullPage:true})
})

test('login sends exact fields and navigates to authenticated workspace',async({page})=>{
 let body; await mock(page,{'/admin/login':r=>{body=r.request().postDataJSON();return r.fulfill({json:ok({accessToken:'live',refreshToken:'refresh',userInfo:account})})}})
 await page.goto('/admin/login'); await page.getByLabel('登录账号',{exact:true}).fill('tester'); await page.getByLabel('密码',{exact:true}).fill('pass123'); await page.getByRole('button',{name:'登录',exact:true}).click(); await expect(page).toHaveURL(/\/admin\/dashboard$/); expect(body).toEqual({username:'tester',password:'pass123'}); await expect(page.locator('.stat-card').first()).toContainText('0')
})

test('user creation validates and sends numeric roleIds; no undocumented fields',async({page})=>{
 await authenticate(page); let body
 await mock(page,{'/admin/role/list':r=>r.fulfill({json:ok([{roleId:4,roleName:'成员',roleCode:'MEMBER'}])}),'/admin/user':r=>{body=r.request().postDataJSON();return r.fulfill({json:ok({userId:8})})}})
 await page.goto('/admin/system/user'); await page.getByRole('button',{name:'新增用户',exact:true}).click(); await page.getByRole('button',{name:'确认',exact:true}).click(); await expect(page.getByText('请填写必填项').first()).toBeVisible()
 await page.getByLabel('登录账号',{exact:true}).fill('newuser'); await page.getByLabel('姓名',{exact:true}).fill('新成员'); await page.getByLabel('角色',{exact:true}).locator('xpath=ancestor::div[contains(@class, "el-select__wrapper")]').click(); await page.getByRole('option',{name:'成员',exact:true}).click(); await page.getByRole('dialog').getByRole('heading').click(); await page.getByRole('button',{name:'确认',exact:true}).click(); await expect(page.getByText('操作成功',{exact:true})).toBeVisible(); expect(body.roleIds).toEqual([4]); expect(body.realName).toBe('新成员'); expect(body).not.toHaveProperty('userId')
})

test('business 500 placeholders and network failure are honest error states',async({page})=>{
 await authenticate(page); await mock(page,{'/admin/user/page':r=>r.fulfill({json:{code:500,data:null,msg:'接口 1-08 尚未实现（骨架占位）'}})})
 await page.goto('/admin/system/user'); await expect(page.getByRole('heading',{name:'后端接口尚未实现'})).toBeVisible(); await expect(page.getByRole('button',{name:'重新加载'})).toBeVisible()
 await page.route('**/api/admin/user/page**',r=>r.abort()); await page.getByRole('button',{name:'重新加载'}).click(); await expect(page.getByText('无法连接服务，请检查后端是否启动')).toBeVisible()
})

test('permissions prevent member access to administration',async({page})=>{
 await authenticate(page); await mock(page,{'/user/info':r=>r.fulfill({json:ok({...account,roles:['MEMBER'],permissions:[]})})}); await page.goto('/admin/system/user'); await expect(page).toHaveURL(/\/403$/)
})

test('401 silently refreshes once and retries original request',async({page})=>{
 await authenticate(page); let refreshed=0; let requests=0
 await mock(page,{'/admin/user/page':r=>{requests++;return r.fulfill({json:requests===1?{code:401,msg:'Expired',data:null}:ok({total:0,list:[]})})},'/user/token/refresh':r=>{refreshed++;expect(r.request().postDataJSON()).toEqual({refreshToken:'test-refresh'});return r.fulfill({json:ok({accessToken:'renewed'})})}})
 await page.goto('/admin/system/user'); await expect(page.getByRole('heading',{name:'这里还没有数据'})).toBeVisible(); await expect.poll(()=>refreshed).toBe(1); await expect.poll(()=>requests).toBe(2); expect(await page.evaluate(()=>localStorage.getItem('iecmp_access_token'))).toBe('renewed')
})


test('budget review uses documented 1 / 2 action values, not record-state codes',async({page})=>{
 await authenticate(page); let payload
 await mock(page,{'/admin/budget/apply/page':r=>r.fulfill({json:ok({total:1,list:[{applyId:12,periodId:4,itemId:8,title:'测试申请',amount:500,status:0}]})}),'/admin/budget/apply/12/audit':r=>{payload=r.request().postDataJSON();return r.fulfill({json:ok(null)})}})
 await page.goto('/admin/budget/apply'); await page.getByRole('row').filter({hasText:'测试申请'}).getByRole('button',{name:/操作/}).click(); await page.getByRole('menuitem',{name:contracts['3-21'].name,exact:true}).click(); await page.getByLabel('审核动作',{exact:true}).locator('xpath=ancestor::div[contains(@class, "el-select__wrapper")]').click(); await page.getByRole('option',{name:'通过',exact:true}).click(); await page.getByRole('button',{name:'确认',exact:true}).click(); await expect.poll(()=>payload).toEqual({auditStatus:1})
})

test('seat drag and layout save call distinct endpoints with nested coordinates',async({page})=>{
 await authenticate(page); const errors=[];page.on('pageerror',e=>errors.push(e.message));let dragBody;let layoutBody
 const areas=[{areaId:1,name:'测试区域',width:1000,height:600,seats:[{seatId:11,seatNo:'A-01',xRatio:10,yRatio:10,widthRatio:12,heightRatio:16,status:2,occupantUserId:7,occupantName:'测试成员'},{seatId:12,seatNo:'A-02',xRatio:35,yRatio:10,widthRatio:12,heightRatio:16,status:1}]}]
 await mock(page,{'/admin/seat/plan':r=>r.fulfill({json:ok(areas)}),'/admin/seat/plan/drag':r=>{dragBody=r.request().postDataJSON();return r.fulfill({json:ok({action:'move'})})},'/admin/seat/batch':r=>{layoutBody=r.request().postDataJSON();return r.fulfill({json:ok({updatedCount:2})})}})
 await page.goto('/admin/seat/plan');const source=page.locator('.seat-node').filter({hasText:'A-01'});const target=page.locator('.seat-node').filter({hasText:'A-02'});await source.dragTo(target);await page.getByRole('button',{name:'确认',exact:true}).click();await expect.poll(()=>dragBody).toEqual({fromSeatId:11,toSeatId:12})
 await page.getByText('布局模式',{exact:true}).click(); const transfer=await page.evaluateHandle(()=>new DataTransfer());await source.dispatchEvent('dragstart',{dataTransfer:transfer});const box=await page.locator('.seat-map').boundingBox();await page.locator('.seat-map').dispatchEvent('drop',{dataTransfer:transfer,clientX:box.x+box.width*.6,clientY:box.y+box.height*.5});await page.getByRole('button',{name:'保存布局'}).click();await expect.poll(()=>layoutBody?.areaId).toBe(1);expect(layoutBody.seats).toHaveLength(2);expect(layoutBody).not.toHaveProperty('seatId');expect(layoutBody.seats[0]).not.toHaveProperty('occupantUserId');expect(errors).toEqual([])
})

test('messages use documented unread total and mark only the selected message',async({page})=>{
 await authenticate(page);let payload
 await mock(page,{'/user/message/unread/count':r=>r.fulfill({json:ok({total:3,dutyCount:2,auditCount:1,systemCount:0})}),'/user/message/page':r=>r.fulfill({json:ok({total:1,list:[{messageId:31,title:'值日提醒',content:'测试消息',isRead:0,msgType:1,createTime:'2026-10-19 09:00:00'}]})}),'/user/message/read':r=>{payload=r.request().postDataJSON();return r.fulfill({json:ok({updatedCount:1})})}})
 await page.goto('/message');await expect(page.locator('.notification-dot')).toBeVisible();await page.getByRole('button',{name:'标记已读',exact:true}).click();await expect.poll(()=>payload).toEqual({messageIds:[31]})
})

test('avatar upload sends multipart data and reuses returned URL in profile update',async({page})=>{
 await authenticate(page);let multipart;let profile
 await mock(page,{'/user/file/upload':r=>{multipart={header:r.request().headers()['content-type'],body:r.request().postDataBuffer().toString()};return r.fulfill({json:ok({fileId:40,url:'https://files.example.test/avatar.png'})})},'/user/info':r=>{if(r.request().method()==='PUT'){profile=r.request().postDataJSON();return r.fulfill({json:ok(null)})}return r.fulfill({json:ok(account)})}})
 await page.goto('/profile');await page.locator('input[type=file]').setInputFiles({name:'avatar.png',mimeType:'image/png',buffer:Buffer.from([137,80,78,71,13,10,26,10])});await expect(page.getByText('上传成功',{exact:true})).toBeVisible();expect(multipart.header).toContain('multipart/form-data; boundary=');expect(multipart.body).toContain('name="bizType"');expect(multipart.body).toContain('avatar');await page.getByRole('button',{name:'保存',exact:true}).click();await expect.poll(()=>profile?.avatarUrl).toBe('https://files.example.test/avatar.png')
})

test('unsafe rich text is displayed as text, never executed',async({page})=>{
 await authenticate(page);await mock(page,{'/user/notice/page':r=>r.fulfill({json:ok({total:1,list:[{noticeId:1,title:'安全公告',content:'<img src=x onerror="window.__xss=1"><script>window.__xss=2</script><b>公告内容</b>',publishTime:'2026-10-19 09:00:00'}]})})});await page.goto('/notice');await expect(page.getByRole('heading',{name:'安全公告'})).toBeVisible();await expect(page.locator('.announcement-list img')).toHaveCount(0);expect(await page.evaluate(()=>window.__xss)).toBeUndefined()
})

test('login and registration screens render in both languages',async({page})=>{
 await mock(page);await page.goto('/admin/login');await page.screenshot({path:'test-results/login-light.png',fullPage:true});await page.goto('/register');await expect(page.getByRole('button',{name:'提交',exact:true})).toBeVisible();await page.getByRole('button',{name:'语言',exact:true}).click();await page.getByRole('menuitem',{name:/English/}).click();await expect(page.getByRole('heading',{name:'Join the center',exact:true})).toBeVisible()
})



test('all authenticated routes render with empty contract responses and no uncaught errors', async({page})=>{
 const errors=[];page.on('pageerror',e=>errors.push(e.message));await authenticate(page);await mock(page)
 for(const config of pages){await page.goto(config.path);await expect(page.locator('.page-heading h1')).toBeVisible();await expect(page.locator('.preview-banner')).toHaveCount(0)}
 expect(errors).toEqual([])
})

test('contribution trend uses real response points and calendar renders documented days', async({page})=>{
 const errors=[];page.on('pageerror',e=>errors.push(e.message));await authenticate(page)
 await mock(page,{'/admin/repo/stat/trend':r=>r.fulfill({json:ok([{date:'2026-10-18',commitCount:3},{date:'2026-10-19',commitCount:5}])}),'/user/duty/schedule/calendar':r=>{const month=new URL(r.request().url()).searchParams.get('month');return r.fulfill({json:ok({month,days:[{date:`${month}-19`,weekday:1,isMine:1,duties:[{scheduleId:1,userId:7,userName:'测试成员',groupName:'开发组',taskContent:'值日',status:0}]}]})})}})
 await page.goto('/admin/repo');await page.getByRole('button',{name:'提交趋势',exact:true}).click();await expect(page.locator('.trend-chart svg')).toBeVisible()
 await page.goto('/duty');await page.getByRole('button',{name:'月历数据',exact:true}).click();await expect(page.locator('.calendar-cell').filter({hasText:'测试成员'})).toBeVisible();expect(errors).toEqual([])
})

test('budget item page waits for required period instead of sending malformed URL',async({page})=>{
 await authenticate(page);const requested=[];await mock(page,{'/user/budget/period/5/item/list':r=>{requested.push(r.request().url());return r.fulfill({json:ok([])})},'/admin/budget/period/page':r=>r.fulfill({json:ok({total:1,list:[{periodId:5,title:'测试周期'}]})})})
 await page.goto('/admin/budget/item');await expect(page.getByText('预算周期',{exact:true}).last()).toBeVisible();expect(requested).toEqual([]);await page.getByLabel('预算周期',{exact:true}).locator('xpath=ancestor::div[contains(@class, "el-select__wrapper")]').click();await page.getByRole('option',{name:'测试周期'}).click();await page.getByRole('button',{name:'查询',exact:true}).click();await expect.poll(()=>requested.length).toBe(1)
})
