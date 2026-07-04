import type { ApiResponse, DashboardOverview, DistrictOption, TravelReport } from '@/types/weather'

const BASE_URL = 'http://localhost:8080/api'

async function request<T>(path: string): Promise<T> {
  const response = await fetch(`${BASE_URL}${path}`)
  if (!response.ok) {
    throw new Error(`请求失败: ${response.status}`)
  }

  const payload = (await response.json()) as ApiResponse<T>
  if (!payload.success) {
    throw new Error(payload.message || '接口返回失败')
  }

  return payload.data
}

export function getDistricts(): Promise<DistrictOption[]> {
  return request<DistrictOption[]>('/meta/districts')
}

export function getDashboardOverview(districtCode: string): Promise<DashboardOverview> {
  const query = districtCode ? `?districtCode=${encodeURIComponent(districtCode)}` : ''
  return request<DashboardOverview>(`/dashboard/overview${query}`)
}

export function getTravelReport(params: {
  districtCode: string
  originAddress: string
  destinationAddress: string
  departureTime: string
  modeCode: string
}): Promise<TravelReport> {
  const query = new URLSearchParams({
    districtCode: params.districtCode,
    originAddress: params.originAddress,
    destinationAddress: params.destinationAddress,
    departureTime: params.departureTime,
    modeCode: params.modeCode,
  })
  return request<TravelReport>(`/travel/report?${query.toString()}`)
}
