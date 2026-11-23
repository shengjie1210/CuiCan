<template>
  <div class="rec-mask" @click.self="$emit('close')">
    <div class="rec-drawer">
      <header class="rec-head">
        <div class="rec-title">
          <i class="bi bi-stars"></i>
          <span>AI 智能推荐</span>
        </div>
        <button class="rec-close" @click="$emit('close')">×</button>
      </header>

      <div class="rec-filter">
        <input v-model="keyword" class="rec-input" placeholder="品类，如：戒指/项链" @keyup.enter="load" />
        <input v-model="maxPrice" class="rec-input" type="number" placeholder="预算上限" @keyup.enter="load" />
        <button class="rec-search" @click="load"><i class="bi bi-search"></i></button>
      </div>

      <div class="rec-body">
        <div
          v-for="(item, i) in items"
          :key="item.id"
          class="rec-card"
          :style="{ animationDelay: i * 0.05 + 's' }"
          @click="goDetail(item.id)">
          <img v-if="item.image" class="rec-img" :src="imgUrl(item.image)" />
          <div v-else class="rec-img placeholder">💎</div>
          <div class="rec-info">
            <div class="rec-name">{{ item.name }}</div>
            <div class="rec-price">¥{{ item.price }}</div>
            <div class="rec-sales">已售 {{ item.sales }}</div>
          </div>
        </div>

        <div v-if="!items.length" class="rec-empty">
          <div class="rec-empty-icon">💎</div>
          <p>没有找到合适的推荐，换个品类或预算试试</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import request from "@/utils/request";

const router = useRouter();
const BASE = "http://localhost:8080";
const items = ref([]);
const keyword = ref("");
const maxPrice = ref("");

function imgUrl(url) {
  if (!url) return "";
  return url.startsWith("http") ? url : BASE + url;
}

function goDetail(id) {
  router.push("/product/" + id);
}

async function load() {
  try {
    const res = await request.get("/api/ai/recommend", {
      params: {
        keyword: keyword.value || undefined,
        maxPrice: maxPrice.value ? Number(maxPrice.value) : undefined,
        limit: 8,
      },
    });
    const data = res?.data ?? [];
    items.value = Array.isArray(data) ? data : [];
  } catch (e) {
    items.value = [];
  }
}

onMounted(load);
</script>

<style scoped>
.rec-mask {
  position: fixed;
  inset: 0;
  z-index: 9001;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(3px);
  -webkit-backdrop-filter: blur(3px);
  display: flex;
  justify-content: flex-end;
}

.rec-drawer {
  width: min(400px, 92vw);
  height: 100%;
  display: flex;
  flex-direction: column;
  background: linear-gradient(160deg, rgba(24, 20, 12, 0.97), rgba(8, 8, 8, 0.99));
  border-left: 1px solid rgba(212, 175, 55, 0.35);
  box-shadow: -24px 0 70px rgba(0, 0, 0, 0.55);
  animation: recDrawerIn 0.35s cubic-bezier(0.2, 0.9, 0.3, 1);
}

@keyframes recDrawerIn {
  from { transform: translateX(60px); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}

.rec-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px;
  border-bottom: 1px solid rgba(212, 175, 55, 0.25);
  background: linear-gradient(90deg, rgba(212, 175, 55, 0.14), transparent);
}

.rec-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 2px;
  color: #f5d36e;
}

.rec-close {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  border: 1px solid rgba(212, 175, 55, 0.3);
  background: rgba(212, 175, 55, 0.12);
  color: #f5d36e;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.rec-close:hover {
  background: rgba(212, 175, 55, 0.28);
  transform: rotate(90deg);
}

.rec-filter {
  display: flex;
  gap: 8px;
  padding: 14px 18px;
  border-bottom: 1px solid rgba(212, 175, 55, 0.18);
}

.rec-input {
  flex: 1;
  min-width: 0;
  height: 38px;
  border-radius: 12px;
  border: 1px solid rgba(212, 175, 55, 0.3);
  background: rgba(20, 20, 20, 0.6);
  color: #efe9d6;
  padding: 0 12px;
  font-size: 13px;
  outline: none;
  font-family: inherit;
  transition: all 0.25s ease;
}

.rec-input:focus {
  border-color: #e8b947;
  box-shadow: 0 0 12px rgba(212, 175, 55, 0.3);
}

.rec-search {
  width: 38px;
  height: 38px;
  flex-shrink: 0;
  border-radius: 12px;
  border: 1px solid #e8b947;
  background: linear-gradient(135deg, #e8b947, #d4af37);
  color: #1a1408;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.rec-search:hover {
  box-shadow: 0 0 16px rgba(212, 175, 55, 0.5);
  transform: translateY(-1px);
}

.rec-body {
  flex: 1;
  overflow-y: auto;
  padding: 18px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  align-content: start;
}

.rec-card {
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  background: linear-gradient(145deg, rgba(30, 25, 12, 0.65), rgba(12, 12, 12, 0.65));
  border: 1px solid rgba(212, 175, 55, 0.28);
  backdrop-filter: blur(12px);
  box-shadow: 0 14px 36px rgba(0, 0, 0, 0.4);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  animation: recCardIn 0.5s ease backwards;
}

.rec-card:hover {
  transform: translateY(-4px);
  border-color: rgba(240, 214, 138, 0.55);
  box-shadow: 0 20px 44px rgba(0, 0, 0, 0.5), 0 0 18px rgba(212, 175, 55, 0.18);
}

@keyframes recCardIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.rec-img {
  width: 100%;
  height: 130px;
  object-fit: cover;
  display: block;
}

.rec-img.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  background: rgba(255, 255, 255, 0.04);
}

.rec-info {
  padding: 12px;
}

.rec-name {
  font-size: 13px;
  color: #efe9d6;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 6px;
}

.rec-price {
  font-size: 17px;
  font-weight: 800;
  color: var(--gold-300, #e8b947);
}

.rec-sales {
  font-size: 11px;
  color: #8e8770;
  margin-top: 4px;
}

.rec-empty {
  grid-column: 1 / -1;
  text-align: center;
  color: #8e8770;
  padding: 60px 0;
}

.rec-empty-icon {
  font-size: 52px;
  margin-bottom: 14px;
  filter: drop-shadow(0 0 14px rgba(212, 175, 55, 0.35));
}
</style>
