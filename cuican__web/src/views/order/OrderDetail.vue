<template>
  <div class="detail-page bg-dark" v-if="detail">
    <BlackGoldBackground />
    <div class="back-bar" @click="$router.back()">← 返回</div>
    <!-- 5 步状态条 -->
    <div class="status-banner" :class="'st-' + detail.order.status">
      <div class="status-icon">{{ statusEmoji }}</div>
      <div class="status-main">
        <div class="status-text">{{ statusText }}</div>
        <div class="status-tip" v-if="statusTip">{{ statusTip }}</div>
      </div>
    </div>
    <!-- 5 步时间轴: 提交/付款/商家备货中/发货/确认 -->
    <div class="section timeline-section" v-if="detail.order.status !== -1">
      <div class="timeline">
        <div
          v-for="(s, i) in timeline"
          :key="i"
          class="step"
          :class="{ active: s.done, current: s.current }">
          <div class="dot"></div>
          <div class="step-name">{{ s.name }}</div>
          <div class="step-time">{{ s.time || "" }}</div>
        </div>
      </div>
    </div>
    <!-- 收货信息 -->
    <div class="section addr-section">
      <div class="section-header">
        <span class="section-title">📍 收货信息</span>
      </div>
      <div class="addr-body">
        <div class="addr-avatar">📦</div>
        <div class="addr-detail">
          <div class="addr-line1">
            <span class="addr-name">{{
              detail.order.receiverName || "—"
            }}</span>
            <span class="addr-phone">{{
              detail.order.receiverPhone || ""
            }}</span>
          </div>
          <div class="addr-line2">
            {{ detail.order.province || "" }} {{ detail.order.city || "" }}
            {{ detail.order.detailAddress || "尚未填写收货地址" }}
          </div>
        </div>
      </div>
    </div>
    <!-- 商品明细 -->
    <div class="section">
      <div class="shop-row">
        <span class="shop-icon">💎</span
        ><span class="shop-name">璀璨官方旗舰店</span>
      </div>
      <div v-for="item in detail.items" :key="item.id" class="product-row">
        <img class="product-img" :src="getImg(item.productImage)" />
        <div class="product-info">
          <div class="product-name">{{ item.productName }}</div>
          <div class="product-meta">数量 × {{ item.quantity }}</div>
        </div>
        <div class="product-price">
          <div class="unit">¥{{ formatMoney(item.price) }}</div>
          <div class="sub">
            小计：¥{{ formatMoney(item.price * item.quantity) }}
          </div>
        </div>
      </div>
    </div>
    <!-- 配送/支付/物流 -->
    <div class="section info-section">
      <div class="info-line">
        <span class="info-label">配送方式</span
        ><span class="info-value">快递配送</span>
      </div>
      <div v-if="detail.order.shipMethod" class="info-line">
        <span class="info-label">物流公司</span
        ><span class="info-value"
          ><span class="pay-badge">{{ detail.order.shipMethod }}</span></span
        >
      </div>
      <div v-if="detail.order.trackingNo" class="info-line">
        <span class="info-label">物流单号</span
        ><span class="info-value mono">{{ detail.order.trackingNo }}</span>
      </div>
      <div class="info-line">
        <span class="info-label">支付方式</span
        ><span class="info-value">
          <span class="pay-badge" v-if="detail.order.payMethod">{{
            payMethodText(detail.order.payMethod)
          }}</span>
          <span v-else style="color: #bbb">未支付</span>
        </span>
      </div>
      <div class="info-line" v-if="detail.order.payTime">
        <span class="info-label">支付时间</span
        ><span class="info-value">{{ formatDate(detail.order.payTime) }}</span>
      </div>
      <div class="info-line" v-if="detail.order.shipTime">
        <span class="info-label">发货时间</span
        ><span class="info-value">{{ formatDate(detail.order.shipTime) }}</span>
      </div>
      <div class="info-line" v-if="detail.order.confirmTime">
        <span class="info-label">收货时间</span
        ><span class="info-value">{{
          formatDate(detail.order.confirmTime)
        }}</span>
      </div>
      <div class="info-line">
        <span class="info-label">订单备注</span
        ><span class="info-value">{{ detail.order.note || "无" }}</span>
      </div>
    </div>
    <!-- 金额明细 -->
    <div class="section money-section">
      <div class="money-line">
        <span>商品总额</span
        ><span class="money-val"
          >¥{{
            formatMoney(detail.order.originalAmount || detail.order.totalAmount)
          }}</span
        >
      </div>
      <div class="money-line" v-if="discountAmount > 0">
        <span>活动优惠</span
        ><span class="money-val discount"
          >-¥{{ formatMoney(discountAmount) }}</span
        >
      </div>
      <div
        class="money-line"
        v-if="detail.order.shipTime || detail.order.payTime">
        <span>运费</span><span class="money-val">¥0.00</span>
      </div>
      <div class="money-line total">
        <span>实付金额</span
        ><span class="money-val total-val"
          >¥{{ formatMoney(detail.order.totalAmount) }}</span
        >
      </div>
    </div>
    <!-- 订单基础信息 -->
    <div class="section order-meta-section">
      <div class="meta-line">
        <span>订单号</span><span class="mono">{{ detail.order.orderNo }}</span>
      </div>
      <div class="meta-line">
        <span>下单时间</span
        ><span>{{ formatDate(detail.order.createTime) }}</span>
      </div>
      <button class="copy-btn" @click="copyOrderNo">复制订单号</button>
    </div>
    <!-- 底部操作栏 -->
    <div class="action-bar" v-if="detail">
      <button
        v-if="detail.order.status === 0"
        class="a-btn a-btn-default"
        @click="contactSeller">
        联系卖家
      </button>
      <button
        v-if="detail.order.status === 0"
        class="a-btn a-btn-danger"
        @click="cancelOrder">
        取消订单
      </button>
      <button
        v-if="detail.order.status === 0"
        class="a-btn a-btn-pay"
        @click="goPay">
        💳 去支付
      </button>
      <button
        v-if="detail.order.status === 1"
        class="a-btn a-btn-default"
        @click="urgeShip">
        提醒发货
      </button>
      <button
        v-if="detail.order.status === 1"
        class="a-btn a-btn-default"
        @click="contactSeller">
        联系卖家
      </button>
      <button
        v-if="detail.order.status === 2"
        class="a-btn a-btn-default"
        @click="trackExpress">
        查看物流
      </button>
      <button
        v-if="detail.order.status === 2"
        class="a-btn a-btn-success"
        @click="confirmReceive">
        确认收货
      </button>
      <button
        v-if="detail.order.status === 3 || detail.order.status === -1"
        class="a-btn a-btn-default"
        @click="contactSeller">
        联系卖家
      </button>
      <button
        v-if="detail.order.status === 3"
        class="a-btn a-btn-primary"
        @click="rebuy">
        再来一单
      </button>
      <button
        v-if="detail.order.status === 3"
        class="a-btn a-btn-default"
        @click="deleteOrder">
        删除订单
      </button>
      <button
        v-if="detail.order.status === -1"
        class="a-btn a-btn-default"
        @click="deleteOrder">
        删除订单
      </button>
    </div>
  </div>
</template>
<script setup>
  
import BlackGoldBackground from "@/components/background/BlackGoldBackground.vue";
import { ref, computed, onMounted, onBeforeUnmount, watch } from "vue";
  import { useRoute, useRouter } from "vue-router";
  import { ElMessage, ElMessageBox } from "element-plus";
  import request from "@/utils/request";

  const route = useRoute();
  const router = useRouter();
  const BASE = "http://localhost:8080";
  const detail = ref(null);

  // 系统配置（来自 /api/system/settings）
  const settings = ref({ countdownMinutes: 15, shipEtaText: "商家正在备货中，预计 1-2 个工作日内发货", cancelWarning: "为防库存占用，超时将自动取消订单" });


  const statusText = computed(() => {
    const map = { "-1": "订单已取消", "0": "等待买家付款", "1": "已支付，等待卖家发货", "2": "卖家已发货，等待买家收货", "3": "订单已完成" };
    return map[String(detail.value?.order?.status)] || "未知状态";
  });

  const statusEmoji = computed(() => {
    const map = { "-1": "✕", "0": "⏳", "1": "✓", "2": "🚚", "3": "🎉" };
    return map[String(detail.value?.order?.status)] || "•";
  });

  const statusTip = computed(() => {
    const o = detail.value?.order;
    if (!o) return "";
    if (o.status === 0) return '剩余 ' + countdownStr.value + ' 后自动取消订单';
    if (o.status === 1) return settings.value.shipEtaText;
    if (o.status === 2) return "商品运输中，请耐心等待";
    if (o.status === 3) return "感谢您的购买，期待您的 ♥";
    if (o.status === -1) return "此订单已取消";
    return "";
  });

  // 5 步时间轴: 提交订单 / 付款 / 商家备货中 / 卖家发货 / 确认收货
 const timeline = computed(() => {
   const o = detail.value?.order || {};
   const fmt = (t) => (t ? String(t).substring(0, 19).replace("T", " ") : "");
   const cur = o.status;
   const steps = [
     { name: "提交订单", time: fmt(o.createTime), done: !!o.createTime },
     { name: "付款",     time: fmt(o.payTime),     done: !!o.payTime },
     { name: "商家备货中", time: fmt(o.payTime),   done: cur >= 2 },
     { name: "卖家发货", time: fmt(o.shipTime),    done: !!o.shipTime },
     { name: "确认收货", time: fmt(o.confirmTime), done: !!o.confirmTime }
  ];
  // 当前步骤 = 第一个未完成的步骤（已完成的不标为 current）
  const firstUndone = steps.findIndex(s => !s.done);
   return steps.map((s, i) => ({
     ...s,
     current: firstUndone !== -1 && i === firstUndone
   }));
 });

  const discountAmount = computed(() => {
    const o = detail.value?.order;
    if (!o) return 0;
    return Math.max(0, parseFloat(o.originalAmount || 0) - parseFloat(o.totalAmount || 0));
  });

  const formatMoney = (n) => parseFloat(n || 0).toFixed(2);

  const formatDate = (t) => {
   if (!t) return "—";
   try { return String(t).substring(0, 19).replace("T", " "); }
   catch (e) { return String(t); }
  };

  const payMethodText = (m) => {
   const map = { alipay: "💙 支付宝", wechat: "💚 微信支付", card: "💳 银行卡", balance: "💰 余额" };
   return map[m] || (m || "—");
  };

  const getImg = (url) => {
    if (!url) return "https://via.placeholder.com/80x80?text=N";
    if (url.startsWith("http")) return url;
    return BASE + url;
  };

  // ========== 倒计时 + 自动取消 ==========
  let pollTimer = null;
  let countdownTimer = null;
  let countdownSec = ref(0);
  let autoCancelled = ref(false);

  const countdownStr = computed(() => {
    const s = Math.max(0, countdownSec.value);
    const m = Math.floor(s / 60);
    const r = s % 60;
    return String(m).padStart(2, "0") + ":" + String(r).padStart(2, "0");
  });

  const deadlineSec = computed(() => {
    if (!detail.value?.order?.createTime) return 0;
    if (detail.value.order.status !== 0) return 0;
    const t = new Date(detail.value.order.createTime).getTime();
    const end = Math.floor(t / 1000) + settings.value.countdownMinutes * 60;
    return end - Math.floor(Date.now() / 1000);
  });

  function startCountdown() {
    stopCountdown();
    countdownSec.value = Math.max(0, deadlineSec.value);
    if (countdownSec.value <= 0) { triggerAutoCancel(); return; }
    countdownTimer = setInterval(() => {
      countdownSec.value = Math.max(0, countdownSec.value - 1);
      if (countdownSec.value <= 0) { stopCountdown(); triggerAutoCancel(); }
    }, 1000);
  }
  function stopCountdown() { if (countdownTimer) { clearInterval(countdownTimer); countdownTimer = null; } }

  async function triggerAutoCancel() {
    if (autoCancelled.value) return;
    autoCancelled.value = true;
    const orderId = detail.value?.order?.id;
    if (!orderId) return;
    ElMessage.warning(settings.value.cancelWarning + ",已自动取消");
    try { await request.post("/order/cancel", null, { params: { orderId } }); }
    catch (e) { autoCancelled.value = false; }
    router.replace("/user/order");
  }

  // ========== 业务函数 ==========
  const copyOrderNo = async () => {
    try { await navigator.clipboard.writeText(detail.value.order.orderNo); ElMessage.success("订单号已复制"); }
    catch (e) { ElMessage.info("浏览器不支持剪贴板,请手动复制"); }
  };

  const contactSeller = () => ElMessage.info("客服:璀璨小助手(演示)");
  const urgeShip = () => ElMessage.success("已提醒卖家尽快发货");
  const trackExpress = () => ElMessage.info("物流跟踪:演示版暂未对接");

  const rebuy = () => {
    const first = detail.value?.items?.[0];
    if (first?.productId) router.push(("/product/" + first.productId));
  };

  const cancelOrder = async () => {
    const orderId = detail.value?.order?.id;
    if (!orderId) return;
    try {
      await ElMessageBox.confirm("取消后订单将被删除，无法恢复.确定取消吗?", "确认取消", { type: "warning" });
      autoCancelled.value = true;
      stopCountdown();
      await request.post("/order/cancel", null, { params: { orderId } });
      ElMessage.success("订单已取消");
      router.replace("/user/order");
    } catch (e) { autoCancelled.value = false; }
  };

  const confirmReceive = async () => {
    const orderId = detail.value?.order?.id;
    if (!orderId) return;
    try {
      await ElMessageBox.confirm("确定已收到货吗?", "确认收货", { type: "warning" });
      await request.post("/order/confirm", null, { params: { orderId } });
      ElMessage.success("感谢您的确认");
      await loadDetail();
    } catch (e) {}
  };

  const goPay = () => {
    const orderId = detail.value?.order?.id;
    if (orderId) router.push(("/order/pay/" + orderId));
  };

  const deleteOrder = async () => {
    const orderId = detail.value?.order?.id;
    if (!orderId) return;
    try {
      await ElMessageBox.confirm("确定删除这个订单吗?", "删除确认", { type: "warning" });
      await request.post("/order/cancel", null, { params: { orderId } });
      ElMessage.success("订单已删除");
      router.replace("/user/order");
    } catch (e) {}
  };

  // ========== 数据加载 ==========
  const loadDetail = async () => {
    const res = await request.get("/order/detail", { params: { orderId: route.params.orderId } });
    detail.value = res.data;
  };

  const loadSettings = async () => {
    try {
      const res = await request.get("/api/system/settings");
      const kv = res.data || {};
      settings.value.countdownMinutes = parseInt(kv["order.countdown_minutes"] || 15);
      settings.value.shipEtaText = kv["order.ship_eta_text"] || settings.value.shipEtaText;
      settings.value.cancelWarning = kv["order.cancel_warning"] || settings.value.cancelWarning;
    } catch (e) {}
  }

  // ========== 5s 轮询（同步 admin 端状态变化） ==========
  function startPolling() {
    stopPolling();
    pollTimer = setInterval(() => {
      if (!detail.value) return;
      // status=0/1/2 时最可能被 admin 改动
      const s = detail.value.order?.status;
      if (s === 0 || s === 1 || s === 2) loadDetail().catch(()=>{});
    }, 5000);
  }
  function stopPolling() { if (pollTimer) { clearInterval(pollTimer); pollTimer = null; } }

  watch(detail, (v) => {
    if (!v?.order) return;
    if (v.order.status === 0) startCountdown(); else stopCountdown();
  }, { deep: true });

  onMounted(async () => {
    try {
      await loadSettings();
      await loadDetail();
      startPolling();
    } catch (e) { ElMessage.error("加载订单详情失败"); }
  });

  onBeforeUnmount(() => { stopCountdown(); stopPolling(); });
</script>
<style scoped>
.detail-page {
  min-height: 100vh;
  position: relative;
  z-index: 0;
  padding: 32px 0 120px;
}
.detail-page > * { position: relative; z-index: 2; }

.back-bar {
  font-size: 14px;
  color: var(--gold-100, #f5d36e);
  cursor: pointer;
  margin-bottom: 16px;
  display: inline-block;
  letter-spacing: 1px;
  transition: all 0.25s ease;
}
.back-bar:hover {
  color: var(--gold-50, #fff9e6);
  transform: translateX(-4px);
  text-shadow: 0 0 12px rgba(212, 175, 55, 0.55);
}

/* ===== 状态条 ===== */
.status-banner {
  max-width: 980px;
  margin: 0 auto 24px;
  padding: 28px 32px;
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.18), rgba(240, 214, 138, 0.08));
  border: 1px solid rgba(212, 175, 55, 0.35);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(240, 214, 138, 0.25);
  display: flex;
  align-items: center;
  gap: 22px;
  color: #efe9d6;
}
.status-banner.st--1 { background: linear-gradient(135deg, rgba(142, 135, 112, 0.15), rgba(142, 135, 112, 0.05)); border-color: rgba(142, 135, 112, 0.3); }
.status-banner.st-0 { background: linear-gradient(135deg, rgba(251, 191, 36, 0.18), rgba(251, 191, 36, 0.05)); border-color: rgba(251, 191, 36, 0.4); }
.status-banner.st-1 { background: linear-gradient(135deg, rgba(96, 165, 250, 0.18), rgba(96, 165, 250, 0.05)); border-color: rgba(96, 165, 250, 0.4); }
.status-banner.st-2 { background: linear-gradient(135deg, rgba(52, 211, 153, 0.18), rgba(52, 211, 153, 0.05)); border-color: rgba(52, 211, 153, 0.4); }
.status-banner.st-3 { background: linear-gradient(135deg, rgba(212, 175, 55, 0.22), rgba(240, 214, 138, 0.08)); border-color: rgba(212, 175, 55, 0.5); }

.status-icon {
  font-size: 48px;
  filter: drop-shadow(0 0 14px rgba(240, 214, 138, 0.55));
  flex-shrink: 0;
}

.status-text {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 2px;
  margin-bottom: 4px;
}

.status-tip {
  font-size: 13px;
  color: #c4bca0;
  letter-spacing: 1px;
}

/* ===== 通用 section ===== */
.section {
  max-width: 980px;
  margin: 0 auto 18px;
  padding: 22px 26px;
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

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--gold-100, #f5d36e);
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

/* ===== 时间轴 ===== */
.timeline {
  display: flex;
  justify-content: space-between;
  position: relative;
  padding: 16px 0;
}
.timeline::before {
  content: "";
  position: absolute;
  top: 32px;
  left: 6%;
  right: 6%;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(212, 175, 55, 0.3) 20%, rgba(212, 175, 55, 0.3) 80%, transparent);
}
.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  position: relative;
}
.dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: rgba(212, 175, 55, 0.25);
  border: 2px solid rgba(212, 175, 55, 0.4);
  margin-bottom: 12px;
  position: relative;
  z-index: 2;
}
.step.active .dot {
  background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
  border-color: var(--gold-300, #e8b947);
  box-shadow: 0 0 14px rgba(212, 175, 55, 0.55);
}
.step.current .dot {
  background: var(--gold-50, #fff9e6);
  border-color: var(--gold-50, #fff9e6);
  box-shadow: 0 0 20px rgba(255, 249, 230, 0.8);
  animation: pulse 1.8s infinite;
}
@keyframes pulse {
  0%   { box-shadow: 0 0 0 0 rgba(255, 249, 230, 0.6); }
  100% { box-shadow: 0 0 0 10px rgba(255, 249, 230, 0); }
}

.step-name {
  font-size: 13px;
  color: #8e8770;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}
.step.active .step-name {
  color: var(--gold-100, #f5d36e);
  font-weight: 600;
}
.step.current .step-name {
  color: var(--gold-50, #fff9e6);
  font-weight: 700;
}
.step-time {
  font-size: 11px;
  color: #6c6552;
  margin-top: 4px;
  letter-spacing: 0.5px;
}

/* ===== 收货信息 ===== */
.addr-body {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}
.addr-avatar {
  font-size: 28px;
  flex-shrink: 0;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.18), rgba(240, 214, 138, 0.12));
  border: 1px solid rgba(212, 175, 55, 0.3);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.addr-line1 {
  display: flex;
  gap: 12px;
  margin-bottom: 6px;
  font-size: 15px;
}
.addr-name {
  font-weight: 700;
  color: #efe9d6;
}
.addr-phone {
  color: #c4bca0;
}
.addr-line2 {
  font-size: 13px;
  color: #c4bca0;
  line-height: 1.7;
}

/* ===== 商品明细 ===== */
.shop-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 14px;
  border-bottom: 1px dashed rgba(212, 175, 55, 0.2);
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: var(--gold-100, #f5d36e);
  letter-spacing: 1px;
}
.shop-icon {
  font-size: 18px;
  filter: drop-shadow(0 0 6px rgba(212, 175, 55, 0.4));
}

.product-row {
  display: flex;
  gap: 14px;
  padding: 12px 0;
  align-items: center;
  border-bottom: 1px dashed rgba(212, 175, 55, 0.12);
}
.product-row:last-child { border-bottom: none; padding-bottom: 0; }

.product-img {
  width: 70px;
  height: 70px;
  border-radius: 10px;
  object-fit: cover;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(212, 175, 55, 0.18);
  flex-shrink: 0;
}
.product-info {
  flex: 1;
  min-width: 0;
}
.product-name {
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
.product-meta {
  font-size: 12px;
  color: #8e8770;
}
.product-price {
  text-align: right;
}
.product-price .unit {
  font-size: 14px;
  font-weight: 700;
  color: #efe9d6;
}
.product-price .sub {
  font-size: 11px;
  color: #8e8770;
  margin-top: 4px;
}

/* ===== 信息行 ===== */
.info-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 13.5px;
}
.info-label {
  color: #8e8770;
  letter-spacing: 1px;
}
.info-value {
  color: #efe9d6;
  font-weight: 500;
}
.info-value.mono {
  font-family: Menlo, Consolas, monospace;
  font-size: 13px;
}
.pay-badge {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 999px;
  background: rgba(212, 175, 55, 0.12);
  border: 1px solid rgba(212, 175, 55, 0.3);
  color: var(--gold-100, #f5d36e);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1px;
}

/* ===== 金额明细 ===== */
.money-line {
  display: flex;
  justify-content: space-between;
  padding: 7px 0;
  font-size: 13.5px;
  color: #c4bca0;
}
.money-val {
  color: #efe9d6;
}
.money-val.discount {
  color: #fbbf24;
  font-weight: 600;
}
.money-line.total {
  border-top: 1px dashed rgba(212, 175, 55, 0.3);
  margin-top: 8px;
  padding-top: 14px;
  font-size: 14px;
}
.total-val {
  color: #fff9e6;
  font-weight: 800;
  font-size: 24px !important;
}

/* ===== 订单 meta ===== */
.order-meta-section .meta-line {
  display: flex;
  justify-content: space-between;
  padding: 5px 0;
  font-size: 12px;
  color: #8e8770;
}
.order-meta-section .mono {
  font-family: Menlo, Consolas, monospace;
  color: #efe9d6;
}

.copy-btn {
  margin-top: 12px;
  padding: 7px 16px;
  border-radius: 999px;
  border: 1px solid rgba(212, 175, 55, 0.35);
  background: transparent;
  color: var(--gold-100, #f5d36e);
  font-size: 12px;
  cursor: pointer;
  letter-spacing: 1px;
  transition: all 0.25s ease;
}
.copy-btn:hover {
  background: rgba(212, 175, 55, 0.15);
  border-color: var(--gold-300, #e8b947);
}

/* ===== 底部操作栏 ===== */
.action-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 14px 24px;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  align-items: center;
  background: rgba(8, 8, 8, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1px solid rgba(212, 175, 55, 0.3);
  box-shadow: 0 -10px 30px rgba(0, 0, 0, 0.5);
  z-index: 50;
}
.a-btn {
  padding: 10px 22px;
  border-radius: 56px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid transparent;
  transition: all 0.25s ease;
  font-family: inherit;
  letter-spacing: 1px;
}
.a-btn-default {
  background: transparent;
  color: var(--gold-100, #f5d36e);
  border-color: rgba(212, 175, 55, 0.3);
}
.a-btn-default:hover {
  background: rgba(212, 175, 55, 0.12);
  border-color: var(--gold-300, #e8b947);
}
.a-btn-primary {
  background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
  color: #1a1408;
  border-color: var(--gold-300, #e8b947);
}
.a-btn-primary:hover { box-shadow: 0 4px 14px rgba(212, 175, 55, 0.4); }
.a-btn-danger {
  background: transparent;
  color: #ff7e7e;
  border-color: rgba(255, 126, 126, 0.4);
}
.a-btn-danger:hover { background: rgba(255, 126, 126, 0.12); }
.a-btn-success {
  background: linear-gradient(135deg, #34d399, #10b981);
  color: #052e1f;
  border-color: #34d399;
}
.a-btn-success:hover { box-shadow: 0 4px 14px rgba(52, 211, 153, 0.4); }
.a-btn-pay {
  background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37) 50%, var(--gold-700, #8a7128));
  color: #1a1408;
  border: 1px solid var(--gold-300, #e8b947);
  padding: 10px 28px;
  box-shadow: 0 6px 18px rgba(212, 175, 55, 0.35);
}
.a-btn-pay:hover {
  box-shadow: 0 10px 28px rgba(212, 175, 55, 0.55);
}

@media (max-width: 720px) {
  .status-banner { flex-direction: column; text-align: center; padding: 20px; }
  .status-icon { font-size: 36px; }
  .status-text { font-size: 18px; }
}
</style>
