import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { pages } from '@/config/navigation'
export function useWorkspace() {
  const route = useRoute()
  const preview = computed(() => Boolean(route.meta.preview))
  const side = computed(() => preview.value ? route.params.side : route.path.startsWith('/admin') ? 'admin' : 'user')
  const page = computed(() => {
    if (!preview.value) return route.meta.page
    const section = Array.isArray(route.params.page) ? route.params.page.join('/') : route.params.page
    const path = (side.value === 'admin' ? '/admin/' : '/') + (section || (side.value === 'admin' ? 'dashboard' : 'home'))
    return pages.find(p => p.path === path && p.side === side.value)
  })
  const link = p => !p ? (side.value === 'admin' ? '/admin' : '/home') : preview.value ? `/preview/${p.side}/${p.path.replace(p.side === 'admin' ? '/admin/' : '/', '')}` : p.path
  return { preview, side, page, link }
}
