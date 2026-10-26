import { createRouter, createWebHistory } from 'vue-router'
import { watch } from 'vue'
import adminRoutes from './routes/admin'
import userRoutes from './routes/user'
import { useUserStore } from '@/store/modules/user'
import { useAppStore } from '@/store/modules/app'
const workspace = () => import('@/views/workspace/WorkspacePage.vue')
const router = createRouter({ history: createWebHistory(), scrollBehavior: () => ({ top: 0 }), routes: [
  { path: '/login', name: 'login', component: () => import('@/views/login/UserLogin.vue'), meta: { public: true, title: '成员登录', en: 'Member sign in' } },
  { path: '/admin/login', name: 'admin-login', component: () => import('@/views/login/AdminLogin.vue'), meta: { public: true, title: '管理端登录', en: 'Administrator sign in' } },
  { path: '/register', name: 'register', component: () => import('@/views/login/Register.vue'), meta: { public: true, title: '申请加入', en: 'Join the center' } },
  { path: '/preview/:side(admin|user)/:page(.*)*', name: 'preview', component: () => import('@/layouts/WorkspaceLayout.vue'), meta: { public: true, preview: true }, children: [{ path: '', component: workspace }] },
  { path: '/admin', component: () => import('@/layouts/AdminLayout.vue'), redirect: '/admin/dashboard', children: adminRoutes },
  { path: '/', component: () => import('@/layouts/UserLayout.vue'), redirect: '/home', children: userRoutes },
  { path: '/403', component: () => import('@/views/error/403.vue'), meta: { public: true, title: '无访问权限', en: 'Access restricted' } },
  { path: '/:pathMatch(.*)*', component: () => import('@/views/error/404.vue'), meta: { public: true, title: '页面不存在', en: 'Page not found' } }
] })
router.beforeEach(async to => {
  const user = useUserStore()
  if (to.meta.public) return true
  if (!user.logged) return { path: to.path.startsWith('/admin') ? '/admin/login' : '/login', query: { redirect: to.fullPath } }
  if (!user.loaded) {
    try { await user.fetchInfo() } catch { user.reset(); return { path: '/login', query: { redirect: to.fullPath } } }
  }
  if (to.path.startsWith('/admin') && !user.canAdmin) return '/403'
  if (to.meta.perm && !user.hasPerm(to.meta.perm)) return '/403'
  return true
})
function updateTitle() { const app = useAppStore(); const meta = router.currentRoute.value.meta; document.title = `${app.locale === 'en-US' ? meta.en || 'Workspace' : meta.title || '工作空间'} · IECMP` }
router.afterEach(updateTitle)
window.addEventListener('iecmp:session-expired', () => { const admin = router.currentRoute.value.path.startsWith('/admin'); const redirect = router.currentRoute.value.fullPath; useUserStore().reset(); router.replace({ path: admin ? '/admin/login' : '/login', query: { redirect } }) })
export function watchLocale() { watch(() => useAppStore().locale, updateTitle) }
export default router
