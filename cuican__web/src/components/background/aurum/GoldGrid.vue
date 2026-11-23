<template>
  <div class="gold-grid" aria-hidden="true">
    <div class="grid-mask"></div>
    <div class="grid-inner"></div>
  </div>
</template>

<script setup>
/**
 * GoldGrid - 金色精密几何网格
 * 营造"高定腕表机芯 / 高级珠宝陈列" 的秩序感
 * 双层网格 + 径向遮罩 + 呼吸闪烁
 */
defineProps({
  size: { type: Number, default: 64 },   // 网格单元像素
  major: { type: Number, default: 256 }, // 主网格像素
  opacity: { type: Number, default: 0.07 },
});
</script>

<style scoped>
.gold-grid {
  position: fixed;
  inset: 0;
  z-index: -5;
  pointer-events: none;
  overflow: hidden;
}

.grid-inner {
  position: absolute;
  inset: -10%;
  background-image:
    linear-gradient(rgba(212, 175, 55, var(--opacity, 0.07)) 1px, transparent 1px),
    linear-gradient(90deg, rgba(212, 175, 55, var(--opacity, 0.07)) 1px, transparent 1px),
    linear-gradient(rgba(240, 214, 138, calc(var(--opacity, 0.07) * 1.6)) 1px, transparent 1px),
    linear-gradient(90deg, rgba(240, 214, 138, calc(var(--opacity, 0.07) * 1.6)) 1px, transparent 1px);
  background-size:
    64px 64px,
    64px 64px,
    256px 256px,
    256px 256px;
  background-position: 0 0, 0 0, 0 0, 0 0;
  transform: perspective(800px) rotateX(60deg) translateY(-15%) scale(1.4);
  transform-origin: 50% 0;
  animation: gridShift 60s linear infinite;
}

.grid-mask {
  position: absolute;
  inset: 0;
  background: radial-gradient(
    ellipse 80% 60% at 50% 40%,
    transparent 0%,
    transparent 30%,
    rgba(5, 5, 5, 0.4) 70%,
    rgba(5, 5, 5, 0.95) 100%
  );
  pointer-events: none;
}

@keyframes gridShift {
  from { background-position: 0 0, 0 0, 0 0, 0 0; }
  to   { background-position: 512px 0, 0 512px, 512px 0, 0 512px; }
}
</style>
