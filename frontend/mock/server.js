import fs from 'node:fs'
import path from 'node:path'
import { Buffer } from 'node:buffer'
import { createMockEngine, MockError } from './engine.js'
import { mockDataDirectory } from './seed.js'

const contracts=JSON.parse(fs.readFileSync(new URL('../src/config/contracts.json',import.meta.url),'utf8').replace(/^\uFEFF/,''))
const patterns=Object.values(contracts).map(c=>({...c,pattern:new RegExp('^'+c.path.replace(/\{(\w+)\}/g,'(?<$1>[^/]+)')+'$')}))
const delay=ms=>new Promise(resolve=>setTimeout(resolve,ms))
const json=(res,value,status=200)=>{res.statusCode=status;res.setHeader('Content-Type','application/json; charset=utf-8');res.setHeader('Cache-Control','no-store');res.end(JSON.stringify(value))}
async function readBody(req){const chunks=[];let size=0;for await(const chunk of req){size+=chunk.length;if(size>22*1024*1024)throw new MockError(400,'请求体最大 22 MB');chunks.push(chunk)}const data=Buffer.concat(chunks),type=req.headers['content-type']||'';if(!data.length)return {values:{},files:[]};if(type.includes('application/json')){try{return {values:JSON.parse(data.toString()),files:[]}}catch{throw new MockError(400,'JSON 格式错误')}}
 if(type.includes('multipart/form-data')){const boundary=/boundary=(?:"([^"]+)"|([^;]+))/.exec(type);if(!boundary)throw new MockError(400,'缺少 multipart boundary');const marker=`--${boundary[1]||boundary[2]}`,values={},files=[];for(const part of data.toString('latin1').split(marker).slice(1)){const split=part.indexOf('\r\n\r\n');if(split<0)continue;const head=part.slice(0,split),body=part.slice(split+4,-2),name=/name="([^"]+)"/.exec(head)?.[1],filename=/filename="([^"]*)"/.exec(head)?.[1];if(!name)continue;if(filename){const bytes=Buffer.from(body,'latin1');files.push({name:Buffer.from(filename,'latin1').toString('utf8'),mime:/Content-Type:\s*([^\r\n]+)/i.exec(head)?.[1]||'application/octet-stream',size:bytes.length,base64:bytes.toString('base64')})}else values[name]=Buffer.from(body,'latin1').toString('utf8')}return {values,files}}
 throw new MockError(400,'不支持的请求类型')
}
function svg(name){
 if(name==='floor')return `<svg xmlns="http://www.w3.org/2000/svg" width="1200" height="700"><rect width="1200" height="700" rx="16" fill="#edf1f8"/><path d="M45 52H1155V650H45Z" fill="#f8faff" stroke="#c8d4e8" stroke-width="3"/><path d="M45 350H1155" stroke="#dce4f2" stroke-dasharray="8 8"/><text x="600" y="680" text-anchor="middle" fill="#99aaca" font-family="sans-serif" font-size="15">IECMP · LOCAL DEMO FLOOR PLAN</text></svg>`
 if(name==='video-cover')return `<svg xmlns="http://www.w3.org/2000/svg" width="960" height="540"><defs><linearGradient id="g"><stop stop-color="#33486c"/><stop offset="1" stop-color="#847aba"/></linearGradient></defs><rect width="960" height="540" fill="url(#g)"/><circle cx="480" cy="240" r="54" fill="#ffffff25"/><path d="M467 215l38 25-38 25Z" fill="white"/><text x="480" y="365" text-anchor="middle" fill="white" font-size="32" font-family="sans-serif">IECMP · LOCAL MEDIA TEST</text></svg>`
 if(name==='asset')return `<svg xmlns="http://www.w3.org/2000/svg" width="600" height="400"><rect width="600" height="400" fill="#edf1fe"/><rect x="130" y="70" width="340" height="220" rx="18" fill="#c2cff5"/><rect x="147" y="87" width="306" height="184" rx="8" fill="#788ecd"/><path d="M300 290v45M235 338h130" stroke="#91a3cf" stroke-width="12"/><text x="300" y="190" text-anchor="middle" fill="white" font-size="30" font-family="sans-serif">IECMP</text></svg>`
 const index=Number(name.split('-')[1])||1,colors=['#879de9','#80b6b2','#b393c5','#d4af7c','#8698bb','#adaca0'];return `<svg xmlns="http://www.w3.org/2000/svg" width="160" height="160"><rect width="160" height="160" rx="36" fill="${colors[index%colors.length]}"/><circle cx="80" cy="58" r="28" fill="#ffffffb0"/><path d="M27 153c0-65 106-65 106 0" fill="#ffffffa0"/></svg>`
}
export function createMockMiddleware(options={}){
 let initial;const databaseFile=path.join(options.directory||mockDataDirectory,'database.json')
 if(options.persist!==false){try{initial=JSON.parse(fs.readFileSync(databaseFile,'utf8'))}catch(error){if(error.code!=='ENOENT')throw new Error(`模拟数据文件不可读，未覆盖原文件：${error.message}`)}}
 const persist=data=>{if(options.persist===false)return;fs.mkdirSync(path.dirname(databaseFile),{recursive:true});const temp=databaseFile+'.tmp';fs.writeFileSync(temp,JSON.stringify(data,null,2));fs.renameSync(temp,databaseFile)}
 const engine=createMockEngine(contracts,initial,persist);let latency=150;const scenarios=new Map();const recent=[]
 if(!initial&&options.persist!==false)engine.save()
 const middleware=async(req,res,next)=>{
  const url=new URL(req.url,'http://localhost'),pathname=url.pathname
  if(!pathname.startsWith('/api/')&&!pathname.startsWith('/__mock'))return next()
  // Local mock mutations are same-origin only; no credentialed CORS endpoints are exposed.
  const host=(req.headers.host||'').toLowerCase(),origin=req.headers.origin
  if(origin&&new URL(origin).host.toLowerCase()!==host)return json(res,{code:403,data:null,msg:'本地测试仅允许同源请求'},403)
  res.setHeader('X-IECMP-Mock','true');res.setHeader('Cache-Control','no-store')
  try{
   if(pathname==='/__mock/'||pathname==='/__mock'){res.setHeader('Content-Type','text/html; charset=utf-8');return res.end(fs.readFileSync(new URL('./console.html',import.meta.url)))}
   if(pathname==='/__mock/status')return json(res,{mode:'mock',backendConnected:false,latency,stats:engine.stats(),scenarios:[...scenarios].map(([id,value])=>({id,...value})),recent:recent.slice(-25).reverse(),accounts:engine.database.users.map(u=>({username:u.username,realName:u.realName,userId:u.userId,roleIds:u.roleIds})),contracts:Object.values(contracts).map(({id,name,method,path})=>({id,name,method,path}))})
   if(pathname==='/__mock/reset'&&req.method==='POST'){engine.reset();scenarios.clear();recent.length=0;latency=150;return json(res,{reset:true})}
   if(pathname==='/__mock/settings'&&req.method==='POST'){const{values}=await readBody(req);latency=Math.min(10000,Math.max(0,+values.latency||0));return json(res,{latency})}
   if(pathname==='/__mock/scenario'&&req.method==='POST'){const{values}=await readBody(req);if(!contracts[values.id])throw new MockError(400,'请选择接口');if(!['success','empty','401','403','500'].includes(String(values.type)))throw new MockError(400,'无效场景');if(values.type==='success')scenarios.delete(values.id);else scenarios.set(values.id,{type:String(values.type),remaining:1});return json(res,{armed:true})}
   if(pathname==='/__mock/scenarios/clear'&&req.method==='POST'){scenarios.clear();return json(res,{cleared:true})}
   const mediaMatch=/^\/__mock\/media\/([\w-]+)\.(svg|mp4|png)$/.exec(pathname)
   if(mediaMatch){if(mediaMatch[2]==='svg'){res.setHeader('Content-Type','image/svg+xml');return res.end(svg(mediaMatch[1]))}const file=new URL(`./media/${mediaMatch[1]}.${mediaMatch[2]}`,import.meta.url);if(!fs.existsSync(file)){res.statusCode=404;return res.end('Local fixture missing')}res.setHeader('Content-Type',mediaMatch[2]==='mp4'?'video/mp4':'image/png');res.setHeader('Accept-Ranges','bytes');const length=fs.statSync(file).size,range=/bytes=(\d+)-(\d*)/.exec(req.headers.range||'');if(range){const start=+range[1],end=range[2]?Math.min(+range[2],length-1):length-1;if(start> end||start>=length){res.statusCode=416;res.setHeader('Content-Range',`bytes */${length}`);return res.end()}res.statusCode=206;res.setHeader('Content-Range',`bytes ${start}-${end}/${length}`);res.setHeader('Content-Length',end-start+1);return fs.createReadStream(file,{start,end}).pipe(res)}res.setHeader('Content-Length',length);return fs.createReadStream(file).pipe(res)}
   const fileMatch=/^\/__mock\/files\/(\d+)$/.exec(pathname)
   if(fileMatch){const row=engine.database.files.find(f=>f.fileId===+fileMatch[1]);if(!row){res.statusCode=404;return res.end('File not found')}res.setHeader('X-Content-Type-Options','nosniff');res.setHeader('Content-Type',/^(image\/(png|jpeg|gif|webp)|application\/pdf)$/.test(row.fileType)?row.fileType:'application/octet-stream');if(!row.fileType.startsWith('image/'))res.setHeader('Content-Disposition',`attachment; filename*=UTF-8''${encodeURIComponent(row.name)}`);return res.end(Buffer.from(row.base64,'base64'))}
   const contract=patterns.find(c=>c.method===req.method.toLowerCase()&&c.pattern.test(pathname.replace(/^\/api/,'')))
   if(!contract){if(pathname.startsWith('/__mock'))return next();return json(res,{code:404,data:null,msg:'本地模拟接口不存在；不会转发后端'})}
   const match=contract.pattern.exec(pathname.replace(/^\/api/,'')),{values,files}=await readBody(req),q={...Object.fromEntries(url.searchParams),...values,...match.groups}
   await delay(latency)
   const scenario=scenarios.get(contract.id);if(scenario){scenarios.delete(contract.id);if(scenario.type==='empty')return json(res,{code:200,data:contract.path.endsWith('/page')?{total:0,pageNum:+q.pageNum||1,pageSize:+q.pageSize||10,pages:0,list:[]}:[],msg:'success'});return json(res,{code:+scenario.type,data:null,msg:`本地模拟：手动触发 ${scenario.type} 测试场景`})}
   const token=(req.headers.authorization||'').replace(/^Bearer\s+/i,'')
   const data=engine.dispatch(contract.id,q,token,files)
   recent.push({time:new Date().toISOString(),id:contract.id,name:contract.name,method:contract.method,path:pathname,code:200});if(recent.length>100)recent.shift()
   if(data?.__export){const cols=['assetId','assetNo','name','categoryName','price','status','keeperName','location'],escape=v=>'"'+String(v??'').replace(/"/g,'""').replace(/^[=+@-]/,"'")+'"';const csv='\uFEFF'+[cols,...data.__export.map(r=>cols.map(k=>r[k]))].map(row=>row.map(escape).join(',')).join('\r\n');res.setHeader('Content-Type','text/csv; charset=utf-8');res.setHeader('Content-Disposition','attachment; filename="assets-mock.csv"');return res.end(csv)}
   if(contract.id==='8-13'){const image=new URL('./media/captcha.png',import.meta.url);if(fs.existsSync(image))data.captchaImage='data:image/png;base64,'+fs.readFileSync(image).toString('base64')}
   return json(res,{code:200,data,msg:'success'})
  }catch(error){const code=error instanceof MockError?error.code:500;recent.push({time:new Date().toISOString(),path:pathname,code,message:error.message});return json(res,{code,data:null,msg:error.message||'本地模拟错误'})}
 }
 middleware.engine=engine
 return middleware
}
