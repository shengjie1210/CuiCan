<template>
  <div class="card-footer d-flex justify-content-between align-items-center py-2" v-if="total > 0">
    <div class="d-flex align-items-center gap-2" v-if="showSizePicker">
      <small class="text-muted">共 {{ total }} 条，第 {{ current }}/{{ totalPages }} 页</small>
      <select class="form-select form-select-sm" style="width:auto" :value="pageSize" @change="$emit('update:pageSize', +$event.target.value); $emit('change', 1)">
        <option v-for="s in sizes" :key="s" :value="s">{{ s }}</option>
      </select>
    </div>
    <small v-else class="text-muted">共 {{ total }} 条</small>
    <nav>
      <ul class="pagination pagination-sm mb-0">
        <li class="page-item" :class="{ disabled: current <= 1 }">
          <a class="page-link" href="#" @click.prevent="$emit('change', 1)">首页</a>
        </li>
        <li class="page-item" :class="{ disabled: current <= 1 }">
          <a class="page-link" href="#" @click.prevent="$emit('change', current - 1)">‹</a>
        </li>
        <li v-for="p in pageRange" :key="p" :class="['page-item', { active: p === current }]">
          <a class="page-link" href="#" @click.prevent="$emit('change', p)">{{ p }}</a>
        </li>
        <li class="page-item" :class="{ disabled: current >= totalPages }">
          <a class="page-link" href="#" @click.prevent="$emit('change', current + 1)">›</a>
        </li>
        <li class="page-item" :class="{ disabled: current >= totalPages }">
          <a class="page-link" href="#" @click.prevent="$emit('change', totalPages)">末页</a>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script setup>
import { computed } from "vue"

const props = defineProps({
  total: { type: Number, default: 0 },
  current: { type: Number, default: 1 },
  pageSize: { type: Number, default: 10 },
  showSizePicker: { type: Boolean, default: false },
  sizes: { type: Array, default: () => [10, 20, 50, 100] }
})

defineEmits(["change", "update:pageSize"])

const totalPages = computed(() => Math.ceil(props.total / props.pageSize) || 1)

const pageRange = computed(() => {
  const tp = totalPages.value
  const cur = props.current
  let s = Math.max(1, cur - 2)
  let e = Math.min(tp, cur + 2)
  if (e - s < 4) {
    if (s === 1) e = Math.min(tp, s + 4)
    else s = Math.max(1, e - 4)
  }
  const a = []
  for (let i = s; i <= e; i++) a.push(i)
  return a
})
</script>
