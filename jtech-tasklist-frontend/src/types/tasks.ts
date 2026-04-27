export interface TaskItem {
  id: string
  userId?: string
  listId: string
  title: string
  description?: string
  completed: boolean
  completedAt?: string | null
  createdAt?: string
  updatedAt?: string
}
