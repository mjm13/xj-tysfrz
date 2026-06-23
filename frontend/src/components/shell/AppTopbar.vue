<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { MenuNode } from '@/api/admin'
import { useAuthStore } from '@/stores/auth'
import { useNavigationStore } from '@/stores/navigation'
import BrandLogo from './BrandLogo.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const navigation = useNavigationStore()

const nowText = ref('')
let timer: ReturnType<typeof setInterval> | undefined

const topBar = computed(() => navigation.topBar)

const avatarChar = computed(() => (auth.username ? auth.username.charAt(0).toUpperCase() : '管'))

function isLinkActive(path: string | null): boolean {
  if (!path) {
    return false
  }
  if (path === '/') {
    return route.path === '/'
  }
  return route.path === path || route.path.startsWith(`${path}/`)
}

function isGroupActive(node: MenuNode): boolean {
  if (node.menuType === 'LINK') {
    return isLinkActive(node.path)
  }
  return node.children.some((child) => isGroupActive(child))
}

function updateTime() {
  const now = new Date()
  const pad = (n: number) => String(n).padStart(2, '0')
  nowText.value = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
}

async function handleLogout() {
  await auth.logout()
  router.push('/login')
}

onMounted(async () => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  if (auth.isAuthenticated && !navigation.loaded) {
    try {
      await navigation.loadNavigation()
    } catch {
      // 导航加载失败不阻塞顶栏
    }
  }
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<template>
  <header class="topbar">
    <BrandLogo />

    <nav class="topbar-nav">
      <template v-for="node in topBar" :key="node.menuCode">
        <RouterLink
          v-if="node.menuType === 'LINK' && node.path"
          :to="node.path"
          class="nav-link"
          :class="{ active: isLinkActive(node.path) }"
        >{{ node.label }}</RouterLink>

        <div
          v-else-if="node.menuType === 'GROUP' && node.children.length"
          class="nav-dropdown"
          :class="{ active: isGroupActive(node) }"
        >
          <span class="nav-dropdown-trigger" :class="{ active: isGroupActive(node) }">{{ node.label }}</span>
          <div class="nav-dropdown-menu">
            <RouterLink
              v-for="item in node.children"
              :key="item.menuCode"
              :to="item.path ?? '/'"
              class="dropdown-item"
            >
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <circle cx="12" cy="8" r="4" />
                <path d="M4 21c0-4 4-7 8-7s8 3 8 7" />
              </svg>
              <div class="menu-body">
                <div class="menu-label">{{ item.label }}</div>
                <div v-if="item.description" class="menu-desc">{{ item.description }}</div>
              </div>
            </RouterLink>
          </div>
        </div>
      </template>
    </nav>

    <div class="topbar-right">
      <span class="time-display">{{ nowText }}</span>
      <div class="user-info">
        <div class="user-avatar">{{ avatarChar }}</div>
        <span class="user-name">{{ auth.username || '管理员' }} · 数据治理岗</span>
      </div>
      <button class="logout-btn" type="button" @click="handleLogout">退出</button>
    </div>
  </header>
</template>

<style scoped>
.topbar {
  height: 64px;
  background: var(--bg-card);
  box-shadow: var(--shadow-1);
  display: flex;
  align-items: center;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 100;
  gap: 32px;
}

.topbar-nav {
  display: flex;
  gap: 32px;
  flex: 1;
  height: 100%;
  align-items: center;
  min-width: 0;
}

.nav-link {
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 400;
  height: 100%;
  display: flex;
  align-items: center;
  position: relative;
  transition: color 200ms;
  white-space: nowrap;
}

.nav-link:hover {
  color: var(--color-primary);
}

.nav-link.active {
  color: var(--color-primary);
  font-weight: 500;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 2px;
  background: var(--color-primary);
}

.nav-dropdown {
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
}

.nav-dropdown-trigger.active {
  color: var(--color-primary);
  font-weight: 500;
}

.nav-dropdown-trigger.active::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 2px;
  background: var(--color-primary);
}

.nav-dropdown-trigger {
  color: var(--text-secondary);
  font-size: 14px;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  position: relative;
  transition: color 200ms;
  white-space: nowrap;
}

.nav-dropdown-trigger::after {
  content: '';
  width: 6px;
  height: 6px;
  border-right: 1.5px solid currentColor;
  border-bottom: 1.5px solid currentColor;
  transform: rotate(45deg);
  margin-left: 4px;
  margin-bottom: 3px;
  transition: transform 200ms;
  opacity: 0.6;
}

.nav-dropdown:hover .nav-dropdown-trigger {
  color: var(--color-primary);
}

.nav-dropdown:hover .nav-dropdown-trigger::after {
  transform: rotate(-135deg);
  margin-bottom: -3px;
  opacity: 1;
}

.nav-dropdown-menu {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%) translateY(-4px);
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;
  box-shadow: var(--shadow-2);
  min-width: 220px;
  padding: 8px;
  opacity: 0;
  visibility: hidden;
  transition: all 200ms;
  z-index: 200;
}

.nav-dropdown:hover .nav-dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(0);
}

.nav-dropdown-menu::before {
  content: '';
  position: absolute;
  top: -8px;
  left: 0;
  right: 0;
  height: 8px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 6px;
  color: var(--text-primary);
  font-size: 14px;
  transition: all 150ms;
}

.dropdown-item:hover {
  background: rgba(37, 99, 235, 0.06);
  color: var(--color-primary);
}

.dropdown-item svg {
  width: 18px;
  height: 18px;
  stroke: currentColor;
  fill: none;
  stroke-width: 1.8;
  flex-shrink: 0;
  opacity: 0.6;
}

.dropdown-item:hover svg {
  opacity: 1;
}

.menu-body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.menu-label {
  font-size: 14px;
  line-height: 18px;
}

.menu-desc {
  font-size: 11px;
  color: var(--text-disabled);
  margin-top: 2px;
  line-height: 14px;
}

.dropdown-item:hover .menu-desc {
  color: var(--color-primary-secondary);
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.time-display {
  font-size: 12px;
  color: var(--text-disabled);
  font-family: 'DIN Alternate', 'Roboto', monospace;
  white-space: nowrap;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 6px;
  border: 1px solid var(--border);
}

.user-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--color-primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 500;
}

.user-name {
  font-size: 14px;
  color: var(--text-secondary);
  white-space: nowrap;
}

.logout-btn {
  font-size: 12px;
  color: var(--text-disabled);
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 150ms;
}

.logout-btn:hover {
  color: var(--color-error);
  background: rgba(245, 63, 63, 0.06);
}
</style>
