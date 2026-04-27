import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { useAuthStore } from './auth'
import * as listService from '@/services/lists.service'
import type { ListItem } from '@/types/lists'

export const useListsStore = defineStore('lists', () => {
  const authStore = useAuthStore()

  const items = ref<ListItem[]>([])
  const activeListId = ref<string | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const activeList = computed(() => items.value.find((item) => item.id === activeListId.value) ?? null)

  function ensureToken(): string {
    if (!authStore.accessToken) {
      throw new Error('Sessão inválida')
    }
    return authStore.accessToken
  }

  async function loadLists() {
    loading.value = true
    error.value = null
    try {
      const data = await listService.fetchLists(ensureToken())
      items.value = data
      if (!activeListId.value && data.length > 0) {
        activeListId.value = data[0].id
      }
      if (activeListId.value && !data.some((item) => item.id === activeListId.value)) {
        activeListId.value = data.length > 0 ? data[0].id : null
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Falha ao carregar listas'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function create(name: string) {
    const created = await listService.createList(name, ensureToken())
    items.value.push(created)
    activeListId.value = created.id
    return created
  }

  async function rename(id: string, name: string) {
    const updated = await listService.updateList(id, name, ensureToken())
    const index = items.value.findIndex((item) => item.id === id)
    if (index >= 0) {
      items.value[index] = updated
    }
    return updated
  }

  async function remove(id: string) {
    await listService.deleteList(id, ensureToken())
    items.value = items.value.filter((item) => item.id !== id)
    if (activeListId.value === id) {
      activeListId.value = items.value.length > 0 ? items.value[0].id : null
    }
  }

  function setActiveList(id: string | null) {
    activeListId.value = id
  }

  function clear() {
    items.value = []
    activeListId.value = null
    loading.value = false
    error.value = null
  }

  return {
    items,
    activeListId,
    activeList,
    loading,
    error,
    loadLists,
    create,
    rename,
    remove,
    setActiveList,
    clear,
  }
})
