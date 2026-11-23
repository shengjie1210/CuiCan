<script setup>
import { ref, watch, computed } from "vue";
import { useRoute } from "vue-router";
import LetterSplash from "@/components/animation/LetterSplash.vue";
import Curtain from "@/components/animation/Curtain.vue";
import QuickSidebar from "@/components/layout/QuickSidebar.vue";
import FloatingBack from "@/components/layout/FloatingBack.vue";

const route = useRoute();
const showLetters = ref(false);
const showMain = ref(true);
const showCurtain = ref(false);

const showSidebar = computed(() => {
  const p = route.path;
  return !(
    p === "/login" ||
    p === "/register" ||
    p === "/address-test" ||
    p.startsWith("/admin") ||
    p.startsWith("/super")
  );
});

function isHome(path) {
  return path === "/" || path === "/index";
}

function playSplash() {
  showLetters.value = true;
  showCurtain.value = false;
  showMain.value = false;
}

if (isHome(route.path)) playSplash();

watch(
  () => route.path,
  (path, oldPath) => {
    if (isHome(path) && path !== oldPath) playSplash();
  },
);

function onLettersFinished() {
  showLetters.value = false;
  showCurtain.value = true;
}

function onCurtainFinished() {
  showCurtain.value = false;
  showMain.value = true;
}
</script>

<template>
  <LetterSplash v-if="showLetters" @lettersFinished="onLettersFinished" />
  <Curtain v-if="showCurtain" @curtainFinished="onCurtainFinished" />
  <router-view v-if="showMain" class="main" />
  <QuickSidebar v-if="showSidebar && showMain" />
  <FloatingBack v-if="showSidebar && showMain" />
</template>
<style scoped>

</style>
