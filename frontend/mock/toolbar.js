// Only served in explicitly enabled Vite mock mode.
const link = document.createElement('a')
link.href = '/__mock/'
link.textContent = '本地模拟 · 测试数据 / 测试控制台'
link.setAttribute('aria-label', '打开本地模拟测试控制台')
Object.assign(link.style, { position: 'fixed', bottom: '12px', right: '16px', zIndex: '9999', padding: '9px 13px', borderRadius: '9px', background: '#334669ed', color: '#fff', font: '12px system-ui', textDecoration: 'none', boxShadow: '0 3px 15px #0002', backdropFilter: 'blur(12px)' })
document.body.appendChild(link)
