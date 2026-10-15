import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getAccessToken, getRefreshToken, setToken, clearToken } from '@/utils/auth'

// 统一请求封装（接口文档 2.3 / 2.8 节）
//   1. HTTP 状态码恒为 200，业务结果由响应体 code 表达；
//   2. code === 200 才 resolve，其余 reject 并提示 msg；
//   3. 401 清空令牌并跳转登录页；403 提示「权限不足」。
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 20000
})

service.interceptors.request.use(
  (config) => {
    const token = getAccessToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  (response) => {
    // 文件流下载：直接返回原始响应，由调用方处理
    if (response.config.responseType === 'blob') {
      return response
    }
    const { code, data, msg } = response.data
    if (code === 200) {
      return data
    }
    if (code === 401) {
      clearToken()
      ElMessage.error(msg || '未登录或登录已过期')
      window.location.href = '/login'
      return Promise.reject(new Error(msg || '未登录或登录已过期'))
    }
    if (code === 403) {
      ElMessage.error(msg || '权限不足')
      return Promise.reject(new Error(msg || '权限不足'))
    }
    ElMessage.error(msg || '请求失败')
    return Promise.reject(new Error(msg || '请求失败'))
  },
  (error) => {
    ElMessage.error(error.message || '网络异常，请稍后重试')
    return Promise.reject(error)
  }
)

/**
 * 刷新令牌（接口 1-04 POST /user/token/refresh）。
 *
 * <p>骨架阶段未接入响应拦截器；实现时在 401 分支前调用本函数做静默续期：
 * 使用 refreshToken 换取新的 accessToken，成功后重放原请求，失败则跳登录页。
 */
export async function refreshToken() {
  const token = getRefreshToken()
  if (!token) {
    return null
  }
  const data = await service.post('/user/token/refresh', { refreshToken: token })
  setToken({ accessToken: data.accessToken, refreshToken: data.refreshToken })
  return data
}

export default service
