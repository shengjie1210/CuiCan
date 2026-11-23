<template>
  <div class="gold-vignette" aria-hidden="true">
    <div class="edge-top"></div>
    <div class="edge-bottom"></div>
    <div class="edge-frame"></div>
  </div>
</template>

<script setup>
/**
 * GoldVignette - 暗角 + 金色边框
 * 像高级相框一样把中央内容聚焦
 * 四角暗角 + 一圈纤细的金色装饰边框
 */
</script>

<style scoped>
.gold-vignette {
  position: fixed;
  inset: 0;
  z-index: -1;
  pointer-events: none;
  overflow: hidden;
}

/* 顶部 / 底部暗角 — 让 Header/Footer 不突兀 */
.edge-top {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 200px;
  background: linear-gradient(180deg, rgba(5, 5, 5, 0.85) 0%, transparent 100%);
}

.edge-bottom {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 240px;
  background: linear-gradient(0deg, rgba(5, 5, 5, 0.95) 0%, transparent 100%);
}

/* 金色装饰边框 */
.edge-frame {
  position: absolute;
  inset: 24px;
  border: 1px solid transparent;
  border-image: linear-gradient(
    135deg,
    rgba(212, 175, 55, 0.5) 0%,
    transparent 25%,
    transparent 75%,
    rgba(240, 214, 138, 0.4) 100%
  ) 1;
  border-radius: 4px;
  opacity: 0.45;
  mask-image: linear-gradient(
    180deg,
    transparent 0%,
    black 15%,
    black 85%,
    transparent 100%
  );
  -webkit-mask-image: linear-gradient(
    180deg,
    transparent 0%,
    black 15%,
    black 85%,
    transparent 100%
  );
}

/* 四角装饰 */
.edge-frame::before,
.edge-frame::after {
  content: "";
  position: absolute;
  width: 32px;
  height: 32px;
  border: 2px solid var(--gold-300, #e8b947);
  filter: drop-shadow(0 0 6px rgba(212, 175, 55, 0.5));
}

.edge-frame::before {
  top: -2px;
  left: -2px;
  border-right: none;
  border-bottom: none;
}

.edge-frame::after {
  bottom: -2px;
  right: -2px;
  border-left: none;
  border-top: none;
}
</style>
