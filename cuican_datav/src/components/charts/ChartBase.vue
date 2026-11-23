<template>
  <div class="echart-box" ref="boxRef"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  option: { type: Object, required: true },
  theme: { type: String, default: "" }
})

const boxRef = ref(null)
let instance = null
let resizeObs = null

function init() {
  if (!boxRef.value) return
  instance = echarts.init(boxRef.value, props.theme)
  instance.setOption(props.option)
  if (typeof ResizeObserver !== 'undefined') {
    resizeObs = new ResizeObserver(() => { if (instance) instance.resize() })
    resizeObs.observe(boxRef.value)
  }
}

onMounted(() => nextTick(init))

onBeforeUnmount(() => {
  if (resizeObs && boxRef.value) resizeObs.unobserve(boxRef.value)
  if (instance) { instance.dispose(); instance = null }
})

watch(
  () => props.option,
  (val) => { if (instance) instance.setOption(val, true) },
  { deep: true }
)
</script>

<style scoped>
.echart-box {
  width: 100%;
  height: 100%;
  min-height: 60px
}
</style>
