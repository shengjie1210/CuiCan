<template>
  <div class="kpi-cards">
    <div
      v-for="k in cards"
      :key="k.label"
      class="kpi-card"
      :style="cardStyle(k)"
    >
      <div class="kpi-icon" :style="iconStyle(k)">
        <i :class="k.icon" :style="{ color: k.color }"></i>
      </div>
      <div class="kpi-body">
        <div class="kpi-label">{{ k.label }}</div>
        <div class="kpi-value">
          <span class="num">{{ k.value }}</span>
          <span class="unit">{{ k.unit }}</span>
        </div>
        <div class="kpi-trend">
          <span class="trend-tag" :class="k.trendDir">{{ k.trend }}</span>
          <span class="trend-comp">{{ k.comp }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { formatMoney } from '@/utils/format'

const store = useDashboardStore()

const cards = computed(() => [
  { label: '总订单', value: store.orders.length, unit: '件', icon: 'bi bi-receipt', color: '#00d9ff', trend: '+12.4%', trendDir: 'up', comp: '较昨日' },
  { label: '总用户', value: store.users.length, unit: '人', icon: 'bi bi-people', color: '#6dffb6', trend: '+8.6%', trendDir: 'up', comp: '本周新增' },
  { label: '总商品', value: store.products.length, unit: '件', icon: 'bi bi-gem', color: '#b153ff', trend: '+5.2%', trendDir: 'up', comp: '上架中' },
  { label: '总销售额', value: formatMoney(store.revenue, 0), unit: '元', icon: 'bi bi-currency-yen', color: '#ffce6e', trend: '+18.3%', trendDir: 'up', comp: '同比上月' }
])

function cardStyle(k) {
  return { borderColor: k.color, boxShadow: '0 0 18px ' + k.color + '44 inset' }
}
function iconStyle(k) {
  return { background: k.color + '22' }
}
</script>

<style scoped>
.kpi-cards {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  grid-auto-rows: minmax(0, 1fr);
  gap: 10px;
  height: 100%;
}
.kpi-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  min-width: 0;
  background: var(--bg-card);
  border: 1px solid;
  border-radius: 6px;
  backdrop-filter: blur(8px);
  transition: transform 0.3s
}
.kpi-card:hover {
  transform: translateY(-2px)
}
.kpi-icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-size: 20px;
  flex-shrink: 0
}
.kpi-body {
  flex: 1;
  min-width: 0
}
.kpi-label {
  font-size: 12px;
  color: #8aa0c4;
  margin-bottom: 4px
}
.kpi-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 4px
}
.num {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  font-family: var(--mono);
  letter-spacing: 0.5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis
}
.unit {
  font-size: 12px;
  color: #8aa0c4
}
.kpi-trend {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px
}
.trend-tag {
  padding: 1px 6px;
  border-radius: 8px;
  font-weight: 600
}
.trend-tag.up {
  background: rgba(109, 255, 182, 0.16);
  color: #6dffb6
}
.trend-tag.down {
  background: rgba(255, 85, 119, 0.16);
  color: #ff5577
}
.trend-comp {
  color: #8aa0c4
}
</style>
