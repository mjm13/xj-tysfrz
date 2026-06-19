import { request } from './client'

export interface SystemInfo {
  platformName: string
  release: string
  backendVersion: string
  buildTime: string
  gitCommit: string
  flywayVersion: string
  apiPrefix: string
  maintenance: boolean
}

export function getSystemInfo(): Promise<SystemInfo> {
  return request<SystemInfo>('/api/system/info')
}

/** 同 tag 策略：前后端 release 字符串必须完全一致 */
export function isVersionCompatible(frontendVersion: string, backendRelease: string): boolean {
  return frontendVersion === backendRelease
}
