<template>
  <!-- TODO 待实现：注册 界面 -->
  <el-card
    class="login-card"
    shadow="never"
  >
    <template #header>
      注册
    </template>
    <el-empty description="页面待实现（骨架占位）" />
  </el-card>
</template>

<script setup>
// 骨架占位：表单、验证码（接口 8-13）、登录（接口 1-23 / 1-24）均待实现。
// 登录成功后：保存双令牌 → 将 userInfo 写入 useUserStore → 跳转目标页。
</script>

<style scoped>
.login-card {
  width: 380px;
}
</style>
