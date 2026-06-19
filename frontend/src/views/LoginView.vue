<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import BrandLogo from '@/components/shell/BrandLogo.vue'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

async function handleLogin() {
  loading.value = true
  try {
    const ok = auth.login(form.username, form.password)
    if (!ok) {
      ElMessage.warning('请输入用户名和密码')
      return
    }
    await router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-shell">
      <div class="login-brand">
        <BrandLogo compact />
        <h1 class="login-platform-title">高校综合身份数据平台</h1>
        <p class="login-platform-desc">
          解决高校各类人员的数据身份管理、权限管理、综合查询等核心业务
        </p>
      </div>

      <div class="login-card">
        <h2 class="login-title">账号登录</h2>
        <p class="login-hint">Mock 模式：任意非空用户名和密码即可登录</p>

        <el-form class="login-form" label-position="top" @submit.prevent="handleLogin">
          <el-form-item label="用户名">
            <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              show-password
              size="large"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-button
            class="login-btn"
            type="primary"
            size="large"
            native-type="submit"
            :loading="loading"
          >
            登录
          </el-button>
        </el-form>
      </div>
    </div>

    <footer class="login-footer">
      <span>© 高校身份数据平台 · 数据治理</span>
      <span>v1.0 · 浅色规范 v1.0</span>
    </footer>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-page);
  padding: 24px;
}

.login-shell {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 80px;
  max-width: 960px;
  width: 100%;
  margin: 0 auto;
}

.login-brand {
  flex: 1;
  max-width: 400px;
}

.login-platform-title {
  margin-top: 24px;
  font-size: 28px;
  font-weight: 600;
  line-height: 36px;
  color: var(--text-primary);
}

.login-platform-desc {
  margin-top: 12px;
  font-size: 14px;
  line-height: 22px;
  color: var(--text-secondary);
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 32px;
  box-shadow: var(--shadow-2);
}

.login-title {
  font-size: 20px;
  font-weight: 600;
  line-height: 28px;
  color: var(--text-primary);
}

.login-hint {
  margin-top: 8px;
  font-size: 12px;
  color: var(--text-disabled);
}

.login-form {
  margin-top: 24px;
}

.login-form :deep(.el-form-item__label) {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
  padding-bottom: 4px;
}

.login-btn {
  width: 100%;
  margin-top: 8px;
}

.login-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--text-disabled);
  padding-top: 24px;
  max-width: 960px;
  width: 100%;
  margin: 0 auto;
  border-top: 1px solid var(--border);
}

@media (max-width: 768px) {
  .login-shell {
    flex-direction: column;
    gap: 32px;
  }

  .login-brand {
    text-align: center;
    max-width: none;
  }

  .login-platform-title {
    font-size: 22px;
    line-height: 30px;
  }
}
</style>
