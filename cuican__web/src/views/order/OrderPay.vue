<template>
  <div class="pay-page bg-dark" v-if="order">
    <BlackGoldBackground />
    <div class="inner">
      <div class="back-bar" @click="$router.back()">← 返回</div>

      <div class="pay-header">
        <div class="pay-icon">💎</div>
        <div class="pay-title">订单支付</div>
        <div class="pay-amount">
          <span class="label">应付金额</span>
          <span class="amount gold-text-strong">¥{{ amount.toFixed(2) }}</span>
        </div>
      </div>

      <div class="section">
        <div class="section-title">订单信息</div>
        <div class="info-row"><span>订单号</span><span class="mono">{{ order.order.orderNo }}</span></div>
        <div class="info-row"><span>状态</span><span class="status-warn">待付款</span></div>
        <div class="info-row"><span>下单时间</span><span>{{ order.order.createTime }}</span></div>
      </div>

      <div class="section">
        <div class="section-title">商品明细</div>
        <div v-for="item in order.items" :key="item.id" class="pay-item">
          <img class="item-img" :src="getImg(item.productImage)" />
          <div class="item-info">
            <div class="item-name">{{ item.productName }}</div>
            <div class="item-meta">¥{{ item.price }} × {{ item.quantity }}</div>
          </div>
          <div class="item-sub gold-text-strong">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
        </div>
      </div>

      <div class="section">
        <div class="section-title">选择支付方式</div>
        <div class="pay-methods">
          <label
            v-for="m in methods"
            :key="m.key"
            class="pay-method"
            :class="{ active: sel === m.key }"
            @click="sel = m.key">
            <span class="m-icon">{{ m.icon }}</span>
            <span class="m-name">{{ m.name }}</span>
            <span class="m-radio">{{ sel === m.key ? "◉" : "○" }}</span>
          </label>
        </div>
      </div>

      <div class="pay-footer">
        <div class="footer-amount">
          <small>实付</small>
          <span class="big gold-text-strong">¥{{ amount.toFixed(2) }}</span>
        </div>
        <button class="pay-btn" :disabled="paying" @click="payNow">
          {{ paying ? "处理中…" : "确认支付" }}
        </button>
      </div>

      <div class="pay-hint">
        🔒 模拟支付环境，本页面不会接入真实支付通道
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";
import BlackGoldBackground from "@/components/background/BlackGoldBackground.vue";

const route = useRoute();
const router = useRouter();
const BASE = "http://localhost:8080";

const order = ref(null);
const sel = ref("alipay");
const paying = ref(false);

const methods = ref([
  { key: "alipay", name: "支付宝", icon: "💙" },
  { key: "wechat", name: "微信支付", icon: "💚" },
  { key: "card", name: "银行卡", icon: "💳" },
  { key: "balance", name: "余额支付", icon: "💰" },
]);

const amount = computed(() => parseFloat(order.value?.order?.totalAmount || 0));

const getImg = (url) => {
  if (!url) return "https://via.placeholder.com/60x60?text=N";
  if (url.startsWith("http")) return url;
  return BASE + url;
};

const payNow = async () => {
  if (!order.value || !order.value.order) return;
  const orderId = order.value.order.id;
  paying.value = true;
  try {
    await request.put("/order/pay", null, { params: { orderId, method: sel.value } });
    ElMessage.success("支付成功！");
    router.push(`/order/detail/${orderId}`);
  } catch (e) {
    ElMessage.error("支付失败：" + (e?.message || "请稍后重试"));
  } finally {
    paying.value = false;
  }
};

const loadDetail = async () => {
  const res = await request.get("/order/detail", { params: { orderId: route.params.orderId } });
  order.value = res.data;
  if (res.data?.order?.status !== 0) {
    router.replace(`/order/detail/${route.params.orderId}`);
  }
};

onMounted(async () => {
  try {
    await loadDetail();
  } catch (e) {}
});
</script>

<style scoped>
  .pay-page {
    min-height: 100vh;
    position: relative;
    z-index: 0;
    padding: 32px 0 120px;
  }

  .inner {
    position: relative;
    z-index: 2;
    max-width: 720px;
    margin: 0 auto;
    padding: 0 24px;
  }

  .back-bar {
    font-size: 14px;
    color: var(--gold-100, #f5d36e);
    cursor: pointer;
    margin-bottom: 20px;
    display: inline-block;
    letter-spacing: 1px;
    transition: all 0.25s ease;
  }
  .back-bar:hover {
    color: var(--gold-50, #fff9e6);
    transform: translateX(-4px);
    text-shadow: 0 0 12px rgba(212, 175, 55, 0.55);
  }

  /* ===== 顶部支付卡 ===== */
  .pay-header {
    text-align: center;
    padding: 36px 20px;
    border-radius: 22px;
    background: linear-gradient(145deg, rgba(30, 25, 12, 0.7), rgba(12, 12, 12, 0.7));
    background-color: rgba(20, 20, 20, 0.7);
    border: 1px solid rgba(212, 175, 55, 0.4);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    box-shadow:
      0 20px 60px rgba(0, 0, 0, 0.55),
      inset 0 1px 0 rgba(240, 214, 138, 0.25),
      0 0 40px rgba(212, 175, 55, 0.15);
    margin-bottom: 22px;
    position: relative;
    overflow: hidden;
  }

  .pay-header::before {
    content: "";
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle at 50% 0%, rgba(240, 214, 138, 0.15), transparent 50%);
    pointer-events: none;
  }

  .pay-icon {
    font-size: 48px;
    margin-bottom: 8px;
    filter: drop-shadow(0 0 16px rgba(240, 214, 138, 0.6));
    position: relative;
    z-index: 2;
  }

  .pay-title {
    font-size: 18px;
    color: var(--gold-100, #f5d36e);
    letter-spacing: 4px;
    margin-bottom: 16px;
    position: relative;
    z-index: 2;
  }

  .pay-amount {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
    position: relative;
    z-index: 2;
  }

  .pay-amount .label {
    font-size: 13px;
    color: #c4bca0;
    letter-spacing: 2px;
  }

  .pay-amount .amount {
    font-size: 40px;
    font-weight: 800;
    letter-spacing: 1px;
  }

  /* ===== 通用 section ===== */
  .section {
    padding: 22px 26px;
    margin-bottom: 16px;
    border-radius: 16px;
    background: linear-gradient(145deg, rgba(30, 25, 12, 0.55), rgba(12, 12, 12, 0.55));
    background-color: rgba(20, 20, 20, 0.6);
    border: 1px solid rgba(212, 175, 55, 0.22);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    box-shadow:
      0 14px 40px rgba(0, 0, 0, 0.45),
      inset 0 1px 0 rgba(212, 175, 55, 0.14);
  }

  .section-title {
    font-size: 15px;
    font-weight: 700;
    color: var(--gold-100, #f5d36e);
    margin-bottom: 16px;
    letter-spacing: 2px;
    position: relative;
    padding-left: 14px;
  }

  .section-title::before {
    content: "";
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 14px;
    background: linear-gradient(180deg, var(--gold-300, #e8b947), var(--gold-700, #8a7128));
    border-radius: 2px;
  }

  .info-row {
    display: flex;
    justify-content: space-between;
    padding: 7px 0;
    font-size: 14px;
    color: #c4bca0;
  }
  .info-row span:first-child {
    color: #8e8770;
    letter-spacing: 1px;
  }
  .info-row .mono {
    font-family: Menlo, Consolas, monospace;
    color: #efe9d6;
    font-size: 13px;
  }

  .status-warn {
    display: inline-block;
    padding: 2px 12px;
    border-radius: 999px;
    background: rgba(245, 158, 11, 0.15);
    border: 1px solid rgba(245, 158, 11, 0.4);
    color: #fbbf24;
    font-size: 12px;
    font-weight: 600;
  }

  /* ===== 商品明细 ===== */
  .pay-item {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 12px 0;
    border-bottom: 1px dashed rgba(212, 175, 55, 0.15);
  }
  .pay-item:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }

  .item-img {
    width: 60px;
    height: 60px;
    border-radius: 10px;
    object-fit: cover;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(212, 175, 55, 0.18);
    flex-shrink: 0;
  }

  .item-info {
    flex: 1;
    min-width: 0;
  }

  .item-name {
    font-size: 14px;
    color: #efe9d6;
    font-weight: 500;
    margin-bottom: 6px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  .item-meta {
    font-size: 12px;
    color: #8e8770;
    letter-spacing: 1px;
  }

  .item-sub {
    font-size: 16px;
    font-weight: 700;
  }

  /* ===== 支付方式 ===== */
  .pay-methods {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 10px;
  }

  .pay-method {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 16px;
    border: 1px solid rgba(212, 175, 55, 0.22);
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.25s ease;
    background: rgba(20, 20, 20, 0.5);
    color: #efe9d6;
  }
  .pay-method:hover {
    border-color: rgba(212, 175, 55, 0.4);
    background: rgba(212, 175, 55, 0.08);
  }
  .pay-method.active {
    border-color: var(--gold-300, #e8b947);
    background: rgba(212, 175, 55, 0.15);
    box-shadow: 0 0 20px rgba(212, 175, 55, 0.2);
  }
  .m-icon { font-size: 24px; }
  .m-name {
    flex: 1;
    font-size: 14px;
    font-weight: 500;
  }
  .m-radio {
    font-size: 18px;
    color: var(--gold-300, #e8b947);
    width: 20px;
    text-align: right;
  }

  /* ===== 底部结算栏 ===== */
  .pay-footer {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    padding: 14px 24px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: rgba(8, 8, 8, 0.92);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-top: 1px solid rgba(212, 175, 55, 0.3);
    box-shadow: 0 -10px 30px rgba(0, 0, 0, 0.5);
    z-index: 50;
  }
  .footer-amount {
    display: flex;
    flex-direction: column;
    gap: 2px;
    font-size: 12px;
    color: #c4bca0;
  }
  .footer-amount small {
    letter-spacing: 2px;
  }
  .footer-amount .big {
    font-size: 24px;
    font-weight: 800;
  }

  .pay-btn {
    padding: 12px 40px;
    border-radius: 56px;
    border: 1px solid var(--gold-300, #e8b947);
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37) 50%, var(--gold-700, #8a7128));
    color: #1a1408;
    font-size: 16px;
    font-weight: 700;
    letter-spacing: 4px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow:
      0 8px 22px rgba(212, 175, 55, 0.35),
      inset 0 1px 0 rgba(255, 249, 230, 0.4);
  }
  .pay-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow:
      0 14px 36px rgba(212, 175, 55, 0.55),
      0 0 20px rgba(240, 214, 138, 0.5);
  }
  .pay-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  .pay-hint {
    text-align: center;
    color: #6c6552;
    font-size: 12px;
    margin-top: 14px;
    letter-spacing: 1px;
  }

  @media (max-width: 540px) {
    .pay-methods { grid-template-columns: 1fr; }
  }
</style>
