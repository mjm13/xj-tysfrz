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
    <div class="login-card">
      <BrandLogo compact />
      <h1 class="login-title">登录指标平台</h1>
      <p class="login-hint">Mock 模式：任意非空用户名和密码即可登录</p>

      <el-form class="login-form" @submit.prevent="handleLogin">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-button
          class="login-btn"
          type="primary"
          native-type="submit"
          :loading="loading"
        >
          登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--gray-50) 0%, var(--login-bg-end) 100%);
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border: 1px solid var(--gray-200);
  border-radius: var(--radius-lg);
  padding: 36px 32px 32px;
  box-shadow: var(--shadow-md);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-title {
  margin-top: 20px;
  font-size: 18px;
  font-weight: 600;
  color: var(--gray-900);
}

.login-hint {
  margin-top: 6px;
  font-size: 11.5px;
  color: var(--gray-500);
  text-align: center;
}

.login-form {
  width: 100%;
  margin-top: 24px;
}

.login-btn {
  width: 100%;
  margin-top: 8px;
}
</style>
