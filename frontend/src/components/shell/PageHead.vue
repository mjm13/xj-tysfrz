<script setup lang="ts">
defineProps<{
  title: string
  description?: string
  badge?: string
  badgeVariant?: 'default' | 'live'
}>()
</script>

<template>
  <div class="page-head">
    <div>
      <h1 class="page-title">{{ title }}</h1>
      <p v-if="description" class="page-desc">{{ description }}</p>
      <span v-if="badge" class="badge" :class="badgeVariant">{{ badge }}</span>
    </div>
    <div v-if="$slots.extra" class="extra">
      <slot name="extra" />
    </div>
  </div>
</template>

<style scoped>
.page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border);
  gap: 24px;
  flex-wrap: wrap;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  line-height: 28px;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.page-desc {
  font-size: 14px;
  color: var(--text-secondary);
}

.badge {
  margin-top: 12px;
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 4px;
  background: var(--bg-secondary);
  color: var(--text-secondary);
}

.badge.live {
  background: rgba(0, 180, 42, 0.1);
  color: var(--color-success);
}

.badge.live::before {
  content: '';
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-success);
  margin-right: 6px;
}

.extra {
  display: flex;
  gap: 16px;
  align-items: center;
}
</style>
