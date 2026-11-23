<template>
  <div class="modal-overlay" v-if="show" @click.self="handleClose">
    <div class="modal-box" :style="boxStyle">
      <!-- 标题栏 -->
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h6 class="mb-0">{{ title }}</h6>
        <button class="btn-close" @click="handleClose">×</button>
      </div>
      <!-- 内容区 -->
      <slot />
      <!-- 底部按钮 -->
      <div class="d-flex gap-2 justify-content-end mt-3" v-if="showFooter">
        <button class="btn btn-secondary" @click="handleClose">取消</button>
        <button class="btn btn-primary" @click="$emit('confirm')" :disabled="confirmLoading">
          {{ confirmLoading ? '处理中...' : confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue"

const props = defineProps({
  show: { type: Boolean, default: false },
  title: { type: String, default: "提示" },
  width: { type: String, default: "480px" },
  showFooter: { type: Boolean, default: true },
  confirmText: { type: String, default: "保存" },
  confirmLoading: { type: Boolean, default: false },
  closable: { type: Boolean, default: true }
})

const emit = defineEmits(["update:show", "confirm", "close"])

const boxStyle = computed(() => ({
  width: props.width,
  maxWidth: "90vw"
}))

function handleClose() {
  if (props.closable) {
    emit("update:show", false)
    emit("close")
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,.35);
  backdrop-filter: blur(3px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
}
.modal-box {
  background: #fff;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 24px 60px rgba(0,0,0,.2);
  max-height: 80vh;
  overflow-y: auto;
}
.btn-close {
  background: none;
  border: none;
  font-size: 22px;
  color: #999;
  cursor: pointer;
}
.btn-close:hover { color: #333; }
</style>
