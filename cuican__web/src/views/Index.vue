<script setup>
  import Banner from "@/components/home/Banner.vue";
  import HomeCarousel from "@/components/home/HomeCarousel.vue";
  import ManifestoSection from "@/components/home/ManifestoSection.vue";
  import BrandPromise from "@/components/home/BrandPromise.vue";
  import BrandStats from "@/components/home/BrandStats.vue";
  import CategoryShowcase from "@/components/home/CategoryShowcase.vue";
  import MarqueeTestimonials from "@/components/home/MarqueeTestimonials.vue";
  import HotProductsCarousel from "@/components/home/HotProductsCarousel.vue";
  import AiRecommendation from "@/components/home/AiRecommendation.vue";
  import ServicesSection from "@/components/home/ServicesSection.vue";
  import AboutSection from "@/components/home/AboutSection.vue";
  import BlogReviews from "@/components/home/BlogReviews.vue";
  import GoldMeteor from "@/components/background/GoldMeteor.vue";
</script>

<template>
  <div class="layout">
    <GoldMeteor />
    <main class="main shell">
      <Banner />
      <BrandPromise />
      <BrandStats />
      <HomeCarousel />
      <ManifestoSection />
      <CategoryShowcase />
      <MarqueeTestimonials />
      <HotProductsCarousel />
      <AiRecommendation />
      <ServicesSection />
      <AboutSection />
      <BlogReviews />
    </main>
  </div>
</template>

<style scoped>
.layout {
  position: relative;
  background: radial-gradient(ellipse at top, rgba(212, 175, 55, 0.08), transparent 42%), #050505;
  color: #f6e7b3;
}
.main {
  position: relative;
  z-index: 1;
}
.shell {
  width: min(100%, 100vw);
  overflow-x: hidden;
  padding-bottom: 40px;
}
.shell > * {
  width: 100%;
  max-width: 100%;
}
.shell > * + * {
  margin-top: 20px;
}
.shell > *:nth-child(2),
.shell > *:nth-child(4) {
  margin-top: 12px;
}
.shell > *:first-child {
  margin-top: 0;
}
</style>
