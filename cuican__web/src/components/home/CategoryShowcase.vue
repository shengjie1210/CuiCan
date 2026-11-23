<template>
  <section class="cat-showcase">
    <div class="cs-bg-grid"></div>
    <div class="cs-inner">
      <div class="cs-head reveal">
        <span class="cs-tag">— EXPLORE COLLECTIONS —</span>
        <h2 class="cs-title">按分类探索</h2>
        <p class="cs-sub">从经典到新潮，每一类都为您闪耀</p>
      </div>

      <div class="cs-grid">
        <a
          v-for="(c, i) in cats"
          :key="c.id"
          class="cs-card reveal"
          :style="{ transitionDelay: (i % 7) * 100 + 'ms' }"
          :href="'/goods?categoryId=' + c.id"
          @mousemove="onTilt($event, i)"
          @mouseleave="onLeave(i)">
          <span class="cs-no">N°{{ String(i + 1).padStart(2, "0") }}</span>
          <div class="cs-ring"></div>
          <div class="cs-ring outer"></div>
          <span class="cs-char">{{ firstChar(c.name) }}</span>
          <span class="cs-name">{{ c.name }}</span>
          <span class="cs-arrow">
            探索
            <svg viewBox="0 0 24 24" width="18" height="18">
              <path d="M5 12h14M13 5l7 7-7 7" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </span>
          <span class="cs-particle p1"></span>
          <span class="cs-particle p2"></span>
          <span class="cs-particle p3"></span>
        </a>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from "vue";
import request from "@/utils/request";

const cats = ref([]);
let observer = null;

const firstChar = (n) => (n ? n.charAt(0) : "✦");

function onTilt(e) {
  const card = e.currentTarget;
  const rect = card.getBoundingClientRect();
  const x = (e.clientX - rect.left) / rect.width - 0.5;
  const y = (e.clientY - rect.top) / rect.height - 0.5;
  card.style.transform = `translateY(-16px) perspective(800px) rotateX(${-y * 8}deg) rotateY(${x * 8}deg)`;
  card.style.setProperty("--mx", `${(x + 0.5) * 100}%`);
  card.style.setProperty("--my", `${(y + 0.5) * 100}%`);
}
function onLeave(i) {
  const card = document.querySelectorAll(".cs-card")[i];
  if (card) card.style.transform = "";
}

onMounted(async () => {
  const seen = new Set();
  const all = [];
  for (const gid of [2, 5]) {
    try {
      const res = await request.get("/api/category/children", { params: { id: gid } });
      const list = Array.isArray(res) ? res : (res && res.data) || [];
      for (const c of list) {
        if (!seen.has(c.id)) {
          seen.add(c.id);
          all.push(c);
        }
      }
    } catch (e) {}
  }
  cats.value = all;
});
onUnmounted(() => {
  if (observer) observer.disconnect();
});
</script>

<style scoped>
.cat-showcase {
  position: relative;
  padding: 80px 0 60px;
  overflow: hidden;
  background: linear-gradient(180deg, transparent, rgba(212, 175, 55, 0.04) 50%, transparent);
}
.cs-bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(212, 175, 55, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(212, 175, 55, 0.06) 1px, transparent 1px);
  background-size: 80px 80px;
  mask-image: radial-gradient(ellipse 60% 60% at 50% 50%, black, transparent 80%);
  -webkit-mask-image: radial-gradient(ellipse 60% 60% at 50% 50%, black, transparent 80%);
  pointer-events: none;
}
.cs-inner {
  position: relative;
  z-index: 1;
  max-width: 1320px;
  margin: 0 auto;
  padding: 0 40px;
}
.cs-head {
  text-align: center;
  margin-bottom: 48px;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.8s cubic-bezier(0.2, 0.9, 0.4, 1.1);
}
.cs-head.reveal.revealed { opacity: 1; transform: translateY(0); }
.cs-tag {
  display: inline-block;
  font-size: 14px;
  letter-spacing: 8px;
  color: var(--gold-300, #e8b947);
  font-weight: 600;
  padding: 8px 24px;
  border: 1px solid rgba(212, 175, 55, 0.4);
  border-radius: 999px;
  background: rgba(212, 175, 55, 0.06);
}
.cs-title {
  font-family: var(--heading-font, "Cormorant", serif);
  font-size: 72px;
  font-weight: 800;
  margin: 28px 0 20px;
  letter-spacing: 12px;
  background: linear-gradient(135deg, var(--gold-50, #fff9e6), var(--gold-300, #e8b947) 50%, var(--gold-500, #d4af37));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  line-height: 1.1;
}
.cs-sub {
  color: #c4bca0;
  font-size: 20px;
  letter-spacing: 4px;
}
.cs-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}
.cs-card {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20px;
  padding: 56px 28px 40px;
  min-height: 280px;
  border-radius: 24px;
  background: linear-gradient(145deg, rgba(30, 25, 12, 0.6), rgba(12, 12, 12, 0.6));
  background-color: rgba(20, 20, 20, 0.6);
  border: 1px solid rgba(212, 175, 55, 0.25);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  box-shadow:
    0 24px 60px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(240, 214, 138, 0.2);
  text-decoration: none;
  color: #efe9d6;
  cursor: pointer;
  opacity: 0;
  transform: translateY(40px);
  transition: all 0.7s cubic-bezier(0.16, 1, 0.3, 1), transform 0.2s ease-out;
  overflow: hidden;
}
.cs-card.reveal.revealed { opacity: 1; transform: translateY(0); }
.cs-card::before {
  content: "";
  position: absolute;
  top: var(--my, 50%);
  left: var(--mx, 50%);
  width: 240px;
  height: 240px;
  background: radial-gradient(circle, rgba(240, 214, 138, 0.2), transparent 70%);
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}
.cs-card:hover::before { opacity: 1; }
.cs-card:hover {
  border-color: rgba(240, 214, 138, 0.6);
  box-shadow:
    0 36px 80px rgba(0, 0, 0, 0.6),
    0 0 50px rgba(212, 175, 55, 0.25),
    inset 0 1px 0 rgba(240, 214, 138, 0.4);
}
.cs-no {
  position: absolute;
  top: 20px;
  left: 24px;
  font-family: var(--heading-font, "Cormorant", serif);
  font-size: 15px;
  color: var(--gold-300, #e8b947);
  letter-spacing: 2px;
  font-weight: 600;
  opacity: 0.7;
}
.cs-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 150px;
  height: 150px;
  border: 1px solid rgba(212, 175, 55, 0.2);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.5s ease;
}
.cs-ring.outer {
  width: 180px;
  height: 180px;
  border-color: rgba(212, 175, 55, 0.1);
  border-style: dashed;
  animation: csRingSpin 16s linear infinite;
}
.cs-card:hover .cs-ring {
  width: 180px;
  height: 180px;
  border-color: rgba(212, 175, 55, 0.4);
  box-shadow: 0 0 50px rgba(212, 175, 55, 0.25);
}
.cs-card:hover .cs-ring.outer {
  width: 220px;
  height: 220px;
  border-color: rgba(240, 214, 138, 0.3);
  animation-duration: 6s;
}
@keyframes csRingSpin { to { transform: translate(-50%, -50%) rotate(360deg); } }
.cs-char {
  font-family: var(--heading-font, "Cormorant", serif);
  font-size: 96px;
  font-weight: 800;
  line-height: 1;
  background: linear-gradient(135deg, var(--gold-50, #fff9e6) 0%, var(--gold-300, #e8b947) 50%, var(--gold-500, #d4af37) 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  letter-spacing: -2px;
  filter: drop-shadow(0 0 16px rgba(212, 175, 55, 0.5));
  transition: all 0.4s ease;
  z-index: 1;
  position: relative;
}
.cs-card:hover .cs-char {
  transform: scale(1.15);
  filter: drop-shadow(0 0 24px rgba(240, 214, 138, 0.8));
}
.cs-name {
  font-size: 19px;
  font-weight: 600;
  letter-spacing: 4px;
  color: #efe9d6;
  z-index: 1;
  position: relative;
}
.cs-arrow {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  letter-spacing: 3px;
  color: var(--gold-300, #e8b947);
  text-transform: uppercase;
  font-weight: 600;
  opacity: 0;
  transform: translateY(8px);
  transition: all 0.3s ease;
  z-index: 1;
  position: relative;
}
.cs-card:hover .cs-arrow { opacity: 1; transform: translateY(0); }
.cs-card:hover .cs-arrow svg { animation: csArrowMove 1s ease-in-out infinite; }
@keyframes csArrowMove {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(4px); }
}
.cs-particle {
  position: absolute;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--gold-300, #e8b947);
  box-shadow: 0 0 10px var(--gold-300, #e8b947);
  opacity: 0;
  pointer-events: none;
}
.cs-particle.p1 { top: 20%; left: 10%; }
.cs-particle.p2 { top: 70%; right: 12%; }
.cs-particle.p3 { bottom: 18%; left: 30%; }
.cs-card:hover .cs-particle { animation: csParticleFloat 2s ease-in-out infinite; }
.cs-card:hover .cs-particle.p1 { animation-delay: 0s; }
.cs-card:hover .cs-particle.p2 { animation-delay: 0.4s; }
.cs-card:hover .cs-particle.p3 { animation-delay: 0.8s; }
@keyframes csParticleFloat {
  0%, 100% { opacity: 0; transform: translateY(0) scale(0.5); }
  50% { opacity: 1; transform: translateY(-14px) scale(1.2); }
}
@media (max-width: 1100px) { .cs-grid { grid-template-columns: repeat(3, 1fr); } .cs-title { font-size: 56px; letter-spacing: 6px; } }
@media (max-width: 800px) { .cs-grid { grid-template-columns: repeat(2, 1fr); } .cs-title { font-size: 48px; } }
</style>
