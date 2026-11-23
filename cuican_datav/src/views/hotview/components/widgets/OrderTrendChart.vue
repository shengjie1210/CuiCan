<template>
  <ChartBase :option="option" />
</template>

<script setup>
import { computed } from 'vue'
import ChartBase from '@/components/charts/ChartBase.vue'
import { useDashboardStore } from '@/stores/dashboard'

const store = useDashboardStore()

const option = computed(() => {
  const t = store.orderTrend || { xAxis: [], count: [], amount: [] }
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross', label: { backgroundColor: '#1a3a6b' } } },
    legend: { data: ['订单数', '销售额'], textStyle: { color: '#d7e1f3', fontSize: 11 }, top: 4, right: 8 },
    grid: { left: '3%', right: '4%', bottom: '8%', top: '18%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: t.xAxis, axisLine: { lineStyle: { color: 'rgba(0,217,255,0.3)' } }, axisLabel: { color: '#8aa0c4', fontSize: 10 } },
    yAxis: [
      { type: 'value', name: '单', nameTextStyle: { color: '#8aa0c4', fontSize: 10 }, axisLine: { show: false }, splitLine: { lineStyle: { color: 'rgba(0,217,255,0.08)', type: 'dashed' } }, axisLabel: { color: '#8aa0c4', fontSize: 10 } },
      { type: 'value', name: '元', position: 'right', nameTextStyle: { color: '#8aa0c4', fontSize: 10 }, axisLine: { show: false }, splitLine: { show: false }, axisLabel: { color: '#8aa0c4', fontSize: 10 } }
    ],
    series: [
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        yAxisIndex: 0,
        itemStyle: { color: '#00d9ff' },
        lineStyle: { width: 2, color: '#00d9ff', shadowColor: 'rgba(0,217,255,0.5)', shadowBlur: 12 },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(0,217,255,0.45)' },
              { offset: 1, color: 'rgba(0,217,255,0.02)' }
            ]
          }
        },
        data: t.count
      },
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        yAxisIndex: 1,
        itemStyle: { color: '#b153ff' },
        lineStyle: { width: 2, color: '#b153ff', shadowColor: 'rgba(177,83,255,0.4)', shadowBlur: 12 },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(177,83,255,0.4)' },
              { offset: 1, color: 'rgba(177,83,255,0.02)' }
            ]
          }
        },
        data: t.amount
      }
    ]
  }
})
</script>
