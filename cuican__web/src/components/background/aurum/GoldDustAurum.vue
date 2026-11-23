<template>
  <div class="gold-dust-aurum" aria-hidden="true">
    <span
      v-for="d in dusts"
      :key="d.id"
      class="dust"
      :style="d.style"
    />
    <span
      v-for="(r, i) in ribbons"
      :key="'r' + i"
      class="ribbon"
      :style="r"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue';

/**
 * GoldDustAurum - 增强版金色尘埃 + 光带
 * 继承 GoldDust 的设计但提高对比度（适配黑底）
 * 加入 comet 拖尾效果
 */
const props = defineProps({
  dustCount:  { type: Number, default: 56 },
  ribbonCount:{ type: Number, default: 3 },
});

function rand(min, max) {
  return Math.random() * (max - min) + min;
}

const dusts = computed(() =>
  Array.from({ length: props.dustCount }, (_, i) => {
    const size = rand(1, 4);
    return {
      id: i,
      style: {
        left: rand(0, 100) + '%',
        width: size + 'px',
        height: size + 'px',
        '--drift': rand(-160, 160) + 'px',
        '--op': String(rand(0.35, 0.85)),
        animationDelay: rand(0, 18) + 's',
        animationDuration:
          rand(14, 28) + 's, ' + rand(2.2, 4.4) + 's',
      },
    };
  }),
);

const ribbons = computed(() =>
  Array.from({ length: props.ribbonCount }, (_, i) => ({
    style: {
      top: rand(8, 90) + '%',
      width: rand(380, 620) + 'px',
      '--rotate': rand(-6, 6) + 'deg',
      '--op': String(rand(0.22, 0.55)),
      animationDelay: rand(0, 12) + 's',
      animationDuration: rand(20, 32) + 's',
    },
  })),
);
</script>

<style scoped>
.gold-dust-aurum {
  position: fixed;
  inset: 0;
  z-index: -2;
  pointer-events: none;
  overflow: hidden;
  mix-blend-mode: screen;
}

.dust {
  position: absolute;
  top: -14px;
  border-radius: 50%;
  background: radial-gradient(circle, #fff9e6 0%, #f5d36e 50%, rgba(212, 175, 55, 0) 80%);
  box-shadow: 0 0 8px rgba(245, 211, 110, 0.7);
  animation:
    dustFlow linear infinite,
    dustTwinkle ease-in-out infinite alternate;
}

.ribbon {
  position: absolute;
  left: -30%;
  height: 1.5px;
  border-radius: 999px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(212, 175, 55, 0.5) 30%,
    rgba(255, 249, 230, 0.95) 50%,
    rgba(245, 211, 110, 0.5) 70%,
    transparent 100%
  );
  filter: blur(1px);
  animation: ribbonFlow linear infinite;
}

@keyframes dustFlow {
  0% {
    transform: translate3d(0, -20px, 0);
    opacity: 0;
  }
  10% { opacity: var(--op, 0.7); }
  85% { opacity: var(--op, 0.7); }
  100% {
    transform: translate3d(var(--drift, 0px), 108vh, 0);
    opacity: 0;
  }
}

@keyframes dustTwinkle {
  from { filter: brightness(0.95); }
  to   { filter: brightness(1.25); }
}

@keyframes ribbonFlow {
  0% {
    transform: translateX(0) rotate(var(--rotate, 0deg));
    opacity: 0;
  }
  12% { opacity: var(--op, 0.6); }
  85% { opacity: var(--op, 0.6); }
  100% {
    transform: translateX(140vw) rotate(var(--rotate, 0deg));
    opacity: 0;
  }
}
</style>
