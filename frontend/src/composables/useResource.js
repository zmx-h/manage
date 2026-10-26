import { ref } from 'vue'
import { invoke } from '@/api/client'
export function useResource() {
  const data = ref(null); const loading = ref(false); const error = ref(null)
  let version = 0
  async function load(id, params = {}, preview = false) {
    const current = ++version
    error.value = null; data.value = null
    if (preview || !id) { loading.value = false; return }
    loading.value = true
    try { const result = await invoke(id, params); if (current === version) data.value = result }
    catch (e) { if (current === version) error.value = e }
    finally { if (current === version) loading.value = false }
  }
  return { data, loading, error, load }
}
