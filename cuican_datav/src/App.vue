<template>
  <RouterView />
</template>

<script setup>
import { RouterView } from 'vue-router'
</script>

<style>
@keyframes pulse {
  0%, 100% { opacity: 0.6 }
  50% { opacity: 1 }
}
</style>
