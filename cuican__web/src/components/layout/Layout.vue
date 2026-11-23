<template>
  <div class="layout">
    <!-- 头部 -->
    <Header />

    <!-- 页面内容 -->
    <div class="main">
      <router-view />
    </div>

    <!-- 底部 -->
    <Footer />
  </div>
</template>

<script setup>
  import Header from "@/components/layout/Header.vue";
  import Footer from "@/components/layout/Footer.vue";
</script>

<style scoped>
  .layout {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }

  .main {
  flex: 1;

  /* ⭐ 关键：创建 BFC */
  overflow: hidden;
}
</style>
