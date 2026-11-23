<template>
  <section class="br-section">
    <div class="br-bg"></div>
    <div class="br-inner">
      <div class="br-head reveal">
        <span class="br-tag">— CUSTOMER VOICES —</span>
        <h2 class="br-title">真实客户说</h2>
        <p class="br-sub">每一份璀璨，都值得被珍藏与分享</p>
      </div>
      <div class="br-grid">
        <div v-for="(r, i) in reviews" :key="r.id" class="br-card reveal" :style="{ transitionDelay: i * 140 + 'ms' }">
          <div class="br-head-row">
            <div class="br-avatar"><span>{{ r.avatar }}</span><span class="br-avatar-ring"></span></div>
            <div class="br-info">
              <div class="br-name">{{ r.name }}</div>
              <div class="br-meta">
                <span class="br-role">{{ r.role }}</span>
                <span class="br-dot">·</span>
                <span class="br-date">{{ r.date }}</span>
              </div>
            </div>
            <div class="br-rating">
              <span v-for="s in 5" :key="s" :class="['br-star', { filled: s <= r.rating }]">★</span>
            </div>
          </div>
          <div class="br-content">
            <span class="br-quote-mark">"</span>
            <p class="br-text">{{ r.content }}</p>
          </div>
          <div class="br-footer">
            <div class="br-product">
              <img :src="r.image" :alt="r.product" class="br-product-img" />
              <div class="br-product-info">
                <div class="br-product-label">购买商品</div>
                <div class="br-product-name gold-text">{{ r.product }}</div>
              </div>
            </div>
            <span class="br-verified">✓ 认证购买</span>
          </div>
          <span class="br-corner tl"></span>
          <span class="br-corner br"></span>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
const reviews = [
  { id: 1, avatar: "L", name: "李女士", role: "VIP 会员", date: "2026.07", rating: 5, content: "收到了婚戒真的太惊艳了，金色光泽超级正，钻石也透亮，戴上手的瞬间老公都看呆了。整个购买流程非常专业，鉴定证书齐全，放心入手。", image: new URL("@/assets/image/首页图/璀璨·天空·挚爱之戒.jpg", import.meta.url).href, product: "璀璨·天空·挚爱之戒" },
  { id: 2, avatar: "W", name: "王先生", role: "钻石 VIP", date: "2026.06", rating: 5, content: "给老婆的生日礼物，定制服务超乎想象。顾问老师耐心沟通每一处细节，成品比设计图还精致。快递包装也是艺术品级别，开箱仪式感拉满。", image: new URL("@/assets/image/首页图/璀璨·爱与美结晶钻戒.jpg", import.meta.url).href, product: "璀璨·爱与美结晶钻戒" },
  { id: 3, avatar: "Z", name: "张小姐", role: "黄金会员", date: "2026.05", rating: 5, content: "回购第 3 件了！从第一款耳环到现在的手镯，每一件都没让我失望。GIA 证书 + 终身保养，让人非常安心。强烈推荐给所有追求品质的姐妹。", image: new URL("@/assets/image/首页图/璀璨·絮语·天使的王冠.jpg", import.meta.url).href, product: "璀璨·絮语·天使的王冠" },
];
</script>

<style scoped>
.br-section { position: relative; padding: 80px 0 72px; overflow: hidden; }
.br-bg {
  position: absolute; inset: 0;
  background:
    radial-gradient(ellipse 50% 40% at 25% 30%, rgba(212, 175, 55, 0.08), transparent 60%),
    radial-gradient(ellipse 50% 40% at 75% 70%, rgba(240, 214, 138, 0.06), transparent 60%);
  pointer-events: none;
}
.br-inner { position: relative; z-index: 1; max-width: 1320px; margin: 0 auto; padding: 0 40px; }
.br-head { text-align: center; margin-bottom: 48px; opacity: 0; transform: translateY(20px); transition: all 0.8s cubic-bezier(0.2, 0.9, 0.4, 1.1); }
.br-head.reveal.revealed { opacity: 1; transform: translateY(0); }
.br-tag { display: inline-block; font-size: 14px; letter-spacing: 8px; color: var(--gold-300, #e8b947); font-weight: 600; padding: 8px 24px; border: 1px solid rgba(212, 175, 55, 0.4); border-radius: 999px; background: rgba(212, 175, 55, 0.06); }
.br-title { font-family: var(--heading-font, "Cormorant", serif); font-size: 72px; font-weight: 800; letter-spacing: 12px; margin: 24px 0 20px; background: linear-gradient(135deg, var(--gold-50, #fff9e6), var(--gold-300, #e8b947) 50%, var(--gold-500, #d4af37)); -webkit-background-clip: text; background-clip: text; color: transparent; line-height: 1.1; }
.br-sub { color: #c4bca0; font-size: 20px; letter-spacing: 4px; }
.br-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.br-card {
  position: relative; padding: 40px; border-radius: 24px;
  background: linear-gradient(145deg, rgba(30, 25, 12, 0.6), rgba(12, 12, 12, 0.6));
  background-color: rgba(20, 20, 20, 0.6);
  border: 1px solid rgba(212, 175, 55, 0.25);
  backdrop-filter: blur(18px); -webkit-backdrop-filter: blur(18px);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.5), inset 0 1px 0 rgba(240, 214, 138, 0.2);
  opacity: 0; transform: translateY(40px); transition: all 0.7s cubic-bezier(0.16, 1, 0.3, 1); overflow: hidden;
}
.br-card.reveal.revealed { opacity: 1; transform: translateY(0); }
.br-card:hover { transform: translateY(-10px); border-color: rgba(240, 214, 138, 0.55); box-shadow: 0 32px 80px rgba(0, 0, 0, 0.6), 0 0 50px rgba(212, 175, 55, 0.2), inset 0 1px 0 rgba(240, 214, 138, 0.4); }
.br-head-row { display: flex; align-items: center; gap: 16px; margin-bottom: 28px; }
.br-avatar {
  position: relative; width: 64px; height: 64px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37) 50%, var(--gold-700, #8a7128));
  color: #1a1408; font-size: 28px; font-weight: 700;
  font-family: var(--heading-font, "Cormorant", serif);
  flex-shrink: 0; box-shadow: 0 6px 20px rgba(212, 175, 55, 0.35);
}
.br-avatar-ring { position: absolute; inset: -5px; border: 1.5px dashed rgba(212, 175, 55, 0.4); border-radius: 50%; animation: brRingSpin 10s linear infinite; }
.br-card:hover .br-avatar-ring { animation-duration: 5s; border-color: rgba(240, 214, 138, 0.7); }
@keyframes brRingSpin { to { transform: rotate(360deg); } }
.br-info { flex: 1; min-width: 0; }
.br-name { font-size: 19px; font-weight: 700; color: #efe9d6; margin-bottom: 4px; }
.br-meta { display: flex; align-items: center; gap: 6px; font-size: 14px; color: #8e8770; }
.br-role { color: var(--gold-300, #e8b947); font-weight: 600; }
.br-dot { color: #6c6552; }
.br-rating { display: flex; gap: 2px; }
.br-star { font-size: 18px; color: rgba(212, 175, 55, 0.25); text-shadow: 0 0 4px rgba(212, 175, 55, 0.3); transition: all 0.3s ease; }
.br-star.filled { color: var(--gold-300, #e8b947); text-shadow: 0 0 10px rgba(212, 175, 55, 0.6); animation: brStarPop 0.4s ease; }
.br-card:hover .br-star.filled { animation: brStarShine 0.6s ease; }
@keyframes brStarPop { from { transform: scale(0); } 60% { transform: scale(1.3); } to { transform: scale(1); } }
@keyframes brStarShine { 0%, 100% { filter: brightness(1); } 50% { filter: brightness(1.6); } }
.br-content { position: relative; padding: 20px 0 28px; margin-bottom: 28px; border-top: 1px dashed rgba(212, 175, 55, 0.18); border-bottom: 1px dashed rgba(212, 175, 55, 0.18); }
.br-quote-mark { position: absolute; top: -16px; left: 0; font-family: var(--heading-font, "Cormorant", serif); font-size: 72px; line-height: 1; color: var(--gold-300, #e8b947); opacity: 0.3; }
.br-text { font-size: 17px; color: #c4bca0; line-height: 1.95; letter-spacing: 0.5px; margin: 0; position: relative; z-index: 1; }
.br-footer { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.br-product { display: flex; align-items: center; gap: 14px; flex: 1; min-width: 0; }
.br-product-img { width: 64px; height: 64px; border-radius: 12px; object-fit: cover; background: rgba(255, 255, 255, 0.04); border: 1px solid rgba(212, 175, 55, 0.2); flex-shrink: 0; }
.br-product-info { min-width: 0; }
.br-product-label { font-size: 12px; color: #8e8770; letter-spacing: 1px; margin-bottom: 2px; }
.br-product-name { font-size: 15px; font-weight: 600; letter-spacing: 1px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.br-verified { font-size: 13px; color: #34d399; letter-spacing: 1px; padding: 6px 12px; border-radius: 999px; background: rgba(52, 211, 153, 0.12); border: 1px solid rgba(52, 211, 153, 0.3); white-space: nowrap; }
.br-corner { position: absolute; width: 28px; height: 28px; border: 2px solid var(--gold-300, #e8b947); opacity: 0.5; transition: opacity 0.3s ease; }
.br-corner.tl { top: 14px; left: 14px; border-right: none; border-bottom: none; }
.br-corner.br { bottom: 14px; right: 14px; border-left: none; border-top: none; }
.br-card:hover .br-corner { opacity: 1; }
@media (max-width: 1000px) { .br-grid { grid-template-columns: 1fr; } .br-title { font-size: 48px; letter-spacing: 6px; } }
</style>
