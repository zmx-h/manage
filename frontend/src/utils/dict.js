import { invoke } from '@/api/client'
const cache = new Map()
export async function loadDict(type, force = false) {
  const hit = cache.get(type)
  if (!force && hit && Date.now() - hit.time < 3600000) return hit.list
  const list = await invoke('8-04', { type })
  cache.set(type, { list, time: Date.now() })
  return list
}
export function dictLabel(type, value) { return cache.get(type)?.list.find(item => item.value === value)?.label ?? value }
