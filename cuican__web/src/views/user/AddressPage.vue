<template>
  <div class="page bg-dark">
    <BlackGoldBackground />
    <div class="inner">
      <div class="back-bar" @click="$router.back()">← 返回</div>
      <div class="title">
        <h2 class="title-text gold-text-strong">📍 我的收货地址</h2>
        <label class="skip-confirm-label">
          <input type="checkbox" v-model="skipConfirm" />
          <span>无需确认，直接删除</span>
        </label>
      </div>
      <div class="addr-toolbar">
        <button class="add-btn" @click="openAdd">+ 新增地址</button>
      </div>
      <div v-if="list.length === 0" class="empty">
        <div class="empty-icon">📍</div>
        <p class="empty-text">暂无地址，请添加一个吧</p>
      </div>
      <div class="addr-grid" v-else>
        <div class="addr-card" v-for="item in list" :key="item.id">
          <div class="card-line"></div>
          <div class="addr-head">
            <div class="name">{{ item.receiverName }} <span class="phone">{{ item.receiverPhone }}</span></div>
            <span class="tag" v-if="item.isDefault">默认</span>
          </div>
          <div class="addr">{{ item.province }} {{ item.city }} {{ item.detailAddress }}</div>
          <div class="addr-actions">
            <button class="addr-a-btn gold-line" @click.stop="openEdit(item)">编辑</button>
            <button class="addr-a-btn danger-line" @click.stop="remove(item.id)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 地址弹窗 -->
    <div class="addr-overlay" :class="{ active: showModal }" @click.self="showModal = false">
      <div class="addr-dialog">
        <div class="addr-dialog-header">
          <span class="addr-dialog-title gold-text-strong">{{ editingId ? "编辑地址" : "新增地址" }}</span>
          <button class="addr-dialog-close" @click="showModal = false">×</button>
        </div>
        <div class="addr-dialog-body">
          <div class="addr-form-row">
            <div class="addr-form-group half">
              <label>收货人</label>
              <input v-model="form.receiverName" placeholder="收货人姓名" />
            </div>
            <div class="addr-form-group half">
              <label>手机号</label>
              <input v-model="form.receiverPhone" placeholder="手机号" />
            </div>
          </div>
          <div class="addr-form-group">
            <label>省 / 市 / 区</label>
            <AddressPicker v-model="formCodes" />
          </div>
          <div class="addr-form-group">
            <label>详细地址</label>
            <input v-model="form.detailAddress" placeholder="街道、门牌号等" />
          </div>
          <div class="addr-form-group checkbox-group">
            <label class="addr-checkbox-label">
              <input type="checkbox" v-model="form.isDefault" true-value="1" false-value="0" />
              <span>设为默认地址</span>
            </label>
          </div>
        </div>
        <div class="addr-dialog-footer">
          <button class="addr-btn addr-btn-default" @click="showModal = false">取消</button>
          <button class="addr-btn addr-btn-primary" @click="saveAddress">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";
import AddressPicker from "@/components/common/AddressPicker.vue";
import { pcaa } from "area-data";
import { namesToCodes } from "@/utils/area";
import BlackGoldBackground from "@/components/background/BlackGoldBackground.vue";

const list = ref([]);
const showModal = ref(false);
const editingId = ref(null);
const skipConfirm = ref(localStorage.getItem("skipAddrConfirm") === "true");
const formCodes = ref([]);
const form = ref({ receiverName: "", receiverPhone: "", province: "", city: "", detailAddress: "", isDefault: "0" });

const syncAreaToForm = () => {
  const codes = formCodes.value || [];
  const [pc, cc, dc] = codes;
  const provinceMap = pcaa["86"] || {};
  const cityMap = pc ? (pcaa[pc] || {}) : {};
  const districtMap = cc ? (pcaa[cc] || {}) : {};
  form.value.province = provinceMap[pc] || "";
  form.value.city = cityMap[cc] || "";
  const districtName = districtMap[dc] || "";
  const cleanDetail = (form.value.detailAddress || "").replace(/^\S+\s+/, "");
  form.value.detailAddress = districtName ? districtName + " " + cleanDetail : cleanDetail;
};
watch(formCodes, () => syncAreaToForm(), { deep: true });
watch(skipConfirm, (v) => localStorage.setItem("skipAddrConfirm", String(v)));

const load = async () => {
  const res = await request.get("/address/list");
  list.value = res.data || [];
};

const openAdd = () => {
  editingId.value = null;
  form.value = { receiverName: "", receiverPhone: "", province: "", city: "", detailAddress: "", isDefault: "0" };
  formCodes.value = [];
  showModal.value = true;
};

const openEdit = (addr) => {
  editingId.value = addr.id;
  let districtName = "";
  let detailOnly = String(addr.detailAddress || "");
  const raw = detailOnly;
  if (addr.city) {
    for (const key of Object.keys(pcaa)) {
      if (key.length === 6 && key.startsWith(addr.province.slice(0, 2))) {
        const name = pcaa[key];
        if (name && raw.startsWith(name)) {
          districtName = name;
          const hit = name;
          const refinedCodes = namesToCodes(addr.province, addr.city, hit);
          formCodes.value = refinedCodes;
          form.value.detailAddress = raw.slice(hit.length).replace(/^\s+/, "");
        }
      }
    }
  }
  form.value = {
    receiverName: addr.receiverName || "",
    receiverPhone: addr.receiverPhone || "",
    province: addr.province || "",
    city: addr.city || "",
    detailAddress: form.value.detailAddress || detailOnly,
    isDefault: addr.isDefault ? "1" : "0",
  };
  showModal.value = true;
};

const saveAddress = async () => {
  if (!form.value.receiverName || !form.value.receiverPhone) {
    ElMessage.warning("请填写必填项");
    return;
  }
  try {
    syncAreaToForm();
    const data = { ...form.value };
    data.isDefault = parseInt(data.isDefault);
    if (editingId.value) {
      data.id = editingId.value;
      await request.post("/address/update", data);
    } else {
      await request.post("/address/add", data);
    }
    showModal.value = false;
    load();
  } catch (e) {
    ElMessage.error("保存失败");
  }
};

const remove = async (id) => {
  const doDel = async () => {
    await request.post("/address/delete?id=" + id);
    load();
  };
  if (skipConfirm.value) {
    await doDel();
  } else {
    try {
      await ElMessageBox.confirm("确定要删除这个地址吗？", "确认删除");
      await doDel();
    } catch (e) {}
  }
};

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
    max-width: 1180px;
    margin: 0 auto;
    padding: 0 32px;
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
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    margin-bottom: 28px;
    flex-wrap: wrap;
    gap: 16px;
  }
  .title-text {
    margin: 0;
    font-size: 32px;
    letter-spacing: 4px;
    font-weight: 800;
    font-family: var(--heading-font);
  }

  .skip-confirm-label {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: var(--gold-100, #f5d36e);
    cursor: pointer;
    user-select: none;
    padding: 8px 14px;
    border-radius: 999px;
    background: rgba(212, 175, 55, 0.08);
    border: 1px solid rgba(212, 175, 55, 0.25);
    transition: all 0.25s ease;
  }
  .skip-confirm-label:hover {
    background: rgba(212, 175, 55, 0.15);
    border-color: var(--gold-300, #e8b947);
  }
  .skip-confirm-label input {
    width: 14px;
    height: 14px;
    margin: 0;
    cursor: pointer;
    accent-color: var(--gold-300, #e8b947);
  }

  .addr-toolbar {
    margin-bottom: 20px;
  }

  .add-btn {
    padding: 10px 28px;
    border-radius: 56px;
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37) 50%, var(--gold-700, #8a7128));
    color: #1a1408;
    border: 1px solid var(--gold-300, #e8b947);
    font-weight: 700;
    letter-spacing: 2px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow:
      0 8px 22px rgba(212, 175, 55, 0.35),
      inset 0 1px 0 rgba(255, 249, 230, 0.4);
  }
  .add-btn:hover {
    transform: translateY(-2px);
    box-shadow:
      0 14px 36px rgba(212, 175, 55, 0.55),
      0 0 20px rgba(240, 214, 138, 0.5);
  }

  .empty {
    text-align: center;
    padding: 80px 0;
  }
  .empty-icon {
    font-size: 64px;
    margin-bottom: 16px;
    filter: drop-shadow(0 0 12px rgba(212, 175, 55, 0.3));
  }
  .empty-text {
    color: #c4bca0;
    font-size: 16px;
  }

  /* ===== 地址卡 ===== */
  .addr-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 20px;
  }

  .addr-card {
    position: relative;
    padding: 22px 24px 18px;
    border-radius: 16px;
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
  .addr-card:hover {
    border-color: rgba(240, 214, 138, 0.5);
    transform: translateY(-4px);
    box-shadow:
      0 22px 60px rgba(0, 0, 0, 0.55),
      0 0 28px rgba(212, 175, 55, 0.22),
      inset 0 1px 0 rgba(240, 214, 138, 0.32);
  }

  .card-line {
    position: absolute;
    top: 8px;
    left: 24px;
    right: 24px;
    height: 1px;
    background: linear-gradient(90deg, transparent, var(--gold-300, #e8b947) 50%, transparent);
    opacity: 0.5;
  }

  .addr-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
  }
  .name {
    font-weight: 700;
    color: #efe9d6;
    font-size: 16px;
  }
  .phone {
    font-weight: 400;
    color: #8e8770;
    font-size: 13px;
    margin-left: 4px;
  }
  .tag {
    display: inline-block;
    padding: 3px 10px;
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
    color: #1a1408;
    border-radius: 999px;
    font-size: 11px;
    font-weight: 700;
    letter-spacing: 1px;
  }

  .addr {
    margin-bottom: 16px;
    color: #c4bca0;
    font-size: 14px;
    line-height: 1.6;
  }

  .addr-actions {
    display: flex;
    gap: 10px;
    border-top: 1px dashed rgba(212, 175, 55, 0.15);
    padding-top: 14px;
  }

  .addr-a-btn {
    padding: 6px 16px;
    border-radius: 999px;
    font-size: 13px;
    cursor: pointer;
    transition: all 0.25s ease;
    background: transparent;
  }
  .gold-line {
    color: var(--gold-100, #f5d36e);
    border: 1px solid rgba(212, 175, 55, 0.35);
  }
  .gold-line:hover {
    background: rgba(212, 175, 55, 0.15);
    border-color: var(--gold-300, #e8b947);
  }
  .danger-line {
    color: #ff7e7e;
    border: 1px solid rgba(255, 126, 126, 0.35);
  }
  .danger-line:hover {
    background: rgba(255, 126, 126, 0.12);
    border-color: #ff7e7e;
  }

  /* ===== 地址弹窗 ===== */
  .addr-overlay {
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
  .addr-overlay.active {
    opacity: 1;
    pointer-events: auto;
  }
  .addr-dialog {
    width: 480px;
    max-width: 92%;
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
  .addr-overlay.active .addr-dialog {
    transform: scale(1) translateY(0);
    opacity: 1;
  }

  .addr-dialog-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px 28px 12px;
    border-bottom: 1px solid rgba(212, 175, 55, 0.2);
  }
  .addr-dialog-title {
    font-size: 20px;
    font-weight: 800;
    flex: 1;
  }
  .addr-dialog-close {
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
  .addr-dialog-close:hover {
    background: rgba(212, 175, 55, 0.3);
    color: var(--gold-50, #fff9e6);
  }

  .addr-dialog-body { padding: 20px 28px; }

  .addr-form-row { display: flex; gap: 14px; margin-bottom: 0; }
  .addr-form-group { margin-bottom: 14px; }
  .addr-form-group.half { flex: 1; min-width: 0; }
  .addr-form-group label {
    display: block;
    font-size: 13px;
    font-weight: 600;
    color: var(--gold-100, #f5d36e);
    margin-bottom: 8px;
    letter-spacing: 1px;
  }
  .addr-form-group input {
    width: 100%;
    height: 44px;
    padding: 0 14px;
    border-radius: 10px;
    border: 1px solid rgba(212, 175, 55, 0.25);
    background: rgba(20, 20, 20, 0.5);
    color: #efe9d6;
    outline: none;
    font-size: 14px;
    transition: all 0.25s ease;
    box-sizing: border-box;
  }
  .addr-form-group input::placeholder { color: rgba(239, 233, 214, 0.4); }
  .addr-form-group input:focus {
    border-color: var(--gold-300, #e8b947);
    box-shadow: 0 0 14px rgba(212, 175, 55, 0.25);
    background: rgba(20, 20, 20, 0.7);
  }

  .checkbox-group { margin-bottom: 0; }
  .addr-checkbox-label {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    font-size: 14px;
    color: var(--gold-100, #f5d36e);
  }
  .addr-checkbox-label input {
    width: 16px;
    height: 16px;
    margin: 0;
    cursor: pointer;
    accent-color: var(--gold-300, #e8b947);
  }

  .addr-dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding: 14px 28px 20px;
    border-top: 1px solid rgba(212, 175, 55, 0.2);
  }

  .addr-btn {
    border-radius: 56px;
    padding: 10px 28px;
    font-weight: 600;
    font-size: 14px;
    cursor: pointer;
    border: none;
    transition: all 0.25s ease;
    font-family: inherit;
    letter-spacing: 1px;
  }
  .addr-btn-default {
    background: transparent;
    color: var(--gold-100, #f5d36e);
    border: 1px solid rgba(212, 175, 55, 0.3);
  }
  .addr-btn-default:hover {
    background: rgba(212, 175, 55, 0.12);
    border-color: var(--gold-300, #e8b947);
  }
  .addr-btn-primary {
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
    color: #1a1408;
    border: 1px solid var(--gold-300, #e8b947);
    box-shadow: 0 6px 18px rgba(212, 175, 55, 0.35);
  }
  .addr-btn-primary:hover {
    box-shadow: 0 10px 28px rgba(212, 175, 55, 0.55);
  }
</style>
