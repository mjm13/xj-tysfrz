<script setup lang="ts">
defineProps<{
  title?: string
  subtitle?: string
}>()
</script>

<template>
  <div class="data-card">
    <div v-if="title || $slots.head" class="card-head">
      <div>
        <div v-if="title" class="card-title">{{ title }}</div>
        <div v-if="subtitle" class="card-sub">{{ subtitle }}</div>
      </div>
      <slot name="head" />
    </div>
    <slot />
  </div>
</template>

<style scoped>
.data-card {
  background: var(--bg-card);
  border-radius: 8px;
  box-shadow: var(--shadow-1);
  padding: 16px;
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
  line-height: 24px;
  padding-left: 12px;
  position: relative;
}

.card-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 4px;
  width: 4px;
  height: 16px;
  background: var(--color-primary);
  border-radius: 2px;
}

.card-sub {
  font-size: 12px;
  color: var(--text-secondary);
  margin-left: 12px;
  margin-top: 4px;
}
</style>
