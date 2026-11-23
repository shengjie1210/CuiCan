<template>
  <ChartBase :option="option" />
</template>

<script setup>
import { computed } from 'vue'
import ChartBase from '@/components/charts/ChartBase.vue'
import { useDashboardStore } from '@/stores/dashboard'

const store = useDashboardStore()

const option = computed(() => {
  const data = (store.categoryDist || []).slice(0, 12)
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ' + '件' + ' ({d}%)' },
    legend: { type: 'scroll', orient: 'vertical', right: 4, top: 'middle', textStyle: { color: '#d7e1f3', fontSize: 11 }, itemWidth: 10, itemHeight: 10 },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['38%', '50%'],
      itemStyle: { borderRadius: 6, borderColor: '#0a1428', borderWidth: 2 },
      label: { show: false },
      labelLine: { show: false },
      emphasis: { label: { show: true, fontSize: 12, fontWeight: 'bold', color: '#fff' } },
      data: data.map((d, i) => ({ ...d, itemStyle: { color: ['#00d9ff', '#5b8dff', '#b153ff', '#6dffb6', '#ffce6e', '#ffa14d', '#ff5577', '#7bd4ff', '#aaff7a', '#ffaad4', '#9caaff', '#ffd57b'][i % 12] } }))
    }]
  }
})
</script>
