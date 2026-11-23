<template>
  <ChartBase :option="option" />
</template>

<script setup>
import { computed } from 'vue'
import ChartBase from '@/components/charts/ChartBase.vue'
import { useDashboardStore } from '@/stores/dashboard'

const store = useDashboardStore()

const option = computed(() => {
  const raw = store.provinceDist || []
  const data = [...raw].sort((a, b) => b.value - a.value).slice(0, 10).reverse()
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: '{b}: {c} ' + '订单' },
    grid: { left: 70, right: 30, top: 10, bottom: 10, containLabel: false },
    xAxis: { type: 'value', show: false },
    yAxis: { type: 'category', data: data.map((d) => d.name), axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#d7e1f3', fontSize: 11 } },
    series: [{
      type: 'bar',
      barWidth: '55%',
      data: data.map((d, i) => ({ value: d.value, itemStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 1, y2: 0,
          colorStops: [
            { offset: 0, color: 'rgba(91,141,255,0.3)' },
            { offset: 1, color: '#00d9ff' }
          ]
        },
        borderRadius: [0, 4, 4, 0]
      } })),
      label: { show: true, position: 'right', color: '#d7e1f3', fontSize: 11 }
    }]
  }
})
</script>
