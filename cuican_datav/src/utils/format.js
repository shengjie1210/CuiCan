// 金额格式化工具

export function formatMoney(n, fixed = 2) {
  const num = Number(n) || 0
  return '¥' + num.toLocaleString('zh-CN', { minimumFractionDigits: fixed, maximumFractionDigits: fixed })
}

export function formatCount(n) {
  const num = Number(n) || 0
  if (num < 1000) return String(num)
  if (num < 10000) return (num / 1000).toFixed(1) + 'k'
  if (num < 100000000) return (num / 10000).toFixed(1) + '万'
  return (num / 100000000).toFixed(2) + '亿'
}

export function formatPercent(n, fixed = 1) {
  const num = Number(n) || 0
  return num.toFixed(fixed) + '%'
}

export const ORDER_STATUS = {
  [-1]: { text: '退款', color: '#909399' },
  [0]:  { text: '待支付', color: '#ffa14d' },
  [1]:  { text: '已支付', color: '#5b8dff' },
  [2]:  { text: '已发货', color: '#00d9ff' },
  [3]:  { text: '已完成', color: '#6dffb6' },
  [4]:  { text: '已取消', color: '#ff5577' }
}

export function orderStatusText(s) {
  const item = ORDER_STATUS[s]
  return item ? item.text : '未知'
}
export function orderStatusColor(s) {
  const item = ORDER_STATUS[s]
  return item ? item.color : '#909399'
}

export function formatDateTime(t) {
  if (!t) return '--'
  const d = new Date(t)
  if (isNaN(d.getTime())) return String(t)
  const pad = (v) => String(v).padStart(2, '0')
  return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate())
         + ' ' + pad(d.getHours()) + ':' + pad(d.getMinutes())
}

export function formatTime(t) {
  const d = new Date(t)
  if (isNaN(d.getTime())) return ''
  const pad = (v) => String(v).padStart(2, '0')
  return pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':' + pad(d.getSeconds())
}

export function formatBytes(bytes) {
  if (bytes == null) return 'N/A'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  let v = bytes, i = 0
  while (v >= 1024 && i < units.length - 1) { v /= 1024, i++ }
  return v.toFixed(1) + ' ' + units[i]
}
