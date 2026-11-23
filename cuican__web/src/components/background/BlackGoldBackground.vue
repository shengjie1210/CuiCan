<template>
  <div class="black-gold-bg" aria-hidden="true">
    <GoldNebula />
    <GoldStars />
    <GoldDustAurum />
    <GoldVignette />
  </div>
</template>

<script setup>
/**
 * BlackGoldBackground - 黑金主题背景（精简版）
 * 移除 GoldGrid 透视网格 + GoldRays 光柱，避免线条杂乱
 * 保留：
 *   -6  GoldNebula      柔和金色极光（4 个光斑缓慢漂移）
 *   -3  GoldStars       微星点闪烁（80 颗，更克制）
 *   -2  GoldDustAurum   金色尘埃飘落 + 光带
 *   -1  GoldVignette    顶部 / 底部暗角 + 金色边框
 * 配合 .bg-dark 容器使用
 */
import GoldNebula    from "@/components/background/aurum/GoldNebula.vue";
import GoldStars     from "@/components/background/aurum/GoldStars.vue";
import GoldDustAurum from "@/components/background/aurum/GoldDustAurum.vue";
import GoldVignette  from "@/components/background/aurum/GoldVignette.vue";
</script>

<style scoped>
.black-gold-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background:
    radial-gradient(ellipse 100% 100% at 50% 50%, #14140a 0%, #050505 90%);
  overflow: hidden;
}
</style>
