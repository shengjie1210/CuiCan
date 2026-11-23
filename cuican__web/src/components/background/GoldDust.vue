<template>
  <div class="gold-dust" aria-hidden="true">
    <span v-for="i in 48" :key="i" class="dust" :style="styleFor(i)"></span>
    <span v-for="i in 2" :key="'r' + i" class="ribbon" :style="ribbonStyle(i)"></span>
  </div>
</template>

<script setup>
function rand(min, max) {
  return Math.random() * (max - min) + min;
}

function styleFor() {
  const size = rand(1, 4);
  const left = rand(0, 100);
  const delay = rand(0, 16);
  const duration = rand(14, 26);
  const drift = rand(-120, 120);
  const opacity = rand(0.25, 0.6);
  return {
    left: left + "%",
    width: size + "px",
    height: size + "px",
    animationDelay: delay + "s",
    animationDuration: duration + "s, " + rand(2.4, 4.2) + "s",
    "--drift": drift + "px",
    "--op": String(opacity),
  };
}

function ribbonStyle(i) {
  const top = rand(8, 88);
  const width = rand(320, 560);
  const duration = rand(18, 28);
  const delay = rand(0, 10);
  const rotate = rand(-8, 8);
  const opacity = rand(0.18, 0.42);
  return {
    top: top + "%",
    width: width + "px",
    animationDelay: delay + "s",
    animationDuration: duration + "s",
    "--rotate": rotate + "deg",
    "--op": String(opacity),
  };
}
</script>

<style scoped>
.gold-dust {
  position: fixed;
  inset: 0;
  z-index: 1;
  pointer-events: none;
  overflow: hidden;
  mix-blend-mode: normal;
  opacity: 0.7;
}

.dust {
  position: absolute;
  top: -14px;
  border-radius: 50%;
  background: radial-gradient(circle, #ffd98a 0%, #e8b64c 55%, rgba(232, 182, 76, 0) 78%);
  box-shadow: 0 0 6px rgba(255, 200, 90, 0.6);
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
    rgba(255, 233, 179, 0.9) 50%,
    rgba(212, 175, 55, 0.5) 70%,
    transparent 100%
  );
  filter: blur(1px);
  mix-blend-mode: normal;
  animation: ribbonFlow linear infinite;
}

@keyframes ribbonFlow {
  0% {
    transform: translateX(0) rotate(var(--rotate, 0deg));
    opacity: 0;
  }
  12% {
    opacity: var(--op, 0.6);
  }
  85% {
    opacity: var(--op, 0.6);
  }
  100% {
    transform: translateX(140vw) rotate(var(--rotate, 0deg));
    opacity: 0;
  }
}

@keyframes dustFlow {
  0% {
    transform: translate3d(0, -20px, 0);
    opacity: 0;
  }
  10% {
    opacity: var(--op, 0.7);
  }
  85% {
    opacity: var(--op, 0.7);
  }
  100% {
    transform: translate3d(var(--drift, 0px), 108vh, 0);
    opacity: 0;
  }
}

@keyframes dustTwinkle {
  from {
    filter: brightness(0.92);
  }
  to {
    filter: brightness(1.15);
  }
}
</style>
