import axios from 'axios'
import type {
  ApiResponse,
  DashboardOverview,
  DistrictOption,
  TravelReport,
  AdminUser,
  User,
  Feedback,
  SystemParam,
  OperationLog,
  LifeIndex,
  WarningNoticeItem,
} from '@/types/weather'

const BASE_URL = 'http://localhost:8080/api'

const http = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data as ApiResponse<unknown>
    if (!payload.success) {
      return Promise.reject(new Error(payload.message || '请求失败'))
    }
    return payload.data
  },
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  },
)

// Public APIs
export function getDistricts(): Promise<DistrictOption[]> {
  return http.get('/meta/districts')
}

export function getDashboardOverview(districtCode: string): Promise<DashboardOverview> {
  const query = districtCode ? `?districtCode=${encodeURIComponent(districtCode)}` : ''
  return http.get(`/dashboard/overview${query}`)
}

export function getTravelReport(params: {
  districtCode: string
  originAddress: string
  destinationAddress: string
  departureTime: string
  modeCode: string
}): Promise<TravelReport> {
  return http.get('/travel/report', { params })
}

// Admin Auth
export function adminLogin(
  username: string,
  password: string,
): Promise<{
  token: string
  username: string
  realName: string
  role: string
}> {
  return http.post('/admin/login', { username, password })
}

export function getAdminInfo(): Promise<{
  username: string
  realName: string
  role: string
  phone: string
  email: string
}> {
  return http.get('/admin/info')
}

// Admin Dashboard Stats
export function getAdminStats(): Promise<Record<string, number>> {
  return http.get('/admin/dashboard/stats')
}

// Admin CRUD
export function getAdminDistricts() {
  return http.get('/admin/districts')
}
export function createDistrict(data: unknown) {
  return http.post('/admin/districts', data)
}
export function updateDistrict(id: number, data: unknown) {
  return http.put(`/admin/districts/${id}`, data)
}
export function deleteDistrict(id: number) {
  return http.delete(`/admin/districts/${id}`)
}

export function getAdminPlaces() {
  return http.get('/admin/places')
}
export function createPlace(data: unknown) {
  return http.post('/admin/places', data)
}
export function updatePlace(id: number, data: unknown) {
  return http.put(`/admin/places/${id}`, data)
}
export function deletePlace(id: number) {
  return http.delete(`/admin/places/${id}`)
}

export function getAdminRiskSegments() {
  return http.get('/admin/risk-segments')
}
export function createRiskSegment(data: unknown) {
  return http.post('/admin/risk-segments', data)
}
export function updateRiskSegment(id: number, data: unknown) {
  return http.put(`/admin/risk-segments/${id}`, data)
}
export function deleteRiskSegment(id: number) {
  return http.delete(`/admin/risk-segments/${id}`)
}

export function getAdminWarnings() {
  return http.get('/admin/warnings')
}
export function createWarning(data: unknown) {
  return http.post('/admin/warnings', data)
}
export function updateWarning(id: number, data: unknown) {
  return http.put(`/admin/warnings/${id}`, data)
}
export function deleteWarning(id: number) {
  return http.delete(`/admin/warnings/${id}`)
}

export function getAdminUsers(): Promise<User[]> {
  return http.get('/admin/users')
}
export function getAdminFeedbacks(): Promise<Feedback[]> {
  return http.get('/admin/feedbacks')
}
export function updateFeedback(id: number, data: unknown) {
  return http.put(`/admin/feedbacks/${id}`, data)
}

export function getAdminAdmins(): Promise<AdminUser[]> {
  return http.get('/admin/admins')
}
export function createAdmin(data: unknown) {
  return http.post('/admin/admins', data)
}
export function updateAdmin(id: number, data: unknown) {
  return http.put(`/admin/admins/${id}`, data)
}
export function deleteAdmin(id: number) {
  return http.delete(`/admin/admins/${id}`)
}

export function getSystemParams(): Promise<SystemParam[]> {
  return http.get('/admin/system-params')
}
export function createSystemParam(data: unknown) {
  return http.post('/admin/system-params', data)
}
export function updateSystemParam(id: number, data: unknown) {
  return http.put(`/admin/system-params/${id}`, data)
}
export function deleteSystemParam(id: number) {
  return http.delete(`/admin/system-params/${id}`)
}

export function getOperationLogs(): Promise<OperationLog[]> {
  return http.get('/admin/operation-logs')
}

export function getLifeIndices(): Promise<LifeIndex[]> {
  return http.get('/admin/life-index')
}
export function createLifeIndex(data: unknown) {
  return http.post('/admin/life-index', data)
}
export function updateLifeIndex(id: number, data: unknown) {
  return http.put(`/admin/life-index/${id}`, data)
}
export function deleteLifeIndex(id: number) {
  return http.delete(`/admin/life-index/${id}`)
}
