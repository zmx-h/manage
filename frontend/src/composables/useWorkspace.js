import { computed } from 'vue'
import { useRoute } from 'vue-router'
export function useWorkspace() {
  const route = useRoute()
  const side = computed(() => route.path.startsWith('/admin') ? 'admin' : 'user')
  const page = computed(() => route.meta.page)
  const link = p => !p ? (side.value === 'admin' ? '/admin' : '/home') : p.path
  return { side, page, link }
}
