<template>
  <!-- 管理端布局骨架：左侧菜单 + 顶部栏 + 内容区；导航数据与样式待实现 -->
  <el-container class="admin-layout">
    <el-aside width="220px">
      <div class="logo">
        IECMP 管理端
      </div>
      <!-- TODO 待实现：遍历 src/router/routes/admin.js 生成菜单，按 meta.perm 过滤 -->
    </el-aside>
    <el-container>
      <el-header height="56px">
        <!-- TODO 待实现：面包屑、用户下拉（退出登录调用接口 1-03） -->
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
// 骨架占位：菜单渲染、用户信息展示、退出登录等逻辑待实现
</script>

<style scoped>
.admin-layout {
  height: 100%;
}

.logo {
  height: 56px;
  line-height: 56px;
  text-align: center;
  font-weight: 600;
}
</style>
