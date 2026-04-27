import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import * as authService from '@/services/auth.service'
import type { SessionUser } from '@/types/auth'

const STORAGE_KEY = 'tasklist-auth'

interface PersistedAuth {
  accessToken: string
  refreshToken: string
  expiresAt: number
  user: SessionUser
}

function readStorage(): PersistedAuth | null {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as PersistedAuth
  } catch {
    localStorage.removeItem(STORAGE_KEY)
    return null
  }
}

function writeStorage(payload: PersistedAuth | null) {
  if (!payload) {
    localStorage.removeItem(STORAGE_KEY)
    return
  }
  localStorage.setItem(STORAGE_KEY, JSON.stringify(payload))
}

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref<string | null>(null)
  const refreshToken = ref<string | null>(null)
  const expiresAt = ref<number | null>(null)
  const user = ref<SessionUser | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => Boolean(accessToken.value))

  function applySession(tokens: { accessToken: string; refreshToken: string; expiresIn: number }, sessionUser: SessionUser) {
    accessToken.value = tokens.accessToken
    refreshToken.value = tokens.refreshToken
    expiresAt.value = Date.now() + tokens.expiresIn * 1000
    user.value = sessionUser

    writeStorage({
      accessToken: tokens.accessToken,
      refreshToken: tokens.refreshToken,
      expiresAt: expiresAt.value,
      user: sessionUser,
    })
  }

  async function register(name: string, email: string, password: string) {
    loading.value = true
    error.value = null
    try {
      await authService.register({ name, email, password })
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Falha ao registrar usuário'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function login(email: string, password: string) {
    loading.value = true
    error.value = null
    try {
      const tokens = await authService.login({ email, password })
      applySession(tokens, { email })
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Falha no login'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function bootstrap() {
    const persisted = readStorage()
    if (!persisted) return

    accessToken.value = persisted.accessToken
    refreshToken.value = persisted.refreshToken
    expiresAt.value = persisted.expiresAt
    user.value = persisted.user

    if (!expiresAt.value || expiresAt.value <= Date.now() + 30_000) {
      await tryRefresh()
    }
  }

  async function tryRefresh() {
    if (!refreshToken.value) {
      logout()
      return
    }

    try {
      const refreshed = await authService.refresh(refreshToken.value)
      applySession(refreshed, user.value ?? { email: '' })
    } catch {
      logout()
    }
  }

  function logout() {
    accessToken.value = null
    refreshToken.value = null
    expiresAt.value = null
    user.value = null
    error.value = null
    writeStorage(null)
  }

  return {
    accessToken,
    refreshToken,
    expiresAt,
    user,
    loading,
    error,
    isAuthenticated,
    register,
    login,
    bootstrap,
    tryRefresh,
    logout,
  }
})
