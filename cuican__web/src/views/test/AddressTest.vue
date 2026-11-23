<template>
  <div class="test-page">
    <div class="card">
      <h1 class="title">🧪 三级省市区联动测试</h1>
      <p class="desc">
        这是一个<strong>完全隔离的干净测试页</strong>，跟 AddressPage / OrderConfirm 没有任何耦合。
        在这里验证通过后，再考虑接回去。
      </p>

      <div class="section">
        <div class="label">① 默认值测试（绑定 [[&apos;440000&apos;,&apos;440300&apos;,&apos;440305&apos;]]）</div>
        <AddressPicker v-model="codesA" @change="(p) => (logA = p)" />
        <div class="result">
          <div>codes: <code>{{ JSON.stringify(codesA) }}</code></div>
          <div>names: <code>{{ JSON.stringify(logA.names) }}</code></div>
        </div>
      </div>

      <div class="section">
        <div class="label">② 空值测试</div>
        <AddressPicker v-model="codesB" @change="(p) => (logB = p)" />
        <div class="result">
          <div>codes: <code>{{ JSON.stringify(codesB) }}</code></div>
          <div>names: <code>{{ JSON.stringify(logB.names) }}</code></div>
        </div>
      </div>

      <div class="section">
        <div class="label">③ 切换省时市/区要清空</div>
        <AddressPicker v-model="codesC" @change="(p) => (logC = p)" />
        <div class="result">
          <div>codes: <code>{{ JSON.stringify(codesC) }}</code></div>
          <div>names: <code>{{ JSON.stringify(logC.names) }}</code></div>
        </div>
        <div class="hint">
          ✅ 操作步骤：先选一个省 → 选一个市 → 选一个区 → 再换一个省。
          市和区应当自动清空。
        </div>
      </div>

      <div class="section">
        <div class="label">④ 整体组装成"地址字符串"</div>
        <AddressPicker v-model="codesD" @change="(p) => (logD = p)" />
        <div class="result">
          <div>codes: <code>{{ JSON.stringify(codesD) }}</code></div>
          <div>组装结果（按你提的"区拼到 detailAddress 前面"）：</div>
          <pre>{{ composedDetail }}</pre>
        </div>
      </div>

      <div class="note">
        <strong>说明</strong>：
        <ul>
          <li>数据源来自 <code>area-data/pcaa</code>（与 vue-area-linkage 同源），不依赖 vue-area-linkage</li>
          <li>底层用 Element Plus 的 el-select（项目里已装），Vue 3 原生</li>
          <li>v-model 绑定 codes 数组；@change 同时给出 names 对象</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import AddressPicker from "@/components/common/AddressPicker.vue";

// ① 默认值：广东 / 深圳 / 南山
const codesA = ref(["440000", "440300", "440305"]);
const logA = ref({ codes: [], names: {} });

// ② 空值
const codesB = ref([]);
const logB = ref({ codes: [], names: {} });

// ③ 切换省
const codesC = ref([]);
const logC = ref({ codes: [], names: {} });

// ④ 组装
const codesD = ref([]);
const logD = ref({ codes: [], names: {} });

const composedDetail = computed(() => {
  const { province, city, district } = logD.value.names || {};
  const { codes } = logD.value;
  return [
    province || "(未选省)",
    city || "(未选市)",
    district ? district + " xxx街道xxx号" : "(未选区) xxx街道xxx号",
    "\n// codes:", codes,
  ].join(" ");
});
</script>

<style scoped>
.test-page {
  min-height: 100vh;
  padding: 30px 16px;
  background: linear-gradient(135deg, #f0f7f5, #fff5f7);
}
.card {
  max-width: 720px;
  margin: 0 auto;
  background: #fff;
  border-radius: 16px;
  padding: 30px 36px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}
.title {
  font-size: 22px;
  margin-bottom: 8px;
  color: #3f877c;
}
.desc {
  color: #666;
  font-size: 14px;
  margin-bottom: 24px;
  line-height: 1.6;
}
.section {
  margin-bottom: 22px;
  padding-bottom: 18px;
  border-bottom: 1px dashed #eee;
}
.section:last-of-type {
  border-bottom: none;
}
.label {
  font-weight: 600;
  margin-bottom: 8px;
  color: #444;
}
.result {
  margin-top: 10px;
  font-size: 13px;
  color: #555;
  background: #f7faf9;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #e8f0ed;
}
.result code,
.result pre {
  background: #fff;
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid #eee;
  font-family: Menlo, Consolas, monospace;
  font-size: 12px;
}
.hint {
  margin-top: 8px;
  padding: 8px 12px;
  background: #fff8e6;
  border-left: 3px solid #f5a623;
  border-radius: 4px;
  font-size: 13px;
  color: #7a5b1f;
}
.note {
  margin-top: 18px;
  padding: 12px 16px;
  background: #eef7ff;
  border-radius: 10px;
  font-size: 13px;
  color: #345;
}
.note ul {
  margin: 6px 0 0 18px;
  padding: 0;
}
.note li {
  margin: 2px 0;
}
</style>