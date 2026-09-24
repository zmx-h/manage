// 用户端路由表。
// 页面组件全部是空壳（src/views/**），只占位不实现界面。
// 路由 meta：title 菜单标题、perm 访问所需权限码（登录即可的写占位文案）。

export default [
  {
    path: "home",
    name: "user-home",
    component: () => import("@/views/user/home/index.vue"),
    meta: { title: "首页" }
  },
  {
    path: "profile",
    name: "user-profile",
    component: () => import("@/views/user/profile/index.vue"),
    meta: { title: "个人中心" }
  },
  {
    path: "repo",
    name: "user-repo",
    component: () => import("@/views/user/repo/index.vue"),
    meta: { title: "我的贡献" }
  },
  {
    path: "budget",
    name: "user-budget",
    component: () => import("@/views/user/budget/index.vue"),
    meta: { title: "预算公示" }
  },
  {
    path: "budget/apply",
    name: "user-budget-apply",
    component: () => import("@/views/user/budget/apply/index.vue"),
    meta: { title: "我的预算申请" }
  },
  {
    path: "asset",
    name: "user-asset",
    component: () => import("@/views/user/asset/index.vue"),
    meta: { title: "资产台账" }
  },
  {
    path: "asset/borrow",
    name: "user-asset-borrow",
    component: () => import("@/views/user/asset/borrow/index.vue"),
    meta: { title: "我的借用" }
  },
  {
    path: "seat",
    name: "user-seat",
    component: () => import("@/views/user/seat/index.vue"),
    meta: { title: "工位平面图" }
  },
  {
    path: "seat/apply",
    name: "user-seat-apply",
    component: () => import("@/views/user/seat/apply/index.vue"),
    meta: { title: "我的工位申请" }
  },
  {
    path: "seat/swap",
    name: "user-seat-swap",
    component: () => import("@/views/user/seat/swap/index.vue"),
    meta: { title: "座位调换" }
  },
  {
    path: "honor",
    name: "user-honor",
    component: () => import("@/views/user/honor/index.vue"),
    meta: { title: "学长学姐展板" }
  },
  {
    path: "honor/submit",
    name: "user-honor-submit",
    component: () => import("@/views/user/honor/submit/index.vue"),
    meta: { title: "提交展板信息" }
  },
  {
    path: "duty",
    name: "user-duty",
    component: () => import("@/views/user/duty/index.vue"),
    meta: { title: "我的值日" }
  },
  {
    path: "duty/swap",
    name: "user-duty-swap",
    component: () => import("@/views/user/duty/swap/index.vue"),
    meta: { title: "换班申请" }
  },
  {
    path: "notice",
    name: "user-notice",
    component: () => import("@/views/user/notice/index.vue"),
    meta: { title: "公告" }
  },
  {
    path: "message",
    name: "user-message",
    component: () => import("@/views/user/message/index.vue"),
    meta: { title: "站内消息" }
  },
  {
    path: "video",
    name: "user-video",
    component: () => import("@/views/user/video/index.vue"),
    meta: { title: "展示视频" }
  }
]
