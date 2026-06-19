<script setup lang="ts">
import { onMounted } from 'vue'
import AppTopbar from '@/components/shell/AppTopbar.vue'
import AppFooter from '@/components/shell/AppFooter.vue'
import { useVersionInfo } from '@/composables/useVersionCheck'

const {
  appVersion,
  buildTime,
  backendRelease,
  backendGitCommit,
  versionMismatch,
  loadVersionInfo,
} = useVersionInfo()

onMounted(() => {
  void loadVersionInfo()
})
</script>

<template>
  <div class="layout">
    <div v-if="versionMismatch" class="version-banner" role="alert">
      前后端版本不一致：前端 v{{ appVersion }}，后端 v{{ backendRelease ?? '未知' }}。请重新部署匹配的发行包。
    </div>
    <AppTopbar />
    <main class="main">
      <router-view />
    </main>
    <AppFooter
      :app-version="appVersion"
      :build-time="buildTime"
      :backend-release="backendRelease"
      :backend-git-commit="backendGitCommit"
    />
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.version-banner {
  background: #fff3e0;
  border-bottom: 1px solid #f6980d;
  color: #8a4b00;
  font-size: 13px;
  padding: 8px 24px;
  text-align: center;
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
