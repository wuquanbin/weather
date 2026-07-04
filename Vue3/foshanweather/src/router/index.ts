import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/admin/dashboard',
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
    },
    {
      path: '/admin',
      component: () => import('@/views/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/admin/dashboard' },
        { path: 'dashboard', name: 'admin-dashboard', component: () => import('@/views/admin/DashboardView.vue') },
        { path: 'weather', name: 'admin-weather', component: () => import('@/views/admin/WeatherManage.vue') },
        { path: 'warnings', name: 'admin-warnings', component: () => import('@/views/admin/WarningManage.vue') },
        { path: 'travel', name: 'admin-travel', component: () => import('@/views/admin/TravelManage.vue') },
        { path: 'users', name: 'admin-users', component: () => import('@/views/admin/UserManage.vue') },
        { path: 'feedback', name: 'admin-feedback', component: () => import('@/views/admin/FeedbackManage.vue') },
        { path: 'system', name: 'admin-system', component: () => import('@/views/admin/SystemManage.vue') },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const token = localStorage.getItem('admin_token')
  if (to.meta.requiresAuth && !token) {
    return { name: 'login' }
  }
  // 不返回任何值，避免 vue-router 5.x 无限循环
})

export default router
