<template>
  <el-dropdown
    trigger="click"
    @command="app.setTheme"
  >
    <button
      class="icon-button"
      :aria-label="t('theme')"
    >
      <AppIcon :name="app.dark ? 'moon' : 'sun'" />
    </button><template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="mode in ['system','light','dark']"
          :key="mode"
          :command="mode"
        >
          <span class="menu-row"><AppIcon
            :name="mode === 'system' ? 'monitor' : mode === 'dark' ? 'moon' : 'sun'"
            :size="16"
          />{{ t(mode) }}<span v-if="app.theme === mode">✓</span></span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
  <el-dropdown
    trigger="click"
    @command="app.setLocale"
  >
    <button
      class="icon-button"
      :aria-label="t('language')"
    >
      <AppIcon name="globe" />
    </button><template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="zh-CN">
          简体中文 {{ app.locale === 'zh-CN' ? '✓' : '' }}
        </el-dropdown-item><el-dropdown-item command="en-US">
          English {{ app.locale === 'en-US' ? '✓' : '' }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>
<script setup>
import AppIcon from './AppIcon.vue'
import { useAppStore } from '@/store/modules/app'
import { useLocale } from '@/locales'
const app = useAppStore(); const { t } = useLocale()
</script>
