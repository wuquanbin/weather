import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { adminLogin, getAdminInfo } from '@/api/admin'

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const username = ref('')
  const realName = ref('')
  const role = ref('')

  const isLoggedIn = computed(() => !!token.value)

  async function login(user: string, password: string) {
    const result = await adminLogin(user, password)
    token.value = result.token
    username.value = result.username
    realName.value = result.realName
    role.value = result.role
    localStorage.setItem('admin_token', result.token)
    localStorage.setItem('admin_user', JSON.stringify(result))
  }

  async function loadInfo() {
    try {
      const info = await getAdminInfo()
      username.value = info.username
      realName.value = info.realName
      role.value = info.role
    } catch {
      logout()
    }
  }

  function logout() {
    token.value = ''
    username.value = ''
    realName.value = ''
    role.value = ''
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
  }

  function restoreSession() {
    const saved = localStorage.getItem('admin_user')
    if (saved) {
      try {
        const parsed = JSON.parse(saved)
        username.value = parsed.username || ''
        realName.value = parsed.realName || ''
        role.value = parsed.role || ''
      } catch { /* ignore */ }
    }
  }

  restoreSession()

  return { token, username, realName, role, isLoggedIn, login, loadInfo, logout }
})