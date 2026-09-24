// 管理端路由表。
// 页面组件全部是空壳（src/views/**），只占位不实现界面。
// 路由 meta：title 菜单标题、perm 访问所需权限码（登录即可的写占位文案）。

export default [
  {
    path: "dashboard",
    name: "admin-dashboard",
    component: () => import("@/views/admin/dashboard/index.vue"),
    meta: { title: "数据看板", perm: "common:stat:list" }
  },
  {
    path: "system/user",
    name: "admin-system-user",
    component: () => import("@/views/admin/system/user/index.vue"),
    meta: { title: "用户管理", perm: "system:user:list" }
  },
  {
    path: "system/role",
    name: "admin-system-role",
    component: () => import("@/views/admin/system/role/index.vue"),
    meta: { title: "角色管理", perm: "system:role:list" }
  },
  {
    path: "system/permission",
    name: "admin-system-permission",
    component: () => import("@/views/admin/system/permission/index.vue"),
    meta: { title: "权限管理", perm: "登录即可" }
  },
  {
    path: "system/register",
    name: "admin-system-register",
    component: () => import("@/views/admin/system/register/index.vue"),
    meta: { title: "注册申请", perm: "system:register:list" }
  },
  {
    path: "system/invite",
    name: "admin-system-invite",
    component: () => import("@/views/admin/system/invite/index.vue"),
    meta: { title: "邀请码管理", perm: "system:invite:list" }
  },
  {
    path: "system/log",
    name: "admin-system-log",
    component: () => import("@/views/admin/system/log/index.vue"),
    meta: { title: "操作日志", perm: "system:log:list" }
  },
  {
    path: "repo",
    name: "admin-repo",
    component: () => import("@/views/admin/repo/index.vue"),
    meta: { title: "仓库与贡献", perm: "repo:account:list" }
  },
  {
    path: "budget/period",
    name: "admin-budget-period",
    component: () => import("@/views/admin/budget/period/index.vue"),
    meta: { title: "预算周期", perm: "budget:period:list" }
  },
  {
    path: "budget/item",
    name: "admin-budget-item",
    component: () => import("@/views/admin/budget/item/index.vue"),
    meta: { title: "预算科目", perm: "budget:item:edit" }
  },
  {
    path: "budget/apply",
    name: "admin-budget-apply",
    component: () => import("@/views/admin/budget/apply/index.vue"),
    meta: { title: "预算审核", perm: "budget:apply:list" }
  },
  {
    path: "budget/expense",
    name: "admin-budget-expense",
    component: () => import("@/views/admin/budget/expense/index.vue"),
    meta: { title: "支出登记", perm: "budget:expense:list" }
  },
  {
    path: "asset",
    name: "admin-asset",
    component: () => import("@/views/admin/asset/index.vue"),
    meta: { title: "资产台账", perm: "asset:info:list" }
  },
  {
    path: "asset/category",
    name: "admin-asset-category",
    component: () => import("@/views/admin/asset/category/index.vue"),
    meta: { title: "资产分类", perm: "asset:category:edit" }
  },
  {
    path: "asset/borrow",
    name: "admin-asset-borrow",
    component: () => import("@/views/admin/asset/borrow/index.vue"),
    meta: { title: "借用审核", perm: "asset:borrow:list" }
  },
  {
    path: "asset/inventory",
    name: "admin-asset-inventory",
    component: () => import("@/views/admin/asset/inventory/index.vue"),
    meta: { title: "资产盘点", perm: "asset:inventory:list" }
  },
  {
    path: "seat/plan",
    name: "admin-seat-plan",
    component: () => import("@/views/admin/seat/plan/index.vue"),
    meta: { title: "平面图管理", perm: "seat:seat:assign" }
  },
  {
    path: "seat/apply",
    name: "admin-seat-apply",
    component: () => import("@/views/admin/seat/apply/index.vue"),
    meta: { title: "工位申请", perm: "seat:apply:list" }
  },
  {
    path: "seat/swap",
    name: "admin-seat-swap",
    component: () => import("@/views/admin/seat/swap/index.vue"),
    meta: { title: "调换审核", perm: "seat:swap:list" }
  },
  {
    path: "honor/alumni",
    name: "admin-honor-alumni",
    component: () => import("@/views/admin/honor/alumni/index.vue"),
    meta: { title: "学长学姐管理", perm: "honor:alumni:list" }
  },
  {
    path: "honor/audit",
    name: "admin-honor-audit",
    component: () => import("@/views/admin/honor/audit/index.vue"),
    meta: { title: "展板审核", perm: "honor:alumni:audit" }
  },
  {
    path: "duty/plan",
    name: "admin-duty-plan",
    component: () => import("@/views/admin/duty/plan/index.vue"),
    meta: { title: "排班计划", perm: "duty:plan:list" }
  },
  {
    path: "duty/schedule",
    name: "admin-duty-schedule",
    component: () => import("@/views/admin/duty/schedule/index.vue"),
    meta: { title: "值日安排", perm: "duty:schedule:list" }
  },
  {
    path: "duty/stat",
    name: "admin-duty-stat",
    component: () => import("@/views/admin/duty/stat/index.vue"),
    meta: { title: "值日统计", perm: "duty:stat:list" }
  },
  {
    path: "notice",
    name: "admin-notice",
    component: () => import("@/views/admin/common/notice/index.vue"),
    meta: { title: "公告管理", perm: "common:notice:list" }
  },
  {
    path: "video",
    name: "admin-video",
    component: () => import("@/views/admin/common/video/index.vue"),
    meta: { title: "视频资源", perm: "common:video:list" }
  }
]
