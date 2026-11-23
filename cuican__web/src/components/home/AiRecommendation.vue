<template>
  <section v-if="items.length" class="ai-recommend">
    <div class="rec-head">
      <div class="rec-title">
        <span class="rec-icon">✦</span>
        <h3 class="rec-name">AI 智能推荐</h3>
        <span class="rec-sub">按热销与你的喜好挑选</span>
      </div>
    </div>

    <div class="rec-grid">
      <div
        v-for="(item, i) in items"
        :key="item.id"
        class="rec-card"
        :style="{ animationDelay: i * 0.06 + 's' }"
        @click="goDetail(item.id)">
        <div class="rec-img-wrap">
          <img v-if="item.image" class="rec-img" :src="imgUrl(item.image)" />
          <div v-else class="rec-img-placeholder">💎</div>
          <div class="rec-shine"></div>
        </div>
        <div class="rec-info">
          <div class="rec-product-name">{{ item.name }}</div>
          <div class="rec-meta">
            <span class="rec-price">¥{{ item.price }}</span>
            <span class="rec-sales">已售 {{ item.sales }}</span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import request from "@/utils/request";

const router = useRouter();
const items = ref([]);
const BASE = "http://localhost:8080";

function imgUrl(url) {
  if (!url) return "";
  return url.startsWith("http") ? url : BASE + url;
}

function goDetail(id) {
  router.push("/product/" + id);
}

onMounted(async () => {
  try {
    const res = await request.get("/api/ai/recommend", { params: { limit: 6 } });
    const data = res?.data ?? [];
    items.value = Array.isArray(data) ? data : [];
  } catch (e) {
    items.value = [];
  }
});
</script>

<style scoped>
.ai-recommend {
  position: relative;
  max-width: 1400px;
  margin: 26px auto 0;
  padding: 0 32px;
}

.rec-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.rec-title {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.rec-icon {
  color: var(--gold-300, #e8b947);
  font-size: 20px;
  filter: drop-shadow(0 0 8px rgba(212, 175, 55, 0.5));
}

.rec-name {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  letter-spacing: 3px;
  color: var(--gold-100, #f5d36e);
}

.rec-sub {
  font-size: 13px;
  color: #c4bca0;
  letter-spacing: 1px;
}

.rec-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 18px;
}

.rec-card {
  border-radius: 18px;
  padding: 12px;
  cursor: pointer;
  background: linear-gradient(145deg, rgba(30, 25, 12, 0.6), rgba(12, 12, 12, 0.6));
  border: 1px solid rgba(212, 175, 55, 0.28);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  box-shadow:
    0 18px 50px rgba(0, 0, 0, 0.45),
    inset 0 1px 0 rgba(240, 214, 138, 0.14);
  transition:
    transform 0.35s cubic-bezier(0.16, 1, 0.3, 1),
    box-shadow 0.35s ease,
    border-color 0.35s ease;
  animation: recIn 0.7s cubic-bezier(0.16, 1, 0.3, 1) backwards;
}

.rec-card:hover {
  transform: translateY(-6px);
  border-color: rgba(240, 214, 138, 0.55);
  box-shadow:
    0 26px 60px rgba(0, 0, 0, 0.55),
    0 0 24px rgba(212, 175, 55, 0.2),
    inset 0 1px 0 rgba(240, 214, 138, 0.3);
}

@keyframes recIn {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.98);
    filter: blur(6px);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
    filter: blur(0);
  }
}

.rec-img-wrap {
  position: relative;
  height: 150px;
  border-radius: 12px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.04);
}

.rec-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.rec-card:hover .rec-img {
  transform: scale(1.08);
}

.rec-img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44px;
}

.rec-shine {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent 30%, rgba(240, 214, 138, 0.15) 50%, transparent 70%);
  pointer-events: none;
}

.rec-info {
  padding: 12px 4px 4px;
}

.rec-product-name {
  font-size: 14px;
  color: #efe9d6;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 8px;
}

.rec-meta {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 8px;
}

.rec-price {
  font-size: 18px;
  font-weight: 800;
  color: var(--gold-300, #e8b947);
}

.rec-sales {
  font-size: 11px;
  color: #8e8770;
}

@media (max-width: 1200px) {
  .rec-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 900px) {
  .rec-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .ai-recommend {
    padding: 0 16px;
  }
}
</style>
