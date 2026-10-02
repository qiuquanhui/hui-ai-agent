import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue')
    },
    {
      path: '/love',
      name: 'love',
      component: () => import('@/views/LoveChatView.vue')
    },
    {
      path: '/manus',
      name: 'manus',
      component: () => import('@/views/ManusChatView.vue')
    }
  ]
})

export default router
