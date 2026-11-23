<template>
  <ChartBase :option="option" />
</template>

<script setup>
import { computed } from 'vue'
import ChartBase from '@/components/charts/ChartBase.vue'
import { useDashboardStore } from '@/stores/dashboard'

const store = useDashboardStore()

const option = computed(() => {
  const data = store.statusDist
  const colors = ['#ffa14d', '#5b8dff', '#00d9ff', '#6dffb6', '#ff5577']
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ' + '单' + ' ({d}%)' },
    legend: { bottom: 4, textStyle: { color: '#d7e1f3', fontSize: 11 }, itemWidth: 10, itemHeight: 10 },
    series: [{
      type: 'pie',
      radius: ['38%', '65%'],
      center: ['50%', '48%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 6, borderColor: '#0a1428', borderWidth: 2 },
      label: { color: '#d7e1f3', fontSize: 11, formatter: '{b}\n{d}%' },
      labelLine: { lineStyle: { color: 'rgba(0,217,255,0.4)' } },
      emphasis: { label: { fontSize: 13, fontWeight: 'bold' }, itemStyle: { shadowBlur: 14, shadowColor: 'rgba(0,217,255,0.5)' } },
      data: (data || []).map((d, i) => ({ ...d, itemStyle: { color: colors[i % colors.length] } }))
    }]
  }
})
</script>
