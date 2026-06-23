import { defineStore } from 'pinia'
import type { MenuNode } from '@/api/admin'
import { fetchNavigation } from '@/api/navigation'
import type { ModuleNavConfig } from '@/config/module-nav'

interface NavigationState {
  topBar: MenuNode[]
  sidebars: Record<string, ModuleNavConfig['groups']>
  loaded: boolean
}

function mapSidebarGroups(
  groups: { label: string; items: MenuNode[] }[],
): ModuleNavConfig['groups'] {
  return groups.map((group) => ({
    label: group.label,
    items: group.items.map((item) => ({
      label: item.label,
      path: item.path ?? undefined,
    })),
  }))
}

export const useNavigationStore = defineStore('navigation', {
  state: (): NavigationState => ({
    topBar: [],
    sidebars: {},
    loaded: false,
  }),
  actions: {
    async loadNavigation(): Promise<void> {
      const data = await fetchNavigation()
      this.topBar = data.topBar
      this.sidebars = Object.fromEntries(
        Object.entries(data.sidebars).map(([key, groups]) => [key, mapSidebarGroups(groups)]),
      )
      this.loaded = true
    },
    clear(): void {
      this.topBar = []
      this.sidebars = {}
      this.loaded = false
    },
    configForModule(moduleKey: string): ModuleNavConfig | undefined {
      const groups = this.sidebars[moduleKey]
      if (!groups?.length) {
        return undefined
      }
      return { moduleKey, groups }
    },
  },
})
