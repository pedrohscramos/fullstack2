<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useListsStore } from '@/stores/lists'
import { useTasksStore } from '@/stores/tasks'
import type { TaskItem } from '@/types/tasks'

const router = useRouter()
const authStore = useAuthStore()
const listsStore = useListsStore()
const tasksStore = useTasksStore()

const creatingList = ref(false)
const newListName = ref('')
const renameDialog = reactive({ open: false, id: '', name: '' })

const creatingTask = ref(false)
const newTaskTitle = ref('')
const newTaskDescription = ref('')
const taskDialog = reactive<{ open: boolean; task: TaskItem | null; title: string; description: string }>({
  open: false,
  task: null,
  title: '',
  description: '',
})

const currentListName = computed(() => listsStore.activeList?.name ?? 'Sem lista selecionada')

async function loadInitialData() {
  await listsStore.loadLists()
  if (listsStore.activeListId) {
    await tasksStore.load(listsStore.activeListId)
  }
}

watch(
  () => listsStore.activeListId,
  async (id) => {
    if (!id) {
      tasksStore.clear()
      return
    }
    await tasksStore.load(id)
  },
)

onMounted(async () => {
  await loadInitialData()
})

async function handleCreateList() {
  if (!newListName.value.trim()) return
  creatingList.value = true
  try {
    await listsStore.create(newListName.value.trim())
    newListName.value = ''
  } finally {
    creatingList.value = false
  }
}

function openRenameDialog(id: string, name: string) {
  renameDialog.open = true
  renameDialog.id = id
  renameDialog.name = name
}

async function saveRenameList() {
  if (!renameDialog.name.trim()) return
  await listsStore.rename(renameDialog.id, renameDialog.name.trim())
  renameDialog.open = false
}

async function handleDeleteList(id: string) {
  if (!window.confirm('Excluir esta lista? Esta ação não poderá ser desfeita.')) return
  await listsStore.remove(id)
}

async function handleCreateTask() {
  if (!listsStore.activeListId || !newTaskTitle.value.trim()) return
  creatingTask.value = true
  try {
    await tasksStore.create(listsStore.activeListId, newTaskTitle.value.trim(), newTaskDescription.value.trim())
    newTaskTitle.value = ''
    newTaskDescription.value = ''
  } finally {
    creatingTask.value = false
  }
}

async function toggleTask(task: TaskItem) {
  await tasksStore.update(task, { completed: !task.completed })
}

function openTaskEditor(task: TaskItem) {
  taskDialog.open = true
  taskDialog.task = task
  taskDialog.title = task.title
  taskDialog.description = task.description ?? ''
}

async function saveTaskEditor() {
  if (!taskDialog.task || !taskDialog.title.trim()) return
  await tasksStore.update(taskDialog.task, {
    title: taskDialog.title.trim(),
    description: taskDialog.description.trim(),
  })
  taskDialog.open = false
}

async function removeTask(taskId: string) {
  if (!window.confirm('Excluir esta tarefa?')) return
  await tasksStore.remove(taskId)
}

async function logout() {
  authStore.logout()
  listsStore.clear()
  tasksStore.clear()
  await router.replace('/login')
}
</script>

<template>
  <v-container fluid class="dashboard">
    <v-row>
      <v-col cols="12" md="4" lg="3">
        <v-card rounded="xl" class="fill-height">
          <v-card-title class="d-flex justify-space-between align-center">
            <span class="text-h6">Listas</span>
            <v-btn icon="mdi-logout" variant="text" @click="logout" />
          </v-card-title>

          <v-card-text>
            <v-text-field
              v-model="newListName"
              label="Nova lista"
              placeholder="Ex: Trabalho"
              variant="outlined"
              density="comfortable"
              hide-details
              class="mb-3"
              @keyup.enter="handleCreateList"
            />
            <v-btn color="primary" block :loading="creatingList" @click="handleCreateList">Criar lista</v-btn>
          </v-card-text>

          <v-divider />

          <v-list lines="two" nav>
            <v-list-item
              v-for="list in listsStore.items"
              :key="list.id"
              :active="list.id === listsStore.activeListId"
              @click="listsStore.setActiveList(list.id)"
            >
              <template #title>{{ list.name }}</template>
              <template #append>
                <v-btn size="small" variant="text" icon="mdi-pencil" @click.stop="openRenameDialog(list.id, list.name)" />
                <v-btn size="small" variant="text" icon="mdi-delete" @click.stop="handleDeleteList(list.id)" />
              </template>
            </v-list-item>
            <v-list-item v-if="listsStore.items.length === 0" title="Nenhuma lista cadastrada" subtitle="Crie sua primeira lista" />
          </v-list>
        </v-card>
      </v-col>

      <v-col cols="12" md="8" lg="9">
        <v-card rounded="xl">
          <v-card-title class="d-flex justify-space-between align-center flex-wrap ga-2">
            <span class="text-h5">{{ currentListName }}</span>
            <v-switch
              v-model="tasksStore.showCompleted"
              color="primary"
              hide-details
              inset
              label="Mostrar concluidas"
            />
          </v-card-title>

          <v-card-text>
            <v-row>
              <v-col cols="12" md="5">
                <v-text-field
                  v-model="newTaskTitle"
                  label="Titulo da tarefa"
                  variant="outlined"
                  density="comfortable"
                  :disabled="!listsStore.activeListId"
                />
              </v-col>
              <v-col cols="12" md="5">
                <v-text-field
                  v-model="newTaskDescription"
                  label="Descricao"
                  variant="outlined"
                  density="comfortable"
                  :disabled="!listsStore.activeListId"
                />
              </v-col>
              <v-col cols="12" md="2">
                <v-btn
                  block
                  color="accent"
                  class="h-100"
                  :disabled="!listsStore.activeListId"
                  :loading="creatingTask"
                  @click="handleCreateTask"
                >
                  Adicionar
                </v-btn>
              </v-col>
            </v-row>

            <v-divider class="my-4" />

            <v-list>
              <v-list-item v-for="task in tasksStore.visibleTasks" :key="task.id" class="task-item" rounded="lg">
                <template #prepend>
                  <v-checkbox-btn :model-value="task.completed" color="success" @update:model-value="toggleTask(task)" />
                </template>

                <v-list-item-title :class="{ done: task.completed }">{{ task.title }}</v-list-item-title>
                <v-list-item-subtitle>{{ task.description || 'Sem descricao' }}</v-list-item-subtitle>

                <template #append>
                  <v-btn size="small" variant="text" icon="mdi-pencil" @click="openTaskEditor(task)" />
                  <v-btn size="small" variant="text" icon="mdi-delete" @click="removeTask(task.id)" />
                </template>
              </v-list-item>
              <v-list-item
                v-if="tasksStore.visibleTasks.length === 0"
                title="Nenhuma tarefa para mostrar"
                subtitle="Crie uma nova tarefa na lista ativa"
              />
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-dialog v-model="renameDialog.open" max-width="460">
      <v-card>
        <v-card-title>Renomear lista</v-card-title>
        <v-card-text>
          <v-text-field v-model="renameDialog.name" label="Novo nome" variant="outlined" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="renameDialog.open = false">Cancelar</v-btn>
          <v-btn color="primary" @click="saveRenameList">Salvar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="taskDialog.open" max-width="520">
      <v-card>
        <v-card-title>Editar tarefa</v-card-title>
        <v-card-text>
          <v-text-field v-model="taskDialog.title" label="Titulo" variant="outlined" class="mb-3" />
          <v-textarea v-model="taskDialog.description" label="Descricao" variant="outlined" rows="3" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="taskDialog.open = false">Cancelar</v-btn>
          <v-btn color="primary" @click="saveTaskEditor">Salvar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style scoped>
.dashboard {
  min-height: 100vh;
}

.task-item {
  border: 1px solid rgba(31, 111, 235, 0.16);
  margin-bottom: 8px;
}

.done {
  text-decoration: line-through;
  color: #6b7280;
}
</style>
