import { pages } from '@/config/navigation'
export default pages.filter(page => page.side === 'admin').map(page => ({
  path: page.path.replace("/admin/", ''), name: page.key,
  component: () => import('@/views/workspace/WorkspacePage.vue'),
  meta: { title: page.title, en: page.en, perm: page.permission, page }
}))
