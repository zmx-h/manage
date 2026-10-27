<template>
  <div class="dashboard-page">
    <div class="page-heading">
      <div><span class="eyebrow">{{ side === 'admin' ? 'CENTER OVERVIEW' : 'YOUR WORKSPACE' }}</span><h1>{{ t('welcome') }}<span class="greeting-name">{{ user.realName ? `，${user.realName}` : '' }}</span><span class="greeting-dot">.</span></h1><p>{{ t('overview') }}</p></div><div class="date-pill">
        <AppIcon
          name="calendar"
          :size="17"
        />{{ date }}
      </div>
    </div>
    <section class="welcome-banner">
      <div>
        <span class="banner-badge"><i />{{ t('brand') }}</span><h2>{{ pair('把日常理顺，让创造发生。', 'Less busywork. More possibility.') }}</h2><p>{{ pair('从今天的值日到下一次突破，你的中心事务，尽在此处。', 'From everyday duties to your next breakthrough. Everything, in one place.') }}</p><router-link
          :to="quickActions[0] ? link(quickActions[0]) : '#'"
          class="banner-link"
        >
          {{ t('allModules') }}<AppIcon
            name="arrow"
            :size="17"
          />
        </router-link>
      </div><div
        class="banner-visual"
        aria-hidden="true"
      >
        <div class="visual-ring ring-one" /><div class="visual-ring ring-two" /><div class="visual-cube">
          <AppIcon
            name="layers"
            :size="66"
          />
        </div><div class="visual-chip chip-check">
          <AppIcon
            name="check"
            :size="24"
          />
        </div><div class="visual-chip chip-code">
          <AppIcon
            name="code"
            :size="27"
          />
        </div><span class="visual-spark">✳</span>
      </div>
    </section>
    <el-alert
      v-if="error"
      :title="error.unimplemented ? t('unavailable') : t('requestFailed')"
      :description="error.code === 0 ? t('networkError') : t('unavailableHint')"
      type="warning"
      :closable="false"
      show-icon
    />
    <div
      v-loading="loading"
      class="stats-grid"
    >
      <article
        v-for="stat in stats"
        :key="stat.label"
        class="stat-card"
      >
        <div class="stat-top">
          <span>{{ t(stat.label) }}</span><span
            class="stat-icon"
            :class="stat.color"
          ><AppIcon
            :name="stat.icon"
            :size="19"
          /></span>
        </div><strong>{{ stat.value ?? '—' }}</strong><div class="stat-foot">
          <span class="mini-line" />{{ data ? t('connected') : t('notConfigured') }}
        </div>
      </article>
    </div>
    <div class="dashboard-grid">
      <section class="panel quick-panel">
        <div class="panel-heading">
          <h3>{{ t('quickLinks') }}</h3><span class="panel-index">01 / WORKFLOW</span>
        </div><div class="quick-grid">
          <router-link
            v-for="item in quickActions"
            :key="item.key"
            :to="link(item)"
            class="quick-card"
          >
            <span class="quick-icon"><AppIcon
              :name="item.icon"
              :size="22"
            /></span><strong>{{ pair(item.title, item.en) }}</strong><AppIcon
              name="arrow"
              :size="16"
            />
          </router-link>
        </div>
      </section>
      <section class="panel budget-panel">
        <div class="panel-heading">
          <h3>{{ t(side === 'admin' ? 'budgetOverview' : 'mySeat') }}</h3><span class="soft-badge">{{ t('today') }}</span>
        </div><template v-if="side === 'admin'">
          <div class="budget-display">
            <el-progress
              type="circle"
              :percentage="budgetPercent"
              :width="126"
              :stroke-width="9"
              color="#6278ef"
            >
              <template #default>
                <strong>{{ data?.budgetStat ? budgetPercent + '%' : '—' }}</strong><small>{{ t('executionRate') }}</small>
              </template>
            </el-progress><div><small>{{ data?.budgetStat?.periodTitle || t('notConfigured') }}</small><p>{{ t('totalAmount') }}<b>{{ money(data?.budgetStat?.totalAmount) }}</b></p><p>{{ t('usedAmount') }}<b>{{ money(data?.budgetStat?.usedAmount) }}</b></p></div>
          </div>
        </template><template v-else>
          <div class="my-seat-card">
            <span class="seat-illustration"><AppIcon
              name="seat"
              :size="50"
            /></span><div><small>{{ data?.mySeat?.areaName || t('seat') }}</small><strong>{{ data?.mySeat?.seatNo || t('noSeat') }}</strong></div>
          </div><router-link
            :to="link(pages.find(p => p.key === 'user-seat'))"
            class="subtle-link"
          >
            {{ t('viewAll') }} →
          </router-link>
        </template>
      </section>
      <section class="panel todo-panel">
        <div class="panel-heading">
          <h3>{{ t(side === 'admin' ? 'pending' : 'nextDuty') }}</h3><AppIcon
            name="clock"
            :size="18"
          />
        </div><template v-if="side === 'admin'">
          <div
            v-if="todos.length"
            class="todo-list"
          >
            <router-link
              v-for="todo in todos"
              :key="todo.key"
              :to="link(todo.page)"
            >
              <span class="todo-indicator" /><span>{{ t(todo.key) }}</span><b>{{ todo.count }}</b><AppIcon
                name="chevron"
                :size="14"
              />
            </router-link>
          </div><div
            v-else
            class="compact-empty"
          >
            <span class="done-icon"><AppIcon
              name="check"
              :size="23"
            /></span><b>{{ data ? t('noPending') : t('waiting') }}</b><small>{{ data ? t('greeting') : t('notConfigured') }}</small>
          </div>
        </template><template v-else>
          <div
            v-if="data?.nextDuty"
            class="next-duty"
          >
            <span class="date-block">{{ data.nextDuty.dutyDate?.slice(-2) }}</span><div><b>{{ data.nextDuty.dutyDate }}</b><p>{{ data.nextDuty.groupName }} · {{ data.nextDuty.taskContent }}</p></div>
          </div><div
            v-else
            class="compact-empty"
          >
            <AppIcon
              name="calendar"
              :size="32"
            /><b>{{ t('noDuty') }}</b><small>{{ t('greeting') }}</small>
          </div>
        </template>
      </section>
      <section class="panel notice-panel">
        <div class="panel-heading">
          <h3>{{ t(side === 'admin' ? 'dutyToday' : 'activity') }}</h3><router-link
            :to="link(pages.find(p => p.key === (side === 'admin' ? 'admin-duty-schedule' : 'user-notice')))"
            class="subtle-link"
          >
            {{ t('viewAll') }} ↗
          </router-link>
        </div><div
          v-if="side === 'admin' && data?.todayDuty?.length"
          class="today-duty-list"
        >
          <div
            v-for="person in data.todayDuty"
            :key="person.userId"
          >
            <span class="avatar small">{{ person.realName?.slice(0,1) }}</span><div><strong>{{ person.realName }}</strong><small>{{ person.groupName }}</small></div><el-tag
              :type="person.status === 1 ? 'success' : 'info'"
              size="small"
            >
              {{ person.status === 1 ? pair('已完成','Completed') : pair('待完成','Scheduled') }}
            </el-tag>
          </div>
        </div><div
          v-else-if="data?.topNotices?.length"
          class="notice-list"
        >
          <div
            v-for="notice in data.topNotices"
            :key="notice.noticeId"
          >
            <AppIcon
              name="file"
              :size="18"
            /><b>{{ notice.title }}</b><small>{{ notice.publishTime?.slice(0,10) }}</small>
          </div>
        </div><div
          v-else
          class="compact-empty horizontal"
        >
          <span class="empty-mini"><AppIcon
            :name="side === 'admin' ? 'calendar' : 'file'"
            :size="28"
          /></span><div><b>{{ t(side === 'admin' ? 'noDuty' : 'noNotice') }}</b><small>{{ t('greeting') }}</small></div>
        </div>
      </section>
    </div>
    <div class="connection-strip">
      <span
        class="tiny-dot"
        :class="{ live: data }"
      /><span>{{ t('service') }}</span><strong>{{ preview ? t('safePreview') : data ? t('connected') : t('waiting') }}</strong><button
        :disabled="preview"
        @click="reload"
      >
        {{ t('refresh') }}<AppIcon
          name="refresh"
          :size="13"
        />
      </button>
    </div>
  </div>
</template>
<script setup>
import { computed, watch } from 'vue'
import { useLocale } from '@/locales'
import { useWorkspace } from '@/composables/useWorkspace'
import { useResource } from '@/composables/useResource'
import { useUserStore } from '@/store/modules/user'
import { pages } from '@/config/navigation'
import AppIcon from '@/components/common/AppIcon.vue'
const { t, pair, isEn } = useLocale(); const { preview, side, link } = useWorkspace(); const user = useUserStore(); const { data, loading, error, load } = useResource()
const date = computed(() => new Intl.DateTimeFormat(isEn.value ? 'en-US' : 'zh-CN',{ month:'long', day:'numeric', weekday:'short', timeZone:'Asia/Shanghai' }).format(new Date()))
const quickActions = computed(() => (side.value === 'admin' ? ['admin-system-user','admin-budget-apply','admin-asset','admin-seat-plan','admin-duty-plan','admin-honor-audit'] : ['user-budget-apply','user-asset','user-seat','user-duty','user-repo','user-message']).map(key => pages.find(p => p.key === key)).filter(p => preview.value || user.hasPerm(p.permission)))
const stats = computed(() => side.value === 'admin' ? [{label:'totalMembers',value:data.value?.userCount,icon:'users',color:'blue'},{label:'totalAssets',value:data.value?.assetStat?.total,icon:'box',color:'amber'},{label:'usedBudget',value:data.value?.budgetStat ? money(data.value.budgetStat.usedAmount) : undefined,icon:'wallet',color:'purple'},{label:'monthCommits',value:data.value?.repoCommitCountThisMonth,icon:'code',color:'green'}] : [{label:'monthCommits',value:data.value?.commitCountThisMonth,icon:'code',color:'blue'},{label:'borrowing',value:data.value?.borrowingCount,icon:'box',color:'amber'},{label:'pendingBudget',value:data.value?.budgetApplyPending,icon:'wallet',color:'purple'},{label:'unread',value:data.value?.unreadMsgCount,icon:'bell',color:'green'}])
const budgetPercent = computed(() => Math.min(100,Math.max(0,Number(data.value?.budgetStat?.executionRate || 0))))
const todoPages = {budgetApply:'admin-budget-apply',assetBorrow:'admin-asset-borrow',seatApply:'admin-seat-apply',seatSwap:'admin-seat-swap',honorAudit:'admin-honor-audit',registerApply:'admin-system-register'}
const todos = computed(() => Object.entries(data.value?.pendingTodo || {}).filter(([key,count]) => count > 0 && todoPages[key]).map(([key,count]) => ({key,count,page:pages.find(p => p.key === todoPages[key])})).filter(item => preview.value || user.hasPerm(item.page.permission)))
function money(value) { return value === undefined ? '—' : new Intl.NumberFormat(isEn.value ? 'en-US' : 'zh-CN',{style:'currency',currency:'CNY',maximumFractionDigits:2}).format(value) }
const reload = () => load(side.value === 'admin' ? '8-11' : '8-10', {}, preview.value)
watch([side,preview], reload, {immediate:true})
</script>
