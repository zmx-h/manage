<template>
  <div
    ref="root"
    class="trend-chart"
    role="img"
    :aria-label="label"
  />
</template>
<script setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { init, use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import { SVGRenderer } from 'echarts/renderers'
import { useAppStore } from '@/store/modules/app'
use([LineChart,GridComponent,TooltipComponent,SVGRenderer])
const props=defineProps({rows:{type:Array,default:()=>[]},label:{type:String,default:'Contributions'}})
const root=ref(); const app=useAppStore(); let chart; let observer
function render(){if(!chart)return;chart.setOption({animation:false,tooltip:{trigger:'axis'},grid:{left:45,right:25,top:30,bottom:40},xAxis:{type:'category',boundaryGap:false,data:props.rows.map(x=>x.date),axisLine:{lineStyle:{color:app.dark?'#374258':'#e7ebf3'}},axisLabel:{color:'#8995ad',fontSize:10},axisTick:{show:false}},yAxis:{type:'value',minInterval:1,splitLine:{lineStyle:{color:app.dark?'#293345':'#f0f2f8',type:'dashed'}},axisLabel:{color:'#8995ad',fontSize:10}},series:[{name:props.label,type:'line',smooth:true,data:props.rows.map(x=>x.commitCount),symbolSize:6,lineStyle:{width:2,color:'#7f96ed'},itemStyle:{color:'#7f96ed'},areaStyle:{color:'#8da3f2',opacity:.12}}]})}
onMounted(()=>{if(!root.value)return;chart=init(root.value,null,{renderer:'svg'});render();observer=new ResizeObserver(()=>chart?.resize());observer.observe(root.value)})
onUnmounted(()=>{observer?.disconnect();chart?.dispose();chart=null})
watch([()=>props.rows,()=>props.label,()=>app.dark],render,{deep:true})
</script>
