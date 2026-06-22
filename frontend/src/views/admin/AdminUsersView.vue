<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  canWriteUsers,
  createUser,
  listOrgNodes,
  listUsers,
  type OrgNodeSummary,
  type UserSummary,
} from '@/api/admin'
import { ApiError } from '@/api/client'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

const loading = ref(false)
const users = ref<UserSummary[]>([])
const orgNodes = ref<OrgNodeSummary[]>([])
const dialogVisible = ref(false)
const submitting = ref(false)

const form = reactive({
  username: '',
  password: '',
  departmentCode: '',
  dataScope: 'OWN_DEPT',
})

const dataScopeOptions = [
  { label: '全局 (GLOBAL)', value: 'GLOBAL' },
  { label: '本部门 (OWN_DEPT)', value: 'OWN_DEPT' },
  { label: '本部门及下级 (OWN_DEPT_AND_SUB)', value: 'OWN_DEPT_AND_SUB' },
]

const canCreate = computed(() => canWriteUsers(auth.permissions))

const departmentOptions = computed(() =>
  orgNodes.value.map((node) => ({
    label: `${node.name} (${node.code})`,
    value: node.code,
  })),
)

async function loadUsers() {
  loading.value = true
  try {
    users.value = await listUsers()
  } catch (err) {
    ElMessage.error(err instanceof ApiError ? err.message : '加载用户列表失败')
  } finally {
    loading.value = false
  }
}

async function loadOrgNodes() {
  if (!canCreate.value) {
    return
  }
  try {
    orgNodes.value = await listOrgNodes()
  } catch {
    // 创建不可用时不阻塞列表
  }
}

function openCreateDialog() {
  form.username = ''
  form.password = ''
  form.departmentCode = ''
  form.dataScope = 'OWN_DEPT'
  dialogVisible.value = true
}

async function submitCreate() {
  if (!form.username.trim() || !form.password.trim() || !form.departmentCode) {
    ElMessage.warning('请填写完整信息')
    return
  }
  submitting.value = true
  try {
    await createUser({
      username: form.username.trim(),
      password: form.password,
      departmentCode: form.departmentCode,
      dataScope: form.dataScope,
    })
    ElMessage.success('平台用户创建成功')
    dialogVisible.value = false
    await loadUsers()
  } catch (err) {
    ElMessage.error(err instanceof ApiError ? err.message : '创建失败')
  } finally {
    submitting.value = false
  }
}

function formatDataScope(scope: string): string {
  const found = dataScopeOptions.find((item) => item.value === scope)
  return found?.label ?? scope
}

onMounted(async () => {
  await Promise.all([loadUsers(), loadOrgNodes()])
})
</script>

<template>
  <div class="admin-users">
    <header class="page-header">
      <div>
        <h1 class="page-title">平台用户</h1>
        <p class="page-desc">管理平台操作者登录账号（InteractiveUser），与业务自然人（PersonUID）无关。</p>
      </div>
      <el-button v-if="canCreate" type="primary" @click="openCreateDialog">新建用户</el-button>
    </header>

    <el-table v-loading="loading" :data="users" stripe class="user-table">
      <el-table-column prop="username" label="用户名" min-width="140" />
      <el-table-column prop="platformUserId" label="平台用户 ID" min-width="160" />
      <el-table-column prop="departmentCode" label="部门 Code" min-width="140" />
      <el-table-column label="数据范围" min-width="200">
        <template #default="{ row }">{{ formatDataScope(row.dataScope) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" />
    </el-table>

    <el-dialog v-model="dialogVisible" title="新建平台用户" width="480px" destroy-on-close>
      <el-form label-width="100px" @submit.prevent="submitCreate">
        <el-form-item label="用户名" required>
          <el-input v-model="form.username" autocomplete="off" placeholder="登录用户名" />
        </el-form-item>
        <el-form-item label="密码" required>
          <el-input v-model="form.password" type="password" show-password autocomplete="new-password" placeholder="初始密码" />
        </el-form-item>
        <el-form-item label="所属部门" required>
          <el-select v-model="form.departmentCode" filterable placeholder="选择 org_node" style="width: 100%">
            <el-option
              v-for="item in departmentOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数据范围" required>
          <el-select v-model="form.dataScope" style="width: 100%">
            <el-option
              v-for="item in dataScopeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCreate">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.admin-users {
  padding: 24px;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.page-desc {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--text-secondary);
}

.user-table {
  width: 100%;
}
</style>
