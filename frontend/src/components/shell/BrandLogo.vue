<script setup lang="ts">
defineProps<{
  compact?: boolean
}>()
</script>

<template>
  <div class="brand" :class="{ compact }">
    <div class="brand-logo">指</div>
    <div class="brand-text">
      <span class="zh">指标平台</span>
      <span class="en">METRIC HUB</span>
    </div>
  </div>
</template>

<style scoped>
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand.compact {
  flex-direction: column;
  text-align: center;
}

.brand-logo {
  width: 32px;
  height: 32px;
  border-radius: 9px;
  background: linear-gradient(135deg, var(--ink-800), var(--blue-600));
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  color: #fff;
  flex-shrink: 0;
  box-shadow: 0 2px 6px rgba(37, 99, 235, 0.3);
}

.brand.compact .brand-logo {
  width: 48px;
  height: 48px;
  font-size: 18px;
  border-radius: 12px;
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.15;
}

.brand-text .zh {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.02em;
  color: var(--gray-900);
}

.brand.compact .brand-text .zh {
  font-size: 18px;
  margin-top: 12px;
}

.brand-text .en {
  font-size: 9.5px;
  color: var(--gray-400);
  letter-spacing: 0.14em;
  text-transform: uppercase;
  font-family: 'JetBrains Mono', monospace;
  margin-top: 1px;
}

.brand.compact .brand-text .en {
  margin-top: 4px;
}
</style>
