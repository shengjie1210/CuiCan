<template>
  <div class="favorite-page bg-dark">
    <BlackGoldBackground />

    <div class="inner">
      <div class="head">
        <h2 class="title gold-text-strong">⭐ 我的收藏</h2>
        <span class="count">{{ list.length }} 件珍品</span>
      </div>

      <div class="grid" v-if="list.length">
        <div
          class="card"
          v-for="item in list"
          :key="item.id"
          @click="goDetail(item.id)">
          <div class="card-line"></div>
          <div class="img-wrap">
            <img :src="item.image" />
            <div class="img-shine"></div>
          </div>
          <div class="info">
            <div class="name">{{ item.name }}</div>
            <div class="price gold-text-strong">¥ {{ item.price }}</div>
            <button class="view-btn">查看详情</button>
          </div>
        </div>
      </div>

      <div class="empty" v-else>
        <div class="empty-icon">💎</div>
        <p class="empty-text">还没有收藏任何珍品</p>
        <button class="empty-btn" @click="$router.push('/goods')">去探索</button>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, onMounted } from "vue";
  import { useRouter } from "vue-router";
  import request from "@/utils/request";
  import BlackGoldBackground from "@/components/background/BlackGoldBackground.vue";

  const router = useRouter();
  const list = ref([]);
  const BASE_URL = "http://localhost:8080";
  const defaultImg = "https://via.placeholder.com/300x300?text=No+Image";

  const load = async () => {
    const res = await request.get("/favorite/list");
    const arr = res.data || [];

    const result = await Promise.all(
      arr.map(async (item) => {
        const productRes = await request.get("/api/product/detail?id=" + item.productId);
        const product = productRes.data.product;
        const images = productRes.data.images || [];
        let img = images.length ? images[0].imageUrl : "";
        if (img && !img.startsWith("http")) img = BASE_URL + img;
        return { ...product, image: img || defaultImg };
      }),
    );
    list.value = result;
  };

  const goDetail = (id) => router.push("/product/" + id);

  onMounted(load);
</script>

<style scoped>
  .favorite-page {
    min-height: 100vh;
    position: relative;
    z-index: 0;
    padding: 32px 0 60px;
  }

  .inner {
    position: relative;
    z-index: 2;
    max-width: 1140px;
    margin: 0 auto;
    padding: 0 32px;
  }

  .back-bar {
    font-size: 14px;
    color: var(--gold-100, #f5d36e);
    cursor: pointer;
    margin-bottom: 16px;
    display: inline-block;
    transition: all 0.25s ease;
    letter-spacing: 1px;
  }
  .back-bar:hover {
    color: var(--gold-50, #fff9e6);
    transform: translateX(-4px);
    text-shadow: 0 0 12px rgba(212, 175, 55, 0.55);
  }

  .head {
    display: flex;
    align-items: baseline;
    gap: 16px;
    margin-bottom: 36px;
  }

  .title {
    font-size: 32px;
    margin: 0;
    letter-spacing: 4px;
    font-weight: 800;
    font-family: var(--heading-font);
  }

  .count {
    color: var(--gold-300, #e8b947);
    font-size: 14px;
    letter-spacing: 2px;
    text-transform: uppercase;
  }

  @keyframes fadeUpCard {
    from { opacity: 0; transform: translateY(18px); }
    to { opacity: 1; transform: translateY(0); }
  }
  .grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
  }

  .card {
    animation: fadeUpCard 0.5s cubic-bezier(0.2, 0.9, 0.4, 1) backwards;
    position: relative;
    padding: 14px;
    border-radius: 20px;
    background: linear-gradient(145deg, rgba(30, 25, 12, 0.55), rgba(12, 12, 12, 0.55));
    background-color: rgba(20, 20, 20, 0.6);
    border: 1px solid rgba(212, 175, 55, 0.28);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    box-shadow:
      0 18px 50px rgba(0, 0, 0, 0.55),
      inset 0 1px 0 rgba(212, 175, 55, 0.16);
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
    overflow: hidden;
  }

  .card:hover {
    transform: translateY(-6px);
    border-color: rgba(240, 214, 138, 0.55);
    box-shadow:
      0 28px 70px rgba(0, 0, 0, 0.65),
      0 0 32px rgba(212, 175, 55, 0.25),
      inset 0 1px 0 rgba(240, 214, 138, 0.4);
  }

  .card:nth-child(1) { animation-delay: 0s; }
  .card:nth-child(2) { animation-delay: 0.06s; }
  .card:nth-child(3) { animation-delay: 0.12s; }
  .card:nth-child(4) { animation-delay: 0.18s; }
  .card:nth-child(5) { animation-delay: 0.24s; }
  .card:nth-child(6) { animation-delay: 0.3s; }
  .card:nth-child(7) { animation-delay: 0.36s; }
  .card:nth-child(8) { animation-delay: 0.42s; }
  .card:nth-child(n+9) { animation-delay: 0.48s; }
  .card-line {
    height: 1px;
    background: linear-gradient(90deg, transparent, var(--gold-300, #e8b947) 50%, transparent);
    margin-bottom: 14px;
    opacity: 0.6;
  }

  .img-wrap {
    position: relative;
    height: 220px;
    overflow: hidden;
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.04);
  }

  .card img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.6s ease;
  }

  .card:hover img {
    transform: scale(1.08);
  }

  .img-shine {
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, transparent 30%, rgba(240, 214, 138, 0.15) 50%, transparent 70%);
    pointer-events: none;
  }

  .info {
    padding: 16px 4px 4px;
    text-align: center;
  }

  .name {
    font-size: 15px;
    color: #efe9d6;
    margin-bottom: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-weight: 500;
  }

  .price {
    font-size: 22px;
    font-weight: 800;
    margin-bottom: 12px;
  }

  .view-btn {
    width: 100%;
    height: 36px;
    border-radius: 999px;
    background: transparent;
    border: 1px solid rgba(212, 175, 55, 0.45);
    color: var(--gold-100, #f5d36e);
    font-size: 13px;
    cursor: pointer;
    transition: all 0.25s ease;
    letter-spacing: 2px;
  }

  .view-btn:hover {
    background: rgba(212, 175, 55, 0.15);
    border-color: var(--gold-300, #e8b947);
    color: var(--gold-50, #fff9e6);
    box-shadow: 0 0 18px rgba(212, 175, 55, 0.35);
  }

  .empty {
    text-align: center;
    padding: 80px 0;
    color: #8e8770;
  }

  .empty-icon {
    font-size: 64px;
    margin-bottom: 20px;
    filter: drop-shadow(0 0 16px rgba(212, 175, 55, 0.4));
  }

  .empty-text {
    font-size: 16px;
    color: #c4bca0;
    margin-bottom: 24px;
  }

  .empty-btn {
    padding: 12px 36px;
    border-radius: 56px;
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
    border: 1px solid var(--gold-300, #e8b947);
    color: #1a1408;
    font-weight: 600;
    cursor: pointer;
    letter-spacing: 2px;
    transition: all 0.3s ease;
    box-shadow:
      0 8px 22px rgba(212, 175, 55, 0.35),
      inset 0 1px 0 rgba(255, 249, 230, 0.4);
  }

  .empty-btn:hover {
    transform: translateY(-2px);
    box-shadow:
      0 14px 36px rgba(212, 175, 55, 0.55),
      0 0 20px rgba(240, 214, 138, 0.5);
  }

  @media (max-width: 960px) {
    .grid { grid-template-columns: repeat(2, 1fr); }
  }
  @media (max-width: 640px) {
    .grid { grid-template-columns: 1fr; }
    .inner { padding: 0 16px; }
  }
</style>

