<template>
  <ChartBase :option="option" />
</template>

<script setup>
import { computed } from 'vue'
import ChartBase from '@/components/charts/ChartBase.vue'
import { useDashboardStore } from '@/stores/dashboard'

const store = useDashboardStore()

const option = computed(() => {
  const t = store.userTrend || { xAxis: [], count: [], cum: [] }
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
    legend: { data: ['新增', '累计'], textStyle: { color: '#d7e1f3', fontSize: 11 }, top: 4, right: 8 },
    grid: { left: '3%', right: '4%', bottom: '8%', top: '18%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: t.xAxis, axisLine: { lineStyle: { color: 'rgba(0,217,255,0.3)' } }, axisLabel: { color: '#8aa0c4', fontSize: 10 } },
    yAxis: [
      { type: 'value', name: '人', nameTextStyle: { color: '#8aa0c4', fontSize: 10 }, axisLine: { show: false }, splitLine: { lineStyle: { color: 'rgba(0,217,255,0.08)', type: 'dashed' } }, axisLabel: { color: '#8aa0c4', fontSize: 10 } },
      { type: 'value', name: '总量', position: 'right', nameTextStyle: { color: '#8aa0c4', fontSize: 10 }, axisLine: { show: false }, splitLine: { show: false }, axisLabel: { color: '#8aa0c4', fontSize: 10 } }
    ],
    series: [
      { name: '新增', type: 'bar', yAxisIndex: 0, barWidth: '40%', data: t.count, itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#6dffb6' }, { offset: 1, color: 'rgba(109,255,182,0.2)' }] }, borderRadius: [4,4,0,0] } },
      { name: '累计', type: 'line', yAxisIndex: 1, smooth: true, symbol: 'circle', symbolSize: 6, itemStyle: { color: '#ffce6e' }, lineStyle: { width: 2, color: '#ffce6e', shadowColor: 'rgba(255,206,110,0.5)', shadowBlur: 10 }, data: t.cum }
    ]
  }
})
</script>
