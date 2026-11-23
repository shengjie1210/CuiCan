<template>
  <div class="cp-wrap" :style="wrapStyle">
    <div class="cp-head" :style="headStyle" @click="$emit('update:collapsed', !collapsed)">
      <i v-if="!collapsed" :class="headerIcon || 'bi bi-list'" class="cp-icon"></i>
      <span v-if="!collapsed" class="cp-title">{{ title }}</span>
      <i :class="collapsed ? 'bi bi-chevron-right' : 'bi bi-chevron-left'" class="cp-arrow"></i>
    </div>
    <div v-if="!collapsed" class="cp-slot"><slot /></div>
  </div>
</template>
<script setup>
import { computed } from "vue"
const props = defineProps({
  collapsed: { type: Boolean, default: false },
  title: { type: String, default: "" },
  headerIcon: { type: String, default: "" },
  width: { type: Number, default: 220 },
  collapsedWidth: { type: Number, default: 56 }
})
defineEmits(["update:collapsed"])
const wrapStyle = computed(function() {
  var w = props.collapsed ? props.collapsedWidth : props.width
  return { width: w + "px", minWidth: w + "px" }
})
const headStyle = computed(function() {
  return {
    cursor: "pointer", display: "flex", alignItems: "center", gap: "8px",
    padding: props.collapsed ? "8px 0" : "8px 16px",
    justifyContent: props.collapsed ? "center" : "flex-start",
    borderBottom: "1px solid #e8e8e8",
    whiteSpace: "nowrap", height: "40px",
    transition: "padding .25s ease"
  }
})
</script>
<style scoped>
.cp-wrap { background: #fafafa; border-radius: 8px; border: 1px solid #e8e8e8; overflow: hidden; transition: width .25s ease, min-width .25s ease; }
.cp-head:hover { background: rgba(0,0,0,.03); }
.cp-icon { font-size: 14px; color: var(--theme-primary, #e6a23c); flex-shrink: 0; }
.cp-title { font-size: 13px; font-weight: 700; color: #444; white-space: nowrap; }
.cp-arrow { font-size: 12px; color: #999; margin-left: auto; flex-shrink: 0; }
.cp-slot { overflow: hidden; }
</style>
