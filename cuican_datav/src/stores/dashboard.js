import { defineStore } from 'pinia'
import { fetchDashboardData, serverMonitor } from '@/api'
import {
  buildOrderStatusData,
  buildOrderTrend,
  buildCategoryData,
  buildTopProducts,
  buildLowStock,
  buildGenderData,
  buildMemberLevelData,
  buildProvinceData,
  buildUserTrend,
  sumAmount
} from '@/utils/dashboard'

export const useDashboardStore = defineStore('dashboard', {
  state: () => ({
    loading: false,
    lastUpdate: 0,
    orders: [],
    products: [],
    users: [],
    categories: [],
    monitor: null,
    errors: [],

    // 派生数据
    statusDist: [],
    orderTrend: null,
    userTrend: null,
    categoryDist: [],
    topProducts: [],
    lowStock: [],
    genderDist: [],
    memberDist: [],
    provinceDist: [],
    revenue: 0
  }),

  actions: {
    async refresh(force = false) {
      // 节流: 10 秒内不重复
      const now = Date.now()
      if (!force && now - this.lastUpdate < 10000) return
      this.loading = true
      try {
        const data = await fetchDashboardData()
        this.orders = data.orders
        this.products = data.products
        this.users = data.users
        this.categories = data.categories
        this.monitor = data.monitor
        this.errors = data.errors
        this.lastUpdate = now
        this.recompute()
      } finally {
        this.loading = false
      }
    },

    recompute() {
      this.statusDist = buildOrderStatusData(this.orders)
      this.orderTrend = buildOrderTrend(this.orders, 14)
      this.userTrend = buildUserTrend(this.users, 14)
      this.categoryDist = buildCategoryData(this.products, this.categories)
      this.topProducts = buildTopProducts(this.products, 10)
      this.lowStock = buildLowStock(this.products, 30)
      this.genderDist = buildGenderData(this.users)
      this.memberDist = buildMemberLevelData(this.users)
      this.provinceDist = buildProvinceData(this.orders)
      this.revenue = sumAmount(this.orders)
    },

    async pollMonitor() {
      // 轻量级刷新监控数据
      try {
        this.monitor = await serverMonitor()
      } catch (e) { /* ignore */ }
    }
  }
})
