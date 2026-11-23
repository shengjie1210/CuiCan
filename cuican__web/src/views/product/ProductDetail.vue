<template>
  <div class="detail-page bg-dark">
    <BlackGoldBackground />
    <div class="detail-container" v-if="product">
      <!-- 缩略图侧栏 -->
      <div class="thumbs">
        <div
          class="thumb-inner"
          :class="{ center: images.length <= visibleCount }">
          <div
            v-for="item in images"
            :key="item.id"
            class="thumb"
            :class="{ active: currentImage === item.imageUrl }"
            :style="{ backgroundImage: `url(${baseURL + item.imageUrl})` }"
            @click="changeImage(item.imageUrl)"></div>
        </div>
      </div>

      <!-- 主图 + 放大镜 -->
      <div
        class="preview"
        ref="previewRef"
        @mousemove="move"
        @mouseenter="showZoom = true"
        @mouseleave="leave"
        @click="toggleZoom">
        <img :src="baseURL + currentImage" class="main-img" />
        <div
          v-if="showZoom"
          class="zoom-box"
          :class="{ deep: deepZoom }"
          :style="zoomStyle"></div>
      </div>

      <!-- 右侧信息 -->
      <div class="info">
        <h1 class="title">{{ product.name }}</h1>
        <div class="desc">{{ product.description }}</div>

        <div class="price-box">
          <div class="price-tag">CUI CAN 尊享价</div>
          <div class="price-row">
            <span class="now gold-text-strong">¥{{ product.price }}</span>
            <span class="old">¥{{ product.originalPrice }}</span>
          </div>
        </div>

        <div class="meta-grid">
          <div class="meta-item">
            <span class="meta-label">销量</span>
            <span class="meta-val gold-text">{{ product.sales }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">库存</span>
            <span class="meta-val">{{ product.stock }}</span>
          </div>
        </div>

        <div class="count-box">
          <span class="count-label">数量</span>
          <el-input-number v-model="count" :min="1" :max="product.stock || 1" />
          <span class="total gold-text-strong"
            >总价 ¥{{ (count * product.price).toFixed(2) }}</span
          >
        </div>

        <div class="btns">
          <div class="like-btn" @click="toggleLike">
            <span
              class="heart"
              :class="{ liked: isLiked, animate: likeAnimate }"
              >❤</span
            >
            <span class="like-text">{{ isLiked ? "已收藏" : "添加收藏" }}</span>
          </div>
          <button class="a-btn a-btn-outline-gold" @click="addCart">
            加入购物车
          </button>
          <button class="a-btn pulsa cta" @click="openOrderModal">
            立即购买
            <span class="star a"></span>
            <span class="star b"></span>
            <span class="star c"></span>
            <span class="star d"></span>
            <span class="star e"></span>
            <span class="star f"></span>
            <span class="star g"></span>
            <span class="rocket">🚀</span>
          </button>
        </div>
      </div>
    </div>

    <OrderConfirmModal
      :visible="showOC"
      :items="ocItems"
      from="buy"
      @close="showOC = false"
      @confirmed="onOC" />
  </div>
</template>

<script setup>
  import { ref, onMounted, watch, nextTick, computed } from "vue";
  import { useRoute, useRouter } from "vue-router";
  import { ElMessage } from "element-plus";
  import request from "@/utils/request";
  import OrderConfirmModal from "@/components/order/OrderConfirmModal.vue";
  import BlackGoldBackground from "@/components/background/BlackGoldBackground.vue";

  const route = useRoute();
  const router = useRouter();
  const product = ref(null);
  const images = ref([]);
  const currentImage = ref("");
  const count = ref(1);
  const baseURL = "http://localhost:8080";
  const visibleCount = 5;
  const previewRef = ref(null);
  const showZoom = ref(false);
  const deepZoom = ref(false);
  const zoomX = ref(0);
  const zoomY = ref(0);
  const bgX = ref(0);
  const bgY = ref(0);
  const previewWidth = ref(520);
  const previewHeight = ref(520);
  const scale = computed(() => (deepZoom.value ? 2.5 : 1.4));
  const boxSize = computed(() => (deepZoom.value ? 130 : 100));
  const zoomStyle = computed(() => ({
    left: zoomX.value + "px",
    top: zoomY.value + "px",
    width: boxSize.value + "px",
    height: boxSize.value + "px",
    backgroundImage: `url(${baseURL + currentImage.value})`,
    backgroundSize:
      previewWidth.value * scale.value +
      "px " +
      previewHeight.value * scale.value +
      "px",
    backgroundPosition: bgX.value + "px " + bgY.value + "px",
  }));

  const move = (e) => {
    const rect = previewRef.value.getBoundingClientRect();
    let x = e.clientX - rect.left;
    let y = e.clientY - rect.top;
    let left = x - boxSize.value / 2;
    let top = y - boxSize.value / 2;
    left = Math.max(0, Math.min(left, rect.width - boxSize.value));
    top = Math.max(0, Math.min(top, rect.height - boxSize.value));
    zoomX.value = left;
    zoomY.value = top;
    bgX.value = -(x * scale.value - boxSize.value / 2);
    bgY.value = -(y * scale.value - boxSize.value / 2);
  };
  const leave = () => {
    showZoom.value = false;
    deepZoom.value = false;
  };
  const toggleZoom = (e) => {
    deepZoom.value = !deepZoom.value;
    if (e) move(e);
  };

  const isLiked = ref(false);
  const likeAnimate = ref(false);
  const checkFavorite = async (productId) => {
    const token = localStorage.getItem("token");
    if (!token) return;
    try {
      const res = await request.get("/favorite/list");
      const arr = res.data || [];
      isLiked.value = arr.some(
        (f) => Number(f.productId) === Number(productId),
      );
    } catch (e) {}
  };
  const toggleLike = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      ElMessage.warning("请先登录");
      router.push("/login");
      return;
    }
    likeAnimate.value = true;
    setTimeout(() => (likeAnimate.value = false), 400);
    try {
      if (isLiked.value) {
        const res = await request.get("/favorite/list");
        const arr = res.data || [];
        const fav = arr.find(
          (f) => Number(f.productId) === Number(product.value.id),
        );
        if (fav)
          await request.post("/favorite/remove", null, {
            params: { productId: fav.productId },
          });
        isLiked.value = false;
        ElMessage.success("已取消收藏");
      } else {
        await request.post("/favorite/add", null, {
          params: { productId: product.value.id },
        });
        isLiked.value = true;
        ElMessage.success("已加入收藏");
      }
    } catch (e) {
      ElMessage.error("操作失败");
    }
  };

  const changeImage = (url) => {
    currentImage.value = url;
  };

  const load = async () => {
    const id = route.params.id;
    try {
      const res = await request.get("/api/product/detail", { params: { id } });
      product.value = res.data.product;
      images.value = res.data.images || [];
      currentImage.value =
        product.value.coverImage ||
        (images.value[0] && images.value[0].imageUrl);
      checkFavorite(product.value.id);
    } catch (e) {
      ElMessage.error("加载失败");
    }
  };

  const addCart = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      ElMessage.warning("请先登录");
      router.push("/login");
      return;
    }
    if (!product.value || count.value < 1) return;
    try {
      await request.post("/api/cart/add", null, {
        params: { productId: product.value.id, quantity: count.value },
      });
      ElMessage.success("已加入购物车");
    } catch (e) {
      ElMessage.error("加入失败");
    }
  };

  const showOC = ref(false);
  const ocItems = ref([]);

  const openOrderModal = () => {
    const token = localStorage.getItem("token");
    if (!token) {
      ElMessage.warning("请先登录");
      router.push("/login");
      return;
    }
    if (!product.value || count.value < 1) return;
    ocItems.value = [
      {
        productId: product.value.id,
        name: product.value.name,
        productImage: product.value.coverImage,
        price: parseFloat(product.value.price),
        quantity: count.value,
      },
    ];
    showOC.value = true;
  };

  const onOC = () => {
    showOC.value = false;
  };

  onMounted(load);
  watch(() => route.params.id, load);
</script>

<style scoped>
  .detail-page {
    min-height: 100vh;
    position: relative;
    z-index: 0;
    padding: 40px 0 80px;
  }

  .detail-container {
    position: relative;
    z-index: 2;
    display: flex;
    gap: 36px;
    max-width: 1280px;
    margin: 0 auto;
    padding: 40px;
    border-radius: 24px;
    background: linear-gradient(
      145deg,
      rgba(30, 25, 12, 0.6),
      rgba(12, 12, 12, 0.6)
    );
    background-color: rgba(20, 20, 20, 0.6);
    border: 1px solid rgba(212, 175, 55, 0.25);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    box-shadow:
      0 30px 80px rgba(0, 0, 0, 0.55),
      0 0 40px rgba(212, 175, 55, 0.15),
      inset 0 1px 0 rgba(240, 214, 138, 0.18);
  }

  /* ===== 缩略图 ===== */
  .thumbs {
    flex: 0 0 110px;
    display: flex;
    align-items: center;
  }
  .thumb-inner {
    display: flex;
    flex-direction: column;
    gap: 14px;
    width: 100%;
  }
  .thumb-inner.center {
    justify-content: center;
    height: 100%;
  }
  .thumb {
    width: 100%;
    height: 90px;
    border-radius: 10px;
    background-size: cover;
    background-position: center;
    background-color: rgba(255, 255, 255, 0.04);
    border: 2px solid transparent;
    cursor: pointer;
    transition: all 0.3s ease;
  }
  .thumb:hover {
    transform: scale(1.06);
    border-color: rgba(212, 175, 55, 0.5);
  }
  .thumb.active {
    border-color: var(--gold-300, #e8b947);
    box-shadow: 0 0 16px rgba(212, 175, 55, 0.4);
  }

  /* ===== 主图 ===== */
  .preview {
    position: relative;
    width: 44%;
    max-width: 540px;
    aspect-ratio: 1/1;
    flex-shrink: 0;
  }
  .main-img {
    width: 100%;
    height: 100%;
    border-radius: 18px;
    object-fit: cover;
    border: 1px solid rgba(212, 175, 55, 0.22);
  }
  .zoom-box {
    position: absolute;
    border-radius: 12px;
    pointer-events: none;
    border: 2px solid var(--gold-50, #fff9e6);
    background-repeat: no-repeat;
    box-shadow: 0 0 30px rgba(212, 175, 55, 0.4);
  }
  .zoom-box.deep {
    border: 2px solid var(--gold-300, #e8b947);
  }
  .zoom-hint {
    position: absolute;
    bottom: 12px;
    right: 12px;
    padding: 5px 12px;
    border-radius: 999px;
    font-size: 11px;
    background: rgba(20, 20, 20, 0.65);
    color: var(--gold-100, #f5d36e);
    border: 1px solid rgba(212, 175, 55, 0.3);
    backdrop-filter: blur(8px);
    pointer-events: none;
    letter-spacing: 1px;
  }

  /* ===== 信息 ===== */
  .info {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-width: 0;
  }

  .title {
    font-size: 36px;
    font-weight: 800;
    line-height: 1.25;
    margin: 0 0 14px;
    letter-spacing: 2px;
    background: linear-gradient(
      135deg,
      #fff9e6 0%,
      var(--gold-300, #e8b947) 50%,
      var(--gold-500, #d4af37) 100%
    );
    background-size: 200% 100%;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    color: transparent;
    animation: titleShift 8s ease-in-out infinite;
  }
  @keyframes titleShift {
    0%,
    100% {
      background-position: 0% 50%;
    }
    50% {
      background-position: 100% 50%;
    }
  }

  .desc {
    font-size: 14px;
    color: #c4bca0;
    line-height: 1.8;
    margin-bottom: 24px;
    letter-spacing: 0.5px;
  }

  /* ===== 价格 ===== */
  .price-box {
    padding: 20px 24px;
    border-radius: 16px;
    background: linear-gradient(
      135deg,
      rgba(212, 175, 55, 0.12),
      rgba(212, 175, 55, 0.04)
    );
    border: 1px solid rgba(212, 175, 55, 0.3);
    margin-bottom: 24px;
  }
  .price-tag {
    font-size: 12px;
    letter-spacing: 4px;
    color: var(--gold-300, #e8b947);
    margin-bottom: 6px;
    text-transform: uppercase;
  }
  .price-row {
    display: flex;
    align-items: baseline;
    gap: 16px;
  }
  .now {
    font-size: 44px;
    font-weight: 800;
  }
  .old {
    font-size: 16px;
    text-decoration: line-through;
    color: #6c6552;
  }

  /* ===== 元信息 ===== */
  .meta-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    margin-bottom: 24px;
  }
  .meta-item {
    padding: 12px 16px;
    border-radius: 12px;
    background: rgba(20, 20, 20, 0.4);
    border: 1px solid rgba(212, 175, 55, 0.15);
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
  .meta-label {
    font-size: 12px;
    color: #8e8770;
    letter-spacing: 2px;
    text-transform: uppercase;
  }
  .meta-val {
    font-size: 16px;
    color: #efe9d6;
    font-weight: 600;
  }
  .meta-val.mono {
    font-family: Menlo, Consolas, monospace;
    font-size: 14px;
  }

  /* ===== 数量 / 总价 ===== */
  .count-box {
    display: flex;
    align-items: center;
    gap: 24px;
    padding: 16px 20px;
    border-radius: 12px;
    background: rgba(20, 20, 20, 0.4);
    border: 1px solid rgba(212, 175, 55, 0.15);
    margin-bottom: 24px;
  }
  .count-label {
    font-size: 13px;
    color: #c4bca0;
    letter-spacing: 2px;
  }
  .total {
    margin-left: auto;
    font-size: 20px;
    font-weight: 800;
  }

  /* 数量加减按钮：主题化（黑金） */
  .count-box :deep(.el-input-number) {
    width: 148px;
  }
  .count-box :deep(.el-input-number .el-input__wrapper) {
    background: rgba(10, 10, 10, 0.55);
    box-shadow: 0 0 0 1px rgba(212, 175, 55, 0.35) inset;
    border-radius: 10px;
    padding: 1px 34px;
  }
  .count-box :deep(.el-input-number .el-input__inner) {
    color: var(--gold-light, #f0c75e);
    font-weight: 800;
    font-size: 15px;
    text-align: center;
  }
  .count-box :deep(.el-input-number__increase),
  .count-box :deep(.el-input-number__decrease) {
    background: rgba(212, 175, 55, 0.1);
    border: none;
    color: var(--gold, #d4af37);
    font-size: 16px;
    font-weight: 800;
    transition: all 0.25s ease;
    width: 30px;
  }
  .count-box :deep(.el-input-number__decrease) {
    border-radius: 9px 0 0 9px;
  }
  .count-box :deep(.el-input-number__increase) {
    border-radius: 0 9px 9px 0;
  }
  .count-box :deep(.el-input-number__increase:hover),
  .count-box :deep(.el-input-number__decrease:hover) {
    background: linear-gradient(135deg, var(--gold, #d4af37), var(--gold-light, #f0c75e));
    color: #1c1406;
  }
  .count-box :deep(.el-input-number__increase.is-disabled),
  .count-box :deep(.el-input-number__decrease.is-disabled) {
    background: rgba(212, 175, 55, 0.05);
    color: rgba(212, 175, 55, 0.35);
  }

  /* ===== 操作区 ===== */
  .btns {
    display: flex;
    align-items: center;
    gap: 14px;
    flex-wrap: wrap;
    margin-top: auto;
  }

  .like-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 20px;
    border-radius: 56px;
    border: 1px solid rgba(212, 175, 55, 0.3);
    background: transparent;
    cursor: pointer;
    transition: all 0.25s ease;
    margin-right: auto;
  }
  .like-btn:hover {
    background: rgba(212, 175, 55, 0.1);
    border-color: var(--gold-300, #e8b947);
  }
  .heart {
    color: #8e8770;
    font-size: 18px;
    transition: all 0.2s ease;
  }
  .heart.liked {
    color: #ff4d6d;
    text-shadow: 0 0 10px rgba(255, 77, 109, 0.6);
  }
  .heart.animate {
    animation: heartBeat 0.4s ease;
  }
  @keyframes heartBeat {
    0% {
      transform: scale(1);
    }
    30% {
      transform: scale(1.4);
    }
    60% {
      transform: scale(0.85);
    }
    100% {
      transform: scale(1);
    }
  }
  .like-text {
    font-size: 13px;
    color: #c4bca0;
    letter-spacing: 1px;
  }

  .a-btn {
    padding: 14px 36px;
    border-radius: 56px;
    font-size: 15px;
    font-weight: 700;
    letter-spacing: 4px;
    cursor: pointer;
    border: 1px solid transparent;
    transition: all 0.3s ease;
    font-family: inherit;
  }
  .a-btn-gold {
    background: linear-gradient(
      135deg,
      var(--gold-300, #e8b947),
      var(--gold-500, #d4af37) 50%,
      var(--gold-700, #8a7128)
    );
    color: #1a1408;
    border-color: var(--gold-300, #e8b947);
    box-shadow: 0 8px 22px rgba(212, 175, 55, 0.35);
  }
  .a-btn-gold:hover {
    transform: translateY(-2px);
    box-shadow: 0 14px 36px rgba(212, 175, 55, 0.55);
  }
  .a-btn-outline-gold {
    background: transparent;
    color: var(--gold-100, #f5d36e);
    border-color: rgba(212, 175, 55, 0.5);
  }
  .a-btn-outline-gold:hover {
    background: rgba(212, 175, 55, 0.12);
    border-color: var(--gold-300, #e8b947);
    color: var(--gold-50, #fff9e6);
  }

  /* ===== 响应式 ===== */

  @media (max-width: 960px) {
    .detail-container {
      flex-direction: column;
      padding: 28px;
    }
    .preview {
      width: 100%;
      max-width: none;
    }
    .thumbs {
      flex: 0 0 auto;
      width: 100%;
    }
    .thumb-inner {
      flex-direction: row;
    }
    .thumb {
      width: 90px;
      height: 90px;
    }
    .title {
      font-size: 28px;
    }
    .now {
      font-size: 32px;
    }
    .meta-grid {
      grid-template-columns: 1fr 1fr;
    }
  }
</style>
