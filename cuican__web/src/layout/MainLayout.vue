<template>
  <div class="layout bg-dark">
    <BlackGoldBackground />
    <Header />
    <div class="main">
      <router-view />
    </div>
    <Footer />
    <FloatingBack />
    <BackToTop />
  </div>
</template>

<script setup>
  import Header from "@/components/layout/Header.vue";
  import Footer from "@/components/layout/Footer.vue";
  import FloatingBack from "@/components/layout/FloatingBack.vue";
  import BackToTop from "@/components/layout/BackToTop.vue";
  import BlackGoldBackground from "@/components/background/BlackGoldBackground.vue";
</script>

<style scoped>
  .layout {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    width: 100%;
    position: relative;
    z-index: 0;
  }
  .main {
    flex: 1;
    position: relative;
    z-index: 2;
  }
</style>

