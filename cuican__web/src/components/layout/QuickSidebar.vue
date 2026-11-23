<template>
  <aside class="quick-sidebar" :class="{ expanded }">
    <button
      class="toggle-btn"
      :title="expanded ? '收起侧边栏' : '展开侧边栏'"
      @click="expanded = !expanded">
      <i
        :class="
          expanded ? 'bi bi-chevron-double-right' : 'bi bi-grid-3x3-gap-fill'
        "></i>
    </button>

    <nav class="nav-list">
      <button
        v-for="item in items"
        :key="item.key"
        class="nav-item"
        :class="{ active: isActive(item) }"
        @click="onClick(item)">
        <span
          class="item-icon"
          :style="{ color: item.color, background: item.color + '22' }">
          <i :class="item.icon"></i>
        </span>
        <span v-if="expanded" class="item-label">{{ item.label }}</span>
      </button>
    </nav>

    <transition name="fade">
      <div v-if="serviceOpen" class="service-panel">
        <div class="service-head">璀璨客服</div>
        <p class="service-line">
          <i class="bi bi-telephone-fill"></i><span>400-888-9999</span>
        </p>
        <p class="service-line">
          <i class="bi bi-clock-fill"></i><span>工作日 9:00-18:00</span>
        </p>
        <button class="service-btn" @click="startChat">
          <i class="bi bi-chat-dots-fill"></i>在线客服
        </button>
      </div>
    </transition>
  </aside>
  <AiChatDrawer v-if="chatOpen" @close="chatOpen = false" />
  <AiRecommendDrawer v-if="recommendOpen" @close="recommendOpen = false" />
</template>

<script setup>
  import { ref, computed, watch } from "vue";
  import { useRoute, useRouter } from "vue-router";
  import { ElMessage } from "element-plus";
  import AiChatDrawer from "@/components/chat/AiChatDrawer.vue";
  import AiRecommendDrawer from "@/components/chat/AiRecommendDrawer.vue";

  const route = useRoute();
  const router = useRouter();

  const expanded = ref(false);
  const serviceOpen = ref(false);
  const chatOpen = ref(false);
  const recommendOpen = ref(false);

  watch(expanded, (v) => {
    if (!v) serviceOpen.value = false;
  });

  const items = [
    {
      key: "home",
      label: "主页",
      icon: "bi bi-person-fill",
      color: "#409eff",
      path: "/user",
    },
    {
      key: "cart",
      label: "购物车",
      icon: "bi bi-cart-fill",
      color: "#ff7e5f",
      path: "/user/cart",
    },
    {
      key: "order",
      label: "订单",
      icon: "bi bi-receipt",
      color: "#67c23a",
      path: "/user/order",
    },
    {
      key: "favorite",
      label: "收藏",
      icon: "bi bi-heart-fill",
      color: "#f56c6c",
      path: "/user/favorite",
    },
    {
      key: "homepage",
      label: "回到首页",
      icon: "bi bi-house-door-fill",
      color: "#d4af37",
      path: "/",
    },
    {
      key: "recommend",
      label: "智能推荐",
      icon: "bi bi-stars",
      color: "#f0c75e",
      action: "recommend",
    },
    {
      key: "service",
      label: "客服",
      icon: "bi bi-headset",
      color: "#e6a23c",
      action: "service",
    },
  ];

  const isActive = (item) => {
    if (!item.path) return false;
    return route.path === item.path;
  };

  const onClick = (item) => {
    if (item.action === "service") {
      chatOpen.value = true;
      return;
    }
    if (item.action === "recommend") {
      recommendOpen.value = true;
      return;
    }
    serviceOpen.value = false;
    router.push(item.path);
  };

  function startChat() {
    ElMessage.info("客服小助手正在接入，请稍候...");
  }
</script>

<style scoped>
  .quick-sidebar {
    position: fixed;
    right: 24px;
    top: 50%;
    transform: translateY(-50%);
    z-index: 5000;
    width: 66px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    padding: 14px 10px;
    border-radius: 22px;
    background: rgba(255, 255, 255, 0.115);
    border: 1px solid rgba(255, 255, 255, 0.296);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    box-shadow:
      0 10px 30px rgba(0, 0, 0, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
    transition: width 0.3s ease;
  }

  .quick-sidebar.expanded {
    width: 210px;
    align-items: stretch;
  }

  .toggle-btn {
    width: 44px;
    height: 44px;
    margin: 0 auto 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;
    border-radius: 14px;
    background: rgba(255, 255, 255, 0.35);
    color: #111;
    font-size: 18px;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .toggle-btn:hover {
    background: rgba(255, 255, 255, 0.55);
    transform: scale(1.05);
  }

  .nav-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    width: 100%;
  }

  .nav-item {
    display: flex;
    align-items: center;
    gap: 12px;
    width: 100%;
    padding: 10px 8px;
    border: none;
    border-radius: 14px;
    background: transparent;
    color: #111;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
    color: white;
  }

  .quick-sidebar:not(.expanded) .nav-item {
    justify-content: center;
    padding: 10px 0;
  }

  .nav-item:hover {
    background: rgba(255, 255, 255, 0.4);
    transform: translateX(-2px);
  }

  .nav-item.active {
    background: rgba(255, 255, 255, 0.5);
    box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.6);
  }

  .item-icon {
    width: 40px;
    height: 40px;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    font-size: 18px;
  }

  .item-label {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .service-panel {
    width: 100%;
    padding: 14px;
    border-radius: 14px;
    background: rgba(255, 255, 255, 0.42);
    border: 1px solid rgba(255, 255, 255, 0.55);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .service-head {
    font-size: 15px;
    font-weight: 700;
    color: #111;
  }

  .service-line {
    margin: 0;
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: #333;
  }

  .service-line i {
    color: #e6a23c;
  }

  .service-btn {
    margin-top: 6px;
    padding: 10px 0;
    border: none;
    border-radius: 999px;
    background: linear-gradient(135deg, #e6a23c, #f56c6c);
    color: #fff;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    transition: transform 0.2s ease;
  }

  .service-btn:hover {
    transform: translateY(-1px);
  }

  .fade-enter-active,
  .fade-leave-active {
    transition:
      opacity 0.2s ease,
      transform 0.2s ease;
  }

  .fade-enter-from,
  .fade-leave-to {
    opacity: 0;
    transform: translateY(-4px);
  }
</style>
