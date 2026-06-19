import { request } from './client'

export function getPing(): Promise<string> {
  return request<string>('/api/ping')
}
