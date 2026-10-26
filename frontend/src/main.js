import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import App from './App.vue'
import router, { watchLocale } from './router'
import store from './store'
import permission from './directives/permission'
import './assets/styles/index.css'
const app = createApp(App)
app.use(store).use(router).use(ElementPlus).use(permission)
watchLocale()
app.mount('#app')
