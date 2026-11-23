<template>
  <div class="addr-picker">
    <el-select
      v-model="provinceCode"
      placeholder="选择省"
      clearable
      :disabled="disabled"
      class="addr-select"
      @change="onProvChange"
    >
      <el-option v-for="p in provinceOptions" :key="p.code" :label="p.name" :value="p.code" />
    </el-select>

    <el-select
      v-model="cityCode"
      placeholder="选择市"
      clearable
      :disabled="disabled || !provinceCode"
      class="addr-select"
      @change="onCityChange"
    >
      <el-option v-for="c in cityOptions" :key="c.code" :label="c.name" :value="c.code" />
    </el-select>

    <el-select
      v-model="districtCode"
      placeholder="选择区（选填）"
      clearable
      :disabled="disabled || !cityCode"
      class="addr-select"
    >
      <el-option v-for="d in districtOptions" :key="d.code" :label="d.name" :value="d.code" />
    </el-select>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { pcaa } from "area-data";

/**
 * pcaa 数据结构：
 *   pcaa["86"]      = { 110000: "北京市", 440000: "广东省", ... }   省份
 *   pcaa[110000]    = { 110100: "北京市" }                         直辖市只有一条同名
 *   pcaa[440000]    = { 440100: "广州市", 440300: "深圳市", ... }
 *   pcaa[110100]    = { 110101: "东城区", 110102: "西城区", ... }   区/县
 *   pcaa[440300]    = { 440303: "罗湖区", 440305: "南山区", ... }
 */
const props = defineProps({
  modelValue: { type: Array, default: () => [] }, // [pc, cc, dc] 形如 ['440000','440300','440305']
  disabled: { type: Boolean, default: false },
});

const emit = defineEmits(["update:modelValue", "change"]);

// 三个独立 ref，与 el-select v-model
const provinceCode = ref((props.modelValue || [])[0] || "");
const cityCode = ref((props.modelValue || [])[1] || "");
const districtCode = ref((props.modelValue || [])[2] || "");

// 父 → 子（编辑回显 modelValue 变化）
watch(
  () => props.modelValue,
  (v) => {
    const a = v || [];
    if (a[0] !== provinceCode.value) provinceCode.value = a[0] || "";
    if (a[1] !== cityCode.value) cityCode.value = a[1] || "";
    if (a[2] !== districtCode.value) districtCode.value = a[2] || "";
  }
);

// 省 选项
const provinceOptions = computed(() => entries("86"));
// 市 选项
const cityOptions = computed(() => (provinceCode.value ? entries(provinceCode.value) : []));
// 区/县 选项
const districtOptions = computed(() => (cityCode.value ? entries(cityCode.value) : []));

function entries(code) {
  const map = pcaa[code] || {};
  return Object.entries(map).map(([c, n]) => ({ code: c, name: n }));
}

// 当任一 ref 变 → 抛到父
watch([provinceCode, cityCode, districtCode], ([pc, cc, dc]) => {
  const out = [pc, cc, dc];
  emit("update:modelValue", out);
  emit("change", { codes: out, names: namesOf(out) });
});

function onProvChange() {
  // 切换省时清掉市/区
  cityCode.value = "";
  districtCode.value = "";
}
function onCityChange() {
  districtCode.value = "";
}

function namesOf(codes) {
  const [pc, cc, dc] = codes;
  const provinceMap = pcaa["86"] || {};
  const cityMap = pc ? (pcaa[pc] || {}) : {};
  const districtMap = cc ? (pcaa[cc] || {}) : {};
  return {
    province: provinceMap[pc] || "",
    city: cityMap[cc] || "",
    district: districtMap[dc] || "",
  };
}
</script>

<style scoped>
.addr-picker {
  display: flex;
  gap: 12px;
  width: 100%;
}
.addr-select {
  flex: 1;
  min-width: 0;
}
</style>