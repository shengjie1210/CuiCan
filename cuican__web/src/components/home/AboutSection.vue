<template>
  <section id="about">
    <div class="ab-bg"></div>
    <div class="container-filud">
      <div class="ab-head reveal">
        <span class="ab-tag">— OUR STORY —</span>
        <h2 class="ab-en">Brand Story</h2>
        <h3 class="ab-title">囍 XI 婚庆系列</h3>
        <p class="ab-sub">每一件璀璨，都是一段故事的开始</p>
      </div>
      <div class="ab-row">
        <div class="ab-img-wrap reveal" style="transition-delay: 0.1s">
          <div class="ab-img-frame">
            <img :src="aboutImage" alt="about" class="ab-img" />
            <div class="ab-img-glow"></div>
            <span class="ab-corner tl"></span>
            <span class="ab-corner tr"></span>
            <span class="ab-corner bl"></span>
            <span class="ab-corner br"></span>
          </div>
          <div class="ab-img-badge">
            <span class="ab-badge-num">20+</span>
            <span class="ab-badge-label">年匠心传承</span>
          </div>
        </div>
        <div class="ab-content reveal" style="transition-delay: 0.25s">
          <p class="ab-paragraph">囍系列是璀璨珠宝为婚庆推出的年度新品，寓意喜庆与祝福。每一对戒指都承载着两个人从相识到相守的全部故事。</p>
          <p class="ab-paragraph en">The Wedding Series is a new annual product launched by Brilliant Jewelry for weddings, implying joy and blessings.</p>
          <div class="ab-features">
            <div class="ab-feat" v-for="(f, i) in features" :key="i" :style="{ transitionDelay: (0.4 + i * 0.1) + 's' }">
              <div class="ab-feat-icon">{{ f.icon }}</div>
              <div class="ab-feat-text">
                <div class="ab-feat-name">{{ f.name }}</div>
                <div class="ab-feat-desc">{{ f.desc }}</div>
              </div>
            </div>
          </div>
          <div class="ab-cta-row">
            <a href="#about" class="ab-btn"><span>了解品牌</span><span class="ab-btn-arrow">→</span></a>
            <a href="#about" class="ab-btn ab-btn-outline"><span>工艺之旅</span></a>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, onUnmounted } from "vue";

const aboutImage = new URL("@/assets/image/首页图/新品热卖图.jpg", import.meta.url).href;

const features = [
  { icon: "✦", name: "GIA 国际认证", desc: "每一颗钻石都有独立证书" },
  { icon: "◆", name: "18K 精工镶嵌", desc: "60+ 道工序，每一道都严苛" },
  { icon: "❖", name: "终身保修服务", desc: "一次购买，永久守护" },
  { icon: "✧", name: "私人定制专家", desc: "一对一专属设计服务" },
];

let observer = null;
onMounted(() => {
  observer = new IntersectionObserver((entries) => {
    entries.forEach((en) => {
      if (en.isIntersecting) {
        en.target.classList.add("revealed");
        observer.unobserve(en.target);
      }
    });
  }, { threshold: 0.15 });
  document.querySelectorAll("#about .reveal").forEach((el) => observer.observe(el));
});
onUnmounted(() => { if (observer) observer.disconnect(); });
</script>

<style scoped>
#about {
  position: relative;
  padding: 96px 0 80px;
  overflow: hidden;
}
.ab-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 20% 30%, rgba(212, 175, 55, 0.08), transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(240, 214, 138, 0.06), transparent 50%);
  pointer-events: none;
}
.container-filud {
  position: relative;
  z-index: 1;
  max-width: 1320px;
  margin: 0 auto;
  padding: 0 40px;
}
.ab-head {
  text-align: center;
  margin-bottom: 48px;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.8s cubic-bezier(0.2, 0.9, 0.4, 1.1);
}
.ab-head.reveal.revealed { opacity: 1; transform: translateY(0); }
.ab-tag {
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
.ab-en {
  font-family: var(--heading-font, "Cormorant", serif);
  font-size: 32px;
  color: var(--gold-300, #e8b947);
  letter-spacing: 12px;
  text-transform: uppercase;
  margin: 24px 0 8px;
  font-weight: 400;
  font-style: italic;
}
.ab-title {
  font-family: var(--heading-font, "Cormorant", serif);
  font-size: 72px;
  font-weight: 800;
  letter-spacing: 12px;
  margin: 8px 0 24px;
  background: linear-gradient(135deg, var(--gold-50, #fff9e6), var(--gold-300, #e8b947) 50%, var(--gold-500, #d4af37));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  line-height: 1.1;
}
.ab-sub {
  color: #c4bca0;
  font-size: 20px;
  letter-spacing: 4px;
}
.ab-row {
  display: flex;
  gap: 80px;
  align-items: center;
}
.ab-img-wrap {
  position: relative;
  flex: 0 0 540px;
  opacity: 0;
  transform: translateX(-40px);
  transition: all 0.9s cubic-bezier(0.2, 0.9, 0.4, 1.1);
}
.ab-img-wrap.reveal.revealed { opacity: 1; transform: translateX(0); }
.ab-img-frame {
  position: relative;
  border-radius: 24px;
  overflow: visible;
  aspect-ratio: 1 / 1;
}
.ab-img {
  width: 100%;
  height: 100%;
  border-radius: 24px;
  object-fit: cover;
  display: block;
  border: 1px solid rgba(212, 175, 55, 0.35);
  box-shadow:
    0 40px 80px rgba(0, 0, 0, 0.55),
    0 0 60px rgba(212, 175, 55, 0.15),
    inset 0 1px 0 rgba(240, 214, 138, 0.25);
  position: relative;
  z-index: 2;
  transition: all 0.5s ease;
}
.ab-img-wrap:hover .ab-img {
  transform: scale(1.02);
  box-shadow:
    0 50px 100px rgba(0, 0, 0, 0.6),
    0 0 70px rgba(212, 175, 55, 0.3),
    inset 0 1px 0 rgba(240, 214, 138, 0.4);
}
.ab-img-glow {
  position: absolute;
  inset: 0;
  border-radius: 24px;
  background: radial-gradient(circle at var(--mx, 50%) var(--my, 50%), rgba(240, 214, 138, 0.25), transparent 50%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
  z-index: 3;
}
.ab-img-wrap:hover .ab-img-glow { opacity: 1; }
.ab-corner {
  position: absolute;
  width: 32px;
  height: 32px;
  border: 2px solid var(--gold-300, #e8b947);
  z-index: 4;
}
.ab-corner.tl { top: -16px; left: -16px; border-right: none; border-bottom: none; }
.ab-corner.tr { top: -16px; right: -16px; border-left: none; border-bottom: none; }
.ab-corner.bl { bottom: -16px; left: -16px; border-right: none; border-top: none; }
.ab-corner.br { bottom: -16px; right: -16px; border-left: none; border-top: none; }
.ab-img-badge {
  position: absolute;
  bottom: -28px;
  right: -28px;
  background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37) 50%, var(--gold-700, #8a7128));
  color: #1a1408;
  border-radius: 24px;
  padding: 28px 36px;
  text-align: center;
  z-index: 5;
  box-shadow:
    0 16px 40px rgba(212, 175, 55, 0.4),
    inset 0 1px 0 rgba(255, 249, 230, 0.4);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}
.ab-badge-num {
  font-family: var(--heading-font, "Cormorant", serif);
  font-size: 56px;
  font-weight: 800;
  line-height: 1;
}
.ab-badge-label {
  font-size: 13px;
  letter-spacing: 3px;
  font-weight: 600;
}
.ab-content {
  flex: 1;
  opacity: 0;
  transform: translateX(40px);
  transition: all 0.9s cubic-bezier(0.2, 0.9, 0.4, 1.1);
}
.ab-content.reveal.revealed { opacity: 1; transform: translateX(0); }
.ab-paragraph {
  font-size: 19px;
  color: #c4bca0;
  line-height: 2;
  margin-bottom: 24px;
  letter-spacing: 1px;
}
.ab-paragraph.en {
  font-family: var(--heading-font, "Cormorant", serif);
  font-size: 18px;
  color: var(--gold-300, #e8b947);
  font-style: italic;
  line-height: 1.8;
  padding-left: 24px;
  border-left: 3px solid rgba(212, 175, 55, 0.4);
  margin: 32px 0 48px;
}
.ab-features {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 48px;
}
.ab-feat {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  border-radius: 16px;
  background: rgba(20, 20, 20, 0.5);
  border: 1px solid rgba(212, 175, 55, 0.18);
  backdrop-filter: blur(8px);
  opacity: 0;
  transform: translateY(12px);
  transition: all 0.6s cubic-bezier(0.2, 0.9, 0.4, 1.1);
}
.ab-content.reveal.revealed .ab-feat { opacity: 1; transform: translateY(0); }
.ab-feat:hover {
  border-color: rgba(240, 214, 138, 0.5);
  background: rgba(212, 175, 55, 0.08);
  transform: translateX(6px);
}
.ab-feat-icon {
  font-size: 30px;
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.18), rgba(240, 214, 138, 0.08));
  color: var(--gold-300, #e8b947);
  flex-shrink: 0;
}
.ab-feat-name {
  font-size: 17px;
  font-weight: 700;
  color: #efe9d6;
  letter-spacing: 1px;
  margin-bottom: 4px;
}
.ab-feat-desc {
  font-size: 14px;
  color: #8e8770;
  letter-spacing: 0.5px;
}
.ab-cta-row {
  display: flex;
  gap: 16px;
}
.ab-btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 18px 40px;
  border-radius: 56px;
  background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37) 50%, var(--gold-700, #8a7128));
  color: #1a1408;
  border: 1px solid var(--gold-300, #e8b947);
  font-weight: 700;
  letter-spacing: 3px;
  text-decoration: none;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow:
    0 10px 28px rgba(212, 175, 55, 0.35),
    inset 0 1px 0 rgba(255, 249, 230, 0.4);
}
.ab-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 40px rgba(212, 175, 55, 0.55);
}
.ab-btn-arrow {
  display: inline-block;
  font-size: 18px;
  transition: transform 0.3s ease;
}
.ab-btn:hover .ab-btn-arrow {
  transform: translateX(4px);
}
.ab-btn-outline {
  background: transparent;
  color: var(--gold-100, #f5d36e);
  border-color: rgba(212, 175, 55, 0.4);
  box-shadow: none;
}
.ab-btn-outline:hover {
  background: rgba(212, 175, 55, 0.1);
  border-color: var(--gold-300, #e8b947);
}
@media (max-width: 992px) {
  .ab-row { flex-direction: column; gap: 64px; }
  .ab-img-wrap { flex: 0 0 auto; width: 100%; max-width: 540px; margin: 0 auto; }
  .ab-img-badge { right: 16px; }
  .ab-features { grid-template-columns: 1fr; }
  .ab-title { font-size: 56px; letter-spacing: 6px; }
  .ab-content { text-align: center; }
  .ab-paragraph.en { border-left: none; padding-left: 0; }
  .ab-cta-row { justify-content: center; flex-wrap: wrap; }
}
</style>
