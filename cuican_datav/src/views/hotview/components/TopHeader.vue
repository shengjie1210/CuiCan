<template>
  <header class="dv-header">
    <div class="left">
      <DvDecoration8 class="deco-left" />
    </div>
    <div class="center">
      <h1 class="title">璀璨珠宝商城运营数据大盘</h1>
      <p class="sub">实时数据中台 · 欢迎来到 璀璨珠宝商城</p>
    </div>
    <div class="right">
      <DvDecoration8 class="deco-right" :reverse="true" />
      <div class="time-text">
        <div class="t">系统当前稳定运行</div>
        <div class="d">数据大屏 v1.0</div>
      </div>
    </div>
    <div class="top-bar">
      <div class="now-time">{{ now }}</div>
      <div class="week">星期{{ week }}</div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const now = ref('')
const week = ref('')
let timer = null

function update() {
  const d = new Date()
  const pad = (v) => String(v).padStart(2, '0')
  now.value = pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':' + pad(d.getSeconds())
  const w = ['日', '一', '二', '三', '四', '五', '六']
  week.value = w[d.getDay()]
}

onMounted(() => { update(); timer = setInterval(update, 1000) })
onBeforeUnmount(() => { if (timer) clearInterval(timer) })
</script>

<style scoped>
.dv-header {
  position: relative;
  height: 92px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 32px 8px;
  z-index: 2;
  overflow: hidden;
}
.dv-header::before {
  content: "";
  position: absolute;
  inset: 0;
  background:
    linear-gradient(180deg, rgba(0, 217, 255, 0.12), rgba(0, 217, 255, 0)),
    radial-gradient(ellipse at center top, rgba(0, 217, 255, 0.14), transparent 62%);
  border-bottom: 1px solid rgba(0, 217, 255, 0.18);
  pointer-events: none;
}
.left,
.right {
  position: relative;
  z-index: 1;
  width: 260px;
  display: flex;
  align-items: center;
}
.right {
  justify-content: flex-end;
}
.center {
  position: relative;
  z-index: 1;
  text-align: center;
  min-width: 0;
}
.title {
  margin: 0;
  font-size: 30px;
  font-weight: 800;
  letter-spacing: 4px;
  line-height: 1.2;
  background: linear-gradient(90deg, #5b8dff, #00d9ff 48%, #6dffb6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  filter: drop-shadow(0 0 14px rgba(0, 217, 255, 0.4));
  white-space: nowrap;
}
.sub {
  margin: 8px 0 0;
  font-size: 12px;
  color: #8aa0c4;
  letter-spacing: 2px;
}
.time-text {
  text-align: right;
  margin-right: 18px;
  line-height: 1.6;
}
.t {
  font-size: 13px;
  font-weight: 600;
  color: #d7e1f3;
}
.d {
  font-size: 11px;
  color: #8aa0c4;
}
.top-bar {
  position: absolute;
  top: 8px;
  right: 32px;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: var(--mono);
  font-size: 12px;
  color: #8aa0c4;
}
.now-time {
  font-size: 16px;
  font-weight: 700;
  color: #00d9ff;
  text-shadow: 0 0 10px rgba(0, 217, 255, 0.5);
}
.week {
  padding: 2px 8px;
  border: 1px solid rgba(0, 217, 255, 0.25);
  border-radius: 10px;
  background: rgba(0, 217, 255, 0.08);
}
.deco-left,
.deco-right {
  width: 180px;
  height: 44px;
}
</style>
