<template>
  <div class="hotview">
    <TopHeader />

    <main class="main">
      <section class="col col-left">
        <BorderPanel :type="9" :main="'订单状态分布'" :sub="'各类订单状态占比'" class="panel-h">
          <OrderStatusPie />
        </BorderPanel>
        <BorderPanel :type="10" :main="'商品分类分布'" :sub="'不同分类商品数量'" class="panel-h">
          <CategoryDistribution />
        </BorderPanel>
        <BorderPanel :type="11" :main="'实时订单动态'" :sub="'最新订单滚动'" class="panel-h panel-scroll">
          <RealtimeOrders />
        </BorderPanel>
      </section>

      <section class="col col-center">
        <BorderPanel :type="8" :main="'核心指标总览'" :sub="'指标实时反映'" class="panel-kpi">
          <KpiCards />
        </BorderPanel>
        <BorderPanel :type="13" :main="'订单与销售额趋势'" :sub="'近 14 日订单数据'" class="panel-h">
          <OrderTrendChart />
        </BorderPanel>
        <BorderPanel :type="13" :main="'用户增长趋势'" :sub="'近 14 日用户注册'" class="panel-h">
          <UserTrendChart />
        </BorderPanel>
      </section>

      <section class="col col-right">
        <BorderPanel :type="12" :main="'热销商品排行'" :sub="'按销量排序的商品'" class="panel-h panel-products">
          <HotProductsRanking />
        </BorderPanel>
        <BorderPanel :type="9" :main="'用户画像'" :sub="'会员等级与性别'" class="panel-h">
          <UserProfilePanel />
        </BorderPanel>
        <BorderPanel :type="10" :main="'省份订单分布'" :sub="'TOP 10 省份'" class="panel-h">
          <ProvinceBarChart />
        </BorderPanel>
        <BorderPanel :type="11" :main="'系统监控'" :sub="'服务器状态'" class="panel-h panel-monitor">
          <SystemMonitorPanel />
        </BorderPanel>
      </section>
    </main>

    <footer class="footer">
      <div class="footer-left">
        <i class="bi bi-shield-check"></i>
        <span>系统当前稳定运行</span>
      </div>
      <div class="footer-mid">
        <button class="refresh-btn" @click="refresh(true)" :disabled="store.loading">
          <i class="bi bi-arrow-clockwise" :class="store.loading ? 'spin' : ''"></i>
          <span>刷新数据</span>
        </button>
      </div>
      <div class="footer-right">
        <i class="bi bi-clock-history"></i>
        <span>数据更新于: {{ formatTime(store.lastUpdate) }}</span>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, h } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { formatTime } from '@/utils/format'
import BoxBorder from '@/components/border/BoxBorder.vue'
import TopHeader from './components/TopHeader.vue'
import KpiCards from './components/widgets/KpiCards.vue'
import OrderTrendChart from './components/widgets/OrderTrendChart.vue'
import OrderStatusPie from './components/widgets/OrderStatusPie.vue'
import CategoryDistribution from './components/widgets/CategoryDistribution.vue'
import UserTrendChart from './components/widgets/UserTrendChart.vue'
import UserProfilePanel from './components/widgets/UserProfilePanel.vue'
import HotProductsRanking from './components/widgets/HotProductsRanking.vue'
import ProvinceBarChart from './components/widgets/ProvinceBarChart.vue'
import SystemMonitorPanel from './components/widgets/SystemMonitorPanel.vue'
import RealtimeOrders from './components/widgets/RealtimeOrders.vue'

const store = useDashboardStore()

const BorderPanel = {
  components: { BoxBorder },
  props: ['type', 'main', 'sub'],
  setup(props, { slots }) {
    const main = String(props.main || '')
    const sub = String(props.sub || '')
    return () => h(BoxBorder, { type: props.type, class: 'border-wrap' }, {
      default: () => [
        h('header', { class: 'panel-header' }, [
          h('span', { class: 'dot' }),
          h('span', { class: 'panel-title' }, main),
          sub ? h('span', { class: 'panel-sub' }, sub) : null
        ]),
        h('div', { class: 'panel-body' }, slots.default ? slots.default() : null)
      ]
    })
  }
}

let pollTimer = null

onMounted(async () => {
  await store.refresh(true)
  pollTimer = setInterval(() => { store.pollMonitor() }, 30000)
})

onBeforeUnmount(() => { if (pollTimer) clearInterval(pollTimer) })

async function refresh(force) { await store.refresh(force) }
</script>

<style scoped>
.hotview {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%
}
.main {
  flex: 1;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1.4fr) minmax(0, 1fr);
  gap: 12px;
  padding: 0 12px 8px;
  min-height: 0
}
.col {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
  min-width: 0
}
.panel-h {
  flex: 1;
  min-height: 0
}
.panel-kpi {
  flex: 0 0 230px;
  min-height: 0
}
.panel-products {
  flex: 2;
  min-height: 0
}
.panel-monitor {
  flex: 1.35;
  min-height: 0
}
.panel-scroll {
  flex: 1.4
}

:deep(.border-wrap) {
  padding: 0;
  height: 100%
}
:deep(.panel-header) {
  height: 38px;
  padding: 0 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(90deg, rgba(0, 217, 255, 0.18), rgba(0, 217, 255, 0));
  border-bottom: 1px solid rgba(0, 217, 255, 0.15)
}
:deep(.panel-title) {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 1px
}
:deep(.panel-sub) {
  margin-left: auto;
  font-size: 11px;
  color: #8aa0c4;
  letter-spacing: 0.5px
}
:deep(.dot) {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #00d9ff;
  box-shadow: 0 0 8px #00d9ff
}
:deep(.panel-body) {
  flex: 1;
  position: relative;
  padding: 10px;
  overflow: hidden
}

.footer {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  font-size: 12px;
  color: #8aa0c4;
  border-top: 1px solid rgba(0, 217, 255, 0.12)
}
.footer-left, .footer-right {
  display: flex;
  align-items: center;
  gap: 6px
}
.footer-left i { color: #6dffb6 }
.footer-right i { color: #ffce6e }
.refresh-btn {
  background: rgba(0, 217, 255, 0.12);
  color: #00d9ff;
  border: 1px solid rgba(0, 217, 255, 0.3);
  padding: 6px 14px;
  border-radius: 18px;
  font-size: 12px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s
}
.refresh-btn:hover {
  background: rgba(0, 217, 255, 0.2);
  box-shadow: 0 0 12px rgba(0, 217, 255, 0.4)
}
.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed
}
.spin {
  animation: rotate 0.8s linear infinite
}
@keyframes rotate {
  from { transform: rotate(0deg) }
  to { transform: rotate(360deg) }
}
</style>

