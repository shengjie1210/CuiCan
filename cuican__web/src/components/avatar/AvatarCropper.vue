<template>
  <div class="ac-overlay" v-if="show" @click.self="$emit('update:show', false)">
    <div class="ac-dialog">
      <div class="ac-head">
        <span class="ac-title">裁剪头像</span>
        <button class="ac-close" @click="$emit('update:show', false)">×</button>
      </div>

      <div
        class="ac-stage"
        ref="stageRef"
        @wheel="onWheel"
        @pointerdown="onPointerDown"
        @pointermove="onPointerMove"
        @pointerup="onPointerUp"
        @pointercancel="onPointerUp">
        <img v-if="src" ref="imgRef" :src="src" alt="头像预览" draggable="false" />
        <div class="ac-hint">滚轮缩放 · 拖动调整位置</div>
      </div>

      <div class="ac-controls">
        <button class="ac-ctrl" @click="zoomBy(-0.1)">−</button>
        <span class="ac-zoom">{{ Math.round(scale * 100) }}%</span>
        <button class="ac-ctrl" @click="zoomBy(0.1)">＋</button>
        <button class="ac-ctrl ac-reset" @click="reset">重置</button>
      </div>

      <div class="ac-foot">
        <button class="ac-btn ac-btn-default" @click="$emit('update:show', false)">取消</button>
        <button class="ac-btn ac-btn-primary" @click="confirm" :disabled="!ready">确认替换</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from "vue"

defineProps({
  show: { type: Boolean, default: false },
  src: { type: String, default: "" }
})
const emit = defineEmits(["update:show", "confirm"])

const VIEW = 300
const OUT = 300

const stageRef = ref(null)
const imgRef = ref(null)
const scale = ref(1)
const offset = ref({ x: 0, y: 0 })
const ready = ref(false)

let dragging = false
let last = { x: 0, y: 0 }

watch(() => imgRef.value, (img) => {
  if (img) {
    scale.value = Math.max(VIEW / img.naturalWidth, VIEW / img.naturalHeight)
    offset.value = { x: 0, y: 0 }
    apply()
    ready.value = true
  }
})

watch(() => scale.value, () => apply())

function minScale() {
  const img = imgRef.value
  if (!img) return 1
  return Math.max(VIEW / img.naturalWidth, VIEW / img.naturalHeight)
}

function apply() {
  const img = imgRef.value
  if (!img) return
  img.style.transform = `translate(-50%, -50%) translate(${offset.value.x}px, ${offset.value.y}px) scale(${scale.value})`
}

function clampOffset() {
  const img = imgRef.value
  if (!img) return
  const dispW = img.naturalWidth * scale.value
  const dispH = img.naturalHeight * scale.value
  const maxX = Math.max(0, (dispW - VIEW) / 2)
  const maxY = Math.max(0, (dispH - VIEW) / 2)
  offset.value.x = Math.min(maxX, Math.max(-maxX, offset.value.x))
  offset.value.y = Math.min(maxY, Math.max(-maxY, offset.value.y))
  apply()
}

function zoomBy(delta) {
  scale.value = Math.min(5, Math.max(minScale(), scale.value + delta))
  clampOffset()
}

function onWheel(e) {
  e.preventDefault()
  zoomBy(e.deltaY < 0 ? 0.1 : -0.1)
}

function onPointerDown(e) {
  dragging = true
  last = { x: e.clientX, y: e.clientY }
  stageRef.value.setPointerCapture(e.pointerId)
}

function onPointerMove(e) {
  if (!dragging) return
  offset.value.x += e.clientX - last.x
  offset.value.y += e.clientY - last.y
  last = { x: e.clientX, y: e.clientY }
  clampOffset()
}

function onPointerUp() {
  dragging = false
}

function reset() {
  scale.value = minScale()
  offset.value = { x: 0, y: 0 }
  apply()
}

function confirm() {
  const img = imgRef.value
  if (!img) return
  const canvas = document.createElement("canvas")
  canvas.width = OUT
  canvas.height = OUT
  const ctx = canvas.getContext("2d")
  const s = scale.value
  const sx = img.naturalWidth / 2 - (VIEW / 2 + offset.value.x) / s
  const sy = img.naturalHeight / 2 - (VIEW / 2 + offset.value.y) / s
  const sw = VIEW / s
  ctx.drawImage(img, sx, sy, sw, sw, 0, 0, OUT, OUT)
  canvas.toBlob((blob) => {
    if (blob) emit("confirm", blob)
  }, "image/png")
}
</script>

<style scoped>
.ac-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.72);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  z-index: 300;
  display: flex;
  align-items: center;
  justify-content: center;
}
.ac-dialog {
  width: 400px;
  max-width: 92%;
  background: linear-gradient(145deg, rgba(30, 25, 12, 0.96), rgba(12, 12, 12, 0.96));
  border: 1px solid rgba(212, 175, 55, 0.4);
  border-radius: 24px;
  padding: 24px 28px 22px;
  color: #efe9d6;
  box-shadow: 0 40px 80px rgba(0, 0, 0, 0.6);
}
.ac-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 14px;
  border-bottom: 1px solid rgba(212, 175, 55, 0.2);
  margin-bottom: 18px;
}
.ac-title {
  font-size: 19px;
  font-weight: 800;
  letter-spacing: 3px;
  background: linear-gradient(135deg, #f5d36e, #e8b947);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
.ac-close {
  background: rgba(212, 175, 55, 0.15);
  border: 1px solid rgba(212, 175, 55, 0.3);
  border-radius: 50%;
  width: 30px; height: 30px;
  color: #f5d36e;
  font-size: 15px;
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
}
.ac-close:hover { background: rgba(212, 175, 55, 0.3); }
.ac-stage {
  position: relative;
  width: 300px;
  height: 300px;
  margin: 0 auto;
  border-radius: 50%;
  overflow: hidden;
  cursor: grab;
  touch-action: none;
  background:
    linear-gradient(45deg, rgba(212, 175, 55, 0.05) 25%, transparent 25%, transparent 75%, rgba(212, 175, 55, 0.05) 75%),
    linear-gradient(45deg, rgba(212, 175, 55, 0.05) 25%, transparent 25%, transparent 75%, rgba(212, 175, 55, 0.05) 75%);
  background-size: 28px 28px;
  background-position: 0 0, 14px 14px;
  border: 2px solid rgba(212, 175, 55, 0.45);
  box-shadow: 0 0 0 4px rgba(0, 0, 0, 0.35), inset 0 0 40px rgba(0, 0, 0, 0.3);
}
.ac-stage:active { cursor: grabbing; }
.ac-stage img {
  position: absolute;
  left: 50%;
  top: 50%;
  max-width: none;
  transform-origin: center;
  user-select: none;
  -webkit-user-drag: none;
  pointer-events: none;
  will-change: transform;
}
.ac-hint {
  position: absolute;
  left: 0; right: 0; bottom: 8px;
  text-align: center;
  font-size: 11px;
  color: rgba(245, 211, 110, 0.85);
  background: rgba(0, 0, 0, 0.45);
  padding: 3px 0;
  pointer-events: none;
}
.ac-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: 16px;
}
.ac-ctrl {
  width: 34px; height: 34px;
  border-radius: 50%;
  background: rgba(212, 175, 55, 0.14);
  border: 1px solid rgba(212, 175, 55, 0.35);
  color: #f5d36e;
  font-size: 17px;
  cursor: pointer;
}
.ac-ctrl:hover { background: rgba(212, 175, 55, 0.28); }
.ac-reset { width: auto; border-radius: 999px; padding: 0 14px; font-size: 13px; }
.ac-zoom {
  min-width: 52px;
  text-align: center;
  font-size: 13px;
  color: #c4bca0;
}
.ac-foot {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
.ac-btn {
  padding: 8px 24px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid transparent;
  font-family: inherit;
  transition: all 0.25s ease;
}
.ac-btn-default {
  background: transparent;
  color: #f5d36e;
  border-color: rgba(212, 175, 55, 0.35);
}
.ac-btn-default:hover { background: rgba(212, 175, 55, 0.12); }
.ac-btn-primary {
  background: linear-gradient(135deg, #e8b947, #d4af37);
  color: #1a1408;
}
.ac-btn-primary:hover { box-shadow: 0 4px 14px rgba(212, 175, 55, 0.4); }
.ac-btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
