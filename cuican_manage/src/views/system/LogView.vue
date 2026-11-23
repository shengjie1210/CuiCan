<template>
  <div>
    <h5 class="mb-3">系统日志</h5>
    <div class="d-flex gap-2 flex-wrap align-items-center mb-2">
      <div class="btn-group btn-group-sm me-2">
        <button v-for="t in logThemes" :key="t.key" :class="['btn', logTheme === t.key ? 'btn-primary' : 'btn-outline-secondary']" @click="logTheme = t.key">
          <i :class="t.icon" class="me-1"></i>{{ t.label }}
        </button>
      </div>
      <div class="btn-group btn-group-sm me-2">
        <button v-for="l in levels" :key="l.key" :class="['btn', currentLevel === l.key ? l.btnClass : 'btn-outline-secondary']" @click="changeLevel(l.key)">{{ l.label }}</button>
      </div>
      <input type="date" class="form-control form-control-sm" style="width:150px" v-model="selectedDate" @change="loadLog" />
      <div class="input-group input-group-sm" style="width:140px">
        <input class="form-control" placeholder="搜索" v-model="searchText">
        <button class="btn btn-outline-secondary" @click="searchText = searchText"><i class="bi bi-search"></i></button>
      </div>
      <button class="btn btn-outline-primary btn-sm" @click="loadLog"><i class="bi bi-arrow-clockwise"></i></button>
      <button class="btn btn-sm" :class="autoRefresh ? 'btn-success' : 'btn-outline-secondary'" @click="autoRefresh = !autoRefresh">
        <i :class="autoRefresh ? 'bi bi-pause-fill' : 'bi bi-play-fill'"></i>
      </button>
      <div class="btn-group btn-group-sm">
        <button class="btn btn-outline-success btn-sm dropdown-toggle" data-bs-toggle="dropdown"><i class="bi bi-download"></i> 导出</button>
        <ul class="dropdown-menu dropdown-menu-end">
          <li><a class="dropdown-item" href="#" @click.prevent="exportLog('html')"><i class="bi bi-filetype-html me-2 text-primary"></i>HTML</a></li>
          <li><a class="dropdown-item" href="#" @click.prevent="exportLog('txt')"><i class="bi bi-filetype-txt me-2 text-secondary"></i>TXT</a></li>
          <li><a class="dropdown-item" href="#" @click.prevent="exportLog('doc')"><i class="bi bi-file-word me-2 text-primary"></i>Word</a></li>
          <li><a class="dropdown-item" href="#" @click.prevent="exportLog('pdf')"><i class="bi bi-filetype-pdf me-2 text-danger"></i>PDF</a></li>
        </ul>
      </div>
      <small class="text-muted" v-if="logLines.length && !loading">{{ logLines.length }} 行</small>
    </div>
    <div class="card border-0 shadow-sm">
      <div class="card-body p-0">
        <div ref="logContainer" style="height:calc(100vh - 300px);overflow-y:auto;font-family:Consolas,monospace;font-size:12px;line-height:1.7;padding:14px" :style="themeStyle">
          <div v-if="loading" class="text-center py-5" :style="{color:themeStyle.color}"><i class="bi bi-hourglass-split d-block fs-1 mb-2"></i>加载中...</div>
          <div v-else-if="logLines.length === 0" class="text-center py-5" :style="{color:themeStyle.color}"><i class="bi bi-inbox d-block fs-1 mb-2 opacity-25"></i>没有查询到日志信息</div>
          <div v-else-if="displayLines.length === 0" class="text-center py-5" :style="{color:themeStyle.color}"><i class="bi bi-search d-block fs-1 mb-2 opacity-25"></i>没有查询到日志信息</div>
          <div v-else v-for="(line, i) in displayLines" :key="i" :style="getLineStyle(line)" class="log-line">{{ line }}</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue"
import request from "@/utils/request"

const logLines = ref([])
const searchText = ref("")
const autoRefresh = ref(false)
const loading = ref(false)
const logTheme = ref("dark")
const currentLevel = ref("all")
const selectedDate = ref(new Date().toISOString().slice(0, 10))
let timer = null

const logThemes = [
  { key: "dark", label: "黑白", icon: "bi bi-moon-stars" },
  { key: "white", label: "白黑", icon: "bi bi-sun" },
  { key: "yellow", label: "金黄", icon: "bi bi-sunrise" },
  { key: "green", label: "绿白", icon: "bi bi-flower1" },
]
const levels = [
  { key: "all", label: "全部", btnClass: "btn-secondary" },
  { key: "error", label: "Error", btnClass: "btn-danger" },
  { key: "warn", label: "Warn", btnClass: "btn-warning" },
  { key: "info", label: "Info", btnClass: "btn-info" },
]

const themeStyle = computed(function() {
  var t = logTheme.value
  if (t === "yellow") return { background: "#fff8dc", color: "#8b6914" }
  if (t === "green") return { background: "#f0f7f0", color: "#2d6a4f" }
  if (t === "white") return { background: "#ffffff", color: "#333333" }
  return { background: "#1e1e1e", color: "#d4d4d4" }
})

const currentFile = computed(function() {
  var date = selectedDate.value
  if (!date) return ""
  if (currentLevel.value === "all") return "all-" + date + ".log"
  return currentLevel.value + "-" + date + ".log"
})

const displayLines = computed(function() {
  var kw = searchText.value.toLowerCase().trim()
  if (!kw) return logLines.value
  return logLines.value.filter(function(l) { return l.toLowerCase().includes(kw) })
})

function getLineStyle(line) {
  if (!line) return { color: themeStyle.value.color }
  var s = line.toUpperCase()
  if (s.includes("[ERROR]") || s.includes("ERROR")) return { color: "#f44747", fontWeight: "bold" }
  if (s.includes("[WARN]") || s.includes("WARN")) return { color: "#b8860b" }
  return { color: themeStyle.value.color }
}

function changeLevel(l) {
  currentLevel.value = l
  loadLog()
}

async function loadLog() {
  loading.value = true
  var file = currentFile.value
  if (!file) { logLines.value = []; loading.value = false; return }
  try {
    var r = await request.get("/admin/log/read", { params: { file: file, lines: 500 } })
    logLines.value = (r && r.data) || []
  } catch (e) {
    logLines.value = []
  }
  loading.value = false
}

function exportLog(format) {
  var lines = displayLines.value
  var fn = (currentFile.value || "log").replace(".log", "") + "_" + new Date().toISOString().slice(0, 10)
  var theme = themeStyle.value

  if (format === "txt") {
    var blob = new Blob([lines.join("\n")], { type: "text/plain;charset=utf-8" })
    downloadBlob(blob, fn + ".txt")
    return
  }

  if (format === "html" || format === "doc") {
    var ext = format === "doc" ? ".doc" : ".html"
    var htmlLines = lines.map(function(l) {
      var c = l.toUpperCase().includes("ERROR") ? "#f44747" : l.toUpperCase().includes("WARN") ? "#b8860b" : theme.color
      return "<div style='padding:2px 0;font-family:Consolas,monospace;font-size:12px;color:" + c + "'>" + escHtml(l) + "</div>"
    }).join("\n")
    var html = "<!DOCTYPE html><html><head><meta charset='utf-8'><title>" + fn + "</title><style>body{background:" + theme.background + ";padding:20px;margin:0}div{white-space:pre-wrap;word-break:break-all}</style></head><body style='background:" + theme.background + "'>" + htmlLines + "</body></html>"
    downloadBlob(new Blob([html], { type: "text/html" }), fn + ext)
    return
  }

  if (format === "pdf") {
    var pw = window.open("", "_blank")
    if (!pw) return
    var plines = lines.map(function(l) {
      var c = l.toUpperCase().includes("ERROR") ? "#f44747" : l.toUpperCase().includes("WARN") ? "#b8860b" : "#333"
      return "<div style='padding:2px 0;font-family:Consolas,monospace;font-size:11px;color:" + c + "'>" + escHtml(l) + "</div>"
    }).join("\n")
    pw.document.write("<html><head><meta charset='utf-8'><title>" + fn + "</title><style>body{padding:30px}div{white-space:pre-wrap;word-break:break-all}</style></head><body>" + plines + "</body></html>")
    pw.document.close()
    setTimeout(function() { pw.print() }, 300)
  }
}

function escHtml(s) {
  return String(s).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;")
}

function downloadBlob(blob, name) {
  var url = URL.createObjectURL(blob)
  var a = document.createElement("a")
  a.href = url; a.download = name
  document.body.appendChild(a); a.click()
  document.body.removeChild(a); URL.revokeObjectURL(url)
}

onMounted(function() {
  loadLog()
  timer = setInterval(function() {
    if (autoRefresh.value) loadLog()
  }, 5000)
})

onUnmounted(function() {
  if (timer) clearInterval(timer)
})
</script>
<style scoped>
.log-line { padding: 1px 0; white-space: pre-wrap; word-break: break-all; }
.log-line:hover { background: rgba(255,255,255,.04); }
</style>
