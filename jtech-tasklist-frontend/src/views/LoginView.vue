<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const mode = ref<'login' | 'register'>('login')
const form = reactive({
  name: '',
  email: '',
  password: '',
})

const error = ref<string | null>(null)
const valid = ref(false)

async function handleSubmit() {
  error.value = null
  try {
    if (mode.value === 'register') {
      await authStore.register(form.name, form.email, form.password)
      mode.value = 'login'
      form.password = ''
      return
    }

    await authStore.login(form.email, form.password)
    await router.replace('/app')
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Falha na autenticação'
  }
}

function toggleMode() {
  error.value = null
  mode.value = mode.value === 'login' ? 'register' : 'login'
}
</script>

<template>
  <v-container class="login-layout" fluid>
    <v-row class="fill-height" align="center" justify="center">
      <v-col cols="12" sm="10" md="7" lg="5">
        <v-card class="pa-6 card-glass" rounded="xl" elevation="10">
          <v-card-title class="text-h4 font-weight-bold mb-2">Tasklist Multi-usuario</v-card-title>
          <v-card-subtitle class="mb-5">
            {{ mode === 'login' ? 'Acesse sua area de trabalho' : 'Crie sua conta para começar' }}
          </v-card-subtitle>

          <v-alert v-if="error" type="error" variant="tonal" class="mb-4">{{ error }}</v-alert>

          <v-form v-model="valid" @submit.prevent="handleSubmit">
            <v-text-field
              v-if="mode === 'register'"
              v-model="form.name"
              label="Nome"
              :rules="[(value) => !!value || 'Nome é obrigatório']"
              prepend-inner-icon="mdi-account"
              variant="outlined"
              class="mb-3"
            />

            <v-text-field
              v-model="form.email"
              label="Email"
              type="email"
              :rules="[
                (value) => !!value || 'Email é obrigatório',
                (value) => /.+@.+\..+/.test(value) || 'Email inválido',
              ]"
              prepend-inner-icon="mdi-at"
              variant="outlined"
              class="mb-3"
            />

            <v-text-field
              v-model="form.password"
              label="Senha"
              type="password"
              :rules="[
                (value) => !!value || 'Senha é obrigatória',
                (value) => value.length >= 6 || 'Senha deve ter ao menos 6 caracteres',
              ]"
              prepend-inner-icon="mdi-lock"
              variant="outlined"
              class="mb-4"
            />

            <v-btn
              color="primary"
              size="large"
              block
              type="submit"
              :loading="authStore.loading"
              :disabled="!valid"
            >
              {{ mode === 'login' ? 'Entrar' : 'Criar conta' }}
            </v-btn>
          </v-form>

          <v-divider class="my-4" />

          <v-btn variant="text" block @click="toggleMode">
            {{ mode === 'login' ? 'Ainda não tem conta? Cadastre-se' : 'Já tem conta? Faça login' }}
          </v-btn>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.login-layout {
  min-height: 100vh;
  background:
    radial-gradient(circle at 10% 15%, rgba(31, 111, 235, 0.22), transparent 40%),
    radial-gradient(circle at 85% 80%, rgba(255, 140, 66, 0.2), transparent 35%),
    linear-gradient(135deg, #eef4ff 0%, #f4f7fb 100%);
}

.card-glass {
  backdrop-filter: blur(8px);
  background: rgba(255, 255, 255, 0.94);
}
</style>
