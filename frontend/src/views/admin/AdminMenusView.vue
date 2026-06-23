<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  canWriteMenus,
  createMenu,
  listMenus,
  listPermissions,
  updateMenu,
  updateMenuVisible,
  type MenuNode,
  type PermissionSummary,
} from '@/api/admin'
import { ApiError } from '@/api/client'
import { useAuthStore } from '@/stores/auth'
import { useNavigationStore } from '@/stores/navigation'

const auth = useAuthStore()
const navigation = useNavigationStore()

const loading = ref(false)
const menuTree = ref<MenuNode[]>([])
const allPermissions = ref<PermissionSummary[]>([])
const selectedNode = ref<MenuNode | null>(null)
const createDialogVisible = ref(false)
const editDialogVisible = ref(false)
const submitting = ref(false)

const createForm = reactive({
  menuCode: '',
  label: '',
  path: '',
  parentCode: '',
  sortOrder: 0,
  menuType: 'LINK' as 'LINK' | 'GROUP',
  moduleKey: '',
  description: '',
  permissionCodes: [] as string[],
})

const editForm = reactive({
  label: '',
  path: '',
  parentCode: '',
  sortOrder: 0,
  menuType: 'LINK' as 'LINK' | 'GROUP',
  moduleKey: '',
  description: '',
  permissionCodes: [] as string[],
})

const canWrite = computed(() => canWriteMenus(auth.permissions))

const treeProps = {
  children: 'children',
  label: 'label',
}

async function loadMenus() {
  loading.value = true
  try {
    menuTree.value = await listMenus()
  } catch (err) {
    ElMessage.error(err instanceof ApiError ? err.message : '加载菜单失败')
  } finally {
    loading.value = false
  }
}

async function loadPermissionCatalog() {
  try {
    allPermissions.value = await listPermissions()
  } catch {
    // 无读权限时不阻塞
  }
}

function handleNodeClick(data: MenuNode) {
  selectedNode.value = data
}

function openCreateDialog() {
  createForm.menuCode = ''
  createForm.label = ''
  createForm.path = ''
  createForm.parentCode = selectedNode.value?.menuCode ?? ''
  createForm.sortOrder = 0
  createForm.menuType = 'LINK'
  createForm.moduleKey = selectedNode.value?.moduleKey ?? ''
  createForm.description = ''
  createForm.permissionCodes = []
  createDialogVisible.value = true
}

function openEditDialog() {
  if (!selectedNode.value) {
    return
  }
  editForm.label = selectedNode.value.label
  editForm.path = selectedNode.value.path ?? ''
  editForm.parentCode = selectedNode.value.parentCode ?? ''
  editForm.sortOrder = selectedNode.value.sortOrder
  editForm.menuType = selectedNode.value.menuType as 'LINK' | 'GROUP'
  editForm.moduleKey = selectedNode.value.moduleKey ?? ''
  editForm.description = selectedNode.value.description ?? ''
  editForm.permissionCodes = [...selectedNode.value.permissionCodes]
  editDialogVisible.value = true
}

async function submitCreate() {
  if (!createForm.menuCode.trim() || !createForm.label.trim()) {
    ElMessage.warning('请填写 menuCode 与名称')
    return
  }
  submitting.value = true
  try {
    await createMenu({
      menuCode: createForm.menuCode.trim(),
      label: createForm.label.trim(),
      path: createForm.menuType === 'LINK' ? createForm.path.trim() || null : null,
      parentCode: createForm.parentCode.trim() || null,
      sortOrder: createForm.sortOrder,
      menuType: createForm.menuType,
      moduleKey: createForm.moduleKey.trim() || null,
      description: createForm.description.trim() || null,
      permissionCodes: createForm.menuType === 'LINK' ? createForm.permissionCodes : [],
    })
    ElMessage.success('菜单已创建')
    createDialogVisible.value = false
    await refreshNavigation()
  } catch (err) {
    ElMessage.error(err instanceof ApiError ? err.message : '创建失败')
  } finally {
    submitting.value = false
  }
}

async function submitEdit() {
  if (!selectedNode.value) {
    return
  }
  submitting.value = true
  try {
    const updated = await updateMenu(selectedNode.value.menuCode, {
      label: editForm.label.trim(),
      path: editForm.menuType === 'LINK' ? editForm.path.trim() || null : null,
      parentCode: editForm.parentCode.trim() || null,
      sortOrder: editForm.sortOrder,
      menuType: editForm.menuType,
      moduleKey: editForm.moduleKey.trim() || null,
      description: editForm.description.trim() || null,
      permissionCodes: editForm.menuType === 'LINK' ? editForm.permissionCodes : [],
    })
    selectedNode.value = updated
    ElMessage.success('菜单已更新')
    editDialogVisible.value = false
    await refreshNavigation()
  } catch (err) {
    ElMessage.error(err instanceof ApiError ? err.message : '更新失败')
  } finally {
    submitting.value = false
  }
}

async function toggleVisible() {
  if (!selectedNode.value || !canWrite.value) {
    return
  }
  submitting.value = true
  try {
    const nextVisible = !selectedNode.value.visible
    const updated = await updateMenuVisible(selectedNode.value.menuCode, nextVisible)
    selectedNode.value = updated
    ElMessage.success(nextVisible ? '已显示' : '已隐藏')
    await refreshNavigation()
  } catch (err) {
    ElMessage.error(err instanceof ApiError ? err.message : '操作失败')
  } finally {
    submitting.value = false
  }
}

async function refreshNavigation() {
  await loadMenus()
  if (auth.isAuthenticated) {
    await navigation.loadNavigation()
  }
}

onMounted(async () => {
  await Promise.all([loadMenus(), loadPermissionCatalog()])
})
</script>

<template>
  <div class="admin-menus">
    <header class="page-header">
      <div>
        <h1 class="page-title">菜单管理</h1>
        <p class="page-desc">维护 platform_menu 导航树；LINK 菜单通过 platform_menu_permission 绑定多个 Permission。</p>
      </div>
      <el-button v-if="canWrite" type="primary" @click="openCreateDialog">新建菜单</el-button>
    </header>

    <div class="content">
      <el-tree
        v-loading="loading"
        :data="menuTree"
        node-key="menuCode"
        default-expand-all
        highlight-current
        :props="treeProps"
        class="menu-tree"
        @node-click="handleNodeClick"
      />

      <aside v-if="selectedNode" class="detail-panel">
        <h2 class="detail-title">{{ selectedNode.label }}</h2>
        <dl class="detail-list">
          <dt>Code</dt><dd>{{ selectedNode.menuCode }}</dd>
          <dt>类型</dt><dd>{{ selectedNode.menuType }}</dd>
          <dt>路径</dt><dd>{{ selectedNode.path || '—' }}</dd>
          <dt>模块</dt><dd>{{ selectedNode.moduleKey || '—' }}</dd>
          <dt>可见</dt><dd>{{ selectedNode.visible ? '是' : '否' }}</dd>
          <dt>Permission</dt>
          <dd>
            <span v-if="!selectedNode.permissionCodes.length">—</span>
            <el-tag v-for="code in selectedNode.permissionCodes" :key="code" size="small" class="perm-tag">{{ code }}</el-tag>
          </dd>
        </dl>
        <div v-if="canWrite" class="detail-actions">
          <el-button @click="openEditDialog">编辑</el-button>
          <el-button :loading="submitting" @click="toggleVisible">
            {{ selectedNode.visible ? '隐藏' : '显示' }}
          </el-button>
        </div>
      </aside>
    </div>

    <el-dialog v-model="createDialogVisible" title="新建菜单" width="560px" destroy-on-close>
      <el-form label-width="110px">
        <el-form-item label="Menu Code" required>
          <el-input v-model="createForm.menuCode" placeholder="如 nav.top.platform.menus" />
        </el-form-item>
        <el-form-item label="名称" required>
          <el-input v-model="createForm.label" />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="createForm.menuType">
            <el-radio value="LINK">LINK</el-radio>
            <el-radio value="GROUP">GROUP</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="createForm.menuType === 'LINK'" label="路径" required>
          <el-input v-model="createForm.path" placeholder="/admin/menus" />
        </el-form-item>
        <el-form-item label="父菜单 Code">
          <el-input v-model="createForm.parentCode" placeholder="留空为顶级" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="createForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="Module Key">
          <el-input v-model="createForm.moduleKey" placeholder="侧栏菜单填写 moduleKey" />
        </el-form-item>
        <el-form-item v-if="createForm.menuType === 'LINK'" label="Permission">
          <el-select v-model="createForm.permissionCodes" multiple filterable placeholder="至少选一个">
            <el-option
              v-for="perm in allPermissions"
              :key="perm.permissionCode"
              :label="perm.permissionCode"
              :value="perm.permissionCode"
            />
          </el-select>
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

    <el-dialog v-model="editDialogVisible" title="编辑菜单" width="560px" destroy-on-close>
      <el-form label-width="110px">
        <el-form-item label="名称" required>
          <el-input v-model="editForm.label" />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="editForm.menuType">
            <el-radio value="LINK">LINK</el-radio>
            <el-radio value="GROUP">GROUP</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="editForm.menuType === 'LINK'" label="路径" required>
          <el-input v-model="editForm.path" />
        </el-form-item>
        <el-form-item label="父菜单 Code">
          <el-input v-model="editForm.parentCode" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="editForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="Module Key">
          <el-input v-model="editForm.moduleKey" />
        </el-form-item>
        <el-form-item v-if="editForm.menuType === 'LINK'" label="Permission">
          <el-select v-model="editForm.permissionCodes" multiple filterable>
            <el-option
              v-for="perm in allPermissions"
              :key="perm.permissionCode"
              :label="perm.permissionCode"
              :value="perm.permissionCode"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.admin-menus {
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
}

.page-desc {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--text-secondary);
}

.content {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.menu-tree {
  flex: 1;
  min-width: 280px;
  padding: 12px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;
}

.detail-panel {
  width: 320px;
  padding: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;
}

.detail-title {
  margin: 0 0 12px;
  font-size: 16px;
}

.detail-list {
  margin: 0;
}

.detail-list dt {
  font-size: 12px;
  color: var(--text-disabled);
  margin-top: 10px;
}

.detail-list dd {
  margin: 4px 0 0;
  font-size: 14px;
}

.perm-tag {
  margin: 0 6px 6px 0;
}

.detail-actions {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}
</style>
