<template>
  <DashboardPage v-if="page && ['admin-dashboard','user-home'].includes(page.key)" />
  <ProfilePage v-else-if="page?.key === 'user-profile'" />
  <SeatPlanPage v-else-if="page && ['admin-seat-plan','user-seat'].includes(page.key)" />
  <ResourcePage v-else-if="page" />
  <div
    v-else
    class="panel"
  >
    <ResourceState />
  </div>
</template>
<script setup>
import { useWorkspace } from '@/composables/useWorkspace'
import DashboardPage from './DashboardPage.vue'
import ResourcePage from './ResourcePage.vue'
import SeatPlanPage from './SeatPlanPage.vue'
import ProfilePage from './ProfilePage.vue'
import ResourceState from '@/components/common/ResourceState.vue'
const { page } = useWorkspace()
</script>
