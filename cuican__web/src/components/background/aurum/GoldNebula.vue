<template>
  <div class="gold-nebula" aria-hidden="true">
    <div
      v-for="(b, i) in blobs"
      :key="i"
      class="blob"
      :style="b.style"
    />
  </div>
</template>

<script setup>
/**
 * GoldNebula - 大尺度金色极光晕
 * 营造"深邃虚空里漂浮的金色星云"质感
 * 3-4 个柔焦光斑缓慢漂移、缩放、呼吸
 */
defineProps({
  count: { type: Number, default: 4 },
});

const blobs = [
  {
    style: {
      '--x': '12%',
      '--y': '8%',
      '--size': '55vmax',
      '--c1': 'rgba(212, 175, 55, 0.18)',
      '--c2': 'rgba(240, 214, 138, 0.10)',
      '--c3': 'transparent',
      '--dur': '32s',
      '--delay': '0s',
      '--dx': '6%',
      '--dy': '4%',
    },
  },
  {
    style: {
      '--x': '85%',
      '--y': '92%',
      '--size': '50vmax',
      '--c1': 'rgba(245, 211, 110, 0.16)',
      '--c2': 'rgba(232, 185, 71, 0.08)',
      '--c3': 'transparent',
      '--dur': '38s',
      '--delay': '-8s',
      '--dx': '-7%',
      '--dy': '-3%',
    },
  },
  {
    style: {
      '--x': '70%',
      '--y': '12%',
      '--size': '38vmax',
      '--c1': 'rgba(212, 175, 55, 0.10)',
      '--c2': 'rgba(255, 249, 230, 0.06)',
      '--c3': 'transparent',
      '--dur': '28s',
      '--delay': '-12s',
      '--dx': '-5%',
      '--dy': '6%',
    },
  },
  {
    style: {
      '--x': '5%',
      '--y': '70%',
      '--size': '42vmax',
      '--c1': 'rgba(138, 113, 40, 0.14)',
      '--c2': 'rgba(212, 175, 55, 0.06)',
      '--c3': 'transparent',
      '--dur': '42s',
      '--delay': '-4s',
      '--dx': '4%',
      '--dy': '-5%',
    },
  },
];
</script>

<style scoped>
.gold-nebula {
  position: fixed;
  inset: 0;
  z-index: -6;
  pointer-events: none;
  overflow: hidden;
  opacity: 1;
}

.blob {
  position: absolute;
  left: var(--x, 50%);
  top: var(--y, 50%);
  width: var(--size, 60vmax);
  height: var(--size, 60vmax);
  transform: translate(-50%, -50%);
  background: radial-gradient(
    circle at 50% 50%,
    var(--c1, rgba(212, 175, 55, 0.18)) 0%,
    var(--c2, rgba(240, 214, 138, 0.10)) 35%,
    var(--c3, transparent) 70%
  );
  filter: blur(60px);
  mix-blend-mode: screen;
  animation:
    nebulaDrift var(--dur, 30s) ease-in-out infinite alternate,
    nebulaPulse 8s ease-in-out infinite;
  animation-delay: var(--delay, 0s), 0s;
}

@keyframes nebulaDrift {
  from { transform: translate(calc(-50% + 0px), calc(-50% + 0px)); }
  to {
    transform: translate(
      calc(-50% + var(--dx, 4%)),
      calc(-50% + var(--dy, 4%))
    );
  }
}

@keyframes nebulaPulse {
  0%, 100% { opacity: 0.85; }
  50%      { opacity: 1; }
}
</style>
