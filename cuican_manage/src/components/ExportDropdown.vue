<template>
  <div class="dropdown d-inline-block">
    <button class="btn btn-outline-success btn-sm dropdown-toggle" data-bs-toggle="dropdown">
      <i class="bi bi-download"></i> 导出
    </button>
    <ul class="dropdown-menu dropdown-menu-end">
      <li class="dropdown-header small text-muted">{{ pageLabel }}</li>
      <li><a class="dropdown-item" href="#" @click.prevent="$emit('export', 'page', 'xls')">
        <i class="bi bi-file-earmark-excel me-2 text-success"></i>导出 Excel
      </a></li>
      <li><a class="dropdown-item" href="#" @click.prevent="$emit('export', 'page', 'html')">
        <i class="bi bi-filetype-html me-2 text-primary"></i>导出 HTML
      </a></li>
      <li v-if="showAll"><hr class="dropdown-divider"></li>
      <li v-if="showAll" class="dropdown-header small text-muted">{{ allLabel }}</li>
      <li v-if="showAll"><a class="dropdown-item" href="#" @click.prevent="$emit('export', 'all', 'xls')">
        <i class="bi bi-file-earmark-excel me-2 text-success"></i>导出 Excel
      </a></li>
      <li v-if="showAll"><a class="dropdown-item" href="#" @click.prevent="$emit('export', 'all', 'html')">
        <i class="bi bi-filetype-html me-2 text-primary"></i>导出 HTML
      </a></li>
    </ul>
  </div>
</template>

<script setup>
defineProps({
  pageLabel: { type: String, default: "此页" },
  allLabel: { type: String, default: "全部" },
  showAll: { type: Boolean, default: true }
})

defineEmits(["export"])
</script>

<style scoped>
.dropdown { position: relative; z-index: 2000; }
.dropdown-menu { z-index: 2001 !important; }
</style>
