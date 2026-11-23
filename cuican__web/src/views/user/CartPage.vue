<template>
  <div class="cart-page bg-dark">
    <BlackGoldBackground />
    <div class="inner">
      <div class="head">
        <h2 class="title gold-text-strong">🛒 我的购物车</h2>
        <span class="continue-link" @click="$router.push('/goods')">继续购物 →</span>
      </div>

      <div v-if="items.length === 0" class="empty">
        <div class="empty-icon">💎</div>
        <p class="empty-text">购物车还没有商品</p>
        <button class="empty-btn" @click="$router.push('/goods')">去探索</button>
      </div>

      <div v-else>
        <div v-for="item in items" :key="item.id" class="cart-card">
          <div class="card-line"></div>
          <label class="cart-check">
            <input
              type="checkbox"
              :checked="item.checked === 1"
              @change="toggleCheck(item)" />
            <span class="check-mark"></span>
          </label>
          <img class="cart-img" :src="getImg(item.productImage)" />
          <div class="cart-info">
            <div class="cart-name">{{ item.productName }}</div>
            <div class="cart-price gold-text-strong">¥{{ item.price }}</div>
          </div>
          <div class="cart-qty">
            <button class="qty-btn" @click="changeQty(item, -1)" :disabled="item.quantity <= 1">−</button>
            <span class="qty-num">{{ item.quantity }}</span>
            <button class="qty-btn" @click="changeQty(item, 1)" :disabled="item.quantity >= item.stock">+</button>
          </div>
          <div class="cart-subtotal gold-text">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          <button class="cart-del" @click="removeItem(item)" title="删除">×</button>
        </div>

        <div class="cart-footer">
          <span class="footer-tip">已选 <span class="gold-text-strong">{{ checkedCount }}</span> 件</span>
          <span class="footer-total">
            <small>合计</small>
            <span class="gold-text-strong">¥{{ total.toFixed(2) }}</span>
          </span>
          <button class="checkout-btn" @click="checkout">结算</button>
        </div>
      </div>
    </div>

    <OrderConfirmModal
      :visible="showOC"
      :items="ocItems"
      from="cart"
      @close="showOC = false"
      @confirmed="onOC" />
  </div>
</template>

<script setup>
  import { ref, computed, onMounted } from "vue";
  import { useRouter } from "vue-router";
  import { ElMessage, ElMessageBox } from "element-plus";
  import request from "@/utils/request";
  import OrderConfirmModal from "@/components/order/OrderConfirmModal.vue";
  import BlackGoldBackground from "@/components/background/BlackGoldBackground.vue";

  const router = useRouter();
  const items = ref([]);
  const BASE = "http://localhost:8080";

  const getImg = (url) => {
    if (!url) return "https://via.placeholder.com/80x80?text=No";
    if (url.startsWith("http")) return url;
    return BASE + url;
  };

  const checkedCount = computed(() => items.value.filter(i => i.checked === 1).length);
  const total = computed(() => items.value.filter(i => i.checked === 1).reduce((s, i) => s + i.price * i.quantity, 0));

  const load = async () => {
    try {
      const res = await request.get("/api/cart/list");
      items.value = res.data || [];
    } catch (e) { items.value = []; }
  };

  const toggleCheck = async (item) => {
    try {
      await request.post("/api/cart/check", null, { params: { cartId: item.id } });
      item.checked = item.checked === 1 ? 0 : 1;
    } catch (e) {}
  };

  const changeQty = async (item, delta) => {
    const qty = item.quantity + delta;
    if (qty < 1 || qty > item.stock) return;
    try {
      await request.post("/api/cart/update", null, { params: { cartId: item.id, quantity: qty } });
      item.quantity = qty;
    } catch (e) {}
  };

  const removeItem = async (item) => {
    try {
      await ElMessageBox.confirm("确定删除该商品？", "确认", { type: "warning" });
      await request.post("/api/cart/delete", null, { params: { cartId: item.id } });
      items.value = items.value.filter(i => i.id !== item.id);
    } catch (e) {}
  };

  const showOC = ref(false);
  const ocItems = ref([]);

  const checkout = async () => {
    const ck = items.value.filter(i => i.checked === 1);
    if (!ck.length) { ElMessage.warning("请选择商品"); return; }
    ocItems.value = ck.map(i => ({
      id: i.id, productId: i.productId, name: i.productName,
      productImage: i.productImage, price: i.price, quantity: i.quantity,
    }));
    showOC.value = true;
  };

  const onOC = () => {
    showOC.value = false;
    load();
    router.push("/user/order");
  };

  onMounted(load);
</script>

<style scoped>
  .cart-page {
    min-height: 100vh;
    position: relative;
    z-index: 0;
    padding: 32px 0 100px;
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
    margin-bottom: 20px;
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
    justify-content: space-between;
    margin-bottom: 32px;
  }

  .title {
    font-size: 32px;
    margin: 0;
    letter-spacing: 4px;
    font-weight: 800;
    font-family: var(--heading-font);
  }

  .continue-link {
    font-size: 14px;
    color: var(--gold-100, #f5d36e);
    cursor: pointer;
    letter-spacing: 2px;
    transition: all 0.25s ease;
  }
  .continue-link:hover {
    color: var(--gold-50, #fff9e6);
    text-shadow: 0 0 12px rgba(212, 175, 55, 0.55);
  }

  /* ===== 商品卡片 ===== */
  @keyframes fadeUpCard {
    from { opacity: 0; transform: translateY(18px); }
    to { opacity: 1; transform: translateY(0); }
  }
  .cart-card {
    animation: fadeUpCard 0.5s cubic-bezier(0.2, 0.9, 0.4, 1) backwards;
  }
  .cart-card:nth-child(1) { animation-delay: 0s; }
  .cart-card:nth-child(2) { animation-delay: 0.05s; }
  .cart-card:nth-child(3) { animation-delay: 0.1s; }
  .cart-card:nth-child(4) { animation-delay: 0.15s; }
  .cart-card:nth-child(5) { animation-delay: 0.2s; }
  .cart-card:nth-child(6) { animation-delay: 0.25s; }
  .cart-card:nth-child(7) { animation-delay: 0.3s; }
  .cart-card:nth-child(8) { animation-delay: 0.35s; }
  .cart-card:nth-child(n+9) { animation-delay: 0.4s; }
  .cart-card {
    display: flex;
    align-items: center;
    gap: 18px;
    padding: 18px 22px;
    margin-bottom: 16px;
    border-radius: 16px;
    background: linear-gradient(145deg, rgba(30, 25, 12, 0.55), rgba(12, 12, 12, 0.55));
    background-color: rgba(20, 20, 20, 0.6);
    border: 1px solid rgba(212, 175, 55, 0.22);
    box-shadow:
      0 14px 40px rgba(0, 0, 0, 0.45),
      inset 0 1px 0 rgba(212, 175, 55, 0.14);
    backdrop-filter: blur(14px);
    -webkit-backdrop-filter: blur(14px);
    transition: all 0.3s ease;
  }

  .cart-card:hover {
    border-color: rgba(240, 214, 138, 0.5);
    transform: translateX(4px);
  }

  .card-line {
    position: absolute;
    top: 8px;
    left: 22px;
    right: 22px;
    height: 1px;
    background: linear-gradient(90deg, transparent, var(--gold-300, #e8b947) 50%, transparent);
    opacity: 0.5;
  }

  /* ===== 自定义 checkbox ===== */
  .cart-check {
    position: relative;
    display: inline-flex;
    align-items: center;
    cursor: pointer;
    flex-shrink: 0;
  }
  .cart-check input {
    position: absolute;
    opacity: 0;
    width: 22px;
    height: 22px;
    cursor: pointer;
  }
  .check-mark {
    width: 22px;
    height: 22px;
    border: 1.5px solid rgba(212, 175, 55, 0.4);
    border-radius: 6px;
    background: rgba(20, 20, 20, 0.5);
    position: relative;
    transition: all 0.25s ease;
  }
  .cart-check input:checked + .check-mark {
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
    border-color: var(--gold-300, #e8b947);
    box-shadow: 0 0 16px rgba(212, 175, 55, 0.4);
  }
  .cart-check input:checked + .check-mark::after {
    content: "";
    position: absolute;
    left: 6px;
    top: 2px;
    width: 6px;
    height: 12px;
    border: solid #1a1408;
    border-width: 0 2px 2px 0;
    transform: rotate(45deg);
  }

  /* ===== 商品图 ===== */
  .cart-img {
    width: 96px;
    height: 96px;
    border-radius: 12px;
    object-fit: cover;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(212, 175, 55, 0.18);
    flex-shrink: 0;
  }

  /* ===== 信息 ===== */
  .cart-info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .cart-name {
    font-size: 15px;
    color: #efe9d6;
    font-weight: 500;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  .cart-price {
    font-size: 18px;
    font-weight: 700;
  }

  /* ===== 数量按钮 ===== */
  .cart-qty {
    display: flex;
    align-items: center;
    gap: 0;
    flex-shrink: 0;
  }

  .qty-btn {
    width: 32px;
    height: 32px;
    border-radius: 10px;
    border: 1px solid rgba(212, 175, 55, 0.35);
    background: linear-gradient(145deg, rgba(30, 25, 12, 0.6), rgba(12, 12, 12, 0.6));
    color: var(--gold-100, #f5d36e);
    font-size: 18px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.25s ease;
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    box-shadow:
      0 4px 12px rgba(0, 0, 0, 0.35),
      inset 0 1px 0 rgba(240, 214, 138, 0.15);
  }

  .qty-btn:hover:not(:disabled) {
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
    border-color: var(--gold-300, #e8b947);
    color: #1a1408;
    box-shadow: 0 0 16px rgba(212, 175, 55, 0.45);
    transform: translateY(-1px);
  }

  .qty-btn:disabled {
    opacity: 0.3;
    cursor: not-allowed;
  }

  .qty-num {
    width: 46px;
    text-align: center;
    font-size: 15px;
    font-weight: 600;
    color: var(--gold-100, #f5d36e);
    height: 32px;
    line-height: 32px;
    border-top: 1px solid rgba(212, 175, 55, 0.3);
    border-bottom: 1px solid rgba(212, 175, 55, 0.3);
    background: rgba(20, 20, 20, 0.5);
  }

  /* ===== 小计 ===== */
  .cart-subtotal {
    font-size: 18px;
    font-weight: 700;
    min-width: 100px;
    text-align: right;
    flex-shrink: 0;
  }

  /* ===== 删除 ===== */
  .cart-del {
    width: 30px;
    height: 30px;
    border: none;
    background: transparent;
    font-size: 22px;
    color: rgba(239, 233, 214, 0.3);
    cursor: pointer;
    transition: all 0.25s ease;
    flex-shrink: 0;
    border-radius: 50%;
  }
  .cart-del:hover {
    color: #ff7e7e;
    background: rgba(255, 100, 100, 0.12);
    transform: scale(1.15) rotate(90deg);
  }

  /* ===== 底部结算栏 ===== */
  .cart-footer {
    position: sticky;
    bottom: 16px;
    margin-top: 24px;
    padding: 18px 28px;
    border-radius: 18px;
    background: linear-gradient(145deg, rgba(30, 25, 12, 0.85), rgba(12, 12, 12, 0.85));
    background-color: rgba(20, 20, 20, 0.85);
    border: 1px solid rgba(212, 175, 55, 0.35);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    box-shadow:
      0 -10px 40px rgba(0, 0, 0, 0.5),
      inset 0 1px 0 rgba(240, 214, 138, 0.2);
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 28px;
    z-index: 5;
  }

  .footer-tip {
    font-size: 14px;
    color: #c4bca0;
    letter-spacing: 1px;
  }

  .footer-total {
    display: flex;
    align-items: baseline;
    gap: 10px;
  }

  .footer-total small {
    font-size: 13px;
    color: #c4bca0;
    letter-spacing: 2px;
  }

  .footer-total .gold-text-strong {
    font-size: 24px;
    font-weight: 800;
  }

  .checkout-btn {
    padding: 12px 36px;
    border-radius: 56px;
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37) 50%, var(--gold-700, #8a7128));
    color: #1a1408;
    border: 1px solid var(--gold-300, #e8b947);
    font-size: 15px;
    font-weight: 700;
    letter-spacing: 4px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow:
      0 8px 22px rgba(212, 175, 55, 0.35),
      inset 0 1px 0 rgba(255, 249, 230, 0.4);
  }
  .checkout-btn:hover {
    transform: translateY(-2px);
    box-shadow:
      0 14px 36px rgba(212, 175, 55, 0.55),
      0 0 20px rgba(240, 214, 138, 0.5),
      inset 0 1px 0 rgba(255, 249, 230, 0.6);
  }

  /* ===== 空状态 ===== */
  .empty {
    text-align: center;
    padding: 80px 0;
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
    box-shadow: 0 8px 22px rgba(212, 175, 55, 0.35);
  }
  .empty-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 14px 36px rgba(212, 175, 55, 0.55);
  }

  @media (max-width: 768px) {
    .cart-card {
      flex-wrap: wrap;
      gap: 12px;
    }
    .cart-img { width: 70px; height: 70px; }
    .cart-qty { order: 5; }
    .cart-subtotal { order: 4; min-width: 80px; }
    .cart-del { order: 6; }
  }
</style>


