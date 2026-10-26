import { defineStore } from 'pinia'
import { getAccessToken, getRefreshToken, setToken, clearToken } from '@/utils/auth'
import { getUserInfo } from '@/api/auth'
export const useUserStore = defineStore('user', {
  state: () => ({ accessToken: getAccessToken(), refreshToken: getRefreshToken(), userId: null, username: '', realName: '', avatarUrl: '', roles: [], permissions: [], loaded: false }),
  getters: {
    logged: state => Boolean(state.accessToken),
    superAdmin: state => state.roles.includes('SUPER_ADMIN'),
    canAdmin: state => state.roles.some(role => ['SUPER_ADMIN','CENTER_HEAD','MINISTER'].includes(role)) || state.permissions.length > 0,
    hasPerm: state => code => !code || code === '登录即可' || state.roles.includes('SUPER_ADMIN') || state.permissions.includes(code)
  },
  actions: {
    setLoginData(data) { this.accessToken = data.accessToken; this.refreshToken = data.refreshToken; setToken(data); this.setUserInfo(data.userInfo) },
    setUserInfo(info = {}) { for (const key of ['userId','username','realName','avatarUrl','roles','permissions']) if (info[key] !== undefined) this[key] = info[key]; this.loaded = true },
    async fetchInfo() { this.setUserInfo(await getUserInfo()) },
    reset() { clearToken(); this.$reset(); this.accessToken = ''; this.refreshToken = ''; this.loaded = false }
  }
})
