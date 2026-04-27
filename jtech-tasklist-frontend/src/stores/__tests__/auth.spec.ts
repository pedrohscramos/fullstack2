import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '../auth'

vi.mock('@/services/auth.service', () => ({
  login: vi.fn(),
  register: vi.fn(),
  refresh: vi.fn(),
}))

import * as authService from '@/services/auth.service'

describe('auth store', () => {
  beforeEach(() => {
    localStorage.clear()
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('login success persists session', async () => {
    vi.mocked(authService.login).mockResolvedValue({
      accessToken: 'jwt',
      refreshToken: 'refresh',
      tokenType: 'Bearer',
      expiresIn: 3600,
    })

    const store = useAuthStore()
    await store.login('john@example.com', 'secret123')

    expect(store.isAuthenticated).toBe(true)
    expect(store.accessToken).toBe('jwt')
    expect(localStorage.getItem('tasklist-auth')).toContain('jwt')
  })

  it('login error updates state', async () => {
    vi.mocked(authService.login).mockRejectedValue(new Error('invalid credentials'))

    const store = useAuthStore()
    await expect(store.login('john@example.com', 'wrong')).rejects.toThrow('invalid credentials')
    expect(store.error).toBe('invalid credentials')
    expect(store.isAuthenticated).toBe(false)
  })

  it('bootstrap should restore persisted session', async () => {
    const payload = {
      accessToken: 'jwt',
      refreshToken: 'refresh',
      expiresAt: Date.now() + 100000,
      user: { email: 'john@example.com' },
    }
    localStorage.setItem('tasklist-auth', JSON.stringify(payload))

    const store = useAuthStore()
    await store.bootstrap()

    expect(store.accessToken).toBe('jwt')
    expect(store.isAuthenticated).toBe(true)
  })
})
