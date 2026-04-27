import type { TaskItem } from '@/types/tasks'
import { request } from './http'

interface CreateTaskPayload {
  listId: string
  title: string
  description?: string
  completed: boolean
}

interface UpdateTaskPayload {
  title: string
  description?: string
  completed: boolean
}

export async function fetchTasks(token: string, listId?: string): Promise<TaskItem[]> {
  const query = listId ? `?listId=${encodeURIComponent(listId)}` : ''
  return request<TaskItem[]>(`/tasks${query}`, { token })
}

export async function createTask(payload: CreateTaskPayload, token: string): Promise<TaskItem> {
  return request<TaskItem>('/tasks', {
    method: 'POST',
    token,
    body: payload,
  })
}

export async function updateTask(id: string, payload: UpdateTaskPayload, token: string): Promise<TaskItem> {
  return request<TaskItem>(`/tasks/${id}`, {
    method: 'PUT',
    token,
    body: payload,
  })
}

export async function deleteTask(id: string, token: string): Promise<void> {
  return request<void>(`/tasks/${id}`, {
    method: 'DELETE',
    token,
  })
}
