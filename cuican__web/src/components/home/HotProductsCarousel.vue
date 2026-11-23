<template>
  <section class="hp-section">
    <div class="hp-bg"></div>
    <div class="hp-inner">
      <div class="hp-head reveal">
        <span class="hp-tag">— HOT COLLECTIONS —</span>
        <h2 class="hp-title">热门臻选</h2>
        <p class="hp-sub">本月最受青睐的璀璨单品，每一件都值得被收藏</p>
        <div class="hp-nav">
          <button class="hp-arrow product-carousel-prev" aria-label="上一页">←</button>
          <a href="/goods" class="hp-more">去购物 <span class="hp-more-arrow">→</span></a>
          <button class="hp-arrow product-carousel-next" aria-label="下一页">→</button>
        </div>
      </div>
      <div class="swiper product-swiper">
        <div class="swiper-wrapper">
          <div
            class="swiper-slide hp-card-wrap reveal"
            v-for="product in products"
            :key="product.id"
            :style="{ transitionDelay: '0.05s' }">
            <a class="hp-card" :href="product.link">
              <span class="hp-badge">臻选</span>
              <div class="hp-img-wrap">
                <img :src="product.image" :alt="product.name" class="hp-img" />
                <span class="hp-shine"></span>
              </div>
              <div class="hp-info">
                <h5 class="hp-name">{{ product.name }}</h5>
                <div class="hp-price gold-text-strong">{{ product.price }}</div>
              </div>
            </a>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
  import { onMounted, onUnmounted } from "vue";
  import Swiper from "swiper";
  import { Navigation } from "swiper/modules";
  import "swiper/css";
  import "swiper/css/navigation";

  // 商品数据（原静态数据，跳转统一进商品列表页）
  const products = [
    {
      id: 1,
      name: "璀璨·永恒·海洋之星",
      price: "$7799.52",
      image: new URL(
        "@/assets/image/首页图/璀璨·永恒·海洋之星.jpg",
        import.meta.url,
      ).href,
      link: "/goods",
    },
    {
      id: 2,
      name: "璀璨·永恒·心相守",
      price: "$5380.5",
      image: new URL(
        "@/assets/image/首页图/璀璨·永恒·心相守.jpg",
        import.meta.url,
      ).href,
      link: "/goods",
    },
    {
      id: 3,
      name: "璀璨·絮语·天使的王冠",
      price: "$2084.52",
      image: new URL(
        "@/assets/image/首页图/璀璨·絮语·天使的王冠.jpg",
        import.meta.url,
      ).href,
      link: "/goods",
    },
    {
      id: 4,
      name: "璀璨·爱与美结晶钻戒",
      price: "$1354.60",
      image: new URL(
        "@/assets/image/首页图/璀璨·爱与美结晶钻戒.jpg",
        import.meta.url,
      ).href,
      link: "/goods",
    },
    {
      id: 5,
      name: "璀璨·玫瑰心约",
      price: "$1520.55",
      image: new URL("@/assets/image/首页图/璀璨·玫瑰心约.jpg", import.meta.url)
        .href,
      link: "/goods",
    },
    {
      id: 6,
      name: "璀璨·宝藏·纯白之戒",
      price: "$3500.00",
      image: new URL(
        "@/assets/image/首页图/璀璨·宝藏·纯白之戒.jpg",
        import.meta.url,
      ).href,
      link: "/goods",
    },
    {
      id: 7,
      name: "璀璨·天空·挚爱之戒",
      price: "$10245.50",
      image: new URL(
        "@/assets/image/首页图/璀璨·天空·挚爱之戒.jpg",
        import.meta.url,
      ).href,
      link: "/goods",
    },
  ];

  let swiperInstance = null;
  let observer = null;

  onMounted(() => {
    swiperInstance = new Swiper(".product-swiper", {
      modules: [Navigation],
      slidesPerView: 1,
      spaceBetween: 22,
      navigation: {
        nextEl: ".product-carousel-next",
        prevEl: ".product-carousel-prev",
      },
      breakpoints: {
        640: { slidesPerView: 2 },
        1024: { slidesPerView: 4 },
      },
    });

    observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((en) => {
          if (en.isIntersecting) {
            en.target.classList.add("revealed");
            observer.unobserve(en.target);
          }
        });
      },
      { threshold: 0.15 },
    );
    document.querySelectorAll(".hp-section .reveal").forEach((el) =>
      observer.observe(el),
    );
  });

  onUnmounted(() => {
    if (swiperInstance) swiperInstance.destroy(true, true);
    if (observer) observer.disconnect();
  });
</script>

<style scoped>
.hp-section {
  position: relative;
  padding: 84px 0 64px;
  overflow: hidden;
  background: linear-gradient(180deg, transparent 0%, rgba(22, 17, 10, 0.4) 50%, transparent 100%);
}
.hp-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(900px 420px at 82% 8%, rgba(212, 175, 55, 0.1), transparent 62%),
    radial-gradient(760px 420px at 8% 92%, rgba(212, 175, 55, 0.08), transparent 60%);
  pointer-events: none;
}
.hp-inner {
  position: relative;
  z-index: 1;
  max-width: 1320px;
  margin: 0 auto;
  padding: 0 40px;
}
.hp-head {
  text-align: center;
  margin-bottom: 46px;
  opacity: 0;
  transform: translateY(24px);
  transition: all 0.8s cubic-bezier(0.16, 1, 0.3, 1);
}
.hp-head.revealed {
  opacity: 1;
  transform: translateY(0);
}
.hp-tag {
  font-size: 13px;
  letter-spacing: 8px;
  color: var(--gold-300, #e8b947);
  text-transform: uppercase;
  font-weight: 600;
}
.hp-title {
  font-family: var(--heading-font, "Cormorant", serif);
  font-size: 64px;
  font-weight: 800;
  letter-spacing: 10px;
  margin: 20px 0 16px;
  background: linear-gradient(135deg, #f5d98a, #e8b947 50%, #d4af37);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  line-height: 1.1;
}
.hp-sub {
  color: #8e8770;
  font-size: 17px;
  letter-spacing: 3px;
}
.hp-nav {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 22px;
  margin-top: 26px;
}
.hp-arrow {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: 1px solid rgba(212, 175, 55, 0.45);
  background: rgba(212, 175, 55, 0.07);
  color: #e8b947;
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}
.hp-arrow:hover {
  background: linear-gradient(135deg, #d4af37, #f0c75e);
  color: #1c1406;
  transform: scale(1.1);
  box-shadow: 0 8px 22px rgba(212, 175, 55, 0.35);
}
.hp-more {
  color: #e8b947;
  text-decoration: none;
  font-weight: 700;
  letter-spacing: 2px;
  border-bottom: 1px solid rgba(212, 175, 55, 0.45);
  padding-bottom: 4px;
  transition: all 0.3s ease;
}
.hp-more:hover {
  color: #fff3d0;
  border-color: #fff3d0;
}
.hp-more-arrow {
  display: inline-block;
  transition: transform 0.3s ease;
}
.hp-more:hover .hp-more-arrow {
  transform: translateX(6px);
}
.hp-card-wrap {
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.7s cubic-bezier(0.16, 1, 0.3, 1);
}
.hp-card-wrap.revealed {
  opacity: 1;
  transform: translateY(0);
}
.hp-card {
  position: relative;
  display: block;
  border-radius: 22px;
  overflow: hidden;
  background: linear-gradient(145deg, rgba(30, 25, 12, 0.72), rgba(12, 12, 12, 0.72));
  background-color: rgba(20, 20, 20, 0.72);
  border: 1px solid rgba(212, 175, 55, 0.24);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  box-shadow: 0 22px 54px rgba(0, 0, 0, 0.42);
  text-decoration: none;
  transition:
    transform 0.45s cubic-bezier(0.16, 1, 0.3, 1),
    box-shadow 0.45s ease,
    border-color 0.45s ease;
}
.hp-card:hover {
  transform: translateY(-10px);
  border-color: rgba(240, 214, 138, 0.58);
  box-shadow:
    0 32px 74px rgba(0, 0, 0, 0.55),
    0 0 42px rgba(212, 175, 55, 0.2);
}
.hp-badge {
  position: absolute;
  top: 14px;
  left: 14px;
  z-index: 3;
  padding: 5px 14px;
  border-radius: 999px;
  font-size: 12px;
  letter-spacing: 2px;
  font-weight: 800;
  color: #241a08;
  background: linear-gradient(135deg, #f0c75e, #d4af37);
  box-shadow: 0 4px 14px rgba(212, 175, 55, 0.45);
}
.hp-img-wrap {
  position: relative;
  overflow: hidden;
  aspect-ratio: 1 / 1;
  background: rgba(255, 255, 255, 0.03);
}
.hp-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition:
    transform 0.9s cubic-bezier(0.16, 1, 0.3, 1),
    filter 0.9s ease;
}
.hp-card:hover .hp-img {
  transform: scale(1.12);
  filter: brightness(1.06);
}
.hp-shine {
  position: absolute;
  inset: 0;
  z-index: 2;
  background: linear-gradient(115deg, transparent 32%, rgba(255, 255, 255, 0.3) 46%, transparent 60%);
  transform: translateX(-130%);
  transition: transform 0.9s ease;
  pointer-events: none;
}
.hp-card:hover .hp-shine {
  transform: translateX(130%);
}
.hp-info {
  padding: 20px 22px 24px;
  text-align: center;
}
.hp-name {
  font-size: 16px;
  font-weight: 700;
  color: #efe9d6;
  letter-spacing: 1px;
  margin: 0 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.hp-price {
  font-size: 21px;
  font-weight: 800;
}
@media (max-width: 1000px) {
  .hp-title {
    font-size: 44px;
    letter-spacing: 6px;
  }
}
</style>
