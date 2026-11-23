<template>
  <!-- 斜向金色流星 + 浮尘背景层（固定覆盖，不拦截点击） -->
  <div class="gm-layer" aria-hidden="true">
    <span
      v-for="m in meteors"
      :key="'m' + m.id"
      class="gm-meteor"
      :style="meteorStyle(m)"></span>
    <span
      v-for="d in dusts"
      :key="'d' + d.id"
      class="gm-dust"
      :style="dustStyle(d)"></span>
  </div>
</template>

<script setup>
// 流星参数：从右上角出发 -> 飞向左下角。
// top = 出发点高度（右上角区域），ty = 落到左下角的垂直位移
const meteors = [
  { id: 1, top: "2%",  ty: "94vh", dur: "12s", delay: "0s",  w: 260 },
  { id: 2, top: "10%", ty: "86vh", dur: "16s", delay: "6s",  w: 340 },
  { id: 3, top: "18%", ty: "80vh", dur: "14s", delay: "10s", w: 220 },
  { id: 4, top: "26%", ty: "72vh", dur: "18s", delay: "3s",  w: 300 },
  { id: 5, top: "34%", ty: "66vh", dur: "15s", delay: "13s", w: 240 },
];

// 浮尘：位置 / 大小 / 时长 / 延迟
const dusts = Array.from({ length: 14 }, (_, i) => ({
  id: i,
  left: (i * 7.3 + 4) % 100,
  top: (i * 13.7 + 6) % 100,
  size: i % 3 === 0 ? 4 : 2.5,
  dur: (7 + (i % 5) * 2.3) + "s",
  delay: (i % 6) * 0.9 + "s",
}));

const meteorStyle = (m) => ({
  top: m.top,
  left: "100vw",
  width: m.w + "px",
  "--ty": m.ty,
  animationDuration: m.dur,
  animationDelay: m.delay,
});

const dustStyle = (d) => ({
  left: d.left + "%",
  top: d.top + "%",
  width: d.size + "px",
  height: d.size + "px",
  animationDuration: d.dur,
  animationDelay: d.delay,
});
</script>

<style scoped>
.gm-layer {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

/* ===== 流星（斜向、带尾翼） ===== */
.gm-meteor {
  position: absolute;
  height: 2px;
  border-radius: 999px;
  background: linear-gradient(
    90deg,
    #fffdf4 0%,
    #ffe9b8 14%,
    rgba(240, 199, 94, 0.55) 38%,
    rgba(212, 175, 55, 0.18) 62%,
    rgba(212, 175, 55, 0) 100%
  );
  box-shadow:
    0 0 6px rgba(240, 199, 94, 0.8),
    0 0 16px rgba(212, 175, 55, 0.4);
  transform: rotate(18deg);
  opacity: 0;
  animation-name: gmFall;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
  will-change: transform, opacity;
}

/* 流星头部光点 */
.gm-meteor::before {
  content: "";
  position: absolute;
  left: -3px;
  top: 50%;
  width: 9px;
  height: 9px;
  transform: translateY(-50%);
  border-radius: 50%;
  background: radial-gradient(circle, #ffffff 0%, #fff3d0 35%, rgba(255, 243, 208, 0) 72%);
  filter: blur(0.4px);
}

@keyframes gmFall {
  0% {
    transform: translate3d(0, 0, 0) rotate(18deg);
    opacity: 0;
  }
  5% {
    opacity: var(--gm-o, 0.85);
  }
  100% {
    transform: translate3d(-170vw, var(--ty, 80vh), 0) rotate(18deg);
    opacity: 0;
  }
}

/* ===== 浮尘粒子 ===== */
.gm-dust {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 243, 208, 0.9), rgba(240, 199, 94, 0.25) 60%, transparent 75%);
  opacity: 0;
  animation-name: gmFloat;
  animation-timing-function: ease-in-out;
  animation-iteration-count: infinite;
  will-change: transform, opacity;
}

@keyframes gmFloat {
  0% {
    transform: translate3d(0, 0, 0);
    opacity: 0;
  }
  25% {
    opacity: 0.55;
  }
  100% {
    transform: translate3d(14px, -110px, 0);
    opacity: 0;
  }
}

/* 小屏弱化，避免干扰阅读 */
@media (max-width: 768px) {
  .gm-meteor {
    animation-duration: 20s !important;
  }
}
</style>
