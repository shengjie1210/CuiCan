<template>
  <section class="services-section">
    <div class="ss-inner">
      <div class="ss-head reveal">
        <span class="ss-en">OUR SERVICES</span>
        <h2 class="ss-title">尊崇服务</h2>
        <p class="ss-sub">从匠心雕琢到终身守护，璀璨为您提供全链路尊享服务</p>
      </div>
      <div class="ss-grid">
        <div
          v-for="(s, i) in services"
          :key="s.title"
          class="ss-card reveal"
          :style="{ transitionDelay: i * 140 + 'ms' }"
          @mouseenter="hovered = i"
          @mouseleave="hovered = null">
          <div class="ss-num">{{ String(i + 1).padStart(2, "0") }}</div>
          <div class="ss-icon">
            <div class="ss-icon-inner">{{ s.icon }}</div>
            <div class="ss-icon-ring"></div>
          </div>
          <h3 class="ss-name">{{ s.title }}</h3>
          <p class="ss-desc">{{ s.description }}</p>
          <div class="ss-en-desc">{{ s.enDescription }}</div>
          <div class="ss-scan"></div>
          <div class="ss-bar"></div>
          <span class="ss-corner tl"></span>
          <span class="ss-corner tr"></span>
          <span class="ss-corner bl"></span>
          <span class="ss-corner br"></span>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from "vue";

const hovered = ref(null);

const services = [
  { icon: "💎", title: "私人定制", description: "一对一专属珠宝顾问，从草图到成品全流程定制", enDescription: "Bespoke Jewelry Design" },
  { icon: "🔍", title: "权威鉴定", description: "国家资质鉴定师 + GIA 国际证书，每一颗都有故事", enDescription: "GIA Certified & Authentication" },
  { icon: "🛡️", title: "终身守护", description: "免费清洗保养 + 终身保修 + 7 天无理由退换", enDescription: "Lifetime Care & Warranty" },
];
</script>

<style scoped>
.services-section {
  position: relative;
  padding: 80px 0 60px;
  overflow: hidden;
}
.services-section::before {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  width: 1000px;
  height: 1000px;
  background: radial-gradient(circle, rgba(212, 175, 55, 0.08), transparent 60%);
  transform: translate(-50%, -50%);
  pointer-events: none;
}
.ss-inner {
  position: relative;
  z-index: 1;
  max-width: 1320px;
  margin: 0 auto;
  padding: 0 40px;
}
.ss-head {
  text-align: center;
  margin-bottom: 48px;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.8s cubic-bezier(0.2, 0.9, 0.4, 1.1);
}
.ss-head.reveal.revealed { opacity: 1; transform: translateY(0); }
.ss-en {
  font-size: 14px;
  letter-spacing: 8px;
  color: var(--gold-300, #e8b947);
  text-transform: uppercase;
  font-weight: 600;
}
.ss-title {
  font-family: var(--heading-font, "Cormorant", serif);
  font-size: 72px;
  font-weight: 800;
  margin: 24px 0 24px;
  letter-spacing: 12px;
  background: linear-gradient(135deg, var(--gold-50, #fff9e6), var(--gold-300, #e8b947) 50%, var(--gold-500, #d4af37));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  line-height: 1.1;
}
.ss-sub {
  color: #c4bca0;
  font-size: 20px;
  letter-spacing: 4px;
}
.ss-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr); gap: 24px;
}
.ss-card {
  position: relative;
  padding: 64px 48px 56px;
  border-radius: 24px;
  background: linear-gradient(145deg, rgba(30, 25, 12, 0.6), rgba(12, 12, 12, 0.6));
  background-color: rgba(20, 20, 20, 0.6);
  border: 1px solid rgba(212, 175, 55, 0.25);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  box-shadow:
    0 24px 60px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(240, 214, 138, 0.2);
  overflow: hidden;
  opacity: 0;
  transform: translateY(40px);
  transition: all 0.7s cubic-bezier(0.16, 1, 0.3, 1);
  cursor: pointer;
}
.ss-card.reveal.revealed { opacity: 1; transform: translateY(0); }
.ss-card:hover {
  transform: translateY(-16px);
  border-color: rgba(240, 214, 138, 0.55);
  box-shadow:
    0 40px 90px rgba(0, 0, 0, 0.6),
    0 0 60px rgba(212, 175, 55, 0.25),
    inset 0 1px 0 rgba(240, 214, 138, 0.4);
}
.ss-num {
  position: absolute;
  top: 28px;
  right: 32px;
  font-family: var(--heading-font, "Cormorant", serif);
  font-size: 80px;
  font-weight: 700;
  background: linear-gradient(135deg, rgba(240, 214, 138, 0.4), rgba(212, 175, 55, 0.1));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  letter-spacing: -2px;
  line-height: 1;
  transition: all 0.4s ease;
}
.ss-card:hover .ss-num {
  background: linear-gradient(135deg, var(--gold-50, #fff9e6), var(--gold-300, #e8b947));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  transform: scale(1.1);
}
.ss-icon {
  position: relative;
  width: 96px;
  height: 96px;
  margin-bottom: 32px;
}
.ss-icon-inner {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.18), rgba(240, 214, 138, 0.08));
  border: 1px solid rgba(212, 175, 55, 0.4);
  border-radius: 50%;
  box-shadow:
    inset 0 1px 0 rgba(240, 214, 138, 0.4),
    0 0 30px rgba(212, 175, 55, 0.25);
  filter: drop-shadow(0 0 10px rgba(240, 214, 138, 0.5));
  transition: all 0.4s ease;
}
.ss-icon-ring {
  position: absolute;
  inset: -10px;
  border: 1px solid rgba(212, 175, 55, 0.25);
  border-radius: 50%;
  border-top-color: transparent;
  border-left-color: transparent;
  animation: ssRingSpin 8s linear infinite;
}
.ss-card:hover .ss-icon-inner {
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.32), rgba(240, 214, 138, 0.15));
  box-shadow:
    inset 0 1px 0 rgba(240, 214, 138, 0.6),
    0 0 40px rgba(212, 175, 55, 0.5);
  transform: scale(1.08) rotate(8deg);
}
.ss-card:hover .ss-icon-ring {
  animation-duration: 4s;
  border-color: rgba(240, 214, 138, 0.55);
}
@keyframes ssRingSpin { to { transform: rotate(360deg); } }
.ss-name {
  font-size: 32px;
  font-weight: 800;
  letter-spacing: 4px;
  margin: 0 0 16px;
  color: #efe9d6;
  position: relative;
  z-index: 1;
}
.ss-desc {
  font-size: 17px;
  color: #c4bca0;
  line-height: 1.85;
  margin: 0 0 16px;
  position: relative;
  z-index: 1;
  letter-spacing: 0.5px;
}
.ss-en-desc {
  font-family: var(--heading-font, "Cormorant", serif);
  font-size: 15px;
  color: var(--gold-300, #e8b947);
  letter-spacing: 3px;
  text-transform: uppercase;
  position: relative;
  z-index: 1;
}
.ss-scan {
  position: absolute;
  top: 0;
  left: -100%;
  width: 50%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(240, 214, 138, 0.18) 50%, transparent);
  transition: left 0.6s ease;
  pointer-events: none;
}
.ss-card:hover .ss-scan { left: 120%; }
.ss-bar {
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 3px;
  background: linear-gradient(90deg, transparent, var(--gold-300, #e8b947) 50%, transparent);
  transform: translateX(-50%);
  transition: width 0.5s ease;
}
.ss-card:hover .ss-bar { width: 80%; }
.ss-corner {
  position: absolute;
  width: 24px;
  height: 24px;
  border: 2px solid var(--gold-300, #e8b947);
  opacity: 0.4;
  transition: all 0.3s ease;
}
.ss-corner.tl { top: 16px; left: 16px; border-right: none; border-bottom: none; }
.ss-corner.tr { top: 16px; right: 16px; border-left: none; border-bottom: none; }
.ss-corner.bl { bottom: 16px; left: 16px; border-right: none; border-top: none; }
.ss-corner.br { bottom: 16px; right: 16px; border-left: none; border-top: none; }
.ss-card:hover .ss-corner {
  opacity: 1;
  width: 36px;
  height: 36px;
}
.ss-card:hover .ss-corner.tl, .ss-card:hover .ss-corner.br { background: var(--gold-300, #e8b947); }
.ss-card:hover .ss-corner.tr, .ss-card:hover .ss-corner.bl { background: var(--gold-100, #f5d36e); }
@media (max-width: 900px) {
  .ss-grid { grid-template-columns: 1fr; }
  .ss-title { font-size: 48px; letter-spacing: 6px; }
}
</style>
