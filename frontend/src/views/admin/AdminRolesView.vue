<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  canWriteRoles,
  createRole,
  listMenuPermissionTree,
  listRoles,
  replaceRolePermissions,
  type MenuNode,
  type RoleSummary,
} from '@/api/admin'
import MenuPermissionTree from '@/components/admin/MenuPermissionTree.vue'
import { ApiError } from '@/api/client'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

const loading = ref(false)
const roles = ref<RoleSummary[]>([])
const menuPermissionTree = ref<MenuNode[]>([])
const createDialogVisible = ref(false)
const editDrawerVisible = ref(false)
const submitting = ref(false)
const editingRole = ref<RoleSummary | null>(null)
const selectedPermissions = ref<string[]>([])

const createForm = reactive({
  roleCode: '',
  name: '',
  description: '',
})

const canCreate = computed(() => canWriteRoles(auth.permissions))
const canEditPermissions = computed(
  () => canWriteRoles(auth.permissions) && editingRole.value?.permissionsEditable,
)

async function loadRoles() {
  loading.value = true
  try {
    roles.value = await listRoles()
  } catch (err) {
    ElMessage.error(err instanceof ApiError ? err.message : '加载角色失败')
  } finally {
    loading.value = false
  }
}

async function loadMenuPermissionTree() {
  if (!auth.canAccessPath('/admin/roles')) {
    return
  }
  try {
    menuPermissionTree.value = await listMenuPermissionTree()
  } catch {
    // 无读权限时不阻塞列表
  }
}

function openCreateDialog() {
  createForm.roleCode = ''
  createForm.name = ''
  createForm.description = ''
  createDialogVisible.value = true
}

async function submitCreate() {
  if (!createForm.roleCode.trim() || !createForm.name.trim()) {
    ElMessage.warning('请填写角色 code 与名称')
    return
  }
  submitting.value = true
  try {
    await createRole({
      roleCode: createForm.roleCode.trim().toUpperCase(),
      name: createForm.name.trim(),
      description: createForm.description.trim() || undefined,
    })
    ElMessage.success('角色创建成功')
    createDialogVisible.value = false
    await loadRoles()
  } catch (err) {
    ElMessage.error(err instanceof ApiError ? err.message : '创建失败')
  } finally {
    submitting.value = false
  }
}

function openEditDrawer(role: RoleSummary) {
  editingRole.value = role
  selectedPermissions.value = [...role.permissionCodes]
  editDrawerVisible.value = true
}

async function submitPermissions() {
  if (!editingRole.value) {
    return
  }
  submitting.value = true
  try {
    await replaceRolePermissions(editingRole.value.roleCode, selectedPermissions.value)
    ElMessage.success('权限已更新（已登录用户需重新登录后生效）')
    editDrawerVisible.value = false
    await loadRoles()
  } catch (err) {
    ElMessage.error(err instanceof ApiError ? err.message : '保存失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadRoles(), loadMenuPermissionTree()])
})
</script>

<template>
  <div class="admin-roles">
    <header class="page-header">
      <div>
        <h1 class="page-title">角色管理</h1>
        <p class="page-desc">维护平台 RBAC 角色与 Permission（module:action）绑定；ADMIN 系统角色权限只读。</p>
      </div>
      <el-button v-if="canCreate" type="primary" @click="openCreateDialog">新建角色</el-button>
    </header>

    <el-table v-loading="loading" :data="roles" stripe class="role-table">
      <el-table-column prop="roleCode" label="角色 Code" width="140" />
      <el-table-column prop="name" label="名称" min-width="140" />
      <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
      <el-table-column label="Permission 数" width="120">
        <template #default="{ row }">{{ row.permissionCodes.length }}</template>
      </el-table-column>
      <el-table-column label="类型" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.systemRole" type="info" size="small">系统</el-tag>
          <el-tag v-else size="small">自定义</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEditDrawer(row)">
            {{ row.permissionsEditable && canCreate ? '编辑权限' : '查看权限' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="createDialogVisible" title="新建角色" width="480px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="角色 Code" required>
          <el-input v-model="createForm.roleCode" placeholder="如 AUDITOR（大写）" />
        </el-form-item>
        <el-form-item label="名称" required>
          <el-input v-model="createForm.name" placeholder="显示名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="createForm.description" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCreate">创建</el-button>
      </template>
    </el-dialog>

    <el-drawer
      v-model="editDrawerVisible"
      :title="editingRole ? `${editingRole.roleCode} · 权限` : '权限'"
      size="480px"
    >
      <p class="drawer-hint">按菜单勾选 Permission；同一菜单可绑定多个 Permission（OR 可见）。</p>
      <el-checkbox-group v-model="selectedPermissions" :disabled="!canEditPermissions">
        <MenuPermissionTree :nodes="menuPermissionTree" :disabled="!canEditPermissions" />
      </el-checkbox-group>
      <template v-if="canEditPermissions" #footer>
        <el-button @click="editDrawerVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitPermissions">保存</el-button>
      </template>
      <p v-if="!canEditPermissions" class="readonly-hint">ADMIN 系统角色权限不可修改。</p>
    </el-drawer>
  </div>
</template>

<style scoped>
.admin-roles {
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

.role-table {
  width: 100%;
}

.perm-row {
  display: block;
  margin-bottom: 8px;
}

.perm-meta {
  margin-left: 8px;
  font-size: 12px;
  color: var(--text-disabled);
}

.readonly-hint {
  margin-top: 16px;
  font-size: 13px;
  color: var(--text-secondary);
}

.drawer-hint {
  margin: 0 0 12px;
  font-size: 13px;
  color: var(--text-secondary);
}
</style>
