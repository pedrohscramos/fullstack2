import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '../auth'
import { useTasksStore } from '../tasks'

vi.mock('@/services/tasks.service', () => ({
  fetchTasks: vi.fn(),
  createTask: vi.fn(),
  updateTask: vi.fn(),
  deleteTask: vi.fn(),
}))

import * as tasksService from '@/services/tasks.service'

describe('tasks store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()

    const authStore = useAuthStore()
    authStore.accessToken = 'jwt'
  })

  it('load tasks success', async () => {
    vi.mocked(tasksService.fetchTasks).mockResolvedValue([
      { id: 't1', listId: 'l1', title: 'Task 1', completed: false },
    ])

    const store = useTasksStore()
    await store.load('l1')

    expect(store.items.length).toBe(1)
    expect(store.visibleTasks.length).toBe(1)
  })

  it('create task inserts at top', async () => {
    vi.mocked(tasksService.createTask).mockResolvedValue({
      id: 't2',
      listId: 'l1',
      title: 'New Task',
      completed: false,
    })

    const store = useTasksStore()
    await store.create('l1', 'New Task', 'desc')

    expect(store.items[0].id).toBe('t2')
  })

  it('hides completed when showCompleted is false', () => {
    const store = useTasksStore()
    store.items = [
      { id: 'a', listId: 'l1', title: 'A', completed: false },
      { id: 'b', listId: 'l1', title: 'B', completed: true },
    ]
    store.showCompleted = false

    expect(store.visibleTasks.length).toBe(1)
    expect(store.visibleTasks[0].id).toBe('a')
  })
})
