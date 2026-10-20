import request from './request'
import contracts from '@/config/contracts.json'

export { contracts }
export function invoke(id, values = {}, options = {}) {
  const contract = contracts[id]
  if (!contract) throw new Error(`Unknown API contract: ${id}`)
  let url = contract.path
  const params = {}; const data = {}
  for (const f of contract.fields.filter(field => !field.parent)) {
    const value = values[f.key]
    if (f.location === 'path') {
      if (value === undefined || value === null || value === '') throw new Error(`Missing path parameter: ${f.key}`)
      url = url.replace(`{${f.key}}`, encodeURIComponent(value))
    } else if (value !== undefined && value !== '' && value !== null) {
      if (f.location === 'query' || contract.method === 'get') params[f.key] = value
      else data[f.key] = value
    }
  }
  if (contract.method === 'get') for (const key of ['pageNum','pageSize','keyword','orderBy']) if (values[key] !== undefined && values[key] !== '') params[key] = values[key]
  let body = data
  if (['8-01','8-02'].includes(id)) {
    body = new FormData()
    for (const [key, value] of Object.entries(data)) {
      if (Array.isArray(value)) value.forEach(file => body.append(key, file))
      else body.append(key, value)
    }
  }
  return request({ url, method: contract.method, params, data: ['get','delete'].includes(contract.method) ? undefined : body, public: !contract.auth, ...options })
}
export function normalizedList(data) { return Array.isArray(data) ? data : (data?.list || []) }
export function plainText(value) { return String(value ?? '').replace(/<[^>]*>/g, ' ').replace(/&nbsp;/g, ' ').trim() }
export function safeUrl(value) { try { const url = new URL(value, window.location.origin); return ['http:','https:'].includes(url.protocol) ? url.href : '' } catch { return '' } }

