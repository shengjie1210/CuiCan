<template>
  <section class="brand-stats">
    <div class="bs-inner">
      <div class="bs-head reveal">
        <span class="bs-en">CUI CAN DATA</span>
        <h2 class="bs-title">璀璨数据</h2>
        <p class="bs-sub">匠心雕琢每一件珠宝，用数据见证璀璨时刻</p>
      </div>
      <div class="bs-grid">
        <div v-for="(s, i) in stats" :key="s.label" class="bs-card reveal" :style="{ transitionDelay: i * 120 + 'ms' }">
          <div class="bs-num">
            <span class="bs-val" :data-count="s.value" :data-decimals="s.decimals || 0">0</span>
            <span class="bs-suffix">{{ s.suffix }}</span>
          </div>
          <div class="bs-label">{{ s.label }}</div>
          <div class="bs-line"></div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, onUnmounted } from "vue";

const stats = [
  { label: "匠心之作", value: 6000, suffix: "+", decimals: 0 },
  { label: "注册会员", value: 12000, suffix: "+", decimals: 0 },
  { label: "好评率", value: 99.8, suffix: "%", decimals: 1 },
  { label: "品牌门店", value: 26, suffix: "+", decimals: 0 },
];

let observer = null;
const animated = new Set();

function runCount(el) {
  const target = Number(el.dataset.count || 0);
  const decimals = Number(el.dataset.decimals || 0);
  const dur = 1800;
  const start = performance.now();
  const tick = (now) => {
    const p = Math.min((now - start) / dur, 1);
    const eased = 1 - Math.pow(1 - p, 3);
    el.textContent = (target * eased).toFixed(decimals);
    if (p < 1) requestAnimationFrame(tick);
    else el.textContent = target.toFixed(decimals);
  };
  requestAnimationFrame(tick);
}

onMounted(() => {
  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((en) => {
        if (en.isIntersecting && !animated.has(en.target)) {
          animated.add(en.target);
          en.target.classList.add("revealed");
          const num = en.target.querySelector(".bs-val");
          if (num) runCount(num);
        }
      });
    },
    { threshold: 0.25 },
  );
  document.querySelectorAll(".brand-stats .reveal").forEach((el) => observer.observe(el));
});
onUnmounted(() => {
  if (observer) observer.disconnect();
});
</script>

<style scoped>
.brand-stats {
  padding: 80px 0 72px;
  background: linear-gradient(160deg, #14100a 0%, #251c0e 55%, #14100a 100%);
  position: relative;
  overflow: hidden;
}
.brand-stats::before {
  content: "";
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse 50% 40% at 25% 30%, rgba(212, 175, 55, 0.08), transparent 60%),
    radial-gradient(ellipse 50% 40% at 75% 70%, rgba(240, 214, 138, 0.06), transparent 60%);
  pointer-events: none;
}
.bs-inner { position: relative; z-index: 1; max-width: 1320px; margin: 0 auto; padding: 0 40px; }
.bs-head { text-align: center; margin-bottom: 48px; opacity: 0; transform: translateY(20px); transition: all 0.8s cubic-bezier(0.2, 0.9, 0.4, 1.1); }
.bs-head.reveal.revealed { opacity: 1; transform: translateY(0); }
.bs-en { font-size: 14px; letter-spacing: 8px; color: var(--gold-300, #e8b947); text-transform: uppercase; font-weight: 600; }
.bs-title { font-family: var(--heading-font, "Cormorant", serif); font-size: 64px; font-weight: 800; margin: 24px 0 20px; letter-spacing: 10px; background: linear-gradient(135deg, var(--gold-50, #fff9e6), var(--gold-300, #e8b947) 50%, var(--gold-500, #d4af37)); -webkit-background-clip: text; background-clip: text; color: transparent; line-height: 1.1; }
.bs-sub { color: #c4bca0; font-size: 20px; letter-spacing: 4px; }
.bs-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 32px; }
.bs-card {
  position: relative;
  padding: 56px 24px 40px;
  border-radius: 24px;
  background: linear-gradient(145deg, rgba(30, 25, 12, 0.6), rgba(12, 12, 12, 0.6));
  background-color: rgba(20, 20, 20, 0.6);
  border: 1px solid rgba(212, 175, 55, 0.25);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.5), inset 0 1px 0 rgba(240, 214, 138, 0.2);
  text-align: center;
  opacity: 0;
  transform: translateY(40px);
  transition: all 0.7s cubic-bezier(0.16, 1, 0.3, 1);
}
.bs-card.reveal.revealed { opacity: 1; transform: translateY(0); }
.bs-card:hover { transform: translateY(-8px); border-color: rgba(240, 214, 138, 0.55); box-shadow: 0 32px 80px rgba(0, 0, 0, 0.6), 0 0 50px rgba(212, 175, 55, 0.2), inset 0 1px 0 rgba(240, 214, 138, 0.4); }
.bs-num { display: flex; align-items: baseline; justify-content: center; gap: 4px; margin-bottom: 16px; }
.bs-val {
  font-family: var(--heading-font, "Cormorant", serif);
  font-size: 72px;
  font-weight: 800;
  background: linear-gradient(135deg, var(--gold-50, #fff9e6) 0%, var(--gold-300, #e8b947) 50%, var(--gold-500, #d4af37) 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  line-height: 1;
  filter: drop-shadow(0 0 20px rgba(212, 175, 55, 0.4));
}
.bs-suffix { font-family: var(--heading-font, "Cormorant", serif); font-size: 40px; font-weight: 600; color: var(--gold-300, #e8b947); }
.bs-label { font-size: 17px; font-weight: 600; color: #c4bca0; letter-spacing: 3px; }
.bs-line { width: 50px; height: 2px; background: linear-gradient(90deg, transparent, var(--gold-300, #e8b947), transparent); margin: 18px auto 0; }
@media (max-width: 960px) { .bs-grid { grid-template-columns: repeat(2, 1fr); } .bs-title { font-size: 48px; } }
</style>
