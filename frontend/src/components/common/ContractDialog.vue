<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="min(720px, 94vw)"
    destroy-on-close
    :close-on-click-modal="!saving"
  >
    <div
      v-if="contract"
      class="contract-caption"
    >
      <span>{{ contract.method.toUpperCase() }}</span><code>{{ contract.path }}</code><small>{{ contract.id }}</small>
    </div>
    <el-alert
      v-if="error"
      :title="error.unimplemented ? t('unavailable') : t('requestFailed')"
      :description="error.message"
      type="warning"
      :closable="false"
      show-icon
    />
    <el-alert
      v-if="hint"
      :title="hint"
      type="info"
      :closable="false"
      show-icon
    />
    <el-form
      v-if="!showingResult"
      ref="formRef"
      :model="values"
      label-position="top"
      class="contract-form"
      @submit.prevent="submit"
    >
      <el-form-item
        v-for="f in fields"
        :key="f.key"
        :prop="f.key"
        :label="labelFor(f.key, isEn)"
        :rules="validation(f)"
        :class="{ 'full-span': ['textarea','json','file'].includes(typeFor(f)) }"
      >
        <el-select
          v-if="options[f.key]"
          v-model="values[f.key]"
          :multiple="f.type === 'array'"
          clearable
          filterable
          :placeholder="labelFor(f.key, isEn)"
        >
          <el-option
            v-for="option in options[f.key]"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
        <el-date-picker
          v-else-if="typeFor(f) === 'datetime' || typeFor(f) === 'date'"
          v-model="values[f.key]"
          :type="typeFor(f) === 'datetime' ? 'datetime' : 'date'"
          :value-format="typeFor(f) === 'datetime' ? 'YYYY-MM-DD HH:mm:ss' : 'YYYY-MM-DD'"
          clearable
        />
        <FileUpload
          v-else-if="uploadFields[f.key]"
          v-model="values[f.key]"
          :biz-type="uploadFields[f.key]"
          :file-id="/FileId$/.test(f.key)"
          :image="/Url$/.test(f.key) && !/certificate/.test(f.key)"
        />
        <el-input-number
          v-else-if="typeFor(f) === 'number'"
          v-model="values[f.key]"
          :precision="f.type === 'integer' ? 0 : 2"
          :controls="false"
        />
        <el-input
          v-else-if="typeFor(f) === 'json'"
          v-model="values[f.key]"
          type="textarea"
          :rows="4"
          :placeholder="t('jsonHint')"
        />
        <el-upload
          v-else-if="typeFor(f) === 'file'"
          :auto-upload="false"
          :limit="f.key === 'files' ? 10 : 1"
          :multiple="f.key === 'files'"
          :on-change="(_, files) => values[f.key] = f.key === 'files' ? files.map(x => x.raw) : files[0]?.raw"
          :on-remove="(_, files) => values[f.key] = f.key === 'files' ? files.map(x => x.raw) : files[0]?.raw"
        >
          <el-button>{{ t('upload') }}</el-button>
        </el-upload>
        <el-input
          v-else
          v-model="values[f.key]"
          :type="typeFor(f) === 'password' ? 'password' : typeFor(f) === 'textarea' ? 'textarea' : 'text'"
          :show-password="typeFor(f) === 'password'"
          :rows="3"
          :maxlength="maxLength(f)"
        />
        <small
          v-if="f.key === 'roleIds' && !options.roleIds"
          class="field-hint"
        >{{ t('jsonHint') }}</small>
      </el-form-item>
    </el-form>
    <div
      v-else
      class="operation-result"
    >
      <AppIcon
        name="check"
        :size="28"
      /><h3>{{ t('saved') }}</h3><DataDetails :value="result" />
    </div>
    <template #footer>
      <el-button @click="visible = false">
        {{ t(showingResult ? 'close' : 'cancel') }}
      </el-button><el-button
        v-if="!showingResult"
        type="primary"
        :loading="saving"
        :disabled="preview || optionsLoading"
        @click="submit"
      >
        {{ t(contract?.method === 'get' ? 'searchAction' : 'confirm') }}
      </el-button>
    </template>
  </el-dialog>
</template>
<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { contracts, invoke, normalizedList } from '@/api/client'
import { loadDict } from '@/utils/dict'
import { labelFor, typeFor, fieldDictionaries } from '@/config/fields'
import { useLocale } from '@/locales'
import { useWorkspace } from '@/composables/useWorkspace'
import { useUserStore } from '@/store/modules/user'
import FileUpload from './FileUpload.vue'
import DataDetails from './DataDetails.vue'
import AppIcon from './AppIcon.vue'
const { t, pair, isEn } = useLocale(); const { preview } = useWorkspace(); const user = useUserStore()
const emit = defineEmits(['done'])
const visible = ref(false); const saving = ref(false); const error = ref(null); const contract = ref(null); const values = reactive({}); const fields = ref([]); const options = reactive({}); const formRef = ref(); const optionsLoading = ref(false); const result = ref(null); const showingResult = ref(false); const hint = ref('')
const uploadFields = { avatarUrl: 'avatar', planImageUrl: 'seat', coverUrl: 'video', certificateUrl: 'honor', attachFileId: 'budget', voucherFileId: 'budget' }
const dialogTitle = computed(() => isEn.value ? `${t(contract.value?.method === 'get' ? 'detail' : 'confirm')} · ${contract.value?.id || ''}` : contract.value?.name || '')
const validation = f => [{ required: f.required, message: t('required'), trigger: ['blur','change'], validator: (_, value, cb) => f.required && (value === undefined || value === null || value === '' || Array.isArray(value) && !value.length && f.key !== 'permCodes') ? cb(new Error(t('required'))) : cb() }]
const maxLength = f => { const match = f.description.match(/(?:最长|最多)\s*(\d+)\s*(?:字|位)/); return match ? Number(match[1]) : undefined }
function idOf(row) { return Object.entries(row).find(([key]) => /^(id|userId|roleId|accountId|periodId|itemId|applyId|expenseId|assetId|borrowId|inventoryId|seatId|swapId|alumniId|recordId|planId|scheduleId|noticeId|videoId|messageId|codeId|inviteCodeId)$/.test(key))?.[1] }
async function open(id, row = {}, defaults = {}) {
  contract.value = contracts[id]; error.value = null; result.value = null; showingResult.value = false; hint.value = ''; Object.keys(values).forEach(k => delete values[k]); Object.keys(options).forEach(k => delete options[k]);
  fields.value = contract.value.fields.filter(f => !f.parent)
  for (const f of fields.value) {
    let value = defaults[f.key] ?? row[f.key]
    if (value === undefined && f.location === 'path') value = idOf(row)
    if (f.key === 'messageIds' && row.messageId) value = [row.messageId]
    if (value === undefined && f.key === 'status' && contract.value.method === 'post' && /1-10|1-17/.test(id)) value = 1
    if (value === undefined && f.type === 'array' && f.required) value = []
    if (typeFor(f) === 'json') value = value === undefined ? '' : JSON.stringify(value, null, 2)
    values[f.key] = value
  }
  visible.value = true; optionsLoading.value = true
  try {
    const lookup = async (key, api, idKey, labelKey, params = {}) => { if (!fields.value.some(f => f.key === key)) return; try { const data = normalizedList(await invoke(api, params)); options[key] = data.map(x => ({ value: x[idKey], label: x[labelKey] || String(x[idKey]) })); if (typeFor(fields.value.find(f => f.key === key)) === 'json' && typeof values[key] === 'string') values[key] = JSON.parse(values[key] || '[]') } catch { hint.value = pair('部分选项暂不可用，可按接口字段填写。', 'Some options are unavailable. Enter the documented values directly.') } }
    if (!preview.value) {
      await Promise.all([
        lookup('roleIds','1-15','roleId','roleName'),
        lookup('periodId', user.canAdmin ? '3-10' : '3-01','periodId','title',{pageSize:100}), lookup('areaId',user.canAdmin ? '5-07':'5-01','areaId','name'),
        ...fields.value.filter(f => fieldDictionaries[f.key] && !(f.key === 'bizType' && !id.startsWith('8-1'))).map(async f => { try { options[f.key] = await loadDict(fieldDictionaries[f.key]) } catch { /* Keep manual field input available. */ } })
      ])
    }
    if (fields.value.some(f => f.key === 'auditStatus' || f.key === 'handleStatus')) for (const key of ['auditStatus','handleStatus']) options[key] = [{value:1,label:t('approved')},{value:2,label:t('rejected')}]
    for (const key of ['isPublic','isShow','isTop','publish','enabled','tracked','overwrite','skipWeekend','fullSync','all']) if (fields.value.some(f => f.key === key)) options[key] = [{value:1,label:pair('是','Yes')},{value:0,label:pair('否','No')}]
    const statusDict = { '3-14': 'budgetPeriodStatus', '4-13': 'assetStatus', '5-17': 'seatStatus', '7-18': 'dutyStatus' }[id]
    if (statusDict && !preview.value) { try { options.status = (await loadDict(statusDict)).filter(x => !(id === '4-13' && x.value === 4 || id === '5-17' && x.value === 2 || id === '7-18' && x.value === 3)) } catch { /* Keep numeric entry if lookup fails. */ } }
    if (['1-10','1-11','1-12','1-17','1-18'].includes(id)) options.status = [{value:1,label:t('enabled')},{value:0,label:t('disabled')}]
    if (fields.value.some(f => f.key === 'borrowType')) options.borrowType = [{value:1,label:pair('借用（需归还）','Borrow (return required)')},{value:2,label:pair('领用','Issue (no return)')}]
    if (fields.value.some(f => f.key === 'gender')) options.gender = [{value:0,label:pair('未知','Unknown')},{value:1,label:pair('男','Male')},{value:2,label:pair('女','Female')}]
    if (fields.value.some(f => f.key === 'platform') && !preview.value) { try { const platforms = await invoke('2-01'); options.platform = normalizedList(platforms).map(x=>({value:x.code || x.platform,label:x.name || x.platformName || x.code})) } catch { /* Manual platform code remains available. */ } }
    if (fields.value.some(f => f.key === 'permCodes') && !preview.value) { try { const tree = await invoke('1-21'); const flatten = nodes => nodes.flatMap(n => n.code ? [{value:n.code,label:`${n.label} · ${n.code}`}] : flatten(n.children || [])); options.permCodes = flatten(tree); if (typeof values.permCodes === 'string') values.permCodes = JSON.parse(values.permCodes || '[]') } catch { /* JSON permission codes remain editable. */ } }
    if (fields.value.some(f => f.key === 'dataScope')) options.dataScope = [{value:1,label:pair('全部数据','All records')},{value:2,label:pair('本人数据','Own records')}]
  } finally { optionsLoading.value = false }
}
async function submit() {
  if (preview.value || !await formRef.value.validate().catch(() => false)) return
  saving.value = true; error.value = null
  try {
    const payload = { ...values }
    for (const f of fields.value) {
      const value = payload[f.key]
      if (f.required && /^(title|name|realName|username|password)$/.test(f.key) && typeof value === 'string' && !value.trim()) throw new Error(t('required'))
      if (/^(amount|totalAmount|budgetAmount|approvedAmount|price)$/.test(f.key) && value !== undefined && value < 0) throw new Error(pair('金额不能为负数','Amounts cannot be negative'))
    }
    if (contract.value.id === '1-07' && (payload.newPassword !== payload.confirmPassword || !/^(?=.*[A-Za-z])(?=.*\d).{6,32}$/.test(payload.newPassword || ''))) throw new Error(t('passwordHint'))
    for (const f of fields.value) if (typeFor(f) === 'json' && typeof payload[f.key] === 'string' && payload[f.key]) { try { payload[f.key] = JSON.parse(payload[f.key]); if (f.type === 'array' && !Array.isArray(payload[f.key]) || f.type === 'object' && (typeof payload[f.key] !== 'object' || Array.isArray(payload[f.key]) || payload[f.key] === null)) throw new Error('type') } catch { throw new Error(`${labelFor(f.key, isEn.value)}: ${t('jsonHint')}`) } }
    const response = await invoke(contract.value.id, payload)
    emit('done', response)
    if (response && typeof response === 'object' && Object.keys(response).length) { result.value = response; showingResult.value = true }
    else { ElMessage.success(t('saved')); visible.value = false }
  } catch (e) { error.value = e }
  finally { saving.value = false }
}
defineExpose({ open })
</script>
