import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '../auth'
import { useListsStore } from '../lists'

vi.mock('@/services/lists.service', () => ({
  fetchLists: vi.fn(),
  createList: vi.fn(),
  updateList: vi.fn(),
  deleteList: vi.fn(),
}))

import * as listsService from '@/services/lists.service'

describe('lists store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()

    const authStore = useAuthStore()
    authStore.accessToken = 'jwt'
  })

  it('load lists success', async () => {
    vi.mocked(listsService.fetchLists).mockResolvedValue([
      { id: '1', name: 'Work' },
      { id: '2', name: 'Home' },
    ])

    const store = useListsStore()
    await store.loadLists()

    expect(store.items.length).toBe(2)
    expect(store.activeListId).toBe('1')
  })

  it('create list updates state', async () => {
    vi.mocked(listsService.createList).mockResolvedValue({ id: '10', name: 'New' })
    const store = useListsStore()

    await store.create('New')

    expect(store.items[0].id).toBe('10')
    expect(store.activeListId).toBe('10')
  })

  it('load lists error sets message', async () => {
    vi.mocked(listsService.fetchLists).mockRejectedValue(new Error('fail to load'))
    const store = useListsStore()

    await expect(store.loadLists()).rejects.toThrow('fail to load')
    expect(store.error).toBe('fail to load')
  })
})
