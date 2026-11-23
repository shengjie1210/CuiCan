<template>
  <DvScrollBoard
    class="scroll-board"
    :config="config"
  />
</template>

<script setup>
import { computed } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { orderStatusText, orderStatusColor, formatDateTime } from '@/utils/format'

const store = useDashboardStore()

const rows = computed(() => {
  return (store.orders || [])
    .slice()
    .sort((a, b) => new Date(b.createTime || 0).getTime() - new Date(a.createTime || 0).getTime())
    .slice(0, 10)
    .map((o) => [
      String(o.orderNo || '--'),
      (o.receiverName || '--'),
      (Number(o.totalAmount) || 0).toFixed(2),
      orderStatusText(o.status),
      formatDateTime(o.createTime)
    ])
})

const config = computed(() => ({
  header: ['订单号', '客户', '金额', '状态', '时间'],
  data: rows.value,
  rowNum: 8,
  headerHeight: 32,
  headerBGC: 'rgba(0, 217, 255, 0.15)',
  oddRowBGC: 'rgba(0, 217, 255, 0.04)',
  evenRowBGC: 'transparent',
  carousel: 'single',
  waitTime: 1200,
  align: ['left', 'center', 'center', 'center', 'center'],
  hoverPause: true
}))
</script>

<style scoped>
.scroll-board {
  width: 100%;
  height: 100%
}
.scroll-board :deep(.header) {
  color: #00d9ff !important;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1px
}
.scroll-board :deep(.row) {
  color: #d7e1f3 !important;
  font-size: 12px
}
.scroll-board :deep(.ceil) {
  font-family: var(--mono)
}
</style>
