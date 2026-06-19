<script setup lang="ts">
defineProps<{
  appVersion: string
  buildTime?: string
  backendRelease?: string | null
  backendGitCommit?: string | null
}>()
</script>

<template>
  <footer class="footer">
    <span>© 高校身份数据平台 · 数据治理</span>
    <span class="version">
      前端 v{{ appVersion }}
      <template v-if="buildTime"> · {{ buildTime.slice(0, 10) }}</template>
      <template v-if="backendRelease"> · 后端 v{{ backendRelease }}</template>
      <template v-if="backendGitCommit"> · {{ backendGitCommit }}</template>
    </span>
  </footer>
</template>

<style scoped>
.footer {
  max-width: 1440px;
  margin: 24px auto 0;
  padding: 24px;
  border-top: 1px solid var(--border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--text-disabled);
}
</style>
