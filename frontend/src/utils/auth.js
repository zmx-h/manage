// 令牌读写：accessToken 与 refreshToken（双令牌方案，见架构文档 4.1 节）
const ACCESS_TOKEN_KEY = 'iecmp_access_token'
const REFRESH_TOKEN_KEY = 'iecmp_refresh_token'

/** 读取 accessToken。 */
export function getAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY) || ''
}

/** 读取 refreshToken。 */
export function getRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY) || ''
}

/** 写入双令牌。 */
export function setToken({ accessToken, refreshToken }) {
  if (accessToken) {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken)
  }
  if (refreshToken) {
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
  }
}

/** 清空双令牌（退出登录 / 令牌失效）。 */
export function clearToken() {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
}
