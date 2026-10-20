import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebarCollapsed: false,
    mobileMenu: false,
    theme: localStorage.getItem('iecmp_theme') || 'system',
    locale: localStorage.getItem('iecmp_locale') || 'zh-CN',
    systemDark: window.matchMedia('(prefers-color-scheme: dark)').matches
  }),
  getters: { dark: (s) => s.theme === 'dark' || (s.theme === 'system' && s.systemDark) },
  actions: {
    setTheme(value) { this.theme = value; localStorage.setItem('iecmp_theme', value) },
    setLocale(value) { this.locale = value; localStorage.setItem('iecmp_locale', value) },
    toggleSidebar() { this.sidebarCollapsed = !this.sidebarCollapsed }
  }
})
