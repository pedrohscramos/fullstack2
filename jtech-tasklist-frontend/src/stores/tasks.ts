import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { useAuthStore } from './auth'
import * as taskService from '@/services/tasks.service'
import type { TaskItem } from '@/types/tasks'

export const useTasksStore = defineStore('tasks', () => {
  const authStore = useAuthStore()

  const items = ref<TaskItem[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const showCompleted = ref(true)

  const visibleTasks = computed(() => {
    if (showCompleted.value) return items.value
    return items.value.filter((task) => !task.completed)
  })

  function ensureToken(): string {
    if (!authStore.accessToken) {
      throw new Error('Sessão inválida')
    }
    return authStore.accessToken
  }

  async function load(listId?: string) {
    loading.value = true
    error.value = null
    try {
      items.value = await taskService.fetchTasks(ensureToken(), listId)
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Falha ao carregar tarefas'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function create(listId: string, title: string, description?: string) {
    const created = await taskService.createTask(
      {
        listId,
        title,
        description,
        completed: false,
      },
      ensureToken(),
    )
    items.value.unshift(created)
    return created
  }

  async function update(task: TaskItem, patch: Partial<Pick<TaskItem, 'title' | 'description' | 'completed'>>) {
    const updated = await taskService.updateTask(
      task.id,
      {
        title: patch.title ?? task.title,
        description: patch.description ?? task.description,
        completed: patch.completed ?? task.completed,
      },
      ensureToken(),
    )

    const index = items.value.findIndex((item) => item.id === task.id)
    if (index >= 0) {
      items.value[index] = updated
    }

    return updated
  }

  async function remove(taskId: string) {
    await taskService.deleteTask(taskId, ensureToken())
    items.value = items.value.filter((task) => task.id !== taskId)
  }

  function clear() {
    items.value = []
    loading.value = false
    error.value = null
  }

  return {
    items,
    visibleTasks,
    loading,
    error,
    showCompleted,
    load,
    create,
    update,
    remove,
    clear,
  }
})
