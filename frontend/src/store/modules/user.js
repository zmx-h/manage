import { defineStore } from 'pinia'
import { getAccessToken, getRefreshToken, setToken, clearToken } from '@/utils/auth'

// 登录态与权限：字段与接口 1-01 / 1-02 / 1-05 的返回结构一一对应。
export const useUserStore = defineStore('user', {
  state: () => ({
    accessToken: getAccessToken(),
    refreshToken: getRefreshToken(),
    userId: null,
    username: '',
    realName: '',
    avatarUrl: '',
    roles: [],
    permissions: [],
    superAdmin: false
  }),

  getters: {
    logged: (state) => Boolean(state.accessToken),
    /** 是否持有某个权限码。 */
    hasPerm: (state) => (code) => state.superAdmin || state.permissions.includes(code)
  },

  actions: {
    /** 保存登录返回的双令牌与用户信息（数据结构见接口 1-01 / 1-02）。 */
    setLoginData(data) {
      this.accessToken = data.accessToken
      this.refreshToken = data.refreshToken
      setToken({ accessToken: data.accessToken, refreshToken: data.refreshToken })
      this.setUserInfo(data.userInfo || data)
    },

    /** 保存用户信息（数据结构见接口 1-05）。 */
    setUserInfo(info) {
      this.userId = info.userId ?? this.userId
      this.username = info.username ?? this.username
      this.realName = info.realName ?? this.realName
      this.avatarUrl = info.avatarUrl ?? this.avatarUrl
      this.roles = info.roles ?? this.roles
      this.permissions = info.permissions ?? this.permissions
      this.superAdmin = this.roles.includes('SUPER_ADMIN')
    },

    /** 退出登录：清空本地状态（服务端黑名单由接口 1-03 完成）。 */
    reset() {
      clearToken()
      this.$reset()
      this.accessToken = ''
      this.refreshToken = ''
    }
  }
})
