import { ref } from 'vue'
import { getSystemInfo, isVersionCompatible } from '@/api/system'

export const appVersion = import.meta.env.VITE_APP_VERSION as string
export const buildTime = import.meta.env.VITE_BUILD_TIME as string | undefined

const backendRelease = ref<string | null>(null)
const backendGitCommit = ref<string | null>(null)
const versionMismatch = ref(false)
const versionCheckError = ref(false)
let loadPromise: Promise<void> | null = null

export function loadVersionInfo(): Promise<void> {
  if (loadPromise) {
    return loadPromise
  }
  loadPromise = (async () => {
    try {
      const info = await getSystemInfo()
      backendRelease.value = info.release
      backendGitCommit.value = info.gitCommit
      if (!import.meta.env.DEV) {
        versionMismatch.value = !isVersionCompatible(appVersion, info.release)
      }
    } catch {
      versionCheckError.value = true
    }
  })()
  return loadPromise
}

export function useVersionInfo() {
  return {
    appVersion,
    buildTime,
    backendRelease,
    backendGitCommit,
    versionMismatch,
    versionCheckError,
    loadVersionInfo,
  }
}
