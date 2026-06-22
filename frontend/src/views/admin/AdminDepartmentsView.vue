<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage, ElTree } from 'element-plus'
import type { LoadFunction } from 'element-plus/es/components/tree/src/tree.type'
import {
  canWriteDepartments,
  createOrgNode,
  listOrgNodeChildren,
  listOrgNodeRoots,
  updateOrgNode,
  type OrgNodeSummary,
} from '@/api/admin'
import { ApiError } from '@/api/client'
import { useAuthStore } from '@/stores/auth'

interface TreeNodeData {
  code: string
  name: string
  leaf: boolean
}

const auth = useAuthStore()
const treeRef = ref<InstanceType<typeof ElTree>>()
const selectedNode = ref<TreeNodeData | null>(null)
const createDialogVisible = ref(false)
const editDialogVisible = ref(false)
const submitting = ref(false)

const createForm = reactive({
  code: '',
  name: '',
})

const editForm = reactive({
  name: '',
})

const canCreate = computed(() => canWriteDepartments(auth.permissions))
const canEdit = computed(() => canWriteDepartments(auth.permissions) && selectedNode.value != null)

function toTreeData(node: OrgNodeSummary): TreeNodeData {
  return {
    code: node.code,
    name: node.name,
    leaf: node.hasChildren === false,
  }
}

const loadNode: LoadFunction = async (node, resolve) => {
  try {
    if (node.level === 0) {
      const roots = await listOrgNodeRoots()
      resolve(roots.map(toTreeData))
      return
    }
    const parentCode = (node.data as TreeNodeData).code
    const children = await listOrgNodeChildren(parentCode)
    resolve(children.map(toTreeData))
  } catch (err) {
    ElMessage.error(err instanceof ApiError ? err.message : '加载组织节点失败')
    resolve([])
  }
}

function handleNodeClick(data: TreeNodeData) {
  selectedNode.value = data
}

function openCreateDialog() {
  if (!selectedNode.value) {
    ElMessage.warning('请先在树中选择一个父节点')
    return
  }
  createForm.code = ''
  createForm.name = ''
  createDialogVisible.value = true
}

function openEditDialog() {
  if (!selectedNode.value) {
    return
  }
  editForm.name = selectedNode.value.name
  editDialogVisible.value = true
}

async function submitCreate() {
  if (!selectedNode.value) {
    return
  }
  if (!createForm.code.trim() || !createForm.name.trim()) {
    ElMessage.warning('请填写节点 code 与名称')
    return
  }
  submitting.value = true
  try {
    await createOrgNode({
      code: createForm.code.trim(),
      name: createForm.name.trim(),
      parentCode: selectedNode.value.code,
    })
    ElMessage.success('组织节点创建成功')
    createDialogVisible.value = false
    treeRef.value?.getNode(selectedNode.value.code)?.expand()
    treeRef.value?.updateKeyChildren(selectedNode.value.code, [])
    const children = await listOrgNodeChildren(selectedNode.value.code)
    treeRef.value?.updateKeyChildren(selectedNode.value.code, children.map(toTreeData))
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
  if (!editForm.name.trim()) {
    ElMessage.warning('请填写名称')
    return
  }
  submitting.value = true
  try {
    const updated = await updateOrgNode(selectedNode.value.code, { name: editForm.name.trim() })
    selectedNode.value.name = updated.name
    const treeNode = treeRef.value?.getNode(selectedNode.value.code)
    if (treeNode) {
      treeNode.data.name = updated.name
    }
    ElMessage.success('组织节点已更新')
    editDialogVisible.value = false
  } catch (err) {
    ElMessage.error(err instanceof ApiError ? err.message : '更新失败')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="admin-departments">
    <header class="page-header">
      <div>
        <h1 class="page-title">部门管理</h1>
        <p class="page-desc">维护 org_node 组织主数据；懒加载树展示，支持新建与编辑（无删除）。</p>
      </div>
      <div class="header-actions">
        <el-button v-if="canEdit" :disabled="!selectedNode" @click="openEditDialog">编辑节点</el-button>
        <el-button v-if="canCreate" type="primary" :disabled="!selectedNode" @click="openCreateDialog">
          新建子节点
        </el-button>
      </div>
    </header>

    <div class="tree-panel">
      <p v-if="selectedNode" class="selection-hint">已选：{{ selectedNode.code }} · {{ selectedNode.name }}</p>
      <p v-else class="selection-hint">点击树节点以选中，再新建或编辑</p>
      <el-tree
        ref="treeRef"
        node-key="code"
        lazy
        :load="loadNode"
        highlight-current
        :props="{ label: 'name', isLeaf: 'leaf' }"
        @node-click="handleNodeClick"
      />
    </div>

    <el-dialog v-model="createDialogVisible" title="新建组织节点" width="480px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="父节点">
          <span>{{ selectedNode?.code }} · {{ selectedNode?.name }}</span>
        </el-form-item>
        <el-form-item label="节点 Code" required>
          <el-input v-model="createForm.code" placeholder="如 NEW-DEPT-01" />
        </el-form-item>
        <el-form-item label="名称" required>
          <el-input v-model="createForm.name" placeholder="显示名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCreate">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" title="编辑组织节点" width="480px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="节点 Code">
          <span>{{ selectedNode?.code }}</span>
        </el-form-item>
        <el-form-item label="名称" required>
          <el-input v-model="editForm.name" />
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
.admin-departments {
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

.header-actions {
  display: flex;
  gap: 8px;
}

.tree-panel {
  max-width: 640px;
  padding: 16px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg-card);
}

.selection-hint {
  margin: 0 0 12px;
  font-size: 13px;
  color: var(--text-secondary);
}
</style>
