<template>
  <section class="container-fluid home-carousel" id="billboard">
    <div class="swiper main-swiper">
      <div class="swiper-wrapper">
        <div
          v-for="(slide, index) in slides"
          :key="index"
          class="swiper-slide"
        >
          <img :src="slide.image" class="slide-img" alt="璀璨珠宝展示" />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
  import { onMounted, onUnmounted } from "vue";
  import Swiper from "swiper";
  import { EffectFade, Autoplay } from "swiper/modules";
  import "swiper/css";
  import "swiper/css/effect-fade";

  const slides = [
    { image: new URL("@/assets/image/首页/shouye1.png", import.meta.url).href },
    { image: new URL("@/assets/image/首页/shouye2.png", import.meta.url).href },
  ];
  let swiperInstance = null;

  onMounted(() => {
    swiperInstance = new Swiper(".main-swiper", {
      modules: [EffectFade, Autoplay],
      effect: "fade",
      fadeEffect: {
        crossFade: true,
      },
      loop: true,
      autoplay: {
        delay: 4000,
        disableOnInteraction: false,
      },
    });
  });

  onUnmounted(() => {
    if (swiperInstance) swiperInstance.destroy(true, true);
  });
</script>

<style scoped>
.home-carousel {
  padding: 0 clamp(12px, 2.5vw, 32px);
}
.main-swiper {
  width: 100%;
  height: clamp(280px, 58vw, 660px);
  overflow: hidden;
  border: 1px solid rgba(212, 175, 55, 0.24);
  background: #080806;
  box-shadow: 0 18px 48px rgba(0, 0, 0, 0.34);
}
.swiper-slide {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: transparent !important;
}
.slide-img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  background: transparent;
}
@media (max-width: 992px) {
  .home-carousel {
    padding: 0 12px;
  }
  .main-swiper {
    height: clamp(220px, 66vw, 420px);
  }
  .slide-img {
    object-fit: contain;
  }
}
</style>
