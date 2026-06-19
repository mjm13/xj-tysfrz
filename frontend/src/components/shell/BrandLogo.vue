<script setup lang="ts">
defineProps<{
  compact?: boolean
}>()
</script>

<template>
  <div class="logo-wrap" :class="{ compact }">
    <div class="logo-mark">
      <svg viewBox="0 0 24 24" aria-hidden="true">
        <circle cx="12" cy="8" r="3" />
        <path d="M5 21c0-4 3-7 7-7s7 3 7 7" />
      </svg>
    </div>
    <div class="logo-text">身份数据平台</div>
  </div>
</template>

<style scoped>
.logo-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-wrap.compact {
  flex-direction: column;
  text-align: center;
  gap: 16px;
}

.logo-mark {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-wrap.compact .logo-mark {
  width: 48px;
  height: 48px;
  border-radius: 8px;
}

.logo-mark svg {
  width: 18px;
  height: 18px;
  stroke: white;
  fill: none;
  stroke-width: 2;
}

.logo-wrap.compact .logo-mark svg {
  width: 24px;
  height: 24px;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 24px;
  white-space: nowrap;
}

.logo-wrap.compact .logo-text {
  font-size: 18px;
}
</style>
