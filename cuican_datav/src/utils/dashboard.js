// 形贩数据可展工具
import { orderStatusText, orderStatusColor } from './format'

export function buildOrderStatusData(orders = []) {
  const map = {}
  orders.forEach((o) => {
    const k = String(o.status)
    map[k] = (map[k] || 0) + 1
  })
  return Object.keys(map).map((k) => ({
    name: orderStatusText(parseInt(k)),
    value: map[k]
  }))
}

export function buildOrderTrend(orders = [], days = 14) {
  const now = new Date()
  const buckets = []
  const map = {}
  for (let i = days - 1; i >= 0; i--) {
    const d = new Date(now.getFullYear(), now.getMonth(), now.getDate() - i)
    const key = d.toISOString().slice(0, 10)
    buckets.push({ key, label: (d.getMonth() + 1) + '/' + d.getDate(), count: 0, amount: 0 })
    map[key] = buckets[buckets.length - 1]
  }
  orders.forEach((o) => {
    const t = o.createTime
    if (!t) return
    const key = String(t).slice(0, 10)
    if (map[key]) {
      map[key].count++
      map[key].amount += Number(o.totalAmount) || 0
    }
  })
  return {
    xAxis: buckets.map((b) => b.label),
    count: buckets.map((b) => b.count),
    amount: buckets.map((b) => Number(b.amount.toFixed(2)))
  }
}

export function buildCategoryData(products = [], categories = []) {
  const catMap = {}
  categories.forEach((c) => { catMap[c.id] = c.name })
  const dist = {}
  products.forEach((p) => {
    const cid = p.categoryId
    if (cid == null) return
    dist[cid] = (dist[cid] || 0) + 1
  })
  return Object.keys(dist).map((k) => ({
    name: catMap[k] || ('#' + k),
    value: dist[k]
  }))
}

export function buildTopProducts(products = [], n = 8) {
  return [...products]
    .sort((a, b) => (b.sales || 0) - (a.sales || 0))
    .slice(0, n)
}

export function buildLowStock(products = [], threshold = 30) {
  return products
    .filter((p) => Number(p.stock) <= threshold)
    .sort((a, b) => Number(a.stock) - Number(b.stock))
    .slice(0, 10)
}

export function buildGenderData(users = []) {
  const m = {}
  users.forEach((u) => {
    const g = u.gender || '未知'
    m[g] = (m[g] || 0) + 1
  })
  return Object.keys(m).map((k) => ({ name: k, value: m[k] }))
}

export function buildMemberLevelData(users = []) {
  const m = {}
  users.forEach((u) => {
    const lv = 'V' + (u.memberLevel || 1)
    m[lv] = (m[lv] || 0) + 1
  })
  return Object.keys(m).sort().map((k) => ({ name: k, value: m[k] }))
}

export function buildProvinceData(orders = []) {
  const m = {}
  orders.forEach((o) => {
    const p = o.province || '未知'
    m[p] = (m[p] || 0) + 1
  })
  return Object.keys(m).map((k) => ({ name: k, value: m[k] }))
}

export function buildUserTrend(users = [], days = 14) {
  const now = new Date()
  const buckets = []
  const map = {}
  for (let i = days - 1; i >= 0; i--) {
    const d = new Date(now.getFullYear(), now.getMonth(), now.getDate() - i)
    const key = d.toISOString().slice(0, 10)
    buckets.push({ key, label: (d.getMonth() + 1) + '/' + d.getDate(), count: 0 })
    map[key] = buckets[buckets.length - 1]
  }
  users.forEach((u) => {
    const t = u.createTime
    if (!t) return
    const key = String(t).slice(0, 10)
    if (map[key]) map[key].count++
  })
  let cum = 0
  const cumArr = buckets.map((b) => { cum += b.count; return cum })
  return {
    xAxis: buckets.map((b) => b.label),
    count: buckets.map((b) => b.count),
    cum: cumArr
  }
}

export function sumAmount(orders = []) {
  return orders.reduce((s, o) => s + (Number(o.totalAmount) || 0), 0)
}

export function pickColor(status) {
  return orderStatusColor(status)
}
