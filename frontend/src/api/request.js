import axios from 'axios'
import { getAccessToken, getRefreshToken, setToken, clearToken } from '@/utils/auth'

export class ApiError extends Error {
  constructor(message, code, unimplemented = false) { super(message); this.name = 'ApiError'; this.code = code; this.unimplemented = unimplemented }
}
const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
const service = axios.create({ baseURL, timeout: 20000 })
let refreshing
export function refreshToken() {
  if (!getRefreshToken()) return Promise.reject(new ApiError('Session expired', 401))
  if (!refreshing) refreshing = axios.post(`${baseURL}/user/token/refresh`, { refreshToken: getRefreshToken() }, { timeout: 15000 })
    .then(({ data }) => { if (data.code !== 200 || !data.data?.accessToken) throw new ApiError(data.msg, 401); setToken(data.data); return data.data })
    .finally(() => { refreshing = undefined })
  return refreshing
}
service.interceptors.request.use(config => {
  const token = getAccessToken()
  if (token && !config.public) config.headers.Authorization = `Bearer ${token}`
  return config
})
function expireSession() {
  clearToken()
  window.dispatchEvent(new Event('iecmp:session-expired'))
}
service.interceptors.response.use(async response => {
  if (response.config.responseType === 'blob') {
    if (!response.data.type?.includes('json')) return response
    const body = JSON.parse(await response.data.text())
    if (body.code === 200) return response
    response.data = body
  }
  const { code, data, msg } = response.data || {}
  if (code === 200) return data
  const config = response.config
  if (code === 401 && !config.public && !config._retry && getRefreshToken()) {
    config._retry = true
    try { const next = await refreshToken(); config.headers.Authorization = `Bearer ${next.accessToken}` }
    catch { expireSession(); throw new ApiError(msg || 'Session expired', 401) }
    return service(config)
  } else if (code === 401 && !config.public) expireSession()
  throw new ApiError(msg || 'Request failed', code, /尚未实现|骨架占位/.test(msg || ''))
}, async error => {
  const config = error.config
  if (error.response?.status === 401 && config && !config.public && !config._retry && getRefreshToken()) {
    config._retry = true
    try { await refreshToken() } catch { expireSession(); throw new ApiError('Session expired', 401) }
    return service(config)
  }
  if (error.response?.status === 401 && !config?.public) expireSession()
  throw new ApiError(error.response?.data?.msg || error.message, error.response?.status || 0)
})
export default service
