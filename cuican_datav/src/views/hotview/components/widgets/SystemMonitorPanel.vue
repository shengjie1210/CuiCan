<template>
  <div class="sys-monitor">
    <div class="info-grid">
      <div class="info-cell">
        <span class="info-label">运行时长</span>
        <span class="info-value">{{ monitor.uptime || '--' }}</span>
        <span class="info-label">Java 版本</span>
        <span class="info-value">{{ monitor.javaVersion || '--' }}</span>
      </div>
      <div class="info-cell">
        <span class="info-label">操作系统</span>
        <span class="info-value">{{ monitor.osName || '--' }}</span>
        <span class="info-label">CPU 核心</span>
        <span class="info-value">{{ monitor.cpuCores || '--' }}</span>
      </div>
    </div>
    <div class="gauges">
      <div class="gauge">
        <span class="gauge-label">CPU</span>
        <div class="gauge-bar">
          <div class="gauge-fill" :style="barStyle(monitor.cpuUsage, '#00d9ff')">
            <span class="gauge-num">{{ fmtPercent(monitor.cpuUsage) }}</span>
          </div>
        </div>
      </div>
      <div class="gauge">
        <span class="gauge-label">JVM</span>
        <div class="gauge-bar">
          <div class="gauge-fill" :style="barStyle(monitor.jvmRate, '#b153ff')">
            <span class="gauge-num">{{ fmtPercent(monitor.jvmRate) }}</span>
          </div>
        </div>
        <span class="gauge-sub">{{ monitor.jvmUsed || '--' }} / {{ monitor.jvmMax || '--' }}</span>
      </div>
      <div class="gauge">
        <span class="gauge-label">内存</span>
        <div class="gauge-bar">
          <div class="gauge-fill" :style="barStyle(monitor.sysRate, '#ffce6e')">
            <span class="gauge-num">{{ fmtPercent(monitor.sysRate) }}</span>
          </div>
        </div>
        <span class="gauge-sub">{{ monitor.sysUsed || '--' }} / {{ monitor.sysTotal || '--' }}</span>
      </div>
      <div class="gauge">
        <span class="gauge-label">磁盘</span>
        <div class="gauge-bar">
          <div class="gauge-fill" :style="barStyle(monitor.diskRate, '#6dffb6')">
            <span class="gauge-num">{{ fmtPercent(monitor.diskRate) }}</span>
          </div>
        </div>
        <span class="gauge-sub">{{ monitor.diskUsed || '--' }} / {{ monitor.diskTotal || '--' }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'

const store = useDashboardStore()
const monitor = computed(() => store.monitor || {})

function barStyle(percentStr, c) {
  if (!percentStr || percentStr === 'N/A') return { width: '0%', background: c + '66' }
  const num = parseFloat(String(percentStr).replace('%', ''))
  const w = isNaN(num) ? 0 : Math.min(num, 100)
  return { width: w + '%', background: 'linear-gradient(90deg, ' + c + '80, ' + c + ')' }
}

function fmtPercent(v) {
  if (v == null || v === 'N/A') return v || 'N/A'
  return String(v).replace(/^\./, '0.')
}
</script>

<style scoped>
.sys-monitor {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 11px
}
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  flex-shrink: 0
}
.info-cell {
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(0, 217, 255, 0.12);
  border-radius: 4px;
  padding: 6px 10px;
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 3px 10px;
  align-items: center;
  min-width: 0
}
.info-label {
  color: #8aa0c4;
  font-size: 10px;
  white-space: nowrap
}
.info-value {
  color: #fff;
  font-family: var(--mono);
  font-size: 11px;
  font-weight: 600;
  text-align: right;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis
}
.gauges {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 6px 12px;
  align-content: center
}
.gauge {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr) auto;
  gap: 6px;
  align-items: center;
  min-width: 0;
  flex: 1 1 46%;
}
.gauge-label {
  color: #d7e1f3;
  font-weight: 600;
  letter-spacing: 1px;
  font-size: 10px;
  white-space: nowrap
}
.gauge-bar {
  height: 16px;
  background: rgba(0, 217, 255, 0.08);
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  min-width: 0
}
.gauge-fill {
  height: 100%;
  border-radius: 8px;
  transition: width 0.5s ease;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right: 6px;
  min-width: 0;
  white-space: nowrap
}
.gauge-num {
  color: #fff;
  font-family: var(--mono);
  font-size: 10px;
  font-weight: 700;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5)
}
.gauge-sub {
  color: #8aa0c4;
  font-family: var(--mono);
  font-size: 10px;
  white-space: nowrap
}
</style>
