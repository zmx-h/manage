<template>
  <div class="upload-control">
    <el-upload
      :show-file-list="false"
      :http-request="upload"
      :accept="image ? '.jpg,.jpeg,.png,.webp,.gif' : '.jpg,.jpeg,.png,.webp,.gif,.pdf,.doc,.docx,.xls,.xlsx'"
      :before-upload="validate"
      :disabled="preview || busy"
    >
      <el-button :loading="busy">
        <AppIcon
          name="plus"
          :size="16"
        />{{ t('upload') }}
      </el-button>
    </el-upload><span
      v-if="modelValue"
      class="field-hint"
    >{{ modelValue }}</span><small class="field-hint">{{ t('uploadHint') }}</small>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { invoke } from '@/api/client'
import { useWorkspace } from '@/composables/useWorkspace'
import { useLocale } from '@/locales'
import AppIcon from './AppIcon.vue'
const props = defineProps({ modelValue: { type: [String,Number], default: '' }, bizType: { type: String, default: 'common' }, image: Boolean, fileId: Boolean }); const emit = defineEmits(['update:modelValue','uploaded'])
const { preview } = useWorkspace(); const { t } = useLocale(); const busy = ref(false)
function validate(file) { const image = /\.(jpg|jpeg|png|webp|gif)$/i.test(file.name); if (props.image && !image || !/\.(jpg|jpeg|png|webp|gif|pdf|doc|docx|xls|xlsx)$/i.test(file.name) || file.size > (image ? 5 : 20)*1024*1024) { ElMessage.error(t('uploadHint')); return false } return true }
async function upload({ file, onSuccess, onError }) { busy.value = true; try { const data = await invoke('8-01', {file, bizType:props.bizType}); emit('update:modelValue', props.fileId ? data.fileId : data.url); emit('uploaded', data); onSuccess(data); ElMessage.success(t('uploadSuccess')) } catch(e) { onError(e); ElMessage.error(e.message) } finally { busy.value = false } }
</script>
