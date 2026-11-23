/**
 * 璀璨管理后台 — 公共工具函数
 * 从各个页面抽取的重复函数，统一放在这里
 */

export const BASE = "http://localhost:8080"

/** 拼接完整图片 URL */
export function getImg(url) {
  if (!url) return ""
  if (url.startsWith("http")) return url
  return BASE + url
}

/** 订单状态 → 中文文本 */
export function statusText(s) {
  return { "-1": "已取消", "0": "待付款", "1": "待发货", "2": "已发货", "3": "已完成" }[String(s)] || "未知"
}

/** 订单状态 → Bootstrap badge class */
export function statusBadge(s) {
  const m = {
    "-1": "badge bg-secondary",
    "0": "badge bg-warning",
    "1": "badge bg-primary",
    "2": "badge bg-info",
    "3": "badge bg-success"
  }
  return m[String(s)] || "badge bg-light text-dark"
}

/** 格式化时间戳 → yyyy-MM-dd HH:mm:ss */
export function formatTime(t) {
  return t ? String(t).substring(0, 19).replace("T", " ") : ""
}

/** 角色ID → 中文文本 */
export function roleText(r) {
  return { 1: "用户", 2: "管理员", 3: "超级管理员" }[r] || "未知"
}

/** 角色ID → Bootstrap badge class */
export function roleBadge(r) {
  const m = { 1: "badge bg-secondary", 2: "badge bg-warning text-dark", 3: "badge bg-danger" }
  return m[r] || "badge bg-light"
}

/** 导出表格到文件 */
export function exportTable(rows, headers, filename, format = "xls") {
  const thHtml = headers.map(h => `<th>${h.label}</th>`).join("")
  const tdHtml = rows.map(row => {
    return "<tr>" + headers.map(h => {
      const val = h.key ? String(row[h.key] ?? "") : (h.render ? h.render(row) : "")
      return `<td>${val}</td>`
    }).join("") + "</tr>"
  }).join("")

  if (format === "html") {
    let h = `<!DOCTYPE html><html><head><meta charset="utf-8"><title>${filename}</title>`
    h += `<style>body{font-family:"Microsoft YaHei",sans-serif;padding:20px}
h2{color:#333;margin-bottom:16px}
table{border-collapse:collapse;width:100%;font-size:13px}
th{background:#e6a23c;color:#fff;padding:8px 10px;text-align:left;white-space:nowrap}
td{padding:6px 10px;border-bottom:1px solid #eee}
</style></head><body><h2>${filename}</h2>`
    h += `<table><tr>${thHtml}</tr>${tdHtml}</table></body></html>`
    _download(h, "text/html", filename + ".html")
    return
  }
  const xml = `<table><tr>${thHtml}</tr>${tdHtml}</table>`
  const html = `<html><meta charset="utf-8"><body>${xml}</body></html>`
  _download(html, "application/vnd.ms-excel", filename + ".xls")
}

function _download(content, mime, filename) {
  const blob = new Blob([content], { type: mime })
  const url = URL.createObjectURL(blob)
  const a = document.createElement("a")
  a.href = url; a.download = filename
  document.body.appendChild(a); a.click()
  document.body.removeChild(a); URL.revokeObjectURL(url)
}
