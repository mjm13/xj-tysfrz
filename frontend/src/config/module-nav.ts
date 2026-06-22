import { canAccessPath } from '@/config/permissions'

export interface SidebarItem {
  label: string
  path?: string
  badge?: string
  badgeVariant?: 'default' | 'warn'
  disabled?: boolean
}

export interface SidebarGroup {
  label: string
  items: SidebarItem[]
}

export interface ModuleNavConfig {
  moduleKey: string
  groups: SidebarGroup[]
}

const PERSONNEL_GROUP: SidebarGroup = {
  label: '人员管理',
  items: [
    { label: '人员基础身份', path: '/identity/basic' },
    { label: '人员分类身份', path: '/identity/classification' },
    { label: '人员岗位身份', path: '/identity/position' },
    { label: '自定义标签身份', path: '/identity/tags' },
    { label: '组织机构体系', path: '/identity/org' },
  ],
}

export const MODULE_NAV: Record<string, ModuleNavConfig> = {
  'platform-admin': {
    moduleKey: 'platform-admin',
    groups: [
      {
        label: '平台管理',
        items: [
          { label: '平台用户', path: '/admin/users' },
          { label: '角色管理', path: '/admin/roles' },
          { label: '部门管理', path: '/admin/departments' },
        ],
      },
    ],
  },
  'identity-basic': {
    moduleKey: 'identity-basic',
    groups: [
      PERSONNEL_GROUP,
      {
        label: '人员基础身份管理',
        items: [
          { label: '数据查询', path: '/identity/basic' },
          { label: 'ETL 监控', path: '/services/etl' },
          { label: '变更记录', path: '/identity/basic/changes', badge: '待开发', badgeVariant: 'warn' },
        ],
      },
    ],
  },
  'identity-classification': {
    moduleKey: 'identity-classification',
    groups: [
      PERSONNEL_GROUP,
      {
        label: '分类管理',
        items: [
          { label: '分类树维护', path: '/identity/classification' },
          { label: '分类映射管理', path: '/identity/classification/mapping' },
          { label: '人员分类详细清单', path: '/identity/classification/roster' },
          { label: '变更记录', path: '/identity/classification/changes' },
        ],
      },
    ],
  },
  'identity-position': {
    moduleKey: 'identity-position',
    groups: [
      PERSONNEL_GROUP,
      {
        label: '岗位身份管理',
        items: [{ label: '岗位身份列表', path: '/identity/position' }],
      },
    ],
  },
  'identity-tags': {
    moduleKey: 'identity-tags',
    groups: [
      PERSONNEL_GROUP,
      {
        label: '标签管理',
        items: [{ label: '自定义标签', path: '/identity/tags' }],
      },
    ],
  },
  'identity-org': {
    moduleKey: 'identity-org',
    groups: [
      PERSONNEL_GROUP,
      {
        label: 'M4 子功能',
        items: [
          { label: '组织树维护', path: '/identity/org' },
          { label: '组织映射管理', path: '/identity/org/mapping' },
          { label: '变更记录', path: '/identity/org/changes' },
        ],
      },
    ],
  },
  'identity-permission': {
    moduleKey: 'identity-permission',
    groups: [
      {
        label: '权限项主数据',
        items: [{ label: '权限项目录', path: '/identity/permission/catalog' }],
      },
      {
        label: '权限数据查看',
        items: [
          { label: '权限矩阵', path: '/identity/permission' },
          { label: '对账视图', path: '/identity/permission/reconcile' },
          { label: '僵尸权限', path: '/identity/permission/zombie' },
        ],
      },
      {
        label: '权限质量检查',
        items: [
          { label: '检查任务', path: '/identity/permission/tasks' },
          { label: '规则配置', path: '/identity/permission/rules' },
          { label: '运行记录', path: '/identity/permission/runs' },
          { label: '数据源', path: '/identity/permission/sources' },
        ],
      },
    ],
  },
  'services-query': {
    moduleKey: 'services-query',
    groups: [
      {
        label: '系统服务',
        items: [
          { label: '身份表查询', path: '/services/query/identity' },
          { label: '主题表查询', path: '/services/query/theme' },
          { label: 'SQL 查询', path: '/services/query/sql' },
          { label: 'ETL 监控', path: '/services/etl' },
          { label: '源头维护', path: '/services/sources' },
          { label: '字典管理', path: '/services/dict', disabled: true, badge: '待开发' },
          { label: 'API 管理', path: '/services/api', disabled: true, badge: '待开发' },
          { label: '系统配置', path: '/services/config', disabled: true, badge: '待开发' },
        ],
      },
    ],
  },
  'services-etl': {
    moduleKey: 'services-etl',
    groups: [
      {
        label: '系统服务',
        items: [
          { label: '身份表查询', path: '/services/query/identity' },
          { label: '主题表查询', path: '/services/query/theme' },
          { label: 'SQL 查询', path: '/services/query/sql' },
          { label: 'ETL 监控', path: '/services/etl' },
          { label: '源头维护', path: '/services/sources' },
        ],
      },
    ],
  },
  'services-sources': {
    moduleKey: 'services-sources',
    groups: [
      {
        label: '系统服务',
        items: [
          { label: '身份表查询', path: '/services/query/identity' },
          { label: '主题表查询', path: '/services/query/theme' },
          { label: 'SQL 查询', path: '/services/query/sql' },
          { label: 'ETL 监控', path: '/services/etl' },
          { label: '源头维护', path: '/services/sources' },
        ],
      },
    ],
  },
}

export function navForModule(moduleKey: string): ModuleNavConfig | undefined {
  return MODULE_NAV[moduleKey]
}

export function filterModuleNav(
  config: ModuleNavConfig,
  permissions: ReadonlySet<string> | string[],
): ModuleNavConfig {
  return {
    ...config,
    groups: config.groups
      .map((group) => ({
        ...group,
        items: group.items.filter((item) => !item.path || canAccessPath(permissions, item.path)),
      }))
      .filter((group) => group.items.length > 0),
  }
}
