<template>
  <div>
    <h5 class="mb-3">服务器监控</h5>
    <div class="d-flex justify-content-between align-items-center mb-2 flex-wrap gap-1">
      <div class="d-flex gap-2 align-items-center">
        <button class="btn btn-outline-primary btn-sm" @click="doRefresh"><i class="bi bi-arrow-clockwise" :class="{ 'btn-spinning': refreshing }"></i> 刷新</button>
        <small class="text-muted" v-if="lastUpdate">更新于 {{ lastUpdate }}</small>
        <span class="badge bg-success-subtle text-success border border-success" style="font-size:10px">● 服务运行中</span>
      </div>
    </div>
    <!-- Row 1: 4 stat cards -->
    <div class="row g-2 mb-3">
      <div class="col-md-3 col-6" v-for="c in topCards" :key="c.label">
        <div class="card border-0 shadow-sm h-100" style="border-radius:10px">
          <div class="card-body p-3 d-flex align-items-start gap-3">
            <div class="rounded-3 d-flex align-items-center justify-content-center flex-shrink-0" :style="{width:'50px',height:'50px',background:c.bg}">
              <i :class="c.icon" :style="{color:c.color,fontSize:'24px'}"></i>
            </div>
            <div class="flex-grow-1" style="min-width:0">
              <div class="text-muted" style="font-size:11px;line-height:1.2">{{ c.label }}</div>
              <div class="fw-bold text-truncate" style="font-size:16px">{{ c.value }}</div>
              <div v-if="c.sub" class="text-muted text-truncate" style="font-size:10px;margin-top:1px">{{ c.sub }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- Row 2: Radar + Big Pie -->
    <div class="row g-3 mb-3">
      <div class="col-md-6">
        <div class="card border-0 shadow-sm h-100">
          <div class="card-header bg-transparent py-2 px-3 d-flex align-items-center gap-2 border-0">
            <span class="rounded-circle d-inline-flex align-items-center justify-content-center" :style="{width:'24px',height:'24px',background:'rgba(64,158,255,.12)'}">
              <i class="bi bi-brightness-alt-high" style="color:#409eff;font-size:13px"></i>
            </span>
            <span class="fw-bold small">系统健康评分</span>
          </div>
          <div class="card-body py-2" style="height:280px"><div id="gc_radar" style="width:100%;height:100%"></div></div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="card border-0 shadow-sm h-100">
          <div class="card-header bg-transparent py-2 px-3 d-flex align-items-center gap-2 border-0">
            <span class="rounded-circle d-inline-flex align-items-center justify-content-center" :style="{width:'24px',height:'24px',background:'rgba(230,162,60,.12)'}">
              <i class="bi bi-pie-chart" style="color:#e6a23c;font-size:13px"></i>
            </span>
            <span class="fw-bold small">资源占用分布</span>
            <span class="ms-auto text-muted small">CPU / 内存 / 磁盘</span>
          </div>
          <div class="card-body py-2" style="height:280px"><div id="gc_pie" style="width:100%;height:100%"></div></div>
        </div>
      </div>
    </div>
    <!-- Row 3: Two donut charts -->
    <div class="row g-3 mb-3">
      <div class="col-md-6">
        <div class="card border-0 shadow-sm h-100">
          <div class="card-header bg-transparent py-2 px-3 d-flex align-items-center gap-2 border-0">
            <span class="rounded-circle d-inline-flex align-items-center justify-content-center" :style="{width:'24px',height:'24px',background:'rgba(103,194,58,.12)'}">
              <i class="bi bi-memory" style="color:#67c23a;font-size:13px"></i>
            </span>
            <span class="fw-bold small">JVM 堆内存</span>
            <span class="ms-auto text-muted small" style="font-size:11px">{{ jvmUsedTxt }} / {{ jvmMaxTxt }}</span>
          </div>
          <div class="card-body py-2" style="height:220px"><div id="gc_jvm" style="width:100%;height:100%"></div></div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="card border-0 shadow-sm h-100">
          <div class="card-header bg-transparent py-2 px-3 d-flex align-items-center gap-2 border-0">
            <span class="rounded-circle d-inline-flex align-items-center justify-content-center" :style="{width:'24px',height:'24px',background:'rgba(230,162,60,.12)'}">
              <i class="bi bi-hdd-stack" style="color:#e6a23c;font-size:13px"></i>
            </span>
            <span class="fw-bold small">磁盘空间</span>
            <span class="ms-auto text-muted small" style="font-size:11px">{{ diskUsedTxt }} / {{ diskTotalTxt }}</span>
          </div>
          <div class="card-body py-2" style="height:220px"><div id="gc_disk" style="width:100%;height:100%"></div></div>
        </div>
      </div>
    </div>
    <!-- Row 4: Detail tables -->
    <div class="row g-3">
      <div class="col-md-6">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent py-2 px-3"><span class="fw-bold small"><i class="bi bi-info-circle me-1 text-primary"></i>系统信息</span></div>
          <div class="card-body py-1 px-3">
            <div v-for="s in sysInfo" :key="s.label" class="d-flex justify-content-between py-1 border-bottom border-light" style="font-size:12px">
              <span class="text-muted">{{ s.label }}</span>
              <span class="fw-medium text-end" style="max-width:55%">{{ s.value }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent py-2 px-3"><span class="fw-bold small"><i class="bi bi-activity me-1 text-success"></i>运行状态</span></div>
          <div class="card-body py-1 px-3">
            <div v-for="s in runInfo" :key="s.label" class="d-flex justify-content-between py-1 border-bottom border-light" style="font-size:12px">
              <span class="text-muted">{{ s.label }}</span>
              <span class="fw-medium">{{ s.value }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue"
import request from "@/utils/request"
import * as echarts from "echarts"

const lastUpdate = ref("")
const topCards = ref([])
const sysInfo = ref([])
const runInfo = ref([])
const jvmUsedTxt = ref(""), jvmMaxTxt = ref(""), jvmFreeTxt = ref("")
const diskUsedTxt = ref(""), diskTotalTxt = ref(""), diskFreeTxt = ref("")
const cpuPct = ref(0), memPct = ref(0), diskPct = ref(0)
const cpuUsageTxt = ref(""), cpuLoadTxt = ref("")

const refreshing = ref(false)
let charts = {}
async function doRefresh() { refreshing.value = true; try { await loadData() } finally { refreshing.value = false } }
let timer = null

function pct(v) {
  if (!v || v === "N/A") return 0
  return parseFloat(String(v).replace("%", "")) || 0
}
function fmt(t) {
  return t ? String(t).substring(0, 19).replace("T", " ") : "-"
}

function renderCharts() {
  const cp = cpuPct.value, mp = memPct.value, dp = diskPct.value
  const hCpu = Math.round(100 - cp), hMem = Math.round(100 - mp), hDisk = Math.round(100 - dp)
  const stability = 90

  // Radar
  let re = document.getElementById("gc_radar")
  if (re) {
    if (charts.radar) charts.radar.dispose()
    const rc = echarts.init(re)
    charts.radar = rc
    rc.setOption({
      tooltip: {
        trigger: "item",
        formatter: function(p) {
          const v = p.value || [hCpu, hMem, hDisk, stability]
          const labels = ["CPU 健康度 (空闲率)", "内存健康度 (空闲率)", "磁盘健康度 (空闲率)", "系统稳定性"]
          let h = "<strong>系统健康评分</strong><br/>"
          labels.forEach(function(l, i) { h += l + ": <span style='font-weight:bold'>" + v[i] + "%</span><br/>" })
          return h
        }
      },
      radar: {
        indicator: [
          { name: "CPU 健康度\n" + hCpu + "%", max: 100 },
          { name: "内存健康度\n" + hMem + "%", max: 100 },
          { name: "磁盘健康度\n" + hDisk + "%", max: 100 },
          { name: "系统稳定性\n" + stability + "%", max: 100 }
        ],
        center: ["50%", "52%"], radius: "70%",
        splitNumber: 4, shape: "circle",
        axisName: { color: "#555", fontSize: 11, rich: { a: { fontSize: 11 }, b: { fontSize: 10, color: "#999" } },
          formatter: function(n) { return n } },
        splitArea: { areaStyle: { color: ["rgba(64,158,255,.02)", "rgba(64,158,255,.06)", "rgba(64,158,255,.1)"] } },
        splitLine: { lineStyle: { color: "rgba(64,158,255,.15)" } },
        axisLine: { lineStyle: { color: "rgba(64,158,255,.2)" } }
      },
      series: [{
        type: "radar",
        data: [{ value: [hCpu, hMem, hDisk, stability], name: "健康评分", areaStyle: { color: "rgba(64,158,255,.25)" } }],
        symbol: "diamond", symbolSize: 10,
        lineStyle: { color: "#409eff", width: 2 },
        itemStyle: { color: "#409eff" },
        emphasis: { lineStyle: { width: 4 }, areaStyle: { color: "rgba(64,158,255,.4)" } }
      }]
    })
  }

  // Resource pie
  let pe = document.getElementById("gc_pie")
  if (pe) {
    if (charts.pie) charts.pie.dispose()
    const pc = echarts.init(pe)
    charts.pie = pc
    pc.setOption({
      tooltip: {
        trigger: "item",
        formatter: function(p) { return "<strong>" + p.name + "</strong><br/>占用率: <span style='font-weight:bold;font-size:15px'>" + p.value + "%</span><br/>占比: " + p.percent + "%" }
      },
      series: [{
        type: "pie",
        radius: ["35%", "68%"],
        center: ["50%", "55%"],
        roseType: "area",
        avoidLabelOverlap: true,
        padAngle: 1,
        itemStyle: { borderRadius: 8, borderColor: "#fff", borderWidth: 2 },
        label: { show: true, formatter: "{b}\n{d}%", fontSize: 11, color: "#666" },
        labelLine: { length: 14, length2: 10 },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: "bold" },
          itemStyle: { shadowBlur: 12, shadowOffsetX: 0, shadowColor: "rgba(0,0,0,.2)" }
        },
        data: [
          { value: Math.round(cp), name: "CPU 占用", itemStyle: { color: "#409eff" } },
          { value: Math.round(mp), name: "JVM 内存占用", itemStyle: { color: "#67c23a" } },
          { value: Math.round(dp), name: "磁盘占用", itemStyle: { color: "#e6a23c" } },
          { value: Math.max(1, Math.round(100 - cp)), name: "CPU 空闲", itemStyle: { color: "#b3d8ff" } }
        ]
      }]
    })
  }

  // JVM donut
  let je = document.getElementById("gc_jvm")
  if (je) {
    if (charts.jvm) charts.jvm.dispose()
    const jc = echarts.init(je)
    charts.jvm = jc
    jc.setOption({
      tooltip: {
        trigger: "item",
        formatter: function(p) {
          return "<strong>" + p.name + "</strong><br/>大小: <span style='font-weight:bold'>" + p.data.realSize + "</span><br/>占比: " + p.percent + "%"
        }
      },
      series: [{
        type: "pie",
        radius: ["50%", "80%"],
        center: ["50%", "55%"],
        avoidLabelOverlap: false,
        padAngle: 2,
        itemStyle: { borderRadius: 6, borderColor: "#fff", borderWidth: 2 },
        label: { show: true, position: "outside", formatter: "{b}\n{d}%", fontSize: 11, color: "#666" },
        labelLine: { length: 12, length2: 8 },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: "bold" },
          itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: "rgba(0,0,0,.2)" }
        },
        data: [
          { value: mp, name: "已使用", realSize: jvmUsedTxt.value, itemStyle: { color: "#67c23a" } },
          { value: Math.max(0.1, 100 - mp), name: "空闲", realSize: jvmFreeTxt.value, itemStyle: { color: "#c8e6c9" } }
        ]
      }]
    })
  }

  // Disk donut
  let dke = document.getElementById("gc_disk")
  if (dke) {
    if (charts.disk) charts.disk.dispose()
    const dc = echarts.init(dke)
    charts.disk = dc
    dc.setOption({
      tooltip: {
        trigger: "item",
        formatter: function(p) {
          return "<strong>" + p.name + "</strong><br/>大小: <span style='font-weight:bold'>" + p.data.realSize + "</span><br/>占比: " + p.percent + "%"
        }
      },
      series: [{
        type: "pie",
        radius: ["50%", "80%"],
        center: ["50%", "55%"],
        avoidLabelOverlap: false,
        padAngle: 2,
        itemStyle: { borderRadius: 6, borderColor: "#fff", borderWidth: 2 },
        label: { show: true, position: "outside", formatter: "{b}\n{d}%", fontSize: 11, color: "#666" },
        labelLine: { length: 12, length2: 8 },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: "bold" },
          itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: "rgba(0,0,0,.2)" }
        },
        data: [
          { value: dp, name: "已使用", realSize: diskUsedTxt.value, itemStyle: { color: "#e6a23c" } },
          { value: Math.max(0.1, 100 - dp), name: "空闲", realSize: diskFreeTxt.value, itemStyle: { color: "#f8d7b0" } }
        ]
      }]
    })
  }
}

async function loadData() {
  lastUpdate.value = new Date().toLocaleTimeString("zh-CN", { hour12: false })

  try {
    const r = await request.get("/admin/server/info")
    const d = (r && r.data) || {}
    topCards.value = [
      { label: "操作系统", value: d.osName || "-", sub: (d.osArch || "").substring(0, 20), icon: "bi bi-pc-display", bg: "rgba(64,158,255,.12)", color: "#409eff" },
      { label: "运行时长", value: d.uptime || "0天", sub: "启动: " + fmt(d.startTime), icon: "bi bi-clock", bg: "rgba(103,194,58,.12)", color: "#67c23a" },
      { label: "CPU 核心", value: (d.processors || "-") + " 核", sub: "Java " + (d.javaVersion || "").substring(0, 8), icon: "bi bi-cpu", bg: "rgba(230,162,60,.12)", color: "#e6a23c" },
      { label: "Java 环境", value: d.javaHome ? d.javaHome.split("\\").slice(-2, -1).pop() || "JDK" : "-", sub: (d.javaVersion || "").substring(0, 10), icon: "bi bi-box", bg: "rgba(144,147,153,.12)", color: "#909399" },
    ]
    sysInfo.value = [
      { label: "操作系统", value: d.osName + " (" + (d.osArch || "") + ")" || "-" },
      { label: "CPU 核心数", value: (d.processors || "-") + " 核" },
      { label: "Java 版本", value: d.javaVersion || "-" },
      { label: "Java 安装", value: d.javaHome || "-" },
      { label: "用户目录", value: d.userDir || "-" },
      { label: "启动时间", value: fmt(d.startTime) },
      { label: "运行时长", value: d.uptime || "-" },
    ]
  } catch (e) {}

  try {
    const r = await request.get("/admin/server/monitor")
    const d = (r && r.data) || {}
    cpuPct.value = pct(d.cpuUsage)
    memPct.value = pct(d.jvmRate)
    diskPct.value = pct(d.diskRate)
    cpuUsageTxt.value = d.cpuUsage || "—"
    cpuLoadTxt.value = d.cpuLoad || "—"
    jvmUsedTxt.value = d.jvmUsed || "—"
    jvmMaxTxt.value = d.jvmMax || "—"
    jvmFreeTxt.value = d.jvmFree || "—"
    diskUsedTxt.value = d.diskUsed || "—"
    diskTotalTxt.value = d.diskTotal || "—"
    diskFreeTxt.value = d.diskFree || "—"

    runInfo.value = [
      { label: "CPU 使用率", value: cpuUsageTxt.value },
      { label: "CPU 平均负载", value: cpuLoadTxt.value },
      { label: "JVM 堆内存", value: jvmUsedTxt.value + " / " + jvmMaxTxt.value },
      { label: "JVM 空闲内存", value: jvmFreeTxt.value },
    ]
    if (d.jvmTotal) runInfo.value.push({ label: "JVM 总内存", value: d.jvmTotal })
    if (d.sysTotal) {
      runInfo.value.push(
        { label: "系统物理内存", value: (d.sysUsed || "—") + " / " + (d.sysTotal || "—") },
        { label: "磁盘总量", value: diskTotalTxt.value },
        { label: "磁盘已用", value: diskUsedTxt.value },
        { label: "磁盘剩余", value: diskFreeTxt.value },
      )
    }
    if (d.sysRate) runInfo.value.push({ label: "物理内存使用率", value: d.sysRate })
  } catch (e) {}

  await nextTick()
  setTimeout(renderCharts, 100)
}

onMounted(function() { loadData(); timer = setInterval(loadData, 30000) })
onUnmounted(function() {
  if (timer) clearInterval(timer)
  for (var k in charts) { if (charts[k]) charts[k].dispose() }
})
</script>
<style scoped>
</style>
