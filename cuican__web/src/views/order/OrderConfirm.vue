<template>
  <div class="confirm-page bg-dark">
    <BlackGoldBackground />
    <div class="inner">
      <div class="back-bar" @click="$router.back()">← 返回</div>
      <h2 class="page-title gold-text-strong">确认订单</h2>

      <!-- 收货信息 -->
      <div class="section">
        <div class="section-title">收货信息</div>
        <div class="form-row">
          <input v-model="form.receiverName" placeholder="收货人" class="form-input half" />
          <input v-model="form.receiverPhone" placeholder="手机号" class="form-input half" />
        </div>
        <div class="form-row">
          <select v-model="selectedAddr" @change="onAddrSelect" class="form-input full">
            <option value="">— 选择已有地址 —</option>
            <option v-for="a in addresses" :key="a.id" :value="a.id">{{ a.receiverName }} {{ a.province }} {{ a.city }} {{ a.detailAddress }}</option>
          </select>
          <button class="small-btn" @click="showAddrForm = !showAddrForm">+ 新增</button>
        </div>
        <div v-if="showAddrForm" class="addr-inline-form">
          <div class="form-row">
            <input v-model="newAddr.receiverName" placeholder="收货人" class="form-input half" />
            <input v-model="newAddr.receiverPhone" placeholder="手机号" class="form-input half" />
          </div>
          <div class="form-row">
            <AddressPicker v-model="newAddrCodes" class="addr-picker" />
          </div>
          <div class="form-row">
            <input v-model="newAddr.detailAddress" placeholder="详细地址" class="form-input full" />
          </div>
          <div class="form-row" style="gap:8px;">
            <button class="small-btn primary" @click="saveNewAddr">保存</button>
            <button class="small-btn" @click="showAddrForm = false">取消</button>
          </div>
        </div>
        <div class="form-row">
          <input v-model="form.note" placeholder="订单备注（可选）" class="form-input full" />
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="section">
        <div class="section-title">商品信息</div>
        <div v-for="(item, i) in items" :key="i" class="order-item">
          <img class="item-img" :src="getImg(item.productImage)" />
          <div class="item-info">
            <div class="item-name">{{ item.productName || item.name }}</div>
            <div class="item-price">¥{{ item.price || 0 }} × {{ item.quantity }}</div>
          </div>
          <div class="item-subtotal gold-text-strong">¥{{ ((item.price || 0) * item.quantity).toFixed(2) }}</div>
        </div>
      </div>

      <!-- 价格明细 -->
      <div class="section price-section">
        <div class="price-row"><span>商品总额</span><span>¥{{ originalTotal.toFixed(2) }}</span></div>
        <div class="price-row" v-if="discountLabel">
          <span>折扣 ({{ discountLabel }})</span>
          <span class="discount-amount">-¥{{ discountAmount.toFixed(2) }}</span>
        </div>
        <div class="price-row total"><span>合计</span><span class="gold-text-strong">¥{{ finalTotal.toFixed(2) }}</span></div>
      </div>

      <button class="submit-btn" :disabled="submitting" @click="submitOrder">
        {{ submitting ? "提交中..." : "确认下单" }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";
import AddressPicker from "@/components/common/AddressPicker.vue";
import { pcaa } from "area-data";
import BlackGoldBackground from "@/components/background/BlackGoldBackground.vue";

const route = useRoute();
const router = useRouter();
const BASE = "http://localhost:8080";

const items = ref([]);
const addresses = ref([]);
const selectedAddr = ref("");
const showAddrForm = ref(false);
const submitting = ref(false);

const form = ref({ receiverName: "", receiverPhone: "", note: "" });
const newAddr = ref({ receiverName: "", receiverPhone: "", province: "", city: "", detailAddress: "" });
const newAddrCodes = ref([]);

const getImg = (url) => {
  if (!url) return "https://via.placeholder.com/60x60?text=N";
  if (url.startsWith("http")) return url;
  return BASE + url;
};

const calcDiscount = (total) => {
  if (total >= 10000) return { rate: 0.88, label: "88折" };
  if (total >= 5000) return { rate: 0.92, label: "92折" };
  if (total >= 1000) return { rate: 0.95, label: "95折" };
  if (total >= 500) return { rate: 0.97, label: "97折" };
  return { rate: 1, label: "" };
};

const originalTotal = computed(() => items.value.reduce((s, i) => s + (i.price || 0) * i.quantity, 0));
const discountInfo = computed(() => calcDiscount(originalTotal.value));
const discountLabel = computed(() => discountInfo.value.label);
const finalTotal = computed(() => originalTotal.value * discountInfo.value.rate);
const discountAmount = computed(() => originalTotal.value - finalTotal.value);

const onAddrSelect = () => {
  const a = addresses.value.find(x => String(x.id) === String(selectedAddr.value));
  if (a) {
    form.value.receiverName = a.receiverName || "";
    form.value.receiverPhone = a.receiverPhone || "";
    form.value.province = a.province || "";
    form.value.city = a.city || "";
    form.value.detailAddress = a.detailAddress || "";
  }
};

const syncAreaToNewAddr = () => {
  const codes = newAddrCodes.value || [];
  const prov = pcaa[codes[0]];
  const city = prov && pcaa[codes[0] + codes[1]];
  const dist = city && pcaa[codes[0] + codes[1] + codes[2]];
  newAddr.value.province = prov ? prov.split("\u00b7")[0] : "";
  newAddr.value.city = city ? city.split("\u00b7")[0] : "";
  newAddr.value.district = dist ? dist.split("\u00b7")[0] : "";
};

const saveNewAddr = async () => {
  if (!newAddr.value.receiverName || !newAddr.value.receiverPhone) {
    ElMessage.warning("请填写收货人和手机号");
    return;
  }
  if (!newAddrCodes.value || newAddrCodes.value.length < 3) {
    ElMessage.warning("请选择省/市/区");
    return;
  }
  syncAreaToNewAddr();
  try {
    await request.post("/address/add", { ...newAddr.value, isDefault: 0 });
    ElMessage.success("地址已新增");
    showAddrForm.value = false;
    newAddrCodes.value = [];
    newAddr.value = { receiverName: "", receiverPhone: "", province: "", city: "", detailAddress: "" };
    const res = await request.get("/address/list");
    addresses.value = res.data || [];
  } catch (e) { ElMessage.error("添加失败"); }
};

const loadItems = async () => {
  const from = route.query.from;
  if (from === "buy") {
    const pid = route.query.productId;
    const qty = parseInt(route.query.quantity) || 1;
    const res = await request.get("/api/product/detail", { params: { id: pid } });
    const p = res.data.product;
    items.value = [{ productImage: p.coverImage, productName: p.name, name: p.name, price: parseFloat(p.price), quantity: qty }];
  } else if (from === "cart") {
    const res = await request.get("/api/cart/checked");
    items.value = (res.data || []).map(i => ({ ...i, price: parseFloat(i.price) }));
  }
};

const submitOrder = async () => {
  if (!form.value.receiverName || !form.value.receiverPhone) {
    ElMessage.warning("请填写收货人信息");
    return;
  }
  const from = route.query.from;
  submitting.value = true;
  try {
    const body = {
      receiverName: form.value.receiverName,
      receiverPhone: form.value.receiverPhone,
      province: form.value.province || "",
      city: form.value.city || "",
      detailAddress: form.value.detailAddress || "",
      note: form.value.note,
    };
    if (from === "buy") {
      body.productId = parseInt(route.query.productId);
      body.quantity = parseInt(route.query.quantity) || 1;
    } else if (from === "cart") {
      body.cartIds = items.value.map(i => i.id);
    }
    const res = await request.post("/order/create", body);
    ElMessage.success("订单已创建");
    router.push("/order/detail/" + res.data);
  } catch (e) {
    ElMessage.error("创建失败");
  } finally {
    submitting.value = false;
  }
};

onMounted(async () => {
  await loadItems();
  try {
    const res = await request.get("/address/list");
    addresses.value = res.data || [];
  } catch (e) {}
});
</script>

<style scoped>
  .confirm-page {
    min-height: 100vh;
    position: relative;
    z-index: 0;
    padding: 32px 0 60px;
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

  .page-title {
    font-size: 32px;
    margin: 0 0 28px;
    letter-spacing: 4px;
    font-weight: 800;
    font-family: var(--heading-font);
  }

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

  .form-row {
    display: flex;
    gap: 12px;
    margin-bottom: 12px;
    align-items: center;
  }

  .form-input {
    height: 44px;
    border-radius: 10px;
    border: 1px solid rgba(212, 175, 55, 0.25);
    background: rgba(20, 20, 20, 0.5);
    color: #efe9d6;
    padding: 0 14px;
    font-size: 14px;
    outline: none;
    transition: all 0.25s ease;
    box-sizing: border-box;
  }
  .form-input::placeholder { color: rgba(239, 233, 214, 0.4); }
  .form-input:focus {
    border-color: var(--gold-300, #e8b947);
    background: rgba(20, 20, 20, 0.7);
    box-shadow: 0 0 14px rgba(212, 175, 55, 0.2);
  }
  .form-input.half { flex: 1; min-width: 0; }
  .form-input.full { width: 100%; }
  select.form-input {
    cursor: pointer;
    appearance: none;
    background-image: linear-gradient(45deg, transparent 50%, var(--gold-300, #e8b947) 50%),
                      linear-gradient(135deg, var(--gold-300, #e8b947) 50%, transparent 50%);
    background-position: calc(100% - 18px) center, calc(100% - 12px) center;
    background-size: 6px 6px;
    background-repeat: no-repeat;
    padding-right: 32px;
  }

  .small-btn {
    padding: 10px 18px;
    border-radius: 8px;
    border: 1px solid rgba(212, 175, 55, 0.3);
    background: transparent;
    color: var(--gold-100, #f5d36e);
    font-size: 13px;
    cursor: pointer;
    white-space: nowrap;
    transition: all 0.25s ease;
  }
  .small-btn:hover {
    background: rgba(212, 175, 55, 0.12);
    border-color: var(--gold-300, #e8b947);
  }
  .small-btn.primary {
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
    color: #1a1408;
    border-color: var(--gold-300, #e8b947);
    font-weight: 700;
  }
  .small-btn.primary:hover {
    box-shadow: 0 6px 18px rgba(212, 175, 55, 0.4);
  }

  .addr-inline-form {
    background: rgba(20, 20, 20, 0.5);
    border-radius: 10px;
    padding: 14px;
    margin-bottom: 12px;
    border: 1px solid rgba(212, 175, 55, 0.18);
  }

  .addr-picker { display: block; width: 100%; }

  .order-item {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 12px 0;
    border-bottom: 1px dashed rgba(212, 175, 55, 0.15);
  }
  .order-item:last-child { border-bottom: none; padding-bottom: 0; }
  .item-img {
    width: 60px; height: 60px;
    border-radius: 10px; object-fit: cover;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(212, 175, 55, 0.18);
    flex-shrink: 0;
  }
  .item-info { flex: 1; min-width: 0; }
  .item-name {
    font-size: 14px; color: #efe9d6; font-weight: 500;
    margin-bottom: 6px;
    overflow: hidden; text-overflow: ellipsis;
    display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;
  }
  .item-price {
    font-size: 12px; color: #8e8770;
    letter-spacing: 1px;
  }
  .item-subtotal {
    font-size: 16px; font-weight: 700;
  }

  .price-row {
    display: flex;
    justify-content: space-between;
    padding: 7px 0;
    font-size: 14px;
    color: #c4bca0;
  }
  .price-row span:first-child { color: #8e8770; }
  .discount-amount { color: #fbbf24; font-weight: 600; }
  .price-row.total {
    border-top: 1px dashed rgba(212, 175, 55, 0.3);
    margin-top: 8px;
    padding-top: 14px;
    font-size: 18px;
    font-weight: 700;
  }

  .submit-btn {
    width: 100%;
    padding: 16px;
    border: 1px solid var(--gold-300, #e8b947);
    border-radius: 56px;
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37) 50%, var(--gold-700, #8a7128));
    color: #1a1408;
    font-size: 17px;
    font-weight: 700;
    letter-spacing: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow:
      0 8px 22px rgba(212, 175, 55, 0.35),
      inset 0 1px 0 rgba(255, 249, 230, 0.4);
  }
  .submit-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow:
      0 14px 36px rgba(212, 175, 55, 0.55),
      0 0 20px rgba(240, 214, 138, 0.5);
  }
  .submit-btn:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
