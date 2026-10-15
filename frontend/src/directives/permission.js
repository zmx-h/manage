// 按钮级权限指令：v-perm="'system:user:add'"
// 权限码清单见《接口开发文档》16.3 节；超级管理员（superAdmin）直接放行。
import { useUserStore } from '@/store/modules/user'

export default {
  install(app) {
    app.directive('perm', {
      mounted(el, binding) {
        const userStore = useUserStore()
        const required = Array.isArray(binding.value) ? binding.value : [binding.value]
        if (!userStore.superAdmin && !required.some((code) => userStore.permissions.includes(code))) {
          el.parentNode && el.parentNode.removeChild(el)
        }
      }
    })
  }
}
