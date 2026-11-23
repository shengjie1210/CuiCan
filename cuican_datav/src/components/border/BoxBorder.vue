<template>
  <component
    :is="borderName"
    :color="colors"
    :reverse="reverse"
    class="box-border"
  >
    <slot />
  </component>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: { type: Number, default: 13 },
  color: { type: Array, default: () => null },
  reverse: { type: Boolean, default: false }
})

const map = { 8: 'DvBorderBox8', 9: 'DvBorderBox9', 10: 'DvBorderBox10', 11: 'DvBorderBox11', 12: 'DvBorderBox12', 13: 'DvBorderBox13' }
const borderName = computed(() => map[props.type] || 'DvBorderBox13')
const colors = computed(() => props.color || ['#00d9ff', '#5b8dff'])
</script>

<style scoped>
.box-border {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column
}
.box-border :deep(.border-box-content) {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  overflow: hidden
}
</style>
