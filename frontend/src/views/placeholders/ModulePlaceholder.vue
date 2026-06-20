<script setup lang="ts">
import { computed } from 'vue'
import AppBreadcrumb from '@/components/shell/AppBreadcrumb.vue'
import PageHead from '@/components/shell/PageHead.vue'
import SectionTitle from '@/components/ui/SectionTitle.vue'
import FilterBar from '@/components/ui/FilterBar.vue'
import DataCard from '@/components/ui/DataCard.vue'
import StatusBadge from '@/components/ui/StatusBadge.vue'
import { SYSU_CLASSIFICATION } from '@/mocks/sysu-cls'
import { SYSU_ORG } from '@/mocks/sysu-org'

const props = defineProps<{
  title: string
  description?: string
  moduleLabel?: string
}>()

const breadcrumbItems = computed(() => [
  { label: '身份数据平台', to: '/' },
  ...(props.moduleLabel ? [{ label: props.moduleLabel }] : []),
  { label: props.title },
])
</script>

<template>
  <div class="placeholder">
    <AppBreadcrumb :items="breadcrumbItems" />
    <PageHead :title="title" :description="description" badge="占位页 · 待实现" />

    <SectionTitle>页面骨架预览</SectionTitle>
    <FilterBar>
      <input class="demo-input" type="text" placeholder="关键词搜索" disabled />
      <select class="demo-select" disabled>
        <option>全部类型</option>
      </select>
      <button class="demo-btn" type="button" disabled>查询</button>
    </FilterBar>

    <DataCard title="Mock 数据连通性" subtitle="来自 demo 原始 JS 迁移">
      <p class="mock-stats">
        分类节点 <strong>{{ SYSU_CLASSIFICATION.length }}</strong> 条 ·
        组织节点 <strong>{{ SYSU_ORG.length }}</strong> 条
      </p>
      <StatusBadge label="占位页" variant="warning" />
    </DataCard>
  </div>
</template>

<style scoped>
.mock-stats {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.mock-stats strong {
  color: var(--color-primary);
  font-family: 'DIN Alternate', 'Roboto', inherit;
}

.demo-input,
.demo-select {
  font-size: 14px;
  padding: 6px 12px;
  border: 1px solid var(--border);
  border-radius: 6px;
  background: var(--bg-secondary);
  color: var(--text-disabled);
  min-width: 160px;
}

.demo-btn {
  font-size: 14px;
  padding: 6px 16px;
  border-radius: 6px;
  background: var(--color-primary);
  color: white;
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
