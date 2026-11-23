<template>
  <div>
    <h5 class="mb-3">控制台</h5>
    <div class="row g-3 mb-3">
      <div class="col-md-3 col-6" v-for="s in stats" :key="s.label">
        <div class="card border-0 shadow-sm h-100" style="border-radius:10px">
          <div class="card-body d-flex align-items-center gap-3" style="padding:18px 22px;min-height:100px">
            <div class="rounded-3 d-flex align-items-center justify-content-center flex-shrink-0" :style="{width:'54px',height:'54px',background:s.bg}">
              <i :class="s.icon" :style="{color:s.color,fontSize:'27px'}"></i>
            </div>
            <div>
              <div style="font-size:24px;font-weight:700;color:#333;line-height:1.2">{{ s.value }}</div>
              <div style="font-size:14px;color:#888;margin-top:3px">{{ s.label }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="row g-3 mb-3">
      <div class="col-md-6">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent py-2 px-3"><span class="fw-bold small"><i class="bi bi-pie-chart me-1" style="color:#409eff"></i>订单状态分布</span></div>
          <div class="card-body py-1" style="height:310px"><div id="gc_orders" style="width:100%;height:100%"></div></div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="card border-0 shadow-sm h-100">
          <div class="card-header bg-transparent py-2 px-3 border-0"><span class="fw-bold small"><i class="bi bi-exclamation-triangle me-1 text-danger"></i>库存预警</span></div>
          <div class="card-body py-1" style="height:310px"><div id="gc_stock" style="width:100%;height:100%"></div></div>
        </div>
      </div>
    </div>
    <div class="row g-3">
      <div class="col-md-6">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent py-2 px-3"><span class="fw-bold small"><i class="bi bi-bar-chart me-1" style="color:#67c23a"></i>商品分类分布</span></div>
          <div class="card-body py-1" style="height:310px"><div id="gc_cats" style="width:100%;height:100%"></div></div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent py-2 px-3"><span class="fw-bold small"><i class="bi bi-activity me-1" style="color:#e6a23c"></i>今日概况</span></div>
          <div class="card-body py-1" style="height:310px"><div id="gc_today" style="width:100%;height:100%"></div></div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue"
import request from "@/utils/request"
import { statusText } from "@/utils/common"
import * as echarts from "echarts"

const stats = ref([]), sysData = ref([])
const lowStockItems = ref([])
let charts = {}

function renderCharts(allOrders, allProducts) {
  nextTick(function() {
    // Orders pie
    var e1 = document.getElementById("gc_orders")
    if (e1) {
      if (charts.orders) charts.orders.dispose()
      var c1 = echarts.init(e1); charts.orders = c1
      var sm = {}, sc = {"-1":"#909399","0":"#f59e0b","1":"#409eff","2":"#67c23a","3":"#2ecc71"}
      for (var i = 0; i < allOrders.length; i++) {
        var k = String(allOrders[i].status)
        sm[k] = (sm[k] || 0) + 1
      }
      c1.setOption({
        tooltip: { trigger: "item", formatter: "{b}: {c} 单 ({d}%)" },
        series: [{
          type: "pie", radius: ["35%", "65%"], center: ["50%", "55%"],
          label: { formatter: "{b}\n{d}%", fontSize: 11, color: "#666" },
          itemStyle: { borderRadius: 6, borderColor: "#fff", borderWidth: 2 },
          emphasis: { itemStyle: { shadowBlur: 10, shadowColor: "rgba(0,0,0,.2)" } },
          animationDuration: 1200,
          data: Object.keys(sm).map(function(k) {
            return { name: statusText(parseInt(k)), value: parseInt(sm[k]), itemStyle: { color: sc[k] || "#999" } }
          })
        }]
      })
    }

    // Stock horizontal bar
    var eStock = document.getElementById("gc_stock")
    if (eStock) {
      if (charts.stock) charts.stock.dispose()
      var cs = echarts.init(eStock); charts.stock = cs
      var items = lowStockItems.value.slice(0, 15)
      cs.setOption({
        tooltip: { trigger: "axis", axisPointer: { type: "shadow" }, formatter: function(params) {
          var p = params[0]
          return "<strong>" + p.name + "</strong><br/>库存: <span style='color:#f56c6c;font-weight:bold'>" + p.value + "</span>"
        }},
        grid: { left: "40%", right: "10%", top: "5%", bottom: "10%", containLabel: false },
        xAxis: { type: "value", splitLine: { lineStyle: { type: "dashed" } } },
        yAxis: {
          type: "category", data: items.map(function(p) { return p.name }).reverse(),
          axisLabel: { fontSize: 10, width: 80, overflow: "truncate" }
        },
        series: [{
          type: "bar", data: items.map(function(p) { return p.stock }).reverse(),
          barWidth: "55%",
          animationDuration: 1500, animationEasing: "bounceOut",
          itemStyle: {
            borderRadius: [0, 4, 4, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: "#f59e0b" }, { offset: 1, color: "#f56c6c" }
            ])
          },
          label: { show: true, position: "right", formatter: "{c}", fontSize: 11, color: "#666" }
        }]
      })
      if (items.length === 0) {
        cs.setOption({ title: { text: "库存充足", left: "center", top: "center", textStyle: { fontSize: 14, color: "#999" } } })
      }
    }

    // Categories bar - dynamic
    var e2 = document.getElementById("gc_cats")
    if (e2) {
      if (charts.cats) charts.cats.dispose()
      var c2 = echarts.init(e2); charts.cats = c2
      var cm = {}
      for (var pi = 0; pi < allProducts.length; pi++) {
        var cid = allProducts[pi].categoryId
        if (cid) cm[cid] = (cm[cid] || 0) + 1
      }
      var entries = Object.keys(cm).slice(0, 20).map(function(cid) {
        return { name: catNames[parseInt(cid)] || "#" + cid, value: parseInt(cm[cid]) }
      })
      c2.setOption({
        tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
        grid: { left: "8%", right: "8%", top: "10%", bottom: "18%" },
        xAxis: { type: "category", data: entries.map(function(e) { return e.name }), axisLabel: { fontSize: 10, rotate: 25, interval: 0 } },
        yAxis: { type: "value", splitLine: { lineStyle: { type: "dashed" } } },
        series: [{
          type: "bar", barWidth: "55%",
          animationDuration: 2000, animationEasing: "elasticOut",
          itemStyle: {
            borderRadius: [4, 4, 0, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: "#409eff" }, { offset: 1, color: "#67c23a" }
            ])
          },
          data: entries.map(function(e) { return e.value })
        }]
      })
    }

    // Today donut
    var e3 = document.getElementById("gc_today")
    if (e3) {
      if (charts.today) charts.today.dispose()
      var c3 = echarts.init(e3); charts.today = c3
      var now = new Date()
      var ts = now.getFullYear() + "-" + String(now.getMonth()+1).padStart(2,"0") + "-" + String(now.getDate()).padStart(2,"0")
      var todayOrders = allOrders.filter(function(o) { return o.createTime && String(o.createTime).startsWith(ts) })
      c3.setOption({
        tooltip: { trigger: "item" },
        legend: { bottom: 0, textStyle: { fontSize: 11 } },
        series: [{
          type: "pie", radius: ["40%", "70%"], center: ["50%", "50%"],
          label: { formatter: "{d}%", fontSize: 11, color: "#666" },
          itemStyle: { borderRadius: 6, borderColor: "#fff", borderWidth: 2 },
          animationDuration: 1200,
          data: [
            { name: "今日订单", value: todayOrders.length, itemStyle: { color: "#409eff" } },
            { name: "历史订单", value: Math.max(1, allOrders.length - todayOrders.length), itemStyle: { color: "#e8e8e8" } }
          ]
        }]
      })
    }
  })
}

var catNames = {}

onMounted(async function() {
  var allOrders = [], allUsers = [], allProducts = []

  try {
    var cr = await request.get("/api/category/list")
    var cats = Array.isArray(cr) ? cr : (cr?.data || [])
    for (var ci = 0; ci < cats.length; ci++) {
      catNames[cats[ci].id] = cats[ci].name
      try {
        var cr2 = await request.get("/api/category/children", { params: { id: cats[ci].id } })
        var kids = Array.isArray(cr2) ? cr2 : (cr2?.data || [])
        for (var ki = 0; ki < kids.length; ki++) { catNames[kids[ki].id] = kids[ki].name }
      } catch (e) {}
    }
  } catch (e) {}

  try {
    var ur = await request.get("/admin/user/list")
    var ud = (ur && ur.data) || []
    allUsers = Array.isArray(ud) ? ud : []
  } catch (e) {}
  try {
    var pr = await request.get("/admin/product/list", { params: { page: 1, size: 9999 } })
    var pd = pr?.data?.list || []
    allProducts = Array.isArray(pd) ? pd : []
    lowStockItems.value = allProducts.filter(function(p) { return p.stock !== undefined && p.stock <= 10 })
  } catch (e) {}
  try {
    var or = await request.get("/admin/order/list", { params: { page: 1, size: 9999 } })
    var d = or && or.data
    allOrders = d?.list || []
  } catch (e) {}
  var revenue = allOrders.reduce(function(s, o) { return s + parseFloat(o.totalAmount || 0) }, 0)
  stats.value = [
    { label: "总订单", value: allOrders.length, icon: "bi bi-receipt", bg: "rgba(64,158,255,.12)", color: "#409eff" },
    { label: "总用户", value: allUsers.length, icon: "bi bi-people", bg: "rgba(103,194,58,.12)", color: "#67c23a" },
    { label: "总商品", value: allProducts.length, icon: "bi bi-gem", bg: "rgba(230,162,60,.12)", color: "#e6a23c" },
    { label: "总销售额", value: "¥" + revenue.toFixed(0), icon: "bi bi-currency-yen", bg: "rgba(255,77,136,.12)", color: "#ff4d88" },
  ]
  renderCharts(allOrders, allProducts)
})

onUnmounted(function() {
  for (var k in charts) { if (charts[k]) charts[k].dispose() }
})
</script>
<style scoped>
</style>
