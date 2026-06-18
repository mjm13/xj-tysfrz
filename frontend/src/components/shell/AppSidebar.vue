<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import BrandLogo from './BrandLogo.vue'

const router = useRouter()
const auth = useAuthStore()

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <aside class="sidebar">
    <div class="side-brand">
      <BrandLogo />
    </div>

    <nav class="side-nav">
      <slot>
        <p class="side-empty">菜单即将上线</p>
      </slot>
    </nav>

    <div class="side-footer">
      <span v-if="auth.username" class="side-user">{{ auth.username }}</span>
      <button class="logout-btn" type="button" @click="handleLogout">退出登录</button>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 232px;
  flex-shrink: 0;
  background: #fff;
  border-right: 1px solid var(--gray-200);
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.side-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 18px 14px 14px;
  margin-bottom: 4px;
  border-bottom: 1px solid var(--gray-100);
}

.side-nav {
  flex: 1;
  overflow-y: auto;
  padding: 8px 10px;
}

.side-empty {
  padding: 10px 12px;
  font-size: 12px;
  color: var(--gray-400);
}

.side-footer {
  padding: 12px 14px;
  border-top: 1px solid var(--gray-100);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.side-user {
  font-size: 12px;
  color: var(--gray-600);
}

.logout-btn {
  align-self: flex-start;
  font-size: 12.5px;
  color: var(--rose);
  padding: 4px 0;
}

.logout-btn:hover {
  text-decoration: underline;
}
</style>
