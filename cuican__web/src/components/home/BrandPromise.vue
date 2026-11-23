<template>
  <div class="brand-promise">
    <div class="bp-track">
      <div v-for="(t, i) in items" :key="'a' + i" class="bp-item">
        <span class="bp-icon">{{ t.icon }}</span>
        <span class="bp-text">{{ t.text }}</span>
        <span class="bp-sep">✦</span>
      </div>
      <div v-for="(t, i) in items" :key="'b' + i" class="bp-item" aria-hidden="true">
        <span class="bp-icon">{{ t.icon }}</span>
        <span class="bp-text">{{ t.text }}</span>
        <span class="bp-sep">✦</span>
      </div>
    </div>
  </div>
</template>

<script setup>
const items = [
  { icon: "💎", text: "天然宝石 · 权威认证" },
  { icon: "🛡️", text: "正品保障 · 假一赔十" },
  { icon: "🚚", text: "顺丰包邮 · 极速送达" },
  { icon: "↩️", text: "7 天无理由退换" },
  { icon: "🔍", text: "专家鉴定 · 品质护航" },
  { icon: "🎁", text: "会员专属 · 生日礼遇" },
];
</script>

<style scoped>
.brand-promise {
  overflow: hidden;
  padding: 18px 0;
  background: linear-gradient(90deg, rgba(212, 175, 55, 0.14), rgba(212, 175, 55, 0.04), rgba(212, 175, 55, 0.14));
  border-top: 1px solid rgba(212, 175, 55, 0.28);
  border-bottom: 1px solid rgba(212, 175, 55, 0.28);
}
.bp-track { display: flex; width: max-content; animation: bpMarquee 30s linear infinite; }
.brand-promise:hover .bp-track { animation-play-state: paused; }
.bp-item {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  white-space: nowrap;
  padding: 0 48px;
  font-size: 19px;
  font-weight: 600;
  color: var(--gold-100, #f5d36e);
  letter-spacing: 2px;
}
.bp-icon { font-size: 24px; filter: drop-shadow(0 0 6px rgba(212, 175, 55, 0.4)); }
.bp-text { background: linear-gradient(135deg, var(--gold-100, #f5d36e), var(--gold-300, #e8b947)); -webkit-background-clip: text; background-clip: text; color: transparent; }
.bp-sep { color: rgba(212, 175, 55, 0.4); font-size: 14px; padding: 0 8px; }
@keyframes bpMarquee {
  from { transform: translateX(0); }
  to { transform: translateX(-50%); }
}
</style>
