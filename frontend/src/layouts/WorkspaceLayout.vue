<template>
  <div
    class="workspace-shell"
    :class="{ 'sidebar-collapsed': app.sidebarCollapsed, 'menu-open': app.mobileMenu }"
  >
    <button
      v-if="app.mobileMenu"
      class="mobile-scrim"
      aria-label="Close menu"
      @click="app.mobileMenu = false"
    />
    <aside class="sidebar">
      <router-link
        :to="link(sidePages[0])"
        class="brand"
      >
        <span class="brand-mark"><span /><span /><span /></span><span class="brand-copy"><strong>IECMP<span class="brand-dot">.</span></strong><small>{{ t('brand') }}</small></span>
      </router-link>
      <div class="workspace-switch">
        <span class="workspace-symbol"><AppIcon
          :name="side === 'admin' ? 'layers' : 'heart'"
          :size="19"
        /></span><span class="sidebar-text"><b>{{ t(side === 'admin' ? 'management' : 'member') }}</b><small>{{ t('platform') }}</small></span><span class="tiny-dot" />
      </div>
      <p class="nav-caption">
        {{ t('workspace') }}
      </p>
      <nav
        class="side-navigation"
        :aria-label="t('workspace')"
      >
        <template
          v-for="group in menuGroups"
          :key="group.key"
        >
          <router-link
            v-if="group.pages.length === 1"
            :to="link(group.pages[0])"
            class="nav-item"
            :class="{ active: page?.key === group.pages[0].key }"
            :title="title(group.pages[0])"
            @click="app.mobileMenu = false"
          >
            <AppIcon :name="group.pages[0].icon" /><span class="sidebar-text">{{ group.key === 'dashboard' ? t('dashboard') : title(group.pages[0]) }}</span>
          </router-link>
          <div
            v-else
            class="nav-group"
          >
            <button
              class="nav-item group-trigger"
              :class="{ 'group-active': page?.group === group.key }"
              :title="t(group.key)"
              @click="toggleGroup(group.key)"
            >
              <AppIcon :name="group.pages[0].icon" /><span class="sidebar-text">{{ t(group.key) }}</span><AppIcon
                class="group-chevron"
                :class="{ rotated: openGroups.has(group.key) }"
                name="chevron"
                :size="13"
              />
            </button>
            <div
              v-if="openGroups.has(group.key) && !app.sidebarCollapsed"
              class="nav-children"
            >
              <router-link
                v-for="item in group.pages"
                :key="item.key"
                :to="link(item)"
                :class="{ active: page?.key === item.key }"
                @click="app.mobileMenu = false"
              >
                <i />{{ title(item) }}
              </router-link>
            </div>
          </div>
        </template>
      </nav>
      <div class="sidebar-bottom">
        <div class="sidebar-note">
          <span class="tiny-dot" /><span class="sidebar-text">{{ preview ? t('safePreview') : 'IECMP · v1.0' }}</span>
        </div>
        <router-link
          :to="preview ? '/login' : '/profile'"
          class="sidebar-account"
        >
          <span class="avatar">{{ initial }}</span><span class="sidebar-text"><b>{{ preview ? t('preview') : user.realName || user.username }}</b><small>{{ preview ? t('loginNow') : user.username }}</small></span><AppIcon
            name="chevron"
            :size="16"
          />
        </router-link>
      </div>
    </aside>
    <div class="workspace-body">
      <header class="topbar">
        <div class="topbar-left">
          <button
            class="icon-button menu-toggle"
            :aria-label="t('workspace')"
            @click="toggleMenu"
          >
            <AppIcon name="menu" />
          </button><span class="breadcrumb-root">{{ t('workspace') }}</span><AppIcon
            name="chevron"
            :size="13"
          /><span class="breadcrumb-current">{{ page ? title(page) : t('dashboard') }}</span>
        </div>
        <div class="topbar-actions">
          <button
            class="search-trigger"
            @click="searchOpen = true"
          >
            <AppIcon
              name="search"
              :size="17"
            /><span>{{ t('search') }}</span><kbd>⌘ K</kbd>
          </button><PreferencesControl /><router-link
            :to="preview ? '/preview/user/message' : '/message'"
            class="icon-button"
            :aria-label="t('unread')"
          >
            <AppIcon name="bell" /><i
              v-if="unread > 0"
              class="notification-dot"
            />
          </router-link><span class="topbar-divider" /><el-dropdown
            trigger="click"
            @command="onAccount"
          >
            <button class="account-trigger">
              <span class="avatar small">{{ initial }}</span><span>{{ preview ? 'IECMP' : user.realName || user.username }}</span><AppIcon
                name="chevron"
                :size="12"
              />
            </button><template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-if="!preview"
                  command="profile"
                >
                  {{ t('profile') }}
                </el-dropdown-item><el-dropdown-item
                  v-if="preview || side === 'admin' || user.canAdmin"
                  command="switch"
                >
                  {{ t(side === 'admin' ? 'switchUser' : 'switchAdmin') }}
                </el-dropdown-item><el-dropdown-item
                  :divided="!preview"
                  command="logout"
                >
                  {{ t(preview ? 'loginNow' : 'logout') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      <main class="workspace-content">
        <div
          v-if="preview"
          class="preview-banner"
        >
          <AppIcon
            name="info"
            :size="17"
          /><span>{{ t('previewNotice') }}</span><router-link to="/login">
            {{ t('loginNow') }} ↗
          </router-link>
        </div>
        <router-view />
        <footer class="workspace-footer">
          <span>© {{ new Date().getFullYear() }} IECMP · {{ t('brand') }}</span><span>Built for ideas. Designed for people.</span>
        </footer>
      </main>
    </div>
    <el-dialog
      v-model="searchOpen"
      :title="t('searchHint')"
      width="min(560px, 92vw)"
      class="search-dialog"
    >
      <el-input
        v-model="searchText"
        :placeholder="t('search')"
        clearable
        autofocus
      /><div class="search-results">
        <button
          v-for="item in searchPages"
          :key="item.key"
          @click="go(item)"
        >
          <AppIcon :name="item.icon" /><span>{{ title(item) }}</span><small>{{ t(item.group) }}</small><AppIcon
            name="arrow"
            :size="16"
          />
        </button><el-empty
          v-if="!searchPages.length"
          :description="t('searchEmpty')"
          :image-size="70"
        />
      </div>
    </el-dialog>
  </div>
</template>
<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { pages, groups } from '@/config/navigation'
import { useWorkspace } from '@/composables/useWorkspace'
import { useAppStore } from '@/store/modules/app'
import { useUserStore } from '@/store/modules/user'
import { useLocale } from '@/locales'
import { invoke } from '@/api/client'
import AppIcon from '@/components/common/AppIcon.vue'
import PreferencesControl from '@/components/common/PreferencesControl.vue'
const app = useAppStore(); const user = useUserStore(); const router = useRouter(); const { t, pair } = useLocale()
const { preview, side, page, link } = useWorkspace()
const title = item => pair(item.title, item.en)
const sidePages = computed(() => pages.filter(p => p.side === side.value && (preview.value || user.hasPerm(p.permission))))
const menuGroups = computed(() => groups.map(key => ({ key, pages: sidePages.value.filter(p => p.group === key) })).filter(g => g.pages.length))
const openGroups = ref(new Set()); const searchOpen = ref(false); const searchText = ref(''); const unread = ref(0)
const initial = computed(() => (user.realName || user.username || 'I').slice(0, 1).toUpperCase())
const searchPages = computed(() => sidePages.value.filter(p => `${p.title} ${p.en}`.toLowerCase().includes(searchText.value.toLowerCase())))
watch(() => page.value?.group, group => { if (group) openGroups.value = new Set([...openGroups.value, group]) }, { immediate: true })
function toggleGroup(key) { if (app.sidebarCollapsed) app.sidebarCollapsed = false; const next = new Set(openGroups.value); next.has(key) ? next.delete(key) : next.add(key); openGroups.value = next }
function toggleMenu() { if (window.innerWidth <= 900) app.mobileMenu = !app.mobileMenu; else app.toggleSidebar() }
function go(item) { router.push(link(item)); searchOpen.value = false; searchText.value = '' }
function keyboard(e) { if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') { e.preventDefault(); searchOpen.value = !searchOpen.value } }
async function onAccount(command) {
  if (command === 'profile') return router.push('/profile')
  if (command === 'switch') return router.push(preview.value ? `/preview/${side.value === 'admin' ? 'user/home' : 'admin/dashboard'}` : side.value === 'admin' ? '/home' : '/admin')
  if (!preview.value) { try { await invoke('1-03') } catch (error) { ElMessage.warning(error.message) } user.reset() }
  router.push(side.value === 'admin' ? '/admin/login' : '/login')
}
let timer
async function fetchUnread() { if (preview.value) return; try { const result = await invoke('8-21'); unread.value = result?.total ?? 0 } catch { /* Badge failure must not block navigation. */ } }
onMounted(() => { window.addEventListener('keydown', keyboard); fetchUnread(); if (!preview.value) timer = window.setInterval(fetchUnread, 60000) })
onUnmounted(() => { window.removeEventListener('keydown', keyboard); if (timer) window.clearInterval(timer) })
</script>
