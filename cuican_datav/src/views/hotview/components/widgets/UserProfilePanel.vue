<template>
  <div class="user-profile">
    <div class="row">
      <div class="title">会员等级</div>
      <ChartBase class="mini" :option="memberOption" />
    </div>
    <div class="row">
      <div class="title">性别</div>
      <ChartBase class="mini" :option="genderOption" />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ChartBase from '@/components/charts/ChartBase.vue'
import { useDashboardStore } from '@/stores/dashboard'

const store = useDashboardStore()

const memberOption = computed(() => {
  const data = store.memberDist || []
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ' + '人' },
    grid: { left: 30, right: 30, top: 10, bottom: 10 },
    xAxis: { type: 'value', show: false },
    yAxis: { type: 'category', show: true, data: data.map((d) => d.name), axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#d7e1f3', fontSize: 11 } },
    series: [{
      type: 'bar',
      barWidth: '55%',
      data: data.map((d, i) => ({ value: d.value, itemStyle: { color: ['#00d9ff', '#5b8dff', '#b153ff', '#6dffb6', '#ffce6e'][i % 5] } })),
      label: { show: true, position: 'right', color: '#d7e1f3', fontSize: 11 },
      itemStyle: { borderRadius: [0, 4, 4, 0] }
    }]
  }
})

const genderOption = computed(() => {
  const data = store.genderDist || []
  // 重新映射中文
  const mapped = data.map((d) => ({ name: d.name === '男' ? '男' : d.name === '女' ? '女' : '未知', value: d.value }))
  const colors = { '男': '#5b8dff', '女': '#ff5577', '未知': '#8aa0c4' }
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ' + '人' + ' ({d}%)' },
    legend: { bottom: 0, textStyle: { color: '#d7e1f3', fontSize: 11 }, itemWidth: 10, itemHeight: 10 },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['50%', '45%'],
      itemStyle: { borderRadius: 6, borderColor: '#0a1428', borderWidth: 2 },
      label: { color: '#d7e1f3', fontSize: 11 },
      emphasis: { itemStyle: { shadowBlur: 12, shadowColor: 'rgba(0,217,255,0.5)' } },
      data: mapped.map((d) => ({ name: d.name, value: d.value, itemStyle: { color: colors[d.name] || '#8aa0c4' } }))
    }]
  }
})
</script>

<style scoped>
.user-profile {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: row;
  gap: 10px
}
.row {
  flex: 1;
  min-width: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(0, 217, 255, 0.12);
  border-radius: 4px;
  padding: 6px 10px;
  overflow: hidden
}
.title {
  font-size: 12px;
  color: #8aa0c4;
  margin-bottom: 3px;
  letter-spacing: 1px;
  flex-shrink: 0
}
.mini {
  flex: 1;
  min-height: 0
}
</style>
