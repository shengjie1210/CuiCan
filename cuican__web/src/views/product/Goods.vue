<template>
  <div class="goods-page">
    <!--  排序 -->
    <div class="sort-float">
      <div class="sort-group">
        <span
          :class="{ active: sortField === 'sales' }"
          @click="changeSort('sales')"
          >销量</span
        >
        <span
          :class="{ active: sortField === 'price' }"
          @click="changeSort('price')"
          >价格</span
        >
        <span
          :class="{ active: sortField === 'name' }"
          @click="changeSort('name')"
          >名称</span
        >
      </div>

      <div class="order-btn" @click="toggleOrder">
        <span :class="order"></span>
      </div>
    </div>

    <!--  布局切换 -->
    <div class="layout-switch">
      <span :class="{ active: layout === 'grid' }" @click="layout = 'grid'"
        >▥</span
      >
      <span :class="{ active: layout === 'list' }" @click="layout = 'list'"
        >☰</span
      >
    </div>

    <!--  商品 -->
    <div class="goods-list" :class="layout" v-if="goodsList.length">
      <div class="goods-card" v-for="item in goodsList" :key="item.id">
        <div class="line"></div>
        <!--  必须这一层（核心） -->
        <div class="goods-card-inner">
          <!-- 图片 -->
          <div class="img-box" @click="goDetail(item.id)">
            <img class="goods-img" :src="baseURL + item.coverImage" />
          </div>

          <!-- 名称（默认显示） -->
          <div class="goods-name">{{ item.name }}</div>

          <!--  隐藏信息（hover才出现） -->
          <div class="goods-extra">
            <div class="goods-price-wrap">
              <div class="goods-price">¥{{ item.price }}</div>
              <div class="goods-original">¥{{ item.originalPrice }}</div>
            </div>

            <div class="goods-sales">已售 {{ item.sales }}</div>

            <button class="detail-btn" @click="goDetail(item.id)">
              查看详情
            </button>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="empty">{{ loading ? "加载中..." : "暂无商品" }}</div>

    <!--  分页 -->
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        v-model:current-page="page"
        v-model:page-size="size"
        :page-sizes="[8, 12, 24, 48]"
        @current-change="onPageChange"
        @size-change="onSizeChange" />
    </div>
  </div>
</template>

<script setup>
  import { ref, watch, onMounted } from "vue";
  import { useRoute, useRouter } from "vue-router";
  import request from "@/utils/request";

  const route = useRoute();
  const router = useRouter();

  const goodsList = ref([]);
  const searchKeyword = ref(String(route.query.keyword || ""));
  const page = ref(Number(route.query.page) || 1);
  const size = ref(12);
  const total = ref(0);
  const loading = ref(false);
  const baseURL = "http://localhost:8080";

  const layout = ref("grid");

  const sortField = ref("sales");
  const order = ref("desc");

  const getGoods = async () => {
    loading.value = true;
    try {
      const res = await request.get("/api/product/list", {
        params: {
          categoryId: route.query.categoryId,
          keyword: searchKeyword.value || undefined,
          sortField: sortField.value,
          order: order.value,
          page: page.value,
          size: size.value,
        },
      });

      const data = res?.data ?? res ?? [];
      goodsList.value = Array.isArray(data) ? data : data?.list || [];
      total.value = Array.isArray(data) ? data.length : data?.total || 0;
    } finally {
      loading.value = false;
    }
  };

  function onPageChange(p) {
    page.value = p;
    window.scrollTo({ top: 0, behavior: "smooth" });
    getGoods();
  }

  function onSizeChange(s) {
    size.value = s;
    page.value = 1;
    window.scrollTo({ top: 0, behavior: "smooth" });
    getGoods();
  }

  const changeSort = (field) => {
    if (sortField.value === field) {
      toggleOrder();
      return;
    }
    sortField.value = field;
    order.value = "desc";
    page.value = 1;
    getGoods();
  };

  const toggleOrder = () => {
    order.value = order.value === "asc" ? "desc" : "asc";
    page.value = 1;
    getGoods();
  };

  const goDetail = (id) => {
    router.push(`/product/${id}`);
  };

  onMounted(getGoods);

  watch(
    () => [route.query.keyword, route.query.categoryId],
    () => {
      searchKeyword.value = String(route.query.keyword || "");
      page.value = Number(route.query.page) || 1;
      getGoods();
    },
  );
</script>

<style scoped>
  /* ================= 页面 ================= */
  .goods-page {
    width: min(1500px, 98%);
    margin: 40px auto 80px;
  }

  /* ================= 分页 ================= */
  .pagination-wrap {
    display: flex;
    justify-content: center;
    margin-top: 48px;
  }

  /* ================= GRID ================= */
  .goods-list.grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 42px;
  }

  /* ================= LIST ================= */
  .goods-list.list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  /* ================= 卡片 ================= */
  .goods-card {
    height: 460px;
    border-radius: 20px;
    overflow: hidden;
    background: linear-gradient(
      145deg,
      rgba(255, 255, 255, 0.286),
      rgba(255, 255, 255, 0.111)
    );
    background-color: rgba(255, 255, 255, 0.325);
    backdrop-filter: blur(1px);
    -webkit-backdrop-filter: blur(1px);
    border: 1px solid rgba(0, 0, 0, 0.215);
    box-shadow:
      0 18px 50px rgba(0, 0, 0, 0.08),
      inset 0 1px 0 rgba(255, 255, 255, 0.8);
    position: relative;
    transition:
      transform 0.35s ease,
      box-shadow 0.35s ease,
      border-color 0.35s ease;
  }

  .goods-card::after {
    content: "";
    position: absolute;
    inset: 0;
    border-radius: 20px;
    background: linear-gradient(
      115deg,
      transparent 30%,
      rgba(212, 175, 55, 0.18) 48%,
      rgba(212, 175, 55, 0.05) 52%,
      transparent 70%
    );
    transform: translateX(-120%);
    transition: transform 0.8s ease;
    pointer-events: none;
  }

  .goods-card:hover {
    transform: translateY(-4px);
    border-color: rgba(212, 175, 55, 0.55);
    box-shadow:
      0 18px 44px rgba(0, 0, 0, 0.1),
      0 0 18px rgba(212, 175, 55, 0.14),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
  }

  .goods-card:hover::after {
    transform: translateX(120%);
  }

  /* ================= 内层 ================= */
  .goods-card-inner {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    transform: translateY(40px);
    transition: transform 0.5s ease;
  }

  /* ================= 图片 ================= */
  .img-box {
    height: 260px;
    display: flex;
    justify-content: center;
  }

  .goods-img {
    width: 90%;
    height: 100%;
    object-fit: cover;
    border-radius: 16px;
    transition: transform 0.6s ease;
  }

  /* ================= 名称 ================= */
  .goods-name {
    margin-top: 14px;
    text-align: center;
    font-size: 32px !important;
  }

  /* ================= 隐藏信息 ================= */
  .goods-extra {
    position: absolute;
    bottom: -100px;
    left: 0;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    opacity: 0;
    transform: translateY(-520px);
    transition:
      transform 0.4s cubic-bezier(0.22, 0.61, 0.36, 1),
      opacity 0.3s ease;
  }

  /* ================= hover ================= */
  .goods-card:hover .goods-card-inner {
    transform: translateY(-120px);
  }

  .goods-card:hover .goods-img {
    transform: scale(1.06);
  }

  .goods-card:hover .goods-extra {
    opacity: 1;
    transform: translateY(0);
  }

  /* ================= 按钮 ================= */
  .detail-btn {
    --color: #d4af37;

    position: relative;
    overflow: hidden;

    padding: 10px 20px;
    border-radius: 999px;
    border: 2px solid var(--color);

    background: transparent;
    color: var(--color);

    font-size: 14px;
    cursor: pointer;

    transition: color 0.4s ease;

    z-index: 1;
  }

  /* 扩散圆 */
  .detail-btn::before {
    content: "";
    position: absolute;
    z-index: -1;

    width: 200%;
    height: 200%;

    background: var(--color);
    border-radius: 50%;

    top: 100%;
    left: 100%;

    transition: all 0.6s ease;
  }

  /* hover 效果 */
  .detail-btn:hover {
    color: #fff;
  }

  .detail-btn:hover::before {
    top: -50%;
    left: -50%;
  }

  /* 点击反馈 */
  .detail-btn:active::before {
    background: #e85d3f;
    transition: background 0s;
  }

  /* ================= LIST ================= */
  .goods-list.list .goods-card {
    height: 160px;
    display: flex;
    align-items: center;
    padding: 0 20px;
    position: relative;
    border-radius: 28px;
    overflow: hidden;
  }

  .goods-list.list .goods-card-inner {
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
    transform: none !important;
    padding: 0;
    width: 100%;
  }

  .goods-list.list .img-box {
    width: 140px;
    height: 120px;
    flex-shrink: 0;
  }

  .goods-list.list .goods-img {
    width: 100%;
    height: 100%;
  }

  .goods-list.list .goods-name {
    margin-left: 20px;
    text-align: left;
    width: 300px;
  }

  .goods-list.list .goods-extra {
    position: static;
    opacity: 1;
    transform: none;
    flex-direction: row;
    gap: 20px;
    margin-left: 20px;
  }

  .goods-list.list .detail-btn {
    margin-left: auto;
  }

  /* ================= 边框动画 ================= */
  .goods-list.list .goods-card::before,
  .goods-list.list .goods-card::after {
    content: "";
    position: absolute;
    inset: 0;
    border-radius: 28px;
    pointer-events: none;
  }

  .goods-list.list .goods-card::before {
    border-top: 2px solid #fb4b02;
    border-bottom: 2px solid #fb4b02;
    transform: scaleX(0);
    transform-origin: right;
    transition: transform 0.4s ease;
  }

  .goods-list.list .goods-card::after {
    border-left: 2px solid #fb4b02;
    border-right: 2px solid #fb4b02;
    transform: scaleY(0);
    transform-origin: bottom;
    transition: transform 0.4s ease;
  }

  .goods-list.list .goods-card:hover::before {
    transform: scaleX(1);
    transform-origin: left;
  }

  .goods-list.list .goods-card:hover::after {
    transform: scaleY(1);
    transform-origin: top;
  }

  /* ================= 布局切换 ================= */
  .layout-switch {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
  }

  .layout-switch span {
    padding: 6px 14px;
    border-radius: 10px;
    cursor: pointer;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(10px);
    transition: all 0.25s ease;
  }

  .layout-switch span:hover {
    transform: translateY(-2px);
  }

  .layout-switch .active {
    background: linear-gradient(135deg, #ffb347, #ff7e5f);
    color: #fff;
  }

  /* ================= 排序 ================= */
  .sort-float {
    position: sticky;
    top: 18px;
    margin: 20px 0 30px;
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }

  .sort-group {
    display: flex;
    gap: 10px;
    padding: 8px 12px;
    border-radius: 14px;
    background: rgba(255, 255, 255, 0.85);
  }

  .sort-group span {
    padding: 4px 10px;
    cursor: pointer;
    color: #666;
    transition: all 0.3s ease;
  }

  .sort-group span.active {
    color: #ff7e5f;
    font-weight: 600;
  }

  /* ================= 箭头 ================= */
  .order-btn {
    margin-left: 10px;
    width: 32px;
    height: 32px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    background: rgba(255, 255, 255, 0.85);
    transition: all 0.25s ease;
  }

  .order-btn:hover {
    transform: translateY(-2px);
  }

  .order-btn span {
    display: block;
    width: 10px;
    height: 10px;
    border-right: 2px solid #ff7e5f;
    border-bottom: 2px solid #ff7e5f;
    transition: transform 0.3s ease;
  }

  .order-btn span.asc {
    transform: rotate(-135deg);
  }

  .order-btn span.desc {
    transform: rotate(45deg);
  }

  /* ================= 名称渐变 ================= */
  .goods-name {
    background: linear-gradient(90deg, #c08a38, #f4c26b, #ff8a65, #c08a38);
    background-size: 300% 100%;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .goods-card:hover .goods-name {
    animation: nameFlow 2s linear infinite;
  }

  @keyframes nameFlow {
    100% {
      background-position: 300% 50%;
    }
  }

  /* ================= 价格 ================= */
  .goods-price {
    font-size: 30px;
    font-weight: 800;
    background: linear-gradient(90deg, #d4af37, #f0c75e, #ffe9b3);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .goods-original {
    color: #999;
    text-decoration: line-through;
  }

  .goods-sales {
    color: #666;
  }
</style>

