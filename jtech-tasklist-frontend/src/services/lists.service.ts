import type { ListItem } from '@/types/lists'
import { request } from './http'

export async function fetchLists(token: string): Promise<ListItem[]> {
  return request<ListItem[]>('/lists', { token })
}

export async function createList(name: string, token: string): Promise<ListItem> {
  return request<ListItem>('/lists', {
    method: 'POST',
    token,
    body: { name },
  })
}

export async function updateList(id: string, name: string, token: string): Promise<ListItem> {
  return request<ListItem>(`/lists/${id}`, {
    method: 'PUT',
    token,
    body: { name },
  })
}

export async function deleteList(id: string, token: string): Promise<void> {
  return request<void>(`/lists/${id}`, {
    method: 'DELETE',
    token,
  })
}
