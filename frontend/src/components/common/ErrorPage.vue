<template>
  <div class="error-page">
    <div class="error-number">
      {{ code }}
    </div><span class="eyebrow">SOMETHING WENT OFF TRACK</span><h1>{{ t(code===403?'permissionDenied':'notFound') }}</h1><p>{{ t(code===403?'forbiddenHint':'notFoundHint') }}</p><router-link to="/home">
      <el-button type="primary">
        {{ t('back') }}<AppIcon
          name="arrow"
          :size="18"
        />
      </el-button>
    </router-link>
  </div>
</template>
<script setup>
import {useLocale} from '@/locales'
import AppIcon from './AppIcon.vue'
defineProps({code:{type:Number,default:404}}); const {t}=useLocale()
</script>
