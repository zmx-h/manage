<template>
  <div class="seat-page">
    <div class="page-heading">
      <div><span class="eyebrow">SPACE / {{ side === 'admin' ? 'MANAGEMENT' : 'WORKSPACE' }}</span><h1>{{ t('seat') }}<span class="greeting-dot">.</span></h1><p>{{ mode === 'layout' ? t('layoutHint') : t('seatHint') }}</p></div><div class="heading-actions">
        <el-button
          v-if="side === 'admin' && can('seat:area:add')"
          :disabled="preview"
          @click="operation.open('5-08')"
        >
          {{ pair('新建区域','New area') }}
        </el-button><el-button
          v-if="side === 'admin' && can('seat:seat:add')"
          type="primary"
          :disabled="preview"
          @click="operation.open('5-11',{}, {areaId:activeAreaId})"
        >
          <AppIcon
            name="plus"
            :size="16"
          />{{ pair('新建工位','New seat') }}
        </el-button>
      </div>
    </div>
    <section class="panel">
      <div class="resource-toolbar">
        <el-select
          v-model="activeAreaId"
          :placeholder="t('selectArea')"
          class="area-select"
        >
          <el-option
            v-for="option in areas"
            :key="option.areaId"
            :label="option.name"
            :value="option.areaId"
          />
        </el-select><div class="toolbar-controls">
          <el-segmented
            v-if="side === 'admin'"
            v-model="mode"
            :options="[{label:t('seatMode'),value:'seat'},{label:t('layoutMode'),value:'layout',disabled:!can('seat:seat:edit')}]"
          /><el-button
            v-if="mode === 'layout'"
            type="primary"
            :disabled="preview || !dirty || !can('seat:seat:edit')"
            :loading="saving"
            @click="saveLayout"
          >
            {{ t('saveLayout') }}
          </el-button><el-button
            v-if="side === 'admin' && area && can('seat:area:edit')"
            :disabled="preview"
            @click="operation.open('5-09',area)"
          >
            {{ pair('区域设置','Area settings') }}
          </el-button><button
            class="icon-button"
            :disabled="preview"
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
      <div
        v-if="area"
        class="seat-legend"
      >
        <span
          v-for="(key,index) in ['free','occupied','reserved','repairing']"
          :key="key"
        ><i :class="`seat-status-${index+1}`" />{{ t(key) }}<b>{{ area.seats?.filter(s=>s.status===index+1).length }}</b></span>
      </div>
      <div
        v-loading="loading"
        class="seat-map-wrapper"
      >
        <ResourceState
          v-if="error || !area"
          :error="error"
          @retry="reload"
        /><div
          v-else
          ref="map"
          class="seat-map"
          :style="{aspectRatio:area.width && area.height ? `${area.width}/${area.height}` : '16/9',backgroundImage:safeUrl(area.planImageUrl) ? `url(&quot;${safeUrl(area.planImageUrl)}&quot;)` : undefined}"
          @dragover.prevent
          @drop.prevent="onMapDrop"
        >
          <button
            v-for="seat in area.seats"
            :key="seat.seatId"
            class="seat-node"
            :class="[`seat-status-${seat.status}`,{dragging:dragged?.seatId===seat.seatId}]"
            :style="seatStyle(seat)"
            :draggable="!preview && side === 'admin' && (mode === 'layout' ? can('seat:seat:edit') : Boolean(seat.occupantUserId) && can('seat:seat:assign'))"
            @dragstart="startDrag($event, seat)"
            @dragend="dragged=null"
            @dragover.prevent
            @drop.prevent.stop="mode === 'layout' ? onMapDrop($event) : dropSeat(seat)"
            @click="selectSeat(seat)"
          >
            <AppIcon
              name="seat"
              :size="19"
            /><strong>{{ seat.seatNo }}</strong><small>{{ seat.occupantName || t(['free','occupied','reserved','repairing'][seat.status-1] || 'unknown') }}</small>
          </button>
        </div>
      </div>
    </section><ContractDialog
      ref="operation"
      @done="reload"
    /><el-drawer
      v-model="detailOpen"
      :title="t('details')"
      size="min(460px,94vw)"
    >
      <DataDetails :value="selectedSeat" /><div class="drawer-actions">
        <template v-if="side === 'admin'">
          <el-button
            v-for="id in seatActions"
            :key="id"
            :disabled="preview"
            @click="seatAction(id)"
          >
            {{ isEn ? contracts[id].method.toUpperCase()+' · '+id : contracts[id].name }}
          </el-button>
        </template><el-button
          v-else
          type="primary"
          :disabled="preview || selectedSeat?.status!==1"
          @click="operation.open('5-04',{}, {seatId:selectedSeat?.seatId,areaId:activeAreaId})"
        >
          {{ t('apply') }}
        </el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script setup>
import { computed, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useLocale } from '@/locales'
import { useWorkspace } from '@/composables/useWorkspace'
import { useUserStore } from '@/store/modules/user'
import { useResource } from '@/composables/useResource'
import { contracts, invoke, normalizedList, safeUrl } from '@/api/client'
import AppIcon from '@/components/common/AppIcon.vue'
import ContractDialog from '@/components/common/ContractDialog.vue'
import ResourceState from '@/components/common/ResourceState.vue'
import DataDetails from '@/components/common/DataDetails.vue'
const { preview, side } = useWorkspace(); const { t, pair, isEn } = useLocale(); const user = useUserStore(); const { data, loading, error, load } = useResource()
const activeAreaId=ref(); const mode=ref('seat'); const operation=ref(); const map=ref(); const dragged=ref(null); const dirty=ref(false); const saving=ref(false); const detailOpen=ref(false); const selectedSeat=ref(null)
const can=permission=>preview.value || user.hasPerm(permission)
const areas=computed(()=>normalizedList(data.value))
const area=computed(()=>areas.value.find(x=>x.areaId===activeAreaId.value))
const seatActions=computed(()=>['5-12','5-15','5-16','5-17','5-14'].filter(id=>can(contracts[id].permission)))
watch(areas,value=>{ if (!value.some(x=>x.areaId===activeAreaId.value)) activeAreaId.value=value[0]?.areaId })
const reload=async()=>{ await load(side.value==='admin'?'5-25':'5-01',{},preview.value); dirty.value=false }
watch([side,preview],reload,{immediate:true})
function seatStyle(seat) { return {left:`${seat.xRatio}%`,top:`${seat.yRatio}%`,width:`${Math.max(seat.widthRatio || 8,3)}%`,height:`${Math.max(seat.heightRatio || 12,4)}%`,transform:`rotate(${seat.rotation || 0}deg)`} }
function startDrag(event,seat) { dragged.value=seat; event.dataTransfer.effectAllowed='move'; event.dataTransfer.setData('text/plain',String(seat.seatId)) }
function selectSeat(seat) { if (dragged.value) return; selectedSeat.value=seat; detailOpen.value=true }
async function dropSeat(target) {
 if (mode.value==='layout') return
 const source=dragged.value; dragged.value=null
 if (!source || source.seatId===target.seatId || preview.value || !can('seat:seat:assign')) return
 if ([3,4].includes(target.status)) { ElMessage.warning(t(target.status===3?'reserved':'repairing')); return }
 try { await ElMessageBox.confirm(`${source.seatNo} → ${target.seatNo}`,t('confirm'),{confirmButtonText:t('confirm'),cancelButtonText:t('cancel')}); await invoke('5-26',{fromSeatId:source.seatId,toSeatId:target.seatId}); ElMessage.success(t('saved')); await reload() } catch(e) { if(e!=='cancel' && e!=='close'){ ElMessage.error(e.message); await reload() } }
}
function onMapDrop(event) { if(mode.value!=='layout' || !dragged.value || preview.value) return; const rect=map.value.getBoundingClientRect(); const seat=dragged.value; seat.xRatio=Math.max(0,Math.min(100-seat.widthRatio,(event.clientX-rect.left)/rect.width*100-seat.widthRatio/2)); seat.yRatio=Math.max(0,Math.min(100-seat.heightRatio,(event.clientY-rect.top)/rect.height*100-seat.heightRatio/2)); dirty.value=true; dragged.value=null }
async function saveLayout() { if(preview.value || !area.value) return; saving.value=true; try { const seats=area.value.seats.map(({seatId,xRatio,yRatio,widthRatio,heightRatio,rotation})=>({seatId,xRatio,yRatio,widthRatio,heightRatio,rotation})); for(let i=0;i<seats.length;i+=200) await invoke('5-13',{areaId:activeAreaId.value,seats:seats.slice(i,i+200)}); dirty.value=false; ElMessage.success(t('saved')) } catch(e) { ElMessage.error(e.message) } finally { saving.value=false } }
function seatAction(id) { const row=selectedSeat.value; if(id==='5-14'){ ElMessageBox.confirm(t('deleteConfirm'),t('confirm'),{type:'warning'}).then(()=>invoke(id,{id:row.seatId})).then(()=>{detailOpen.value=false;reload()}).catch(e=>{if(e!=='cancel'&&e!=='close')ElMessage.error(e.message)}) } else operation.value.open(id,row,{id:row.seatId,seatId:row.seatId,areaId:activeAreaId.value}) }
</script>
