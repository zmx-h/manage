<template>
  <div
    v-if="page"
    class="resource-page"
  >
    <div class="page-heading">
      <div><span class="eyebrow">{{ page.group.toUpperCase() }} / {{ side === 'admin' ? 'MANAGEMENT' : 'MY WORKSPACE' }}</span><h1>{{ pair(page.title, page.en) }}<span class="greeting-dot">.</span></h1><p>{{ t(page.group === 'members' ? 'membersHint' : 'resourceHint') }}</p></div><div class="heading-actions">
        <el-button
          v-for="id in createActions"
          :key="id"
          :type="id === createActions[0] ? 'primary' : 'default'"
          @click="execute(id)"
        >
          <AppIcon
            :name="contracts[id].method === 'get' ? 'file' : 'plus'"
            :size="16"
          />{{ actionName(id) }}
        </el-button>
      </div>
    </div>
    <div
      v-if="tabs.length > 1"
      class="page-tabs"
    >
      <button
        v-for="tab in tabs"
        :key="tab.id"
        :class="{ active: activeApi === tab.id }"
        @click="changeTab(tab.id)"
      >
        {{ pair(tab.zh, tab.en) }}
      </button>
    </div>
    <section class="panel resource-panel">
      <div class="resource-toolbar">
        <div class="toolbar-title">
          <AppIcon
            :name="page.icon"
            :size="19"
          /><strong>{{ pair(page.title, page.en) }}</strong><span
            v-if="data"
            class="count-badge"
          >{{ total }}</span>
        </div><div class="toolbar-controls">
          <el-input
            v-if="supportsKeyword"
            v-model="query.keyword"
            :placeholder="t('keyword')"
            clearable
            @keyup.enter="search"
            @clear="search"
          >
            <template #prefix>
              <AppIcon
                name="search"
                :size="16"
              />
            </template>
          </el-input><el-button
            :class="{ 'filter-active': showFilters }"
            @click="showFilters = !showFilters"
          >
            <AppIcon
              name="layers"
              :size="16"
            />{{ t('filter') }}
          </el-button><button
            class="icon-button"
            :disabled="loading"
            :aria-label="t('refresh')"
            @click="reload"
          >
            <AppIcon
              name="refresh"
              :size="18"
            />
          </button>
        </div>
      </div>
      <el-form
        v-if="showFilters"
        :model="query"
        label-position="top"
        class="filter-form"
        @submit.prevent="search"
      >
        <el-form-item
          v-for="f in filterFields"
          :key="f.key"
          :label="activeApi === '2-11' && f.key === 'userId' ? pair('成员','Member') : labelFor(activeApi === '3-03' && f.key === 'id' ? 'periodId' : f.key, isEn)"
        >
          <el-select
            v-if="filterOptions[f.key]"
            v-model="query[f.key]"
            clearable
            :placeholder="activeApi === '2-11' && f.key === 'userId' ? pair('当前登录账号','Signed-in account') : t('all')"
          >
            <el-option
              v-for="option in filterOptions[f.key]"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select><el-date-picker
            v-else-if="f.key === 'month'"
            v-model="query.month"
            type="month"
            value-format="YYYY-MM"
          />
          <el-date-picker
            v-else-if="f.key === 'year'"
            v-model="query[f.key]"
            type="year"
            value-format="YYYY"
          />
          <el-date-picker
            v-else-if="/Time$|Date$/.test(f.key)"
            v-model="query[f.key]"
            :type="/Time$/.test(f.key) ? 'datetime' : 'date'"
            :value-format="/Time$/.test(f.key) ? 'YYYY-MM-DD HH:mm:ss' : 'YYYY-MM-DD'"
          /><el-input-number
            v-else-if="['integer','number'].includes(f.type)"
            v-model="query[f.key]"
            :controls="false"
          /><el-input
            v-else
            v-model="query[f.key]"
            clearable
          />
        </el-form-item><div class="filter-buttons">
          <el-button @click="reset">
            {{ t('reset') }}
          </el-button><el-button
            type="primary"
            native-type="submit"
          >
            {{ t('searchAction') }}
          </el-button>
        </div>
      </el-form>
      <div
        v-loading="loading"
        class="resource-body"
      >
        <ResourceState
          v-if="!loading && (error || !rows.length && !objectData)"
          :error="error"
          @retry="reload"
        />
        <template v-else-if="activeApi === '1-21'">
          <el-tree
            :data="rows"
            :props="{label:'label',children:'children'}"
            default-expand-all
            class="permission-tree"
          >
            <template #default="{data:node}">
              <span>{{ node.label }}</span><code v-if="node.code">{{ node.code }}</code>
            </template>
          </el-tree>
        </template>
        <template v-else-if="activeApi === '7-03' && data?.days">
          <el-calendar v-model="calendarDate">
            <template #date-cell="{data:day}">
              <div
                class="calendar-cell"
                :class="{mine:calendarDay(day.day)?.isMine}"
              >
                <b>{{ day.day.slice(-2) }}</b><span
                  v-for="entry in calendarDay(day.day)?.duties || []"
                  :key="entry.scheduleId"
                >{{ entry.userName }} <small>{{ entry.groupName }}</small></span>
              </div>
            </template>
          </el-calendar>
        </template>
        <template v-else-if="['2-10','2-15'].includes(activeApi) && rows.length">
          <TrendChart
            :rows="rows"
            :label="t('monthCommits')"
          />
        </template>
        <template v-else-if="activeApi === '6-01'">
          <div class="alumni-grid">
            <article
              v-for="row in rows"
              :key="row.alumniId"
              class="alumni-card"
            >
              <div class="alumni-cover">
                <el-avatar
                  :size="64"
                  :src="safeUrl(row.avatarUrl)"
                >
                  {{ row.name?.slice(0,1) }}
                </el-avatar><span class="alumni-year">{{ row.graduationYear }}</span>
              </div><h3>{{ row.name }}</h3><p>{{ row.company || row.major }}</p><small>{{ row.position }}</small><el-button
                text
                type="primary"
                @click="execute('6-02', row)"
              >
                {{ t('detail') }} →
              </el-button>
            </article>
          </div>
        </template>
        <template v-else-if="activeApi === '8-15'">
          <div class="video-grid">
            <article
              v-for="row in rows"
              :key="row.videoId"
              class="video-card"
            >
              <video
                v-if="safeUrl(row.playUrl)"
                controls
                preload="none"
                :poster="safeUrl(row.coverUrl)"
                :src="safeUrl(row.playUrl)"
              /><h3>{{ row.title }}</h3><small>{{ t('mediaHint') }}</small>
            </article>
          </div>
        </template>
        <template v-else-if="activeApi === '8-05'">
          <div class="announcement-list">
            <article
              v-for="row in rows"
              :key="row.noticeId"
            >
              <span class="notice-type"><AppIcon
                name="file"
                :size="20"
              /></span><div><span class="eyebrow">{{ row.publishTime }}</span><h3>{{ row.title }}</h3><p>{{ plainText(row.content) }}</p></div>
            </article>
          </div>
        </template>
        <template v-else-if="activeApi === '8-20'">
          <div class="message-list">
            <article
              v-for="row in rows"
              :key="row.messageId"
              :class="{ unread: row.isRead === 0 }"
            >
              <span class="message-icon"><AppIcon
                :name="row.msgType === 1 ? 'calendar' : 'bell'"
                :size="20"
              /></span><div>
                <h3>
                  {{ row.title }}<i
                    v-if="row.isRead === 0"
                    class="tiny-dot"
                  />
                </h3><p>{{ plainText(row.content) }}</p><small>{{ row.createTime }}</small>
              </div><el-button
                v-if="row.isRead === 0"
                text
                type="primary"
                @click="markRead(row)"
              >
                {{ t('markRead') }}
              </el-button>
            </article>
          </div>
        </template>
        <template v-else-if="objectData">
          <div
            v-if="statCards.length"
            class="inline-stats"
          >
            <article
              v-for="card in statCards"
              :key="card.key"
            >
              <span>{{ labelFor(card.key, isEn) }}</span><strong>{{ card.value }}</strong>
            </article>
          </div><div
            v-if="activeApi === '2-11' && data?.cells"
            class="contribution-view"
          >
            <div class="heatmap">
              <div
                v-for="cell in data.cells"
                :key="cell.date"
                class="heatmap-cell"
                :class="`level-${cell.level}`"
                :title="`${cell.date}: ${cell.count}`"
              />
            </div><small>{{ user.canAdmin ? pair('可在筛选条件中指定成员；未选择时显示当前登录账号。','Pick a member in filters; defaults to the signed-in account.') : pair('提交记录由后台自动采集，无需手动同步。','Contributions are collected automatically. No manual sync needed.') }}</small>
          </div><DataDetails
            v-else
            :value="objectData"
          />
        </template>
        <template v-else>
          <el-table
            :data="rows"
            :row-key="primaryId"
            :tree-props="{children:'children'}"
            class="business-table"
            :row-class-name="() => 'business-row'"
          >
            <el-table-column
              v-for="column in columns"
              :key="column.key"
              :prop="column.key"
              :label="labelFor(column.key, isEn)"
              :min-width="column.key === 'remark' || column.key === 'title' ? 200 : 130"
              show-overflow-tooltip
            >
              <template #default="{ row }">
                <el-tag
                  v-if="column.key === 'status' || column.key === 'isRead'"
                  :type="statusType(row[column.key])"
                  effect="light"
                  round
                  size="small"
                >
                  {{ statusLabel(row[column.key]) }}
                </el-tag><span
                  v-else-if="typeof row[column.key] === 'number' && /Amount|Price|Value$/.test(column.key)"
                  class="number-cell"
                >{{ Number(row[column.key]).toLocaleString(isEn ? 'en-US' : 'zh-CN', {minimumFractionDigits:2}) }}</span><span v-else>{{ formatValue(row[column.key], column.key) }}</span>
              </template>
            </el-table-column><el-table-column
              v-if="rowActions.length"
              :label="t('actions')"
              width="160"
              fixed="right"
            >
              <template #default="{ row }">
                <el-button
                  text
                  type="primary"
                  @click="showDetails(row)"
                >
                  {{ t('detail') }}
                </el-button><el-dropdown
                  trigger="click"
                  @command="id => execute(id,row)"
                >
                  <el-button
                    text
                    type="primary"
                  >
                    {{ t('actions') }}⌄
                  </el-button><template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item
                        v-for="id in rowActions"
                        :key="id"
                        :command="id"
                        :disabled="!actionAllowed(id,row)"
                      >
                        {{ actionName(id) }}
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </div>
      <div class="resource-footer">
        <span>{{ total }} {{ t('count') }}<small class="contract-version">{{ activeApi }} · API v1.6</small></span><el-pagination
          v-if="contract?.path.endsWith('/page')"
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :page-sizes="[10,20,50,100]"
          :total="total"
          layout="prev, pager, next, sizes"
          small
          @current-change="reload"
          @size-change="search"
        />
      </div>
    </section>
    <div class="resource-footnote">
      <AppIcon
        name="shield"
        :size="14"
      /><span>{{ t('contractHint') }}</span><code>{{ contract?.method.toUpperCase() }} {{ contract?.path }}</code>
    </div>
    <ContractDialog
      ref="operation"
      @done="reload"
    />
    <el-drawer
      v-model="drawer"
      :title="t('details')"
      size="min(580px, 94vw)"
    >
      <DataDetails :value="selected" />
    </el-drawer>
  </div>
</template>
<script setup>
import { computed, defineAsyncComponent, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { contracts, invoke, normalizedList, plainText, safeUrl } from '@/api/client'
import { dictionaryTypes } from '@/config/navigation'
import { labelFor, fieldDictionaries } from '@/config/fields'
import { useUserStore } from '@/store/modules/user'
import { useWorkspace } from '@/composables/useWorkspace'
import { useResource } from '@/composables/useResource'
import { useLocale } from '@/locales'
import { loadDict } from '@/utils/dict'
import AppIcon from '@/components/common/AppIcon.vue'
import ResourceState from '@/components/common/ResourceState.vue'
import ContractDialog from '@/components/common/ContractDialog.vue'
import DataDetails from '@/components/common/DataDetails.vue'
const TrendChart = defineAsyncComponent(() => import('@/components/common/TrendChart.vue'))
const calendarDate = ref(new Date())
const { page, side } = useWorkspace(); const { t, pair, isEn } = useLocale(); const user = useUserStore()
const { data, error, loading, load } = useResource(); const activeApi = ref(''); const query = reactive({pageNum:1,pageSize:10}); const showFilters = ref(false); const filterOptions = reactive({}); const statusOptions = ref([]); const operation = ref(); const drawer = ref(false); const selected = ref(null)
const tabGroups = {
 'admin-repo':[['2-13','托管账户','Hosting accounts'],['2-14','贡献排行','Contribution ranking'],['2-15','提交趋势','Commit trends'],['2-11','贡献热力图','Contribution heatmap'],['2-16','采集日志','Collection logs']],
 'user-repo':[['2-03','绑定账户','Connected accounts'],['2-07','我的仓库','Repositories'],['2-09','提交记录','Commits'],['2-10','提交趋势','Commit trend'],['2-11','贡献热力图','Contribution heatmap'],['2-12','贡献概览','Overview'],['2-06','采集状态','Collection status']],
 'user-budget':[['3-01','周期公示','Published periods'],['3-04','支出公示','Published expenses'],['3-09','我的预算统计','My budget statistics']],
 'user-duty':[['7-01','值日安排','Schedule'],['7-02','我的值日','My duties'],['7-03','月历数据','Calendar']],
 'admin-asset-category':[['4-03','资产分类','Categories'],['4-16','资产流水','Asset history']],
 'admin-asset':[['4-08','资产台账','Asset register'],['4-24','资产盘点','Inventory checks'],['4-04','资产统计','Asset statistics']],
 'user-asset':[['4-01','资产台账','Asset register'],['4-04','资产统计','Asset statistics']],
 'admin-honor-alumni':[['6-05','学长学姐管理','Alumni'],['8-16','视频资源','Video library']],
 'user-honor':[['6-01','学长学姐展板','Alumni showcase'],['8-15','展示视频','Video library']],
 'admin-budget-period':[['3-10','预算周期','Budget periods'],['3-25','执行看板','Execution overview']],
 'admin-duty-plan':[['7-08','排班计划','Duty plans'],['7-20','提醒配置','Reminder settings']]
}
const tabActions = {
 '4-16':{create:['4-15'],actions:[]},
 '4-24':{create:['4-23','4-25'],actions:['4-26']},
 '8-16':{create:['8-17'],actions:['8-18','8-19']}
}
const tabs = computed(() => (tabGroups[page.value.key] || [[page.value.api,page.value.title,page.value.en]]).filter(([id]) => canUse(id)).map(([id,zh,en]) => ({id,zh,en})))
const contract = computed(() => contracts[activeApi.value])
const createActions = computed(() => {
 const config = activeApi.value === page.value.api ? { create: page.value.create } : tabActions[activeApi.value]
 return [...new Set(config?.create || [])].filter(canUse)
})
const rowActions = computed(() => {
 if (activeApi.value === '2-07') return ['2-08']
 if (activeApi.value === '2-03') return ['2-04','2-05']
 if (activeApi.value !== page.value.api) return (tabActions[activeApi.value]?.actions || []).filter(canUse)
 return page.value.actions.filter(canUse)
})
const filterFields = computed(() => (contract.value?.fields || []).filter(f => !f.parent && f.key !== 'keyword'))
const supportsKeyword = computed(() => contract.value?.fields.some(f => f.key === 'keyword') || contract.value?.path.endsWith('/page'))
const rows = computed(() => {
 if (Array.isArray(data.value) || Array.isArray(data.value?.list)) return normalizedList(data.value)
 if (activeApi.value === '7-02') return data.value?.upcoming || data.value?.schedules || []
 return []
})
const total = computed(() => data.value?.total ?? rows.value.length)
const objectData = computed(() => data.value && !Array.isArray(data.value) && !Array.isArray(data.value.list) ? data.value : null)
const statCards = computed(() => objectData.value ? Object.entries(objectData.value).filter(([,v]) => typeof v === 'number').map(([key,value])=>({key,value})) : [])
const columns = computed(() => {
 const defined = []; const seen = new Set()
 for (const f of contract.value?.returns || []) {
   if (['total','pageNum','pageSize','pages','list','children','accessToken','refreshToken'].includes(f.key) || ['object','array'].includes(f.type) || seen.has(f.key) || /url$/i.test(f.key)) continue
   if (rows.value.length && !rows.value.some(r => f.key in r)) continue
   seen.add(f.key); defined.push(f)
 }
 return defined.slice(0,9)
})
function canUse(id) { const c = contracts[id]; return c && (!c.auth || user.hasPerm(c.permission)) }
function actionName(id) { const c = contracts[id]; if (!isEn.value) return c.name; if (c.method === 'delete') return t('remove'); if (/audit/.test(c.path)) return pair('审核','Review'); if (/cancel|withdraw/.test(c.path)) return t('cancel'); if (c.method === 'get') return id === '4-27' ? 'Export' : t('detail'); if (id === '8-22') return t('markRead'); return `${c.method === 'post' ? t('add') : t('edit')} · ${id}` }
function actionAllowed(id,row) { if(id === '6-19') return [1,2].includes(row.status); if(id === '7-04') return row.userId === user.userId && row.status === 0; if(id === '4-07') return [1,4].includes(row.status) && row.borrowType !== 2; if(['5-22','7-07'].includes(id)) return row.targetUserId === user.userId && row.status === 0; if(id === '1-34') return [0,2].includes(row.status); if(id === '1-35') return [1,3,5].includes(row.status); if (/cancel|withdraw/.test(contracts[id].path)) return row.status === 0; if (/audit/.test(contracts[id].path)) return id === '5-24' ? row.status === 1 : row.status === 0; return true }
function formatValue(value, key) { if (key === 'usedCount') return value ? pair('已使用','Used') : pair('未使用','Unused'); if (value === null || value === undefined) return '—'; if (Array.isArray(value)) return value.map(v => typeof v === 'object' ? v.label || v.roleName || '' : v).join(', '); if (typeof value === 'object') return plainText(JSON.stringify(value)); return plainText(value) }
function statusLabel(value) { return statusOptions.value.find(x => x.value === value)?.label ?? String(value ?? '—') }
function calendarDay(date) { return data.value?.days?.find(day=>day.date===date) }
watch(calendarDate,value=>{ if(activeApi.value !== '7-03')return; const month=`${value.getFullYear()}-${String(value.getMonth()+1).padStart(2,'0')}`; if(month !== query.month){query.month=month; reload()} })
function statusType(value) { if (value === 1) return 'success'; if (value === 2) return 'warning'; if (value >= 3) return 'info'; return 'primary' }
function reload() {
  const missing = filterFields.value.find(f=>f.required && (query[f.key] === undefined || query[f.key] === null || query[f.key] === ''))
  if (missing) { data.value = null; error.value = null; showFilters.value = true; return }
  return load(activeApi.value, query)
}
function search() { query.pageNum = 1; reload() }
function reset() { Object.keys(query).forEach(k=>delete query[k]); Object.assign(query,{pageNum:1,pageSize:10}); showFilters.value = filterFields.value.some(f=>f.required); reload() }
function changeTab(id) { activeApi.value = id; reset(); if (id === '2-11' && user.canAdmin) showFilters.value = true; if (id === '7-03') { query.month = `${calendarDate.value.getFullYear()}-${String(calendarDate.value.getMonth()+1).padStart(2,'0')}`; reload() } loadOptions() }
function showDetails(row) { selected.value = row; drawer.value = true }
function primaryId(row) { const first = contracts[page.value.api].returns.find(f=>/Id$/.test(f.key) && f.type === 'integer'); return row[first?.key] ?? row.id ?? row.code ?? row.module ?? row.date ?? row.name }
async function execute(id,row = {}) {
 if (id === '4-27') { try { const response = await invoke(id,query,{responseType:'blob'}); const url = URL.createObjectURL(response.data); const anchor = document.createElement('a'); anchor.href=url; const disposition = response.headers['content-disposition'] || ''; const match=disposition.match(/filename\*=UTF-8''([^;]+)|filename="?([^";]+)/i); anchor.download = match ? decodeURIComponent(match[1]||match[2]) : 'assets.xlsx'; anchor.click(); URL.revokeObjectURL(url) } catch(e) { ElMessage.error(e.message) } return }
 const c = contracts[id]; const defaults = {}; const identity=primaryId(row)
 const entityKey = contracts[page.value.api].returns.find(f=>/Id$/.test(f.key) && f.type === 'integer')?.key
 for (const f of c.fields.filter(f=>!f.parent)) if (f.location === 'path' || f.key === entityKey && f.required && row[f.key] === undefined && ['put','delete'].includes(c.method)) defaults[f.key] = identity
 if (id === '4-25') delete defaults.id
 if (id === '4-05') defaults.assetId = row.assetId
 if (['1-11','4-11'].includes(id)) { try { row = await invoke(id === '1-11' ? '1-09' : '4-09', { id: identity }) } catch (e) { ElMessage.error(e.message); return } }
 if (id === '8-22' && !row.messageId) defaults.all = 1
 if (id === '7-05') defaults.scheduleId = row.scheduleId
 if (id === '1-34') defaults.passScore = 60
 if (id === '1-35') {
   try {
     await ElMessageBox.confirm(pair('将重新生成注册码并发送邮件，原注册码立即失效。', 'A new code will be generated and emailed; the old code expires immediately.'), actionName(id), { confirmButtonText:t('confirm'), cancelButtonText:t('cancel'), type:'warning' })
     const result = await invoke(id, { ...defaults, id: identity })
     if (result?.code) await ElMessageBox.alert(pair(`邮件发送失败，请线下告知注册码：${result.code}`, `Email failed. Share this code offline: ${result.code}`), pair('注册码', 'Registration code'), { confirmButtonText:t('confirm') })
     else if (result?.mailSent === false) ElMessage.warning(result.mailError || pair('邮件发送失败，请稍后重试', 'Email failed, try again later'))
     else ElMessage.success(pair('注册码已发送', 'Registration code sent'))
     reload()
   } catch (e) { if (e !== 'cancel' && e !== 'close') ElMessage.error(e.message) }
   return
 }
 if (c.method === 'delete' || /cancel|withdraw|\/finish$/.test(c.path)) {
   try { await ElMessageBox.confirm(t('deleteConfirm'), actionName(id), {confirmButtonText:t('confirm'),cancelButtonText:t('cancel'),type:'warning'}); await invoke(id,{...row,...defaults}); ElMessage.success(t('saved')); reload() } catch(e) { if (e !== 'cancel' && e !== 'close') ElMessage.error(e.message) } return
 }
 if (c.method === 'get' && c.fields.every(f=>f.location === 'path')) {
   try { selected.value = await invoke(id,{...row,...defaults}); drawer.value=true } catch(e) { ElMessage.error(e.message) } return
 }
 operation.value.open(id,row,defaults)
}
async function markRead(row) { try { await invoke('8-22',{messageIds:[row.messageId]}); reload() } catch(e) { ElMessage.error(e.message) } }
async function loadOptions() {
 Object.keys(filterOptions).forEach(k=>delete filterOptions[k]); statusOptions.value=[]
 const statusType=dictionaryTypes[activeApi.value]
 if (activeApi.value === '3-03') { try { filterOptions.id = normalizedList(await invoke(user.canAdmin ? '3-10':'3-01',{pageSize:100})).map(x=>({value:x.periodId,label:x.title})) } catch { /* Keep period ID filter available. */ } }
 if (activeApi.value === '2-11' && user.canAdmin) { try { filterOptions.userId = normalizedList(await invoke('1-08',{pageSize:100,status:1})).map(x=>({value:x.userId,label:x.realName ? `${x.realName}${x.studentNo ? ` · ${x.studentNo}` : ''}` : x.username})) } catch { /* Fall back to numeric user ID input. */ } }
 if (statusType) { try { statusOptions.value=await loadDict(statusType); filterOptions.status=statusOptions.value } catch { /* Preserve manual filters when dictionaries are unavailable. */ } }
 await Promise.all(filterFields.value.filter(f=>fieldDictionaries[f.key]).map(async f=>{ try { filterOptions[f.key]=await loadDict(fieldDictionaries[f.key]) } catch { /* Manual value entry remains available. */ } }))
}
watch(() => page.value?.key, () => { activeApi.value=page.value.api; reset(); loadOptions() }, {immediate:true})
</script>
