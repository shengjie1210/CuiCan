<template>
  <DvScrollBoard class="rank-board" :config="config" />
</template>

<script setup>
import { computed } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'

const store = useDashboardStore()

const rows = computed(() =>
  store.topProducts.slice(0, 10).map((p, idx) => {
    const color = idx < 3 ? '#ff5577' : '#ffce6e'
    return [
      '<span style="color:' + color + ';font-weight:700">' + (idx + 1) + '</span>',
      '<span style="color:#d7e1f3">' + (p.name || '--') + '</span>',
      '<span style="color:' + color + ';font-weight:700">' + (p.sales || 0) + '</span>'
    ]
  })
)

const config = computed(() => ({
  header: ['排名', '商品名称', '销量'],
  data: rows.value,
  rowNum: 9,
  headerHeight: 32,
  headerBGC: 'rgba(0, 217, 255, 0.15)',
  oddRowBGC: 'rgba(0, 217, 255, 0.05)',
  evenRowBGC: 'transparent',
  carousel: 'single',
  waitTime: 1200,
  align: ['center', 'left', 'right'],
  hoverPause: true
}))
</script>

<style scoped>
.rank-board {
  width: 100%;
  height: 100%;
}
.rank-board :deep(.header) {
  color: #ffce6e !important;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1px;
}
.rank-board :deep(.row) {
  color: #d7e1f3 !important;
  font-size: 12px;
}
.rank-board :deep(.ceil) {
  font-family: var(--mono);
}
</style>
