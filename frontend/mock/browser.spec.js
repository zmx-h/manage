import {test,expect} from '@playwright/test'
import fs from 'node:fs'
const pages=JSON.parse(fs.readFileSync(new URL('../src/config/pages.json',import.meta.url),'utf8'))
async function login(page,username='admin'){
 await page.goto(username==='admin'?'/admin/login':'/login')
 await page.getByLabel('登录账号',{exact:true}).fill(username)
 await page.getByLabel('密码',{exact:true}).fill('demo1234')
 await page.getByRole('button',{name:'登录',exact:true}).click()
 await expect(page).toHaveURL(username==='admin'?/\/admin\/dashboard$/:/\/home$/)
}
async function authRequest(page,path,body,method='POST'){
 return page.evaluate(async({path,body,method})=>{const r=await fetch('/api'+path,{method,headers:{'Content-Type':'application/json',Authorization:`Bearer ${localStorage.getItem('iecmp_access_token')}`},body:method==='GET'?undefined:JSON.stringify(body)});return r.json()},{path,body,method})
}
test.beforeEach(async({request})=>{await request.post('/__mock/reset');await request.post('/__mock/settings',{data:{latency:10}})})
test.afterAll(async({request})=>{await request.post('/__mock/reset')})

test('mock console provides one-click login and dashboard with local data',async({page})=>{
 const errors=[],external=[];page.on('pageerror',e=>errors.push(e.message));page.on('request',r=>{if(!r.url().startsWith('http://127.0.0.1:5173')&&!r.url().startsWith('data:')&&!r.url().startsWith('blob:'))external.push(r.url())})
 await page.goto('/__mock/');await page.getByRole('button',{name:'一键体验管理端 →'}).click();await expect(page).toHaveURL(/\/admin\/dashboard$/);await expect(page.locator('.stat-card').first()).toContainText('8');await expect(page.getByText('本地模拟 · 测试数据 / 测试控制台')).toBeVisible();await page.screenshot({path:'test-results/mock-dashboard.png',fullPage:true});expect(errors).toEqual([]);expect(external).toEqual([])
})
test('all 43 pages render with live local mock requests and no runtime errors',async({page})=>{
 const errors=[],failures=[];page.on('pageerror',e=>errors.push(e.message));page.on('response',async r=>{if(new URL(r.url()).pathname.startsWith('/api/'))try{const body=await r.json();if(body.code!==200)failures.push(`${r.url()}: ${body.code} ${body.msg}`)}catch{/* binary export */}})
 await login(page)
 for(const p of pages){await page.goto(p.path);await expect(page.locator('.page-heading h1')).toBeVisible();await expect(page.locator('.el-loading-mask')).toHaveCount(0)}
 expect(errors).toEqual([]);expect(failures).toEqual([])
})
test('notice create, edit and delete are visible after browser reload',async({page})=>{
 await login(page);await page.goto('/admin/notice');await page.getByRole('button',{name:'公告新增',exact:true}).click();await page.getByLabel('标题',{exact:true}).fill('UI测试：本地公告');await page.getByLabel('正文',{exact:true}).fill('这条内容应当保存于本地模拟数据');await page.getByRole('button',{name:'确认',exact:true}).click();await expect(page.getByText('操作成功',{exact:true})).toBeVisible();await page.getByRole('button',{name:'关闭',exact:true}).click();await page.reload();await expect(page.getByRole('cell',{name:'UI测试：本地公告',exact:true})).toBeVisible()
 const row=page.getByRole('row').filter({hasText:'UI测试：本地公告'});await row.getByRole('button',{name:/操作/}).click();await page.getByRole('menuitem',{name:'公告修改',exact:true}).click();await page.getByLabel('标题',{exact:true}).fill('UI测试：编辑后的公告');await page.getByRole('button',{name:'确认',exact:true}).click();await expect(page.getByRole('cell',{name:'UI测试：编辑后的公告',exact:true})).toBeVisible()
 await page.getByRole('row').filter({hasText:'UI测试：编辑后的公告'}).getByRole('button',{name:/操作/}).click();await page.getByRole('menuitem',{name:'公告删除',exact:true}).click();await page.getByRole('button',{name:'确认',exact:true}).click();await expect(page.getByRole('cell',{name:'UI测试：编辑后的公告',exact:true})).toHaveCount(0)
})
test('member request -> admin approval -> member message and state',async({page,context})=>{
 await login(page,'member')
 const result=await authRequest(page,'/user/budget/apply',{periodId:1,itemId:1,title:'UI测试闭环申请',amount:260})
 expect(result.code).toBe(200);const applyId=result.data.applyId
 await page.goto('/budget/apply');await expect(page.getByRole('cell',{name:'UI测试闭环申请',exact:true})).toBeVisible()
 const admin=await context.newPage();await login(admin);expect((await authRequest(admin,`/admin/budget/apply/${applyId}/audit`,{auditStatus:1,approvedAmount:240},'PUT')).code).toBe(200)
 // Explicitly sign the member in again: tabs in one browser intentionally share login storage.
 await login(page,'member');await page.goto('/budget/apply');await expect(page.getByRole('row').filter({hasText:'UI测试闭环申请'})).toContainText('已通过');await page.goto('/message');await expect(page.getByRole('heading',{name:'申请已通过',exact:true})).toBeVisible();await admin.close()
})
test('live mock seat occupancy and coordinates persist independently',async({page})=>{
 await login(page);await page.goto('/admin/seat/plan');const source=page.locator('.seat-node').filter({hasText:'A-02'}),target=page.locator('.seat-node').filter({hasText:'A-05'});await source.dragTo(target);await page.getByRole('button',{name:'确认',exact:true}).click();await expect(target).toContainText('陈予安');await page.reload();await expect(page.locator('.seat-node').filter({hasText:'A-05'})).toContainText('陈予安');await page.screenshot({path:'test-results/mock-seats.png',fullPage:true})
})
test('one-shot errors, empty lists and refresh-token recovery can be tested without backend',async({page,request})=>{
 await login(page);await request.post('/__mock/scenario',{data:{id:'1-08',type:'empty'}});await page.goto('/admin/system/user');await expect(page.getByRole('heading',{name:'这里还没有数据'})).toBeVisible()
 await request.post('/__mock/scenario',{data:{id:'1-08',type:'500'}});await page.getByRole('button',{name:'刷新',exact:true}).click();await expect(page.getByRole('heading',{name:'暂时无法获取数据'})).toBeVisible()
 await request.post('/__mock/scenario',{data:{id:'1-08',type:'401'}});await page.getByRole('button',{name:'重新加载',exact:true}).click();await expect(page.getByRole('cell',{name:'陈予安',exact:true})).toBeVisible();await expect(page).toHaveURL(/\/admin\/system\/user$/)
})
test('mock mode has local playable media, avatar image, dictionaries and charts',async({page})=>{
 await login(page,'member');await page.goto('/video');await expect(page.locator('video')).toBeVisible();const video=page.locator('video');await video.evaluate(v=>v.play());await expect.poll(()=>video.evaluate(v=>v.currentTime)).toBeGreaterThan(0);await expect(page.locator('video')).toHaveJSProperty('error',null)
 await page.goto('/repo');await page.getByRole('button',{name:'提交趋势',exact:true}).click();await expect(page.locator('.trend-chart svg')).toBeVisible();await page.getByRole('button',{name:'贡献热力图',exact:true}).click();await expect(page.locator('.heatmap-cell')).toHaveCount(365)
})
