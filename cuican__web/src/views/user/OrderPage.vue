<template>
  <div class="page bg-dark">
    <BlackGoldBackground />
    <div class="inner">
      <h2 class="title gold-text-strong">📦 我的订单</h2>

      <div class="order-tabs">
        <span
          v-for="tab in tabs"
          :key="tab.key"
          class="order-tab"
          :class="{ active: activeTab === tab.key }"
          @click="activeTab = tab.key">
          {{ tab.label }}
        </span>
      </div>

      <div v-if="filteredOrders.length === 0" class="empty">
        <div class="empty-icon">📦</div>
        <p class="empty-text">暂无订单</p>
        <button class="empty-btn" @click="$router.push('/goods')">去逛逛</button>
      </div>

      <div class="order-list">
        <div
          v-for="item in pagedOrders"
          :key="item.id"
          class="order-card">
          <div class="card-line"></div>
          <div class="top">
            <span class="order-no">订单号：<span class="mono">{{ item.orderNo }}</span></span>
            <span class="status" :class="'st-' + item.status">{{ statusText(item.status) }}</span>
          </div>
          <div class="mid">
            <div class="mid-amount">
              <small>订单金额</small>
              <span class="gold-text-strong big">¥ {{ item.totalAmount }}</span>
            </div>
            <div class="mid-time">创建时间：{{ item.createTime }}</div>
          </div>
          <div class="action">
            <button v-if="item.status === 0" class="a-btn a-btn-gold" @click="goPay(item.id)">去支付</button>
            <button v-if="item.status === 0" class="a-btn a-btn-danger" @click="cancelOrder(item.id)">取消订单</button>
            <button v-if="item.status === 2" class="a-btn a-btn-success" @click="confirmOrder(item.id)">确认收货</button>
            <button class="a-btn a-btn-default" @click="openDetail(item.id)">查看详情</button>
          </div>
        </div>
      </div>

      <div class="order-pagination" v-if="total > 0">
        <span class="pg-size-label">每页</span>
        <select v-model="pageSize" class="pg-size" @change="onPageSizeChange">
          <option v-for="n in [2, 3, 5, 8, 10]" :key="n" :value="n">{{ n }}</option>
        </select>
        <button class="pg-btn" :disabled="page <= 1" @click="changePage(page - 1)">‹</button>
        <button
          v-for="p in pageNumbers"
          :key="p"
          class="pg-num"
          :class="{ active: p === page }"
          @click="changePage(p)">
          {{ p }}
        </button>
        <button class="pg-btn" :disabled="page >= pageCount" @click="changePage(page + 1)">›</button>
        <span class="pg-info">共 {{ total }} 条</span>
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <div class="detail-overlay" :class="{ active: showDetail }" @click.self="showDetail = false">
      <div class="detail-dialog">
        <div class="detail-header">
          <span class="detail-title gold-text-strong">订单详情</span>
          <button class="detail-link-btn" @click="openDetail(detail.order.id)">完整详情 →</button>
          <button class="detail-close" @click="showDetail = false">×</button>
        </div>
        <div class="detail-body" v-if="detail">
          <div class="detail-section">
            <div class="detail-row"><span>订单号</span><span class="mono">{{ detail.order.orderNo }}</span></div>
            <div class="detail-row"><span>状态</span><span :style="{ color: statusColor(detail.order.status) }">{{ statusText(detail.order.status) }}</span></div>
            <div class="detail-row"><span>总金额</span><span class="gold-text-strong">¥ {{ detail.order.totalAmount }}</span></div>
            <div class="detail-row"><span>创建时间</span><span>{{ detail.order.createTime }}</span></div>
          </div>
          <div class="detail-items-section">
            <div class="detail-items-title">商品明细</div>
            <div v-for="item in detail.items" :key="item.id" class="detail-item">
              <img v-if="getImgSrc(item.productImage)" class="detail-item-img" :src="getImgSrc(item.productImage)" />
              <div v-else class="detail-item-img-placeholder">💎</div>
              <div class="detail-item-info">
                <div class="detail-item-name">{{ item.productName }}</div>
                <div class="detail-item-meta">¥{{ item.price }} × {{ item.quantity }}</div>
              </div>
            </div>
          </div>

          <div class="detail-actions" v-if="detail">
            <button v-if="detail.order.status === 0" class="d-btn d-btn-gold" @click="goPay(detail.order.id)">去支付</button>
            <button v-if="detail.order.status === 0" class="d-btn d-btn-danger" @click="cancelInModal">取消订单</button>
            <button v-if="detail.order.status === 2" class="d-btn d-btn-success" @click="confirmInModal">确认收货</button>
            <button class="d-btn d-btn-default" @click="showDetail = false">关闭</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, computed, watch, onMounted } from "vue";
  import { useRouter } from "vue-router";
  import { ElMessage, ElMessageBox } from "element-plus";
  import request from "@/utils/request";
  import BlackGoldBackground from "@/components/background/BlackGoldBackground.vue";

  const router = useRouter();
  const orders = ref([]);

  const tabs = [
    { key: "all", label: "全部" },
    { key: "0", label: "待付款" },
    { key: "1", label: "待发货" },
    { key: "2", label: "已发货" },
    { key: "3", label: "已完成" },
    { key: "-1", label: "已取消" },
  ];
  const activeTab = ref("all");
  const filteredOrders = computed(() => {
    if (activeTab.value === "all") return orders.value;
    return orders.value.filter(o => o.status === parseInt(activeTab.value));
  });

  const page = ref(1);
  const pageSize = ref(5);
  const total = computed(() => filteredOrders.value.length);
  const pageCount = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)));
  const pagedOrders = computed(() =>
    filteredOrders.value.slice((page.value - 1) * pageSize.value, page.value * pageSize.value),
  );
  const pageNumbers = computed(() => {
    const c = pageCount.value;
    const p = page.value;
    if (c <= 7) return Array.from({ length: c }, (_, i) => i + 1);
    const set = new Set([1, 2, p - 1, p, p + 1, c - 1, c]);
    return [...set].filter((n) => n >= 1 && n <= c).sort((a, b) => a - b);
  });

  const changePage = (p) => {
    if (p < 1 || p > pageCount.value) return;
    page.value = p;
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const onPageSizeChange = () => {
    page.value = 1;
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  watch(activeTab, () => { page.value = 1; });
  watch(total, () => { if (page.value > pageCount.value) page.value = Math.max(1, pageCount.value); });

  const statusText = (status) => {
    switch (status) {
      case -1: return "已取消";
      case 0: return "待付款";
      case 1: return "待发货";
      case 2: return "已发货";
      case 3: return "已完成";
      default: return "未知";
    }
  };

  const statusColor = (status) => {
    switch (status) {
      case -1: return "#8e8770";
      case 0: return "#fbbf24";
      case 1: return "#60a5fa";
      case 2: return "#34d399";
      case 3: return "#d4af37";
      default: return "#c4bca0";
    }
  };

  const BASE_URL = "http://localhost:8080";
  const showDetail = ref(false);
  const detail = ref(null);

  const getImgSrc = (url) => {
    if (!url || url.startsWith("img/")) return null;
    if (url.startsWith("http")) return url;
    return BASE_URL + url;
  };

  const load = async () => {
    try {
      const res = await request.get("/order/list");
      orders.value = res.data || [];
    } catch (e) {
      orders.value = [];
    }
  };

  const goPay = (id) => router.push(`/order/pay/${id}`);
  const openDetail = (id) => router.push(`/order/detail/${id}`);

  const cancelOrder = async (id) => {
    try {
      await ElMessageBox.confirm("确定取消该订单？", "确认", { type: "warning" });
      await request.post("/order/cancel", null, { params: { orderId: id } });
      ElMessage.success("订单已取消");
      load();
    } catch (e) {}
  };

  const confirmOrder = async (id) => {
    try {
      await request.post("/order/confirm", null, { params: { orderId: id } });
      ElMessage.success("已确认收货");
      load();
    } catch (e) {}
  };

  const openDetailModal = async (id) => {
    try {
      const res = await request.get("/order/detail", { params: { orderId: id } });
      detail.value = res.data;
      showDetail.value = true;
    } catch (e) {}
  };

  // Bind global function used by template
  const cancelInModal = () => cancelOrder(detail.value.order.id);
  const confirmInModal = () => confirmOrder(detail.value.order.id);

  onMounted(load);
</script>

<style scoped>
  .page {
    min-height: 100vh;
    position: relative;
    z-index: 0;
    padding: 32px 0 60px;
  }
  .inner {
    position: relative;
    z-index: 2;
    max-width: 1080px;
    margin: 0 auto;
    padding: 0 24px;
  }

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

  .title {
    font-size: 32px;
    margin: 0 0 24px;
    letter-spacing: 4px;
    font-weight: 800;
    font-family: var(--heading-font);
  }

  /* ===== Tabs ===== */
  .order-tabs {
    display: flex;
    gap: 8px;
    margin-bottom: 24px;
    flex-wrap: wrap;
  }
  .order-tab {
    padding: 8px 20px;
    border-radius: 999px;
    font-size: 13px;
    cursor: pointer;
    background: rgba(20, 20, 20, 0.6);
    border: 1px solid rgba(212, 175, 55, 0.2);
    color: #c4bca0;
    transition: all 0.3s ease;
    letter-spacing: 2px;
  }
  .order-tab:hover {
    background: rgba(212, 175, 55, 0.12);
    border-color: rgba(212, 175, 55, 0.4);
    color: var(--gold-50, #fff9e6);
  }
  .order-tab.active {
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
    color: #1a1408;
    border-color: var(--gold-300, #e8b947);
    font-weight: 700;
    box-shadow: 0 4px 14px rgba(212, 175, 55, 0.35);
  }

  /* ===== 订单卡片 ===== */
  .order-list { display: flex; flex-direction: column; gap: 16px; }

  /* ===== 分页 ===== */
  .order-pagination {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    margin-top: 28px;
    flex-wrap: wrap;
  }
  .pg-btn,
  .pg-num {
    min-width: 36px;
    height: 36px;
    padding: 0 10px;
    border-radius: 12px;
    border: 1px solid rgba(212, 175, 55, 0.3);
    background: linear-gradient(145deg, rgba(30, 25, 12, 0.6), rgba(12, 12, 12, 0.6));
    color: var(--gold-100, #f5d36e);
    font-size: 14px;
    cursor: pointer;
    transition: all 0.25s ease;
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-family: inherit;
  }
  .pg-size-label {
    font-size: 13px;
    color: #c4bca0;
    letter-spacing: 1px;
  }
  .pg-size {
    height: 36px;
    padding: 0 10px;
    border-radius: 12px;
    border: 1px solid rgba(212, 175, 55, 0.3);
    background: linear-gradient(145deg, rgba(30, 25, 12, 0.6), rgba(12, 12, 12, 0.6));
    color: var(--gold-100, #f5d36e);
    font-size: 14px;
    cursor: pointer;
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    outline: none;
    font-family: inherit;
  }
  .pg-size option {
    background: #12100b;
    color: #f5d36e;
  }
  .pg-size:focus {
    border-color: var(--gold-300, #e8b947);
    box-shadow: 0 0 12px rgba(212, 175, 55, 0.3);
  }
  .pg-btn:hover:not(:disabled),
  .pg-num:hover {
    background: rgba(212, 175, 55, 0.18);
    border-color: var(--gold-300, #e8b947);
    color: var(--gold-50, #fff9e6);
    transform: translateY(-2px);
    box-shadow: 0 4px 14px rgba(212, 175, 55, 0.3);
  }
  .pg-num.active {
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
    color: #1a1408;
    border-color: var(--gold-300, #e8b947);
    font-weight: 700;
    box-shadow: 0 4px 16px rgba(212, 175, 55, 0.45);
  }
  .pg-btn:disabled {
    opacity: 0.35;
    cursor: not-allowed;
    transform: none;
  }
  .pg-info {
    margin-left: 8px;
    font-size: 13px;
    color: #c4bca0;
    letter-spacing: 1px;
  }

  @keyframes fadeUpCard {
    from { opacity: 0; transform: translateY(18px); }
    to { opacity: 1; transform: translateY(0); }
  }
  .order-card {
    animation: fadeUpCard 0.5s cubic-bezier(0.2, 0.9, 0.4, 1) backwards;
    position: relative;
    padding: 20px 24px;
    border-radius: 18px;
    background: linear-gradient(145deg, rgba(30, 25, 12, 0.55), rgba(12, 12, 12, 0.55));
    background-color: rgba(20, 20, 20, 0.6);
    border: 1px solid rgba(212, 175, 55, 0.22);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    box-shadow:
      0 14px 40px rgba(0, 0, 0, 0.45),
      inset 0 1px 0 rgba(212, 175, 55, 0.14);
    transition: all 0.3s ease;
  }
  .order-card:nth-child(1) { animation-delay: 0s; }
  .order-card:nth-child(2) { animation-delay: 0.06s; }
  .order-card:nth-child(3) { animation-delay: 0.12s; }
  .order-card:nth-child(4) { animation-delay: 0.18s; }
  .order-card:nth-child(5) { animation-delay: 0.24s; }
  .order-card:nth-child(n+6) { animation-delay: 0.3s; }
  .order-card:hover {
    border-color: rgba(240, 214, 138, 0.5);
    transform: translateY(-2px);
  }
  .card-line {
    position: absolute;
    top: 8px; left: 24px; right: 24px;
    height: 1px;
    background: linear-gradient(90deg, transparent, var(--gold-300, #e8b947) 50%, transparent);
    opacity: 0.5;
  }

  .top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 14px;
  }
  .order-no {
    font-size: 14px;
    color: #c4bca0;
    letter-spacing: 1px;
  }
  .order-no .mono {
    font-family: Menlo, Consolas, monospace;
    color: #efe9d6;
    font-size: 13px;
  }

  .status {
    padding: 4px 14px;
    border-radius: 999px;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 1px;
  }
  .status.st--1 { background: rgba(142, 135, 112, 0.18); color: #8e8770; border: 1px solid rgba(142, 135, 112, 0.35); }
  .status.st-0 { background: rgba(251, 191, 36, 0.18); color: #fbbf24; border: 1px solid rgba(251, 191, 36, 0.4); }
  .status.st-1 { background: rgba(96, 165, 250, 0.18); color: #60a5fa; border: 1px solid rgba(96, 165, 250, 0.4); }
  .status.st-2 { background: rgba(52, 211, 153, 0.18); color: #34d399; border: 1px solid rgba(52, 211, 153, 0.4); }
  .status.st-3 { background: rgba(212, 175, 55, 0.18); color: var(--gold-300, #e8b947); border: 1px solid rgba(212, 175, 55, 0.4); }

  .mid {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    padding: 14px 0;
    border-top: 1px dashed rgba(212, 175, 55, 0.15);
    border-bottom: 1px dashed rgba(212, 175, 55, 0.15);
    margin-bottom: 16px;
  }
  .mid-amount {
    display: flex;
    align-items: baseline;
    gap: 10px;
  }
  .mid-amount small {
    font-size: 12px;
    color: #8e8770;
    letter-spacing: 2px;
  }
  .mid-amount .big {
    font-size: 24px;
    font-weight: 800;
  }
  .mid-time {
    font-size: 12px;
    color: #8e8770;
    letter-spacing: 1px;
  }

  .action {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
    justify-content: flex-end;
  }

  .a-btn {
    padding: 8px 22px;
    border-radius: 56px;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    border: 1px solid transparent;
    transition: all 0.25s ease;
    font-family: inherit;
    letter-spacing: 1px;
  }
  .a-btn-gold {
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
    color: #1a1408;
    border-color: var(--gold-300, #e8b947);
    box-shadow: 0 4px 14px rgba(212, 175, 55, 0.3);
  }
  .a-btn-gold:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(212, 175, 55, 0.5); }
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
  .a-btn-default {
    background: transparent;
    color: var(--gold-100, #f5d36e);
    border-color: rgba(212, 175, 55, 0.3);
  }
  .a-btn-default:hover { background: rgba(212, 175, 55, 0.12); border-color: var(--gold-300, #e8b947); }

  /* ===== 空状态 ===== */
  .empty {
    text-align: center;
    padding: 80px 0;
  }
  .empty-icon { font-size: 64px; margin-bottom: 16px; filter: drop-shadow(0 0 12px rgba(212, 175, 55, 0.3)); }
  .empty-text { color: #c4bca0; font-size: 16px; margin-bottom: 24px; }
  .empty-btn {
    padding: 12px 32px;
    border-radius: 56px;
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
    color: #1a1408;
    border: 1px solid var(--gold-300, #e8b947);
    font-weight: 700;
    cursor: pointer;
    letter-spacing: 2px;
    box-shadow: 0 8px 22px rgba(212, 175, 55, 0.35);
  }
  .empty-btn:hover { transform: translateY(-2px); box-shadow: 0 14px 36px rgba(212, 175, 55, 0.55); }

  /* ===== 详情弹窗 ===== */
  .detail-overlay {
    position: fixed;
    top: 0; left: 0;
    width: 100%; height: 100%;
    background: rgba(0, 0, 0, 0.7);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    z-index: 100;
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    pointer-events: none;
    transition: opacity 0.35s ease;
  }
  .detail-overlay.active { opacity: 1; pointer-events: auto; }
  .detail-dialog {
    width: 520px;
    max-width: 90%;
    max-height: 84vh;
    background: linear-gradient(145deg, rgba(30, 25, 12, 0.92), rgba(12, 12, 12, 0.92));
    background-color: rgba(20, 20, 20, 0.95);
    border-radius: 22px;
    border: 1px solid rgba(212, 175, 55, 0.4);
    box-shadow:
      0 40px 80px rgba(0, 0, 0, 0.6),
      inset 0 1px 0 rgba(240, 214, 138, 0.25);
    display: flex;
    flex-direction: column;
    transform: scale(0.92) translateY(20px);
    opacity: 0;
    transition: all 0.35s cubic-bezier(0.2, 0.9, 0.4, 1.1);
    color: #efe9d6;
  }
  .detail-overlay.active .detail-dialog {
    transform: scale(1) translateY(0);
    opacity: 1;
  }

  .detail-header {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 20px 24px 12px;
    border-bottom: 1px solid rgba(212, 175, 55, 0.2);
  }
  .detail-title {
    font-size: 20px;
    font-weight: 800;
    flex: 1;
  }
  .detail-link-btn {
    background: rgba(212, 175, 55, 0.15);
    border: 1px solid rgba(212, 175, 55, 0.3);
    border-radius: 12px;
    padding: 4px 12px;
    font-size: 12px;
    color: var(--gold-100, #f5d36e);
    cursor: pointer;
    transition: 0.2s;
  }
  .detail-link-btn:hover { background: rgba(212, 175, 55, 0.25); }
  .detail-close {
    background: rgba(212, 175, 55, 0.15);
    border: 1px solid rgba(212, 175, 55, 0.3);
    border-radius: 50%;
    width: 32px; height: 32px;
    font-size: 16px;
    cursor: pointer;
    color: var(--gold-100, #f5d36e);
    display: flex;
    align-items: center;
    justify-content: center;
    transition: 0.2s;
  }
  .detail-close:hover { background: rgba(212, 175, 55, 0.3); color: var(--gold-50, #fff9e6); }

  .detail-body {
    padding: 16px 24px 20px;
    overflow-y: auto;
    flex: 1;
  }

  .detail-section {
    background: rgba(20, 20, 20, 0.5);
    border-radius: 12px;
    padding: 14px 18px;
    margin-bottom: 16px;
    border: 1px solid rgba(212, 175, 55, 0.18);
  }
  .detail-row {
    display: flex;
    justify-content: space-between;
    padding: 6px 0;
    font-size: 14px;
    color: #c4bca0;
  }
  .detail-row span:first-child { color: #8e8770; }
  .detail-row .mono {
    font-family: Menlo, Consolas, monospace;
    color: #efe9d6;
    font-size: 13px;
  }

  .detail-items-section {
    border-top: 1px solid rgba(212, 175, 55, 0.2);
    padding-top: 14px;
  }
  .detail-items-title {
    font-size: 15px;
    font-weight: 700;
    color: var(--gold-100, #f5d36e);
    margin-bottom: 12px;
    letter-spacing: 2px;
  }
  .detail-item {
    display: flex;
    gap: 14px;
    padding: 10px 0;
    border-bottom: 1px dashed rgba(212, 175, 55, 0.15);
  }
  .detail-item:last-child { border-bottom: none; }
  .detail-item-img {
    width: 56px; height: 56px;
    border-radius: 10px;
    object-fit: cover;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(212, 175, 55, 0.18);
    flex-shrink: 0;
  }
  .detail-item-img-placeholder {
    width: 56px; height: 56px;
    border-radius: 10px;
    background: linear-gradient(135deg, rgba(212, 175, 55, 0.18), rgba(240, 214, 138, 0.12));
    display: flex; align-items: center; justify-content: center;
    font-size: 24px;
    flex-shrink: 0;
  }
  .detail-item-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .detail-item-name {
    font-size: 14px;
    color: #efe9d6;
    font-weight: 500;
    margin-bottom: 4px;
  }
  .detail-item-meta {
    font-size: 13px;
    color: #fbbf24;
    font-weight: 600;
  }

  .detail-actions {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
    flex-wrap: wrap;
    margin-top: 16px;
    padding-top: 14px;
    border-top: 1px dashed rgba(212, 175, 55, 0.2);
  }
  .d-btn {
    padding: 8px 22px;
    border-radius: 56px;
    font-size: 13px;
    font-weight: 500;
    cursor: pointer;
    border: 1px solid transparent;
    transition: all 0.25s ease;
    font-family: inherit;
  }
  .d-btn-gold {
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
    color: #1a1408;
    border-color: var(--gold-300, #e8b947);
  }
  .d-btn-gold:hover { box-shadow: 0 4px 14px rgba(212, 175, 55, 0.4); }
  .d-btn-danger {
    background: transparent;
    border-color: rgba(255, 126, 126, 0.4);
    color: #ff7e7e;
  }
  .d-btn-danger:hover { background: rgba(255, 126, 126, 0.12); }
  .d-btn-success {
    background: linear-gradient(135deg, #34d399, #10b981);
    color: #052e1f;
    border-color: #34d399;
  }
  .d-btn-success:hover { box-shadow: 0 4px 14px rgba(52, 211, 153, 0.4); }
  .d-btn-default {
    background: transparent;
    color: var(--gold-100, #f5d36e);
    border-color: rgba(212, 175, 55, 0.3);
  }
  .d-btn-default:hover { background: rgba(212, 175, 55, 0.12); }
</style>


