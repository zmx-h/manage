<template>
  <div
    class="resource-state"
    role="status"
  >
    <div class="state-orbit">
      <AppIcon
        :name="error ? 'layers' : 'inbox'"
        :size="30"
      />
    </div>
    <h3>{{ error ? (error.unimplemented ? t('unavailable') : t('requestFailed')) : t('empty') }}</h3>
    <p>{{ error ? (error.code === 0 ? t('networkError') : error.unimplemented ? t('unavailableHint') : error.message) : t('emptyHint') }}</p>
    <el-button
      v-if="error"
      @click="$emit('retry')"
    >
      {{ t('retry') }}
    </el-button>
  </div>
</template>
<script setup>
import { useLocale } from '@/locales'
import AppIcon from './AppIcon.vue'
defineProps({ error: { type: Object, default: null } }); defineEmits(['retry'])
const { t } = useLocale()
</script>
