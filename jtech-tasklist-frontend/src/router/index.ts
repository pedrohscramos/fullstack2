import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { pinia } from '@/plugins/pinia'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/app',
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/app',
      name: 'app',
      component: () => import('@/views/AppView.vue'),
    },
    {
      path: '/app/lists/:listId',
      name: 'app-list',
      component: () => import('@/views/AppView.vue'),
    },
  ],
})

let bootPromise: Promise<void> | null = null

router.beforeEach(async (to) => {
  const authStore = useAuthStore(pinia)

  if (!bootPromise) {
    bootPromise = authStore.bootstrap()
  }
  await bootPromise

  const isPublic = Boolean(to.meta.public)
  const authenticated = authStore.isAuthenticated

  if (!isPublic && !authenticated) {
    return { name: 'login' }
  }

  if (to.name === 'login' && authenticated) {
    return { name: 'app' }
  }

  return true
})

export default router
