<script setup lang="ts">
import AppTopbar from '@/components/shell/AppTopbar.vue'
import AppFooter from '@/components/shell/AppFooter.vue'
</script>

<template>
  <div class="layout">
    <AppTopbar />
    <main class="main">
      <router-view />
    </main>
    <AppFooter />
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main {
  flex: 1;
  max-width: 1440px;
  width: 100%;
  margin: 0 auto;
  padding: 24px;
  min-width: 0;
}
</style>
