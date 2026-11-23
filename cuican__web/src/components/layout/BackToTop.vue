<template>
  <transition name="bt-fade">
    <div v-if="visible" class="back-to-top" @click="scrollTop" title="回到顶部">
      <span class="bt-icon">↑</span>
      <span class="bt-text">顶部</span>
    </div>
  </transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue"

const visible = ref(false)
const onScroll = () => {
  visible.value = window.scrollY > 300
}
const scrollTop = () => {
  window.scrollTo({ top: 0, behavior: "smooth" })
}
onMounted(() => {
  window.addEventListener("scroll", onScroll, { passive: true })
  onScroll()
})
onUnmounted(() => {
  window.removeEventListener("scroll", onScroll)
})
</script>

<style scoped>
.back-to-top {
  position: fixed;
  right: 24px;
  bottom: 24px;
  width: 54px;
  height: 54px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 9998;
  background: radial-gradient(circle at 30% 28%, #f5d36e, #e8b947 55%, #d4af37);
  color: #1a1408;
  border: 2px solid rgba(255, 255, 255, 0.4);
  box-shadow:
    0 10px 28px rgba(212, 175, 55, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
  user-select: none;
  /* 小球掉落动画：从顶部掉下 + 回弹 */
  animation: ballDrop 0.65s cubic-bezier(0.22, 1.2, 0.36, 1) both;
}
.back-to-top:hover {
  transform: scale(1.1);
  box-shadow:
    0 14px 34px rgba(212, 175, 55, 0.65),
    inset 0 1px 0 rgba(255, 255, 255, 0.7);
}
.bt-icon {
  font-size: 20px;
  font-weight: 900;
  line-height: 1;
}
.bt-text {
  font-size: 9px;
  letter-spacing: 2px;
  margin-top: 2px;
  font-weight: 600;
}
.bt-fade-enter-active,
.bt-fade-leave-active {
  transition: opacity 0.3s ease;
}
.bt-fade-enter-from,
.bt-fade-leave-to {
  opacity: 0;
}
@keyframes ballDrop {
  0% {
    opacity: 0;
    transform: translateY(-340px) scale(0.5);
  }
  55% {
    opacity: 1;
    transform: translateY(8px) scale(1.05);
  }
  75% {
    transform: translateY(-6px) scale(0.97);
  }
  88% {
    transform: translateY(3px) scale(1.01);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>
