<template>
  <div>
    <h5 class="mb-3">AI 使用统计</h5>
    <div class="row g-3 mb-3">
      <div class="col-sm-6 col-xl-3">
        <div class="card border-0 shadow-sm">
          <div class="card-body py-3 d-flex align-items-center gap-3">
            <div class="stat-icon" style="background:rgba(64,158,255,.12);color:#409eff"><i class="bi bi-chat-dots"></i></div>
            <div><div class="small text-muted">总请求数</div><div class="fw-bold fs-5">{{ summary.totalRequests || 0 }}</div></div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card border-0 shadow-sm">
          <div class="card-body py-3 d-flex align-items-center gap-3">
            <div class="stat-icon" style="background:rgba(103,194,58,.12);color:#67c23a"><i class="bi bi-coin"></i></div>
            <div><div class="small text-muted">总 Token 消耗</div><div class="fw-bold fs-5">{{ formatNum(summary.totalTokens) }}</div></div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card border-0 shadow-sm">
          <div class="card-body py-3 d-flex align-items-center gap-3">
            <div class="stat-icon" style="background:rgba(230,162,60,.12);color:#e6a23c"><i class="bi bi-input-cursor"></i></div>
            <div><div class="small text-muted">提示 Token</div><div class="fw-bold fs-5">{{ formatNum(summary.promptTokens) }}</div></div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card border-0 shadow-sm">
          <div class="card-body py-3 d-flex align-items-center gap-3">
            <div class="stat-icon" style="background:rgba(245,108,108,.12);color:#f56c6c"><i class="bi bi-send"></i></div>
            <div><div class="small text-muted">生成 Token</div><div class="fw-bold fs-5">{{ formatNum(summary.completionTokens) }}</div></div>
          </div>
        </div>
      </div>
    </div>
    <div class="row g-3">
      <div class="col-xl-8">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent py-2 px-3 d-flex justify-content-between align-items-center">
            <span class="fw-bold small"><i class="bi bi-graph-up me-1" style="color:#e6a23c"></i>近 14 天调用趋势</span>
            <button class="btn btn-outline-primary btn-sm" @click="loadAll"><i class="bi bi-arrow-clockwise"></i> 刷新</button>
          </div>
          <div class="card-body" style="height:360px"><div ref="trendEl" style="width:100%;height:100%"></div></div>
        </div>
      </div>
      <div class="col-xl-4">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent py-2 px-3"><span class="fw-bold small"><i class="bi bi-pie-chart me-1" style="color:#e6a23c"></i>模型调用占比</span></div>
          <div class="card-body" style="height:360px"><div ref="pieEl" style="width:100%;height:100%"></div></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from "vue"
import request from "@/utils/request"
import * as echarts from "echarts"

const trendEl = ref(null), pieEl = ref(null)
const summary = reactive({ totalRequests: 0, totalTokens: 0, promptTokens: 0, completionTokens: 0 })
let trendChart = null, pieChart = null

function formatNum(n) {
  if (!n) return "0"
  if (n >= 10000) return (n / 10000).toFixed(1) + " 万"
  return Number(n).toLocaleString("zh-CN")
}

function renderTrend(days) {
  if (!trendEl.value) return
  if (trendChart) trendChart.dispose()
  trendChart = echarts.init(trendEl.value)
  const dates = days.map(d => d.date.slice(5))
  trendChart.setOption({
    tooltip: { trigger: "axis", axisPointer: { type: "cross" } },
    legend: { data: ["请求数", "总 Token"], top: 0, textStyle: { fontSize: 11 } },
    grid: { left: "8%", right: "9%", top: 40, bottom: "12%", containLabel: true },
    xAxis: { type: "category", data: dates, boundaryGap: false, axisLabel: { fontSize: 10 } },
    yAxis: [
      { type: "value", name: "请求数", nameTextStyle: { fontSize: 10 }, splitLine: { lineStyle: { type: "dashed" } }, axisLabel: { fontSize: 10 } },
      { type: "value", name: "Token", nameTextStyle: { fontSize: 10 }, splitLine: { show: false }, axisLabel: { fontSize: 10, formatter: v => v >= 10000 ? (v / 10000) + "万" : v } }
    ],
    series: [
      {
        name: "请求数", type: "bar", yAxisIndex: 0, barWidth: "50%",
        itemStyle: { borderRadius: [4, 4, 0, 0], color: "#409eff" },
        data: days.map(d => d.requestCount)
      },
      {
        name: "总 Token", type: "line", yAxisIndex: 1, smooth: true,
        symbolSize: 5,
        lineStyle: { width: 2, color: "#e6a23c" },
        itemStyle: { color: "#e6a23c" },
        areaStyle: { opacity: 0.08, color: "#e6a23c" },
        data: days.map(d => d.totalTokens)
      }
    ]
  })
}

function renderPie(models) {
  if (!pieEl.value) return
  if (pieChart) pieChart.dispose()
  pieChart = echarts.init(pieEl.value)
  if (!models || models.length === 0) {
    pieChart.setOption({ title: { text: "暂无数据", left: "center", top: "center", textStyle: { fontSize: 14, color: "#999" } } })
    return
  }
  const palette = ["#409eff", "#e6a23c", "#67c23a", "#f56c6c", "#9b59b6", "#2ecc71"]
  pieChart.setOption({
    tooltip: { trigger: "item", formatter: "{b}: {c} 次 ({d}%)" },
    legend: { bottom: 0, textStyle: { fontSize: 11 }, type: "scroll" },
    series: [{
      type: "pie", radius: ["38%", "68%"], center: ["50%", "46%"],
      label: { formatter: "{b}\n{d}%", fontSize: 11, color: "#666" },
      itemStyle: { borderRadius: 6, borderColor: "#fff", borderWidth: 2 },
      animationDuration: 1200,
      data: models.map((m, i) => ({ name: m.model, value: m.requestCount, itemStyle: { color: palette[i % palette.length] } }))
    }]
  })
}

async function loadAll() {
  try {
    const [tr, sm] = await Promise.all([
      request.get("/admin/ai/usage/trend", { params: { days: 14 } }),
      request.get("/admin/ai/usage/summary")
    ])
    const td = tr?.data, sd = sm?.data
    summary.totalRequests = td?.totalRequests || 0
    summary.totalTokens = td?.totalTokens || 0
    summary.promptTokens = td?.promptTokens || 0
    summary.completionTokens = td?.completionTokens || 0
    nextTick(() => {
      renderTrend(td?.days || [])
      renderPie(sd?.models || [])
    })
  } catch (e) { /* 权限不足或未登录时静默 */ }
}

function onResize() {
  if (trendChart) trendChart.resize()
  if (pieChart) pieChart.resize()
}

onMounted(() => {
  loadAll()
  window.addEventListener("resize", onResize)
})
onUnmounted(() => {
  window.removeEventListener("resize", onResize)
  if (trendChart) { trendChart.dispose(); trendChart = null }
  if (pieChart) { pieChart.dispose(); pieChart = null }
})
</script>

<style scoped>
.stat-icon { width: 44px; height: 44px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0; }
</style>
