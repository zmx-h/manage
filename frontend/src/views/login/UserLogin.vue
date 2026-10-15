<template>
  <!-- TODO 待实现：用户登录 界面 -->
  <el-card
    class="login-card"
    shadow="never"
  >
    <template #header>
      用户登录
    </template>
    <el-empty description="页面待实现（骨架占位）" />
  </el-card>
</template>

<script setup>
// 骨架占位：表单、验证码（接口 8-13）、登录（接口 1-02）均待实现。
// 登录成功后：保存双令牌 → 将 userInfo 写入 useUserStore → 跳转目标页。
</script>

<style scoped>
.login-card {
  width: 380px;
}
</style>
