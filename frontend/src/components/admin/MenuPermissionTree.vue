<script setup lang="ts">
import type { MenuNode } from '@/api/admin'

defineOptions({ name: 'MenuPermissionTree' })

defineProps<{
  nodes: MenuNode[]
  disabled?: boolean
}>()
</script>

<template>
  <div v-for="node in nodes" :key="node.menuCode" class="menu-perm-node">
    <div v-if="node.menuType === 'GROUP'" class="menu-group">{{ node.label }}</div>
    <div v-else-if="node.menuType === 'LINK' && node.permissionCodes.length" class="menu-link-row">
      <div class="menu-link-label">{{ node.label }}</div>
      <div class="perm-checkboxes">
        <el-checkbox
          v-for="code in node.permissionCodes"
          :key="code"
          :value="code"
          :disabled="disabled"
        >{{ code }}</el-checkbox>
      </div>
    </div>
    <MenuPermissionTree
      v-if="node.children.length"
      :nodes="node.children"
      :disabled="disabled"
    />
  </div>
</template>

<style scoped>
.menu-group {
  margin: 12px 0 6px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.menu-link-row {
  margin-bottom: 10px;
  padding-left: 8px;
}

.menu-link-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.perm-checkboxes {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
</style>
