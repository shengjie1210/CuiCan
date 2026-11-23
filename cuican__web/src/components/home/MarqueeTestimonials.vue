<template>
  <section class="mt-section">
    <div class="mt-bg"></div>
    <div class="mt-head reveal">
      <span class="mt-tag">— HAPPY CUSTOMERS —</span>
      <h2 class="mt-title">来自全球的璀璨赞誉</h2>
    </div>
    <div class="mt-row mt-row-1">
      <div class="mt-track">
        <div v-for="(t, i) in row1" :key="'a' + i" class="mt-card">
          <div class="mt-card-inner">
            <div class="mt-quote-mark">"</div>
            <p class="mt-text">{{ t.text }}</p>
            <div class="mt-meta">
              <div class="mt-avatar">{{ t.avatar }}</div>
              <div>
                <div class="mt-name">{{ t.name }}</div>
                <div class="mt-loc">{{ t.loc }} · {{ t.stars }}★</div>
              </div>
            </div>
          </div>
        </div>
        <div v-for="(t, i) in row1" :key="'aa' + i" class="mt-card" aria-hidden="true">
          <div class="mt-card-inner">
            <div class="mt-quote-mark">"</div>
            <p class="mt-text">{{ t.text }}</p>
            <div class="mt-meta">
              <div class="mt-avatar">{{ t.avatar }}</div>
              <div>
                <div class="mt-name">{{ t.name }}</div>
                <div class="mt-loc">{{ t.loc }} · {{ t.stars }}★</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="mt-row mt-row-2">
      <div class="mt-track reverse">
        <div v-for="(t, i) in row2" :key="'b' + i" class="mt-card">
          <div class="mt-card-inner">
            <div class="mt-quote-mark">"</div>
            <p class="mt-text">{{ t.text }}</p>
            <div class="mt-meta">
              <div class="mt-avatar">{{ t.avatar }}</div>
              <div>
                <div class="mt-name">{{ t.name }}</div>
                <div class="mt-loc">{{ t.loc }} · {{ t.stars }}★</div>
              </div>
            </div>
          </div>
        </div>
        <div v-for="(t, i) in row2" :key="'bb' + i" class="mt-card" aria-hidden="true">
          <div class="mt-card-inner">
            <div class="mt-quote-mark">"</div>
            <p class="mt-text">{{ t.text }}</p>
            <div class="mt-meta">
              <div class="mt-avatar">{{ t.avatar }}</div>
              <div>
                <div class="mt-name">{{ t.name }}</div>
                <div class="mt-loc">{{ t.loc }} · {{ t.stars }}★</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
const baseTestimonials = [
  { avatar: "L", name: "Linda", loc: "上海", stars: 5, text: "从选购到定制，顾问 Lily 给了非常专业的建议，成品比预想还美，包装也充满仪式感。" },
  { avatar: "M", name: "Michael", loc: "纽约", stars: 5, text: "海外购买过程丝滑，快递 7 天到家，钻石比证书上的还闪耀。" },
  { avatar: "S", name: "Sophie", loc: "巴黎", stars: 5, text: "璀璨的法式设计让我惊艳，复古与现代的融合，完美匹配我的 dress。" },
  { avatar: "Z", name: "周先生", loc: "北京", stars: 5, text: "求婚钻戒选了这里，老婆第一眼就说“太闪了”，认证齐全，强烈推荐。" },
  { avatar: "K", name: "Kenji", loc: "东京", stars: 5, text: "和日本的工艺完全不同的设计风格，金色质感更温暖，细节处理精致。" },
  { avatar: "Y", name: "Yuki", loc: "首尔", stars: 5, text: "闺蜜婚戒推荐来的，开箱瞬间被金光闪到，终身保养让人很安心。" },
  { avatar: "A", name: "Anna", loc: "伦敦", stars: 5, text: "客服英文邮件回复超快，定制流程清晰透明，戒指实物比照片还惊艳。" },
  { avatar: "陈", name: "陈先生", loc: "深圳", stars: 5, text: "回购第三次了，品质一如既往的稳定。GIA 证书 + 鉴定师签字，特别放心。" },
  { avatar: "L", name: "Lisa", loc: "悉尼", stars: 5, text: "璀璨珠宝的 K 金工艺世界一流，佩戴一年无任何变色，售后服务也贴心。" },
  { avatar: "高", name: "高女士", loc: "杭州", stars: 5, text: "老公送我的周年礼物，开箱时金光闪闪的体验，仪式感直接拉满。" },
];
const row1 = baseTestimonials.slice(0, 5);
const row2 = baseTestimonials.slice(5);
</script>

<style scoped>
.mt-section { position: relative; padding: 80px 0 72px; overflow: hidden; }
.mt-bg { position: absolute; inset: 0; background: linear-gradient(180deg, transparent, rgba(212, 175, 55, 0.04) 50%, transparent); pointer-events: none; }
.mt-head { text-align: center; margin-bottom: 48px; position: relative; z-index: 1; opacity: 0; transform: translateY(20px); transition: all 0.8s cubic-bezier(0.2, 0.9, 0.4, 1.1); }
.mt-head.reveal.revealed { opacity: 1; transform: translateY(0); }
.mt-tag { display: inline-block; font-size: 14px; letter-spacing: 8px; color: var(--gold-300, #e8b947); font-weight: 600; padding: 8px 24px; border: 1px solid rgba(212, 175, 55, 0.4); border-radius: 999px; background: rgba(212, 175, 55, 0.06); }
.mt-title { font-family: var(--heading-font, "Cormorant", serif); font-size: 64px; font-weight: 800; letter-spacing: 10px; margin: 24px 0 0; background: linear-gradient(135deg, var(--gold-50, #fff9e6), var(--gold-300, #e8b947) 50%, var(--gold-500, #d4af37)); -webkit-background-clip: text; background-clip: text; color: transparent; line-height: 1.1; }
.mt-row { position: relative; z-index: 1; margin-bottom: 32px; mask-image: linear-gradient(90deg, transparent 0%, black 8%, black 92%, transparent 100%); -webkit-mask-image: linear-gradient(90deg, transparent 0%, black 8%, black 92%, transparent 100%); }
.mt-track { display: flex; gap: 28px; width: max-content; animation: mtMarquee 60s linear infinite; }
.mt-track.reverse { animation: mtMarquee 60s linear infinite reverse; }
.mt-row:hover .mt-track { animation-play-state: paused; }
@keyframes mtMarquee { from { transform: translateX(0); } to { transform: translateX(-50%); } }
.mt-card { flex-shrink: 0; width: 480px; }
.mt-card-inner {
  position: relative; padding: 36px 40px; border-radius: 20px;
  background: linear-gradient(145deg, rgba(30, 25, 12, 0.6), rgba(12, 12, 12, 0.6));
  background-color: rgba(20, 20, 20, 0.6);
  border: 1px solid rgba(212, 175, 55, 0.25);
  backdrop-filter: blur(16px); -webkit-backdrop-filter: blur(16px);
  box-shadow: 0 20px 56px rgba(0, 0, 0, 0.45), inset 0 1px 0 rgba(240, 214, 138, 0.2);
  transition: all 0.4s ease; cursor: pointer; overflow: hidden;
}
.mt-card-inner:hover { transform: translateY(-6px); border-color: rgba(240, 214, 138, 0.55); box-shadow: 0 32px 80px rgba(0, 0, 0, 0.55), 0 0 40px rgba(212, 175, 55, 0.2), inset 0 1px 0 rgba(240, 214, 138, 0.4); }
.mt-quote-mark { position: absolute; top: 8px; right: 20px; font-family: var(--heading-font, "Cormorant", serif); font-size: 64px; line-height: 1; color: var(--gold-300, #e8b947); opacity: 0.35; }
.mt-text { font-size: 17px; color: #c4bca0; line-height: 1.85; margin: 0 0 24px; min-height: 90px; }
.mt-meta { display: flex; align-items: center; gap: 14px; padding-top: 20px; border-top: 1px dashed rgba(212, 175, 55, 0.2); }
.mt-avatar { width: 48px; height: 48px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37)); color: #1a1408; font-weight: 700; font-size: 20px; font-family: var(--heading-font, "Cormorant", serif); box-shadow: 0 0 14px rgba(212, 175, 55, 0.4); flex-shrink: 0; }
.mt-name { font-size: 17px; font-weight: 700; color: #efe9d6; letter-spacing: 1px; }
.mt-loc { font-size: 13px; color: var(--gold-300, #e8b947); letter-spacing: 1px; }
</style>
