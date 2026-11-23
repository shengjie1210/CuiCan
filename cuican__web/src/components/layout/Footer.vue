<template>
  <footer class="footer">
    <div class="footer-inner">
      <span class="footer-brand">{{ settings['site.name'] || '璀璨珠宝商城' }}</span>
      <span class="footer-copy">{{ settings['site.copyright'] || '© 2026 璀璨' }}</span>
      <span class="footer-icp" v-if="settings['site.icp']">{{ settings['site.icp'] }}</span>
    </div>
  </footer>
</template>

<script setup>
import { ref, onMounted } from "vue"
import request from "@/utils/request"

const settings = ref({})
onMounted(async () => {
  try {
    const res = await request.get("/api/system/settings")
    settings.value = res.data || {}
  } catch (e) {}
})
</script>

<style scoped>
  .footer {
    text-align: center;
    padding: 15px;
    background: #222;
    color: #aaa;
  }
  .footer-inner {
    display: flex;
    flex-direction: column;
    gap: 4px;
    align-items: center;
  }
  .footer-brand {
    font-weight: 600;
    color: #ccc;
  }
  .footer-copy {
    font-size: 12px;
  }
  .footer-icp {
    font-size: 11px;
    opacity: 0.6;
  }
</style>
