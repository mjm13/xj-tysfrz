import { request } from './client'
import type { MenuNode } from './admin'

export interface NavigationData {
  topBar: MenuNode[]
  sidebars: Record<string, SidebarGroupFromApi[]>
}

export interface SidebarGroupFromApi {
  label: string
  items: MenuNode[]
}

export function fetchNavigation(): Promise<NavigationData> {
  return request<NavigationData>('/api/navigation')
}
