<script setup lang="ts">
defineProps<{
  title: string
}>()
</script>

<template>
  <div class="section-title">{{ title }}</div>
</template>

<style scoped>
.section-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
  line-height: 24px;
  margin-bottom: 16px;
  padding-left: 12px;
  position: relative;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 4px;
  width: 4px;
  height: 16px;
  background: var(--color-primary);
  border-radius: 2px;
}
</style>
