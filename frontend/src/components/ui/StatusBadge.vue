<script setup lang="ts">
defineProps<{
  label: string
  variant?: 'default' | 'success' | 'warning' | 'error' | 'live'
}>()
</script>

<template>
  <span class="status-badge" :class="variant ?? 'default'">{{ label }}</span>
</template>

<style scoped>
.status-badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  white-space: nowrap;
  background: var(--bg-secondary);
  color: var(--text-secondary);
}

.status-badge.success {
  background: rgba(0, 180, 42, 0.1);
  color: var(--color-success);
}

.status-badge.warning {
  background: rgba(255, 125, 0, 0.1);
  color: var(--color-warning);
}

.status-badge.error {
  background: rgba(245, 63, 63, 0.1);
  color: var(--color-error);
}

.status-badge.live {
  background: rgba(0, 180, 42, 0.1);
  color: var(--color-success);
}

.status-badge.live::before {
  content: '';
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-success);
  margin-right: 6px;
  vertical-align: middle;
}
</style>
