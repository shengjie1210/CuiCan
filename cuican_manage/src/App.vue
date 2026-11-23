<template>
  <RouterView />
</template>

<script setup lang="ts">
import { RouterView } from "vue-router"
</script>

<style>
@keyframes btn-spin {
  from { transform: rotate(0deg) }
  to { transform: rotate(360deg) }
}
.btn-spinning i,
.btn-spinning .bi {
  animation: btn-spin 0.7s linear infinite;
  display: inline-block;
}
</style>
