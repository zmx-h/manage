import { createRouter, createWebHistory } from "vue-router"
import { getAccessToken } from "@/utils/auth"
import adminRoutes from "./routes/admin"
import userRoutes from "./routes/user"

// 路由总表：登录页 + 两套布局（管理端 / 用户端）
export const routes = [
  {
    path: "/login",
    name: "login",
    component: () => import("@/views/login/UserLogin.vue"),
    meta: { title: "登录", public: true }
  },
  {
    path: "/admin/login",
    name: "admin-login",
    component: () => import("@/views/login/AdminLogin.vue"),
    meta: { title: "管理端登录", public: true }
  },
  {
    path: "/register",
    name: "register",
    component: () => import("@/views/login/Register.vue"),
    meta: { title: "注册", public: true }
  },
  {
    path: "/admin",
    component: () => import("@/layouts/AdminLayout.vue"),
    redirect: "/admin/dashboard",
    children: adminRoutes
  },
  {
    path: "/",
    component: () => import("@/layouts/UserLayout.vue"),
    redirect: "/home",
    children: userRoutes
  },
  {
    path: "/403",
    name: "forbidden",
    component: () => import("@/views/error/403.vue"),
    meta: { title: "无访问权限", public: true }
  },
  {
    path: "/:pathMatch(.*)*",
    name: "not-found",
    component: () => import("@/views/error/404.vue"),
    meta: { title: "页面不存在", public: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// 全局前置守卫：未登录跳登录页。
// 骨架阶段只做登录判断；菜单与按钮的权限判断（useUserStore().hasPerm）待页面接入后开启。
router.beforeEach((to) => {
  document.title = (to.meta.title ? to.meta.title + " - " : "") + (import.meta.env.VITE_APP_TITLE || "IECMP")
  if (to.meta.public) {
    return true
  }
  if (!getAccessToken()) {
    return { path: to.path.startsWith("/admin") ? "/admin/login" : "/login", query: { redirect: to.fullPath } }
  }
  return true
})

export default router
