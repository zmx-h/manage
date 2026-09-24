import { defineStore } from 'pinia'

// 界面状态：侧边栏折叠、面包屑、主题等
export const useAppStore = defineStore('app', {
  state: () => ({
    sidebarCollapsed: false,
    /** 页面标题，由路由 meta.title 同步 */
    pageTitle: ''
  }),

  actions: {
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },
    setPageTitle(title) {
      this.pageTitle = title || ''
    }
  }
})
