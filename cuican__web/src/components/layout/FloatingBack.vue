<template>
  <div class="floating-back" :style="{ left: x+'px', top: y+'px' }"
    @mousedown="startDrag" @mouseup="stopDrag" @click="goBack">&#8592;</div>
</template>
<script setup>
import { ref } from "vue"; import { useRouter } from "vue-router";
const router = useRouter();
const x = ref(16), y = ref(80);
let d = false, sx = 0, sy = 0, ox = 0, oy = 0;
const startDrag = (e) => { d = true; sx = e.clientX; sy = e.clientY; ox = x.value; oy = y.value;
  document.addEventListener("mousemove", onDrag); document.addEventListener("mouseup", stopDrag); };
const onDrag = (e) => { if (!d) return; x.value = ox + (e.clientX - sx); y.value = oy + (e.clientY - sy); };
const stopDrag = () => { if (d) { d = false; document.removeEventListener("mousemove", onDrag); document.removeEventListener("mouseup", stopDrag); }};
const goBack = () => { if (!d) router.back(); };
</script>
<style scoped>
.floating-back {
  position: fixed; z-index: 9999; width: 42px; height: 42px; border-radius: 16px;
  background: rgba(255,255,255,0.28); color: #000; display: flex; align-items: center; justify-content: center;
  font-size: 20px; cursor: pointer; box-shadow: 0 8px 24px rgba(0,0,0,0.12), inset 0 1px 0 rgba(255,255,255,0.55);
  border: 1px solid rgba(255,255,255,0.45); backdrop-filter: blur(16px); -webkit-backdrop-filter: blur(16px);
  user-select: none; transition: box-shadow 0.2s, transform 0.15s;
}
.floating-back:hover { box-shadow: 0 10px 30px rgba(0,0,0,0.18), inset 0 1px 0 rgba(255,255,255,0.65); transform: scale(1.08); }
</style>
