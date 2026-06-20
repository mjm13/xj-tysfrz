<script setup lang="ts">
import { useRoute } from 'vue-router'
import type { ModuleNavConfig } from '@/config/module-nav'

const props = defineProps<{
  config: ModuleNavConfig
}>()

const route = useRoute()

function isActive(path?: string) {
  if (!path) return false
  return route.path === path
}
</script>

<template>
  <aside class="sidebar">
    <template v-for="group in config.groups" :key="group.label">
      <div class="sidebar-section">{{ group.label }}</div>
      <template v-for="item in group.items" :key="item.label">
        <RouterLink
          v-if="item.path && !item.disabled"
          :to="item.path"
          class="sidebar-link"
        >
          <div class="sidebar-item" :class="{ active: isActive(item.path) }">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <circle cx="12" cy="8" r="4" />
              <path d="M4 21c0-4 4-7 8-7s8 3 8 7" />
            </svg>
            <span>{{ item.label }}</span>
            <span
              v-if="item.badge"
              class="badge"
              :class="{ warn: item.badgeVariant === 'warn' }"
            >{{ item.badge }}</span>
          </div>
        </RouterLink>
        <div v-else class="sidebar-item disabled">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <circle cx="12" cy="8" r="4" />
            <path d="M4 21c0-4 4-7 8-7s8 3 8 7" />
          </svg>
          <span>{{ item.label }}</span>
          <span v-if="item.badge" class="badge">{{ item.badge }}</span>
        </div>
      </template>
    </template>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: var(--bg-card);
  border-right: 1px solid var(--border);
  padding: 16px 0;
  align-self: stretch;
}

.sidebar-section {
  padding: 8px 16px;
  font-size: 12px;
  color: var(--text-disabled);
  letter-spacing: 1px;
}

.sidebar-section:not(:first-child) {
  margin-top: 8px;
}

.sidebar-link {
  display: block;
  text-decoration: none;
  color: inherit;
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  margin: 2px 8px;
  font-size: 14px;
  color: var(--text-secondary);
  border-radius: 6px;
  transition: all 200ms;
  cursor: pointer;
}

.sidebar-item svg {
  width: 16px;
  height: 16px;
  stroke: currentColor;
  fill: none;
  stroke-width: 1.8;
  flex-shrink: 0;
}

.sidebar-item:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.sidebar-item.active {
  background: rgba(37, 99, 235, 0.08);
  color: var(--color-primary);
  font-weight: 500;
}

.sidebar-item.disabled {
  color: var(--text-disabled);
  cursor: not-allowed;
  opacity: 0.5;
}

.sidebar-item .badge {
  margin-left: auto;
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 3px;
  background: var(--bg-secondary);
  color: var(--text-disabled);
  font-weight: 400;
}

.sidebar-item .badge.warn {
  background: rgba(255, 125, 0, 0.1);
  color: var(--color-warning);
}
</style>
