<template>
  <div class="gold-rays" aria-hidden="true">
    <div
      v-for="(r, i) in rays"
      :key="i"
      class="ray"
      :style="r"
    />
  </div>
</template>

<script setup>
/**
 * GoldRays - 自上而下的金色光柱
 * 像高级珠宝店天花板投下的射灯，营造聚光感
 * 多条窄光柱带轻微倾斜 + 缓慢漂移
 */
const rays = [
  {
    left: '8%',
    width: '1.5px',
    height: '110vh',
    background: 'linear-gradient(180deg, transparent 0%, rgba(245, 211, 110, 0.55) 30%, rgba(212, 175, 55, 0.25) 70%, transparent 100%)',
    animationDuration: '14s',
    animationDelay: '0s',
    opacity: 0.7,
    rotate: '-3deg',
  },
  {
    left: '28%',
    width: '1px',
    height: '90vh',
    background: 'linear-gradient(180deg, transparent 0%, rgba(255, 249, 230, 0.4) 40%, rgba(212, 175, 55, 0.15) 80%, transparent 100%)',
    animationDuration: '18s',
    animationDelay: '-5s',
    opacity: 0.55,
    rotate: '2deg',
  },
  {
    left: '48%',
    width: '1.5px',
    height: '100vh',
    background: 'linear-gradient(180deg, transparent 0%, rgba(240, 214, 138, 0.6) 25%, rgba(138, 113, 40, 0.2) 75%, transparent 100%)',
    animationDuration: '16s',
    animationDelay: '-8s',
    opacity: 0.65,
    rotate: '-1deg',
  },
  {
    left: '68%',
    width: '1px',
    height: '95vh',
    background: 'linear-gradient(180deg, transparent 0%, rgba(212, 175, 55, 0.5) 35%, rgba(245, 211, 110, 0.18) 80%, transparent 100%)',
    animationDuration: '20s',
    animationDelay: '-3s',
    opacity: 0.5,
    rotate: '3deg',
  },
  {
    left: '85%',
    width: '2px',
    height: '85vh',
    background: 'linear-gradient(180deg, transparent 0%, rgba(255, 249, 230, 0.6) 30%, rgba(212, 175, 55, 0.22) 75%, transparent 100%)',
    animationDuration: '22s',
    animationDelay: '-12s',
    opacity: 0.6,
    rotate: '-2deg',
  },
];
</script>

<style scoped>
.gold-rays {
  position: fixed;
  inset: 0;
  z-index: -4;
  pointer-events: none;
  overflow: hidden;
  mix-blend-mode: screen;
}

.ray {
  position: absolute;
  top: -10vh;
  filter: blur(1.5px);
  transform-origin: top center;
  animation: rayFloat linear infinite;
  animation-duration: inherit;
  animation-delay: inherit;
  opacity: var(--op, 0.6);
  transform: rotate(var(--rot, 0deg));
}

@keyframes rayFloat {
  0%   { transform: translateX(0) rotate(var(--rot, 0deg)); opacity: 0; }
  10%  { opacity: var(--op, 0.6); }
  50%  { transform: translateX(40px) rotate(var(--rot, 0deg)); opacity: var(--op, 0.6); }
  90%  { opacity: var(--op, 0.6); }
  100% { transform: translateX(-40px) rotate(var(--rot, 0deg)); opacity: 0; }
}
</style>
