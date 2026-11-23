<template>
  <div class="oc-overlay" :class="{ active: visible }" @click.self="$emit('close')">
    <div class="oc-dialog">
      <div class="oc-header">
        <span class="oc-title">确认订单</span>
        <button class="oc-close" @click="$emit('close')">×</button>
      </div>
      <div class="oc-body">
        <div class="oc-section">
          <div class="oc-section-title">收货信息</div>
          <div class="oc-row">
            <input v-model="f.receiverName" placeholder="收货人" class="oc-inp" style="flex:1" />
            <input v-model="f.receiverPhone" placeholder="手机号" class="oc-inp" style="flex:1" />
          </div>
          <div class="oc-row">
            <select v-model="selAddr" @change="onSelAddr" class="oc-inp" style="flex:1">
              <option value="">— 选择已有地址 —</option>
              <option v-for="a in addrs" :key="a.id" :value="a.id">{{ a.receiverName }} {{ a.province }} {{ a.city }} {{ a.detailAddress }}</option>
            </select>
            <button class="oc-btn-s" @click="toggleNew">+新增</button>
          </div>
          <div v-if="showNew" class="oc-new">
            <div class="oc-row" style="display:flex;gap:10px;margin-bottom:8px">
              <input v-model="newReceiverName" placeholder="收货人" class="oc-inp" style="flex:1" />
              <input v-model="newReceiverPhone" placeholder="手机号" class="oc-inp" style="flex:1" />
            </div>
            <AddressPicker v-model="newAddrCodes" />
            <input v-model="newDet" placeholder="详细地址（街道、门牌号等）" class="oc-inp" style="width:100%;margin-top:6px" />
            <div class="oc-row" style="gap:8px;margin-top:8px">
              <button class="oc-btn oc-btn-pri" style="padding:7px 16px;font-size:13px" @click="saveNewAddr">保存</button>
              <button class="oc-btn oc-btn-def" style="padding:7px 16px;font-size:13px" @click="cancelNew">取消</button>
            </div>
          </div>
          <input v-model="f.note" placeholder="备注(可选)" class="oc-inp" style="width:100%;margin-top:8px" />
        </div>
        <div class="oc-section items-sect">
          <div class="oc-section-title">商品列表</div>
          <div v-for="(item,i) in items" :key="i" class="oc-item">
            <img class="oc-img" :src="imgUrl(item)" />
            <div class="oc-info">
              <div class="oc-nm">{{ item.productName||item.name }}</div>
              <div class="oc-meta">¥{{ item.price||0 }} × {{ item.quantity }}</div>
            </div>
            <div class="oc-sub">¥{{ ((item.price||0)*item.quantity).toFixed(2) }}</div>
          </div>
        </div>
        <div class="oc-section">
          <div class="oc-pr"><span>商品总额</span><span>¥{{ ot.toFixed(2) }}</span></div>
          <div class="oc-pr" v-if="dl"><span>折扣 ({{ dl }})</span><span style="color:#e05a4f;">-¥{{ da.toFixed(2) }}</span></div>
          <div class="oc-pr oc-t"><span>实付</span><span>¥{{ ft.toFixed(2) }}</span></div>
        </div>
      </div>
      <div class="oc-footer">
        <button class="oc-btn oc-btn-def" @click="$emit('close')">取消</button>
        <button class="oc-btn oc-btn-pri" :disabled="sub" @click="submitOrder">{{ sub ? '提交中...' : '确认下单' }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";
import { pcaa } from "area-data";
import AddressPicker from "@/components/common/AddressPicker.vue";

const props = defineProps({ visible: Boolean, items: Array, from: String });
const emit = defineEmits(["close","confirmed"]);
const BASE = "http://localhost:8080";

const f = ref({ receiverName: "", receiverPhone: "", note: "" });
const addrs = ref([]);
const selAddr = ref("");
const showNew = ref(false);

// 新增地址内联表单
const newReceiverName = ref("");
const newReceiverPhone = ref("");
const newAddrCodes = ref([]); // [pc, cc, dc]
const newDet = ref("");

const sub = ref(false);

// 区拼到 detailAddress 前 + 名字回填
const syncAreaToNew = () => {
  const [pc, cc, dc] = newAddrCodes.value || [];
  const provinceMap = pcaa["86"] || {};
  const cityMap = pc ? (pcaa[pc] || {}) : {};
  const districtMap = cc ? (pcaa[cc] || {}) : {};
  const districtName = districtMap[dc] || "";
  const cleanDetail = (newDet.value || "").replace(/^\S+\s+/, "");
  newDet.value = districtName ? districtName + " " + cleanDetail : cleanDetail;
};
watch(newAddrCodes, () => syncAreaToNew(), { deep: true });

const imgUrl = (item) => {
  const u = item.productImage || item.image || "";
  if (!u) return "https://via.placeholder.com/48x48?text=N";
  if (u.startsWith("http")) return u;
  return BASE + u;
};

const dc = (t) => {
  if (t >= 10000) return { r: 0.88, l: "88折" };
  if (t >= 5000) return { r: 0.92, l: "92折" };
  if (t >= 1000) return { r: 0.95, l: "95折" };
  if (t >= 500) return { r: 0.97, l: "97折" };
  return { r: 1, l: "" };
};

const ot = computed(() => props.items.reduce((s,i) => s + (s_item_price(i)) * i.quantity, 0));
const s_item_price = (i) => parseFloat(i.price || 0);
const di = computed(() => dc(ot.value));
const dl = computed(() => di.value.l);
const da = computed(() => ot.value * (1 - di.value.r));
const ft = computed(() => ot.value * di.value.r);

const toggleNew = () => {
  showNew.value = !showNew.value;
  if (showNew.value) {
    newReceiverName.value = "";
    newReceiverPhone.value = "";
    newAddrCodes.value = [];
    newDet.value = "";
  }
};
const cancelNew = () => {
  showNew.value = false;
  newAddrCodes.value = [];
  newReceiverName.value = "";
  newReceiverPhone.value = "";
  newDet.value = "";
};

const saveNewAddr = async () => {
  if (!newReceiverName.value || !newReceiverPhone.value) {
    ElMessage.warning("请填写必填项");
    return;
  }
  syncAreaToNew();
  // 拿到最终 province/city 名字
  const [pc, cc] = newAddrCodes.value || [];
  const province = (pcaa["86"] || {})[pc] || "";
  const city = pc ? ((pcaa[pc] || {})[cc] || "") : "";
  if (!province || !city) {
    ElMessage.warning("请选择省/市/区");
    return;
  }
  try {
    await request.post("/address/add", {
      receiverName: newReceiverName.value,
      receiverPhone: newReceiverPhone.value,
      province,
      city,
      detailAddress: newDet.value,
      isDefault: 0,
    });
    ElMessage.success("地址已新增");
    showNew.value = false;
    newAddrCodes.value = [];
    newReceiverName.value = "";
    newReceiverPhone.value = "";
    newDet.value = "";
    const r = await request.get("/address/list");
    addrs.value = r.data || [];
  } catch (e) {
    ElMessage.error("添加失败");
  }
};

const onSelAddr = () => {
  const a = addrs.value.find(x => String(x.id) === String(selAddr.value));
  if (a) {
    f.value.receiverName = a.receiverName || "";
    f.value.receiverPhone = a.receiverPhone || "";
  }
};

const submitOrder = async () => {
  if (!f.value.receiverName || !f.value.receiverPhone) {
    ElMessage.warning("请填写收货人信息");
    return;
  }
  // 如果选了已有地址，province/city/detailAddress 走 a；否则来自新增表单（或空）
  let province = "", city = "", detailAddress = "";
  if (selAddr.value) {
    const a = addrs.value.find(x => String(x.id) === String(selAddr.value));
    if (a) {
      province = a.province || "";
      city = a.city || "";
      detailAddress = a.detailAddress || "";
    }
  }
  sub.value = true;
  try {
    const body = {
      receiverName: f.value.receiverName,
      receiverPhone: f.value.receiverPhone,
      province,
      city,
      detailAddress,
      note: f.value.note
    };
    if (props.from === "buy") {
      body.productId = props.items[0]?.productId || props.items[0]?.id;
      body.quantity = props.items[0]?.quantity || 1;
    } else if (props.from === "cart") {
      body.cartIds = props.items.map(i => i.id);
    }
    const res = await request.post("/order/create", body);
    ElMessage.success("订单已创建");
    emit("confirmed", res.data);
  } catch (e) {
    ElMessage.error("创建失败");
  } finally {
    sub.value = false;
  }
};

watch(() => props.visible, async (v) => {
  if (!v) return;
  f.value = { receiverName: "", receiverPhone: "", note: "" };
  selAddr.value = "";
  showNew.value = false;
  newAddrCodes.value = [];
  newReceiverName.value = "";
  newReceiverPhone.value = "";
  newDet.value = "";
  try {
    const r = await request.get("/address/list");
    addrs.value = r.data || [];
  } catch (e) {
    addrs.value = [];
  }
});
</script>

<style scoped>
.oc-overlay { position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.35);backdrop-filter:blur(3px);z-index:200;display:flex;align-items:center;justify-content:center;opacity:0;pointer-events:none;transition:opacity .35s; }
.oc-overlay.active { opacity:1;pointer-events:auto; }
.oc-dialog { width:540px;max-width:92%;max-height:85vh;display:flex;flex-direction:column;background:#fff;border-radius:20px;border:1px solid rgba(180,215,205,.5);box-shadow:0 40px 70px -20px rgba(0,0,0,.3);transform:scale(.3);opacity:0;transition:all .4s cubic-bezier(.34,1.2,.64,1); }
.oc-overlay.active .oc-dialog { transform:scale(1);opacity:1; }
.oc-header { display:flex;align-items:center;justify-content:space-between;padding:18px 24px 10px;border-bottom:1px solid #e8f0ed;flex-shrink:0; }
.oc-title { font-size:18px;font-weight:700;background:linear-gradient(125deg,#3f877c,#cb829f);background-clip:text;-webkit-background-clip:text;color:transparent; }
.oc-close { background:rgba(200,200,200,.3);border:none;border-radius:50%;width:30px;height:30px;font-size:15px;cursor:pointer;color:#666;display:flex;align-items:center;justify-content:center;transition:.2s; }
.oc-close:hover { background:rgba(200,200,200,.5);color:#333; }
.oc-body { flex:1;overflow-y:auto;padding:16px 24px; }
.oc-section { margin-bottom:14px; }
.oc-section-title { font-size:14px;font-weight:600;color:#3f877c;margin-bottom:10px; }
.oc-row { display:flex;gap:10px;margin-bottom:8px;align-items:center; }
.oc-inp { height:38px;border-radius:10px;border:1px solid rgba(180,200,195,.5);padding:0 12px;font-size:13px;outline:none;transition:.2s;box-sizing:border-box;background:#f8faf9; width:100%;}
.oc-inp:focus { border-color:#88bfb3;background:#fff; }
.oc-btn-s { padding:7px 14px;border-radius:8px;border:1px solid rgba(180,200,195,.5);background:#f0f5f3;font-size:12px;cursor:pointer;color:#555;white-space:nowrap; }
.oc-new { background:#f7faf9;border-radius:10px;padding:12px;margin-top:8px;border:1px solid #e8f0ed; }
.items-sect { max-height:200px;overflow-y:auto; }
.oc-item { display:flex;align-items:center;gap:12px;padding:8px 0;border-bottom:1px solid #f0f5f3; }
.oc-item:last-child { border-bottom:none; }
.oc-img { width:48px;height:48px;border-radius:8px;object-fit:cover;background:#f0f0f0;flex-shrink:0; }
.oc-info { flex:1;min-width:0; }
.oc-nm { font-size:13px;font-weight:500; }
.oc-meta { font-size:12px;color:#999;margin-top:2px; }
.oc-sub { font-size:14px;font-weight:700;color:#e05a4f; }
.oc-pr { display:flex;justify-content:space-between;padding:4px 0;font-size:13px;color:#666; }
.oc-pr.oc-t { border-top:1px solid #e8f0ed;margin-top:4px;padding-top:10px;font-size:17px;font-weight:700;color:#e05a4f; }
.oc-footer { display:flex;justify-content:flex-end;gap:10px;padding:12px 24px 18px;border-top:1px solid #e8f0ed;flex-shrink:0; }
.oc-btn { padding:9px 24px;border-radius:56px;font-size:14px;font-weight:500;cursor:pointer;border:none;transition:.2s;font-family:inherit; }
.oc-btn-def { background:#fff2e8;border:1px solid #dac5b0;color:#8e6a52; }
.oc-btn-pri { background:linear-gradient(135deg,#ff7e5f,#ff4d88);color:#fff;font-weight:600; }
.oc-btn-pri:hover { box-shadow:0 4px 14px rgba(255,77,136,.3); }
.oc-btn-pri:disabled { opacity:.5;cursor:not-allowed; }
</style>