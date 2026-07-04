export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

export interface DistrictOption {
  id: number
  code: string
  name: string
  serviceArea: string
  highlights: string
  transportFocus: string
}

export interface CurrentWeather {
  districtCode: string
  districtName: string
  weatherType: string
  temperature: number
  apparentTemperature: number
  humidity: number
  windDirection: string
  windScale: string
  airQuality: string
  precipitationProbability: number
  comfortLevel: string
  uvLevel: string
  travelIndex: string
  pressure: number | null
  visibility: number | null
  observationTime: string
}

export interface ForecastDay {
  forecastDate: string
  weekLabel: string
  weatherType: string
  lowTemperature: number
  highTemperature: number
  precipitationProbability: number
  windDirection: string
  windScale: string
  travelAdvice: string
}

export interface TravelSuggestionItem {
  scenarioCode: string
  title: string
  summary: string
  recommendation: string
  priorityTag: string
  iconKey: string
  priority: number
}

export interface TravelPlaceItem {
  id: number
  districtCode: string
  districtName: string
  name: string
  category: string
  address: string
  location: string
  indoor: boolean
  weatherTags: string
  sceneTags: string
  recommendLevel: number
  highlight: string
  matchReason: string
}

export interface RiskSegmentItem {
  id: number
  districtCode: string
  districtName: string
  name: string
  location: string
  riskType: string
  triggerWeatherTags: string
  description: string
  advice: string
  priority: number
}

export interface WarningNoticeItem {
  warningType: string
  severity: string
  title: string
  content: string
  issuedAt: string
  expiresAt: string
  status: string
  impactArea: string
  defenseGuidance: string
}

export interface KnowledgeDocumentItem {
  id: number
  title: string
  category: string
  summary: string
  content: string
  tags: string
  sourceType: string
  ragReady: boolean
  lastIndexedAt: string | null
}

export interface ModuleStatusItem {
  code: string
  name: string
  status: string
  progress: number
  description: string
}

export interface TravelWeatherSnapshot {
  districtCode: string
  districtName: string
  weatherType: string
  temperature: number
  precipitationProbability: number
  windDirection: string
  windScale: string
  travelIndex: string
}

export interface TravelRouteSummary {
  modeCode: string
  modeLabel: string
  originAddress: string
  destinationAddress: string
  originLocation: string
  destinationLocation: string
  originDistrictName: string
  destinationDistrictName: string
  distanceText: string
  durationText: string
  mainInstruction: string
}

export interface TravelReport {
  departureTime: string
  weatherSummary: string
  travelAdvice: string
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH' | string
  route: TravelRouteSummary
  originWeather: TravelWeatherSnapshot
  destinationWeather: TravelWeatherSnapshot
  riskSegments: RiskSegmentItem[]
  recommendedPlaces: TravelPlaceItem[]
}

export interface DashboardOverview {
  district: DistrictOption
  currentWeather: CurrentWeather
  forecast: ForecastDay[]
  travelSuggestions: TravelSuggestionItem[]
  recommendedPlaces: TravelPlaceItem[]
  riskSegments: RiskSegmentItem[]
  activeWarnings: WarningNoticeItem[]
  knowledgeDocuments: KnowledgeDocumentItem[]
  moduleStatus: ModuleStatusItem[]
}

// Admin types
export interface AdminUser {
  id: number
  username: string
  realName: string
  phone: string
  email: string
  role: string
  status: number
  lastLoginTime: string | null
}

export interface User {
  id: number
  openid: string
  nickname: string
  avatarUrl: string
  districtCode: string
  phone: string
  status: number
  lastLoginTime: string | null
}

export interface Feedback {
  id: number
  userId: number | null
  feedbackType: string
  content: string
  imageUrls: string
  status: string
  reply: string
  createdAt: string
}

export interface SystemParam {
  id: number
  paramKey: string
  paramValue: string
  description: string
  group: string
}

export interface OperationLog {
  id: number
  operatorName: string
  operationType: string
  module: string
  description: string
  requestParams: string
  ip: string
  status: number
  createdAt: string
}

export interface LifeIndex {
  id: number
  district: DistrictOption
  indexDate: string
  indexType: string
  level: string
  description: string
  advice: string
}