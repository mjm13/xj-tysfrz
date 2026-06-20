<script setup lang="ts">
defineProps<{
  items: Array<{ label: string; to?: string }>
}>()
</script>

<template>
  <nav class="breadcrumb" aria-label="面包屑">
    <template v-for="(item, i) in items" :key="i">
      <span v-if="i > 0" class="sep">/</span>
      <RouterLink v-if="item.to && i < items.length - 1" :to="item.to" class="link">
        {{ item.label }}
      </RouterLink>
      <span v-else class="last">{{ item.label }}</span>
    </template>
  </nav>
</template>

<style scoped>
.breadcrumb {
  font-size: 14px;
  color: var(--text-disabled);
  margin-bottom: 16px;
}

.sep {
  margin: 0 8px;
}

.link {
  color: var(--text-disabled);
  transition: color 150ms;
}

.link:hover {
  color: var(--color-primary);
}

.last {
  color: var(--text-primary);
}
</style>
