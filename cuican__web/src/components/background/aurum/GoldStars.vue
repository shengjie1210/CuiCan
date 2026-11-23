<template>
  <div class="gold-stars" aria-hidden="true">
    <span
      v-for="s in stars"
      :key="s.id"
      class="star"
      :style="s.style"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue';

/**
 * GoldStars - 150+ 颗金色微星点闪烁
 * 像夜空里的金色粉尘，越小越亮
 * 不同尺寸/不同时延/不同闪烁节奏
 */
const props = defineProps({
  count: { type: Number, default: 80 },
});

function rand(min, max) {
  return Math.random() * (max - min) + min;
}

const stars = computed(() =>
  Array.from({ length: props.count }, (_, i) => {
    const size = rand(0.6, 2.4);
    const isBright = size > 1.8;
    return {
      id: i,
      style: {
        left: rand(0, 100) + '%',
        top: rand(0, 100) + '%',
        width: size + 'px',
        height: size + 'px',
        '--dur': rand(2.4, 5.5) + 's',
        '--delay': rand(0, 5) + 's',
        '--op': String(rand(isBright ? 0.7 : 0.25, isBright ? 1 : 0.6)),
        '--glow': isBright ? '8px' : '3px',
        background: isBright
          ? 'radial-gradient(circle, #fff9e6 0%, #f5d36e 50%, rgba(212, 175, 55, 0) 80%)'
          : 'radial-gradient(circle, #f5d36e 0%, rgba(212, 175, 55, 0.4) 60%, rgba(212, 175, 55, 0) 90%)',
      },
    };
  }),
);
</script>

<style scoped>
.gold-stars {
  position: fixed;
  inset: 0;
  z-index: -3;
  pointer-events: none;
  overflow: hidden;
}

.star {
  position: absolute;
  border-radius: 50%;
  animation: starTwinkle ease-in-out infinite alternate;
}

@keyframes starTwinkle {
  from {
    transform: scale(0.7);
    opacity: calc(var(--op, 0.5) * 0.55);
    box-shadow: 0 0 var(--glow, 3px) rgba(212, 175, 55, 0.4);
  }
  to {
    transform: scale(1);
    opacity: var(--op, 0.8);
    box-shadow: 0 0 var(--glow, 3px) rgba(245, 211, 110, 0.85);
  }
}
</style>
