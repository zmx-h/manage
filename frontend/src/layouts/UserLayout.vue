<template>
  <!-- 用户端布局骨架：顶部导航 + 内容区 -->
  <el-container class="user-layout">
    <el-header height="56px">
      <div class="logo">
        IECMP
      </div>
      <!-- TODO 待实现：导航菜单、消息红点（接口 8-21）、个人入口 -->
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
// 骨架占位：导航与用户信息展示待实现
</script>

<style scoped>
.user-layout {
  height: 100%;
}

.logo {
  font-weight: 600;
}
</style>
