<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import AppTopbar from '@/components/shell/AppTopbar.vue'
import AppFooter from '@/components/shell/AppFooter.vue'
import ModuleSidebar from '@/components/shell/ModuleSidebar.vue'
import { filterModuleNav, navForModule } from '@/config/module-nav'
import { useVersionInfo } from '@/composables/useVersionCheck'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const auth = useAuthStore()

const moduleKey = computed(() => {
  const matched = [...route.matched].reverse()
  for (const record of matched) {
    const key = record.meta.moduleKey
    if (typeof key === 'string') return key
  }
  return ''
})

const navConfig = computed(() => {
  const base = navForModule(moduleKey.value)
  if (!base || !auth.profile) {
    return base
  }
  return filterModuleNav(base, auth.profile.permissions)
})

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
    <div class="body">
      <ModuleSidebar v-if="navConfig" :config="navConfig" />
      <main class="main">
        <router-view />
      </main>
    </div>
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

.body {
  flex: 1;
  display: flex;
  min-height: 0;
  background: var(--bg-page);
}

.main {
  flex: 1;
  min-width: 0;
  padding: 24px;
  overflow: auto;
}
</style>
