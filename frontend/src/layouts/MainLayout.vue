<script setup lang="ts">
import AppSidebar from '@/components/shell/AppSidebar.vue'
</script>

<template>
  <div class="layout">
    <AppSidebar />
    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.main {
  flex: 1;
  padding: 20px 24px 40px;
  min-width: 0;
}
</style>
