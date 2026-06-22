import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const ModulePlaceholder = () => import('@/views/placeholders/ModulePlaceholder.vue')

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/HomeView.vue'),
        },
      ],
    },
    {
      path: '/admin',
      component: () => import('@/layouts/ModuleLayout.vue'),
      children: [
        {
          path: 'users',
          name: 'platform-admin-users',
          component: () => import('@/views/admin/AdminUsersView.vue'),
          meta: { moduleKey: 'platform-admin' },
        },
      ],
    },
    {
      path: '/identity',
      component: () => import('@/layouts/ModuleLayout.vue'),
      children: [
        {
          path: 'basic',
          name: 'identity-basic',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-basic' },
          props: {
            title: '人员基础身份',
            description: '人员进档 · 一人一ID · 多源头采集',
            moduleLabel: '身份管理',
          },
        },
        {
          path: 'basic/changes',
          name: 'identity-basic-changes',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-basic' },
          props: {
            title: '变更记录',
            description: '人员基础身份变更历史',
            moduleLabel: '身份管理',
          },
        },
        {
          path: 'classification',
          name: 'identity-classification',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-classification' },
          props: {
            title: '分类树维护',
            description: '树形分类 · 标准属性 · 实例挂载',
            moduleLabel: '身份管理',
          },
        },
        {
          path: 'classification/mapping',
          name: 'identity-classification-mapping',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-classification' },
          props: {
            title: '分类映射管理',
            description: '分类与源头字段映射',
            moduleLabel: '身份管理',
          },
        },
        {
          path: 'classification/roster',
          name: 'identity-classification-roster',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-classification' },
          props: {
            title: '人员分类详细清单',
            description: '按分类维度查看在档人员',
            moduleLabel: '身份管理',
          },
        },
        {
          path: 'classification/changes',
          name: 'identity-classification-changes',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-classification' },
          props: {
            title: '变更记录',
            description: '分类身份变更历史',
            moduleLabel: '身份管理',
          },
        },
        {
          path: 'position',
          name: 'identity-position',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-position' },
          props: {
            title: '人员岗位身份',
            description: '广义岗位 · 一人多身份',
            moduleLabel: '身份管理',
          },
        },
        {
          path: 'org',
          name: 'identity-org',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-org' },
          props: {
            title: '组织树维护',
            description: '院系树 · 强绑定身份',
            moduleLabel: '身份管理',
          },
        },
        {
          path: 'org/mapping',
          name: 'identity-org-mapping',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-org' },
          props: {
            title: '组织映射管理',
            description: '组织与源头字段映射',
            moduleLabel: '身份管理',
          },
        },
        {
          path: 'org/changes',
          name: 'identity-org-changes',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-org' },
          props: {
            title: '变更记录',
            description: '组织机构变更历史',
            moduleLabel: '身份管理',
          },
        },
        {
          path: 'tags',
          name: 'identity-tags',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-tags' },
          props: {
            title: '自定义标签身份',
            description: '自定义群组 · 灵活标注 · 便捷查询',
            moduleLabel: '身份管理',
          },
        },
        {
          path: 'permission',
          name: 'identity-permission',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-permission' },
          props: {
            title: '权限矩阵',
            description: '权限项 · 类型授权 · 状态联动',
            moduleLabel: '权限管理',
          },
        },
        {
          path: 'permission/catalog',
          name: 'identity-permission-catalog',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-permission' },
          props: {
            title: '权限项目录',
            description: '权限项主数据维护',
            moduleLabel: '权限管理',
          },
        },
        {
          path: 'permission/reconcile',
          name: 'identity-permission-reconcile',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-permission' },
          props: {
            title: '对账视图',
            description: '跨系统权限对账',
            moduleLabel: '权限管理',
          },
        },
        {
          path: 'permission/zombie',
          name: 'identity-permission-zombie',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-permission' },
          props: {
            title: '僵尸权限',
            description: '无效或过期权限识别',
            moduleLabel: '权限管理',
          },
        },
        {
          path: 'permission/tasks',
          name: 'identity-permission-tasks',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-permission' },
          props: {
            title: '检查任务',
            description: '权限质量检查任务',
            moduleLabel: '权限管理',
          },
        },
        {
          path: 'permission/rules',
          name: 'identity-permission-rules',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-permission' },
          props: {
            title: '规则配置',
            description: '权限质量检查规则',
            moduleLabel: '权限管理',
          },
        },
        {
          path: 'permission/runs',
          name: 'identity-permission-runs',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-permission' },
          props: {
            title: '运行记录',
            description: '质量检查运行历史',
            moduleLabel: '权限管理',
          },
        },
        {
          path: 'permission/sources',
          name: 'identity-permission-sources',
          component: ModulePlaceholder,
          meta: { moduleKey: 'identity-permission' },
          props: {
            title: '数据源',
            description: '权限质量检查数据源',
            moduleLabel: '权限管理',
          },
        },
      ],
    },
    {
      path: '/services',
      component: () => import('@/layouts/ModuleLayout.vue'),
      children: [
        {
          path: 'query',
          redirect: '/services/query/identity',
        },
        {
          path: 'query/identity',
          name: 'services-query-identity',
          component: ModulePlaceholder,
          meta: { moduleKey: 'services-query' },
          props: {
            title: '身份表查询',
            description: '预置身份主题表查询',
            moduleLabel: '系统服务',
          },
        },
        {
          path: 'query/theme',
          name: 'services-query-theme',
          component: ModulePlaceholder,
          meta: { moduleKey: 'services-query' },
          props: {
            title: '主题表查询',
            description: '业务主题宽表查询',
            moduleLabel: '系统服务',
          },
        },
        {
          path: 'query/sql',
          name: 'services-query-sql',
          component: ModulePlaceholder,
          meta: { moduleKey: 'services-query' },
          props: {
            title: 'SQL 查询',
            description: '自定义 SQL 数据查询',
            moduleLabel: '系统服务',
          },
        },
        {
          path: 'etl',
          name: 'services-etl',
          component: ModulePlaceholder,
          meta: { moduleKey: 'services-etl' },
          props: {
            title: 'ETL 监控',
            description: '数据接入与同步任务监控',
            moduleLabel: '系统服务',
          },
        },
        {
          path: 'sources',
          name: 'services-sources',
          component: ModulePlaceholder,
          meta: { moduleKey: 'services-sources' },
          props: {
            title: '源头维护',
            description: '数据源头系统配置与维护',
            moduleLabel: '系统服务',
          },
        },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  auth.bindTokenGetter()

  if (to.meta.public && auth.isAuthenticated && to.path === '/login') {
    return { path: '/' }
  }

  if (!to.meta.public) {
    if (!auth.isAuthenticated) {
      return { path: '/login' }
    }
    if (!auth.profile) {
      const ok = await auth.restoreSession()
      if (!ok) {
        return { path: '/login' }
      }
    }
    const moduleKey = to.meta.moduleKey
    if (typeof moduleKey === 'string' && !auth.canAccessModule(moduleKey)) {
      return { path: '/' }
    }
  }

  return true
})

export default router
