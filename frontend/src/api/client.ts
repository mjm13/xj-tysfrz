export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export class ApiError extends Error {
  code: number

  constructor(code: number, message: string) {
    super(message)
    this.name = 'ApiError'
    this.code = code
  }
}

const baseURL = (import.meta.env.VITE_API_BASE_URL as string | undefined)?.replace(/\/$/, '')
  ?? 'http://localhost:8080'

export async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`
  const response = await fetch(`${baseURL}${normalizedPath}`, {
    headers: {
      Accept: 'application/json',
      ...init?.headers,
    },
    ...init,
  })

  const body = (await response.json()) as ApiResponse<T>
  if (body.code !== 0) {
    throw new ApiError(body.code, body.message)
  }
  return body.data
}
