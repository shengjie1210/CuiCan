<template>
  <section class="manifesto" ref="root">
    <div class="mf-glow mf-glow-a"></div>
    <div class="mf-glow mf-glow-b"></div>
    <div class="mf-inner" :style="{ transform: 'translateY(' + mfY + 'px)' }">
      <span class="mf-kicker">— CUI CAN · 璀璨珠宝 —</span>
      <h2 class="mf-line mf-line-1">让每一件珠宝</h2>
      <h2 class="mf-line mf-line-2">
        都成为<span class="mf-gold">传世的璀璨</span>
        <span class="mf-spark">✦</span>
      </h2>
      <div class="mf-divider"><span></span><i class="mf-gem">◆</i><span></span></div>
      <p class="mf-sub">匠心 · 甄选 · 永恒 —— 只为挚爱的你</p>
      <div class="mf-cta">
        <a href="/goods" class="mf-btn">探索臻品</a>
        <a href="#about" class="mf-btn mf-btn-ghost">品牌故事</a>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";

const root = ref(null);
const mfY = ref(0);
let ticking = false;
let observer = null;

function onScroll() {
  if (ticking) return;
  ticking = true;
  requestAnimationFrame(() => {
    if (root.value) {
      const r = root.value.getBoundingClientRect();
      const mid = r.top + r.height / 2 - window.innerHeight / 2;
      mfY.value = Math.max(-36, Math.min(36, mid * -0.12));
    }
    ticking = false;
  });
}

onMounted(() => {
  window.addEventListener("scroll", onScroll, { passive: true });
  onScroll();
  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((en) => {
        if (en.isIntersecting) {
          root.value && root.value.classList.add("in-view");
          observer.disconnect();
        }
      });
    },
    { threshold: 0.2 },
  );
  if (root.value) observer.observe(root.value);
});
onUnmounted(() => {
  window.removeEventListener("scroll", onScroll);
  if (observer) observer.disconnect();
});
</script>

<style scoped>
.manifesto {
  position: relative;
  min-height: 66vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background:
    radial-gradient(720px 420px at 12% 18%, rgba(212, 175, 55, 0.14), transparent 62%),
    radial-gradient(620px 400px at 88% 82%, rgba(212, 175, 55, 0.1), transparent 60%),
    linear-gradient(175deg, #16110a 0%, #241b0e 48%, #16110a 100%);
}
.mf-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.35;
  pointer-events: none;
}
.mf-glow-a {
  width: 340px;
  height: 340px;
  left: -80px;
  top: -60px;
  background: rgba(240, 199, 94, 0.5);
  animation: mfPulse 7s ease-in-out infinite;
}
.mf-glow-b {
  width: 300px;
  height: 300px;
  right: -70px;
  bottom: -70px;
  background: rgba(212, 175, 55, 0.4);
  animation: mfPulse 9s ease-in-out 1.5s infinite;
}
@keyframes mfPulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.28;
  }
  50% {
    transform: scale(1.25);
    opacity: 0.45;
  }
}
.mf-inner {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 80px 24px;
  will-change: transform;
}
.mf-kicker {
  display: inline-block;
  font-size: 13px;
  letter-spacing: 9px;
  color: rgba(240, 199, 94, 0.75);
  text-transform: uppercase;
  margin-bottom: 30px;
  opacity: 0;
  transform: translateY(16px);
  transition: all 0.9s ease 0.1s;
}
.mf-line {
  font-family: var(--heading-font, "Cormorant", serif);
  font-size: clamp(40px, 7vw, 92px);
  font-weight: 800;
  letter-spacing: 10px;
  color: #f5e6c0;
  line-height: 1.25;
  margin: 0;
  opacity: 0;
  transform: translateY(34px);
  transition: all 0.9s cubic-bezier(0.16, 1, 0.3, 1);
}
.mf-line-2 {
  transition-delay: 0.22s;
}
.mf-gold {
  background: linear-gradient(120deg, #f5d98a, #f0c75e 40%, #fff3d0 60%, #d4af37);
  background-size: 220% auto;
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  animation: mfShimmer 5s linear infinite;
}
@keyframes mfShimmer {
  to {
    background-position: -220% center;
  }
}
.mf-spark {
  display: inline-block;
  margin-left: 14px;
  font-size: 0.6em;
  color: #fff3d0;
  animation: mfTwinkle 2.4s ease-in-out infinite;
}
@keyframes mfTwinkle {
  0%,
  100% {
    transform: scale(1) rotate(0deg);
    opacity: 0.55;
  }
  50% {
    transform: scale(1.35) rotate(30deg);
    opacity: 1;
  }
}
.mf-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin: 34px auto 26px;
  max-width: 420px;
  opacity: 0;
  transition: opacity 1s ease 0.5s;
}
.mf-divider span {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(212, 175, 55, 0.55));
}
.mf-divider span:last-child {
  background: linear-gradient(90deg, rgba(212, 175, 55, 0.55), transparent);
}
.mf-gem {
  font-style: normal;
  color: #f0c75e;
  font-size: 13px;
  text-shadow: 0 0 12px rgba(240, 199, 94, 0.7);
}
.mf-sub {
  font-size: 16px;
  letter-spacing: 5px;
  color: rgba(245, 230, 192, 0.72);
  margin: 0 0 40px;
  opacity: 0;
  transform: translateY(18px);
  transition: all 0.9s ease 0.55s;
}
.mf-cta {
  display: flex;
  justify-content: center;
  gap: 18px;
  opacity: 0;
  transform: translateY(18px);
  transition: all 0.9s ease 0.7s;
}
.mf-btn {
  padding: 15px 44px;
  border-radius: 999px;
  font-size: 15px;
  letter-spacing: 3px;
  font-weight: 700;
  color: #20170a;
  background: linear-gradient(135deg, #f0c75e, #d4af37);
  text-decoration: none;
  box-shadow: 0 12px 30px rgba(212, 175, 55, 0.35);
  transition: all 0.3s ease;
}
.mf-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 18px 40px rgba(212, 175, 55, 0.5);
}
.mf-btn-ghost {
  background: transparent;
  color: #f5e6c0;
  border: 1px solid rgba(240, 199, 94, 0.5);
  box-shadow: none;
}
.mf-btn-ghost:hover {
  background: rgba(212, 175, 55, 0.12);
  box-shadow: 0 0 24px rgba(212, 175, 55, 0.18);
}
.manifesto.in-view .mf-kicker,
.manifesto.in-view .mf-sub,
.manifesto.in-view .mf-cta {
  opacity: 1;
  transform: translateY(0);
}
.manifesto.in-view .mf-line {
  opacity: 1;
  transform: translateY(0);
}
.manifesto.in-view .mf-divider {
  opacity: 1;
}
</style>
