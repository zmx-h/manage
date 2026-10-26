<template>
  <el-config-provider :locale="elementLocale">
    <router-view />
  </el-config-provider>
</template>
<script setup>
import 'dayjs/locale/zh-cn'
import { computed, onMounted, onUnmounted, watch } from 'vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'
import { useAppStore } from '@/store/modules/app'
const app = useAppStore()
const elementLocale = computed(() => app.locale === 'en-US' ? en : zhCn)
const media = window.matchMedia('(prefers-color-scheme: dark)')
const sync = e => { app.systemDark = e.matches }
onMounted(() => media.addEventListener('change', sync))
onUnmounted(() => media.removeEventListener('change', sync))
watch(() => app.dark, value => { document.documentElement.classList.toggle('dark', value); document.documentElement.style.colorScheme = value ? 'dark' : 'light' }, { immediate: true })
watch(() => app.locale, value => { document.documentElement.lang = value }, { immediate: true })
</script>
