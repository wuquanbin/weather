<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getAdminStats, getDashboardOverview, getDistricts } from '@/api/admin'
import type { DashboardOverview, DistrictOption } from '@/types/weather'

const stats = ref<Record<string, number>>({})
const districts = ref<DistrictOption[]>([])
const selectedCode = ref('')
const overview = ref<DashboardOverview | null>(null)
const loading = ref(true)

function roundTemp(val: number | string | undefined): string {
  if (val === undefined || val === null) return '--'
  return Math.round(Number(val)).toString()
}

function getDayLabel(dateStr: string): string {
  if (!dateStr) return ''
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const target = new Date(dateStr)
  target.setHours(0, 0, 0, 0)
  const diff = Math.floor((target.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))
  if (diff === -1) return '昨天'
  if (diff === 0) return '今天'
  if (diff === 1) return '明天'
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return weekDays[target.getDay()]
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${month}/${day}`
}

function isToday(dateStr: string): boolean {
  if (!dateStr) return false
  const today = new Date()
  const target = new Date(dateStr)
  return today.getFullYear() === target.getFullYear() &&
    today.getMonth() === target.getMonth() &&
    today.getDate() === target.getDate()
}

function getWeatherIcon(weatherType: string): string {
  if (!weatherType) return '🌤️'
  const t = weatherType
  if (t.includes('雷')) return '⛈️'
  if (t.includes('暴雪')) return '🌨️'
  if (t.includes('大雪') || t.includes('中雪')) return '❄️'
  if (t.includes('小雪') || t.includes('阵雪')) return '🌨️'
  if (t.includes('暴雨')) return '🌧️'
  if (t.includes('大雨') || t.includes('中雨')) return '🌧️'
  if (t.includes('小雨') || t.includes('阵雨') || t.includes('毛毛雨')) return '🌦️'
  if (t.includes('雨')) return '🌧️'
  if (t.includes('雪')) return '❄️'
  if (t.includes('雾') || t.includes('霾')) return '🌫️'
  if (t.includes('多云')) return '⛅'
  if (t.includes('阴')) return '☁️'
  if (t.includes('晴')) return '☀️'
  if (t.includes('沙') || t.includes('尘')) return '💨'
  return '🌤️'
}

const statCards = computed(() => [
  { label: '总用户数', value: stats.value.totalUsers ?? 0, color: '#1d79d7' },
  { label: '活跃预警', value: stats.value.activeWarnings ?? 0, color: '#e6a23c' },
  { label: '反馈总数', value: stats.value.totalFeedback ?? 0, color: '#409eff' },
  { label: '待处理反馈', value: stats.value.pendingFeedback ?? 0, color: '#f56c6c' },
  { label: '覆盖区域', value: stats.value.totalDistricts ?? 0, color: '#67c23a' },
])

onMounted(async () => {
  try {
    const [s, d] = await Promise.all([getAdminStats(), getDistricts()])
    stats.value = s
    districts.value = d
    if (d.length > 0) {
      selectedCode.value = d[0].code
      await loadOverview()
    }
  } catch (e) {
    console.error('Dashboard init error:', e)
  } finally {
    loading.value = false
  }
})

async function loadOverview() {
  if (!selectedCode.value) return
  overview.value = await getDashboardOverview(selectedCode.value)
}
</script>

<template>
  <div class="admin-dashboard" v-loading="loading">
    <div class="stat-row">
      <div v-for="card in statCards" :key="card.label" class="stat-card">
        <div class="stat-label">{{ card.label }}</div>
        <div class="stat-value" :style="{ color: card.color }">{{ card.value }}</div>
      </div>
    </div>

    <div class="section-card">
      <div class="section-header">
        <h3>区域天气总览</h3>
        <el-select v-model="selectedCode" @change="loadOverview" style="width: 160px" size="small">
          <el-option v-for="d in districts" :key="d.code" :label="d.name" :value="d.code" />
        </el-select>
      </div>
      <div v-if="overview" class="overview-grid">
        <div class="overview-item">
          <span class="ov-label">区域</span>
          <span class="ov-value">{{ overview.currentWeather.districtName }}</span>
        </div>
        <div class="overview-item">
          <span class="ov-label">天气</span>
          <span class="ov-value">{{ overview.currentWeather.weatherType }}</span>
        </div>
        <div class="overview-item">
          <span class="ov-label">温度</span>
          <span class="ov-value">{{ roundTemp(overview.currentWeather.temperature) }}°C</span>
        </div>
        <div class="overview-item">
          <span class="ov-label">体感</span>
          <span class="ov-value">{{ roundTemp(overview.currentWeather.apparentTemperature) }}°C</span>
        </div>
        <div class="overview-item">
          <span class="ov-label">湿度</span>
          <span class="ov-value">{{ overview.currentWeather.humidity }}%</span>
        </div>
        <div class="overview-item">
          <span class="ov-label">风况</span>
          <span class="ov-value">{{ overview.currentWeather.windDirection }}{{ overview.currentWeather.windScale }}</span>
        </div>
        <div class="overview-item">
          <span class="ov-label">空气质量</span>
          <span class="ov-value">{{ overview.currentWeather.airQuality }}</span>
        </div>
        <div class="overview-item">
          <span class="ov-label">出行指数</span>
          <span class="ov-value">{{ overview.currentWeather.travelIndex }}</span>
        </div>
      </div>
    </div>

    <div class="section-card" v-if="overview">
      <div class="section-header">
        <h3>天气预报</h3>
      </div>
      <div class="forecast-scroll">
        <div class="forecast-row">
          <div v-for="f in overview.forecast" :key="f.forecastDate" class="forecast-card" :class="{ 'forecast-today': isToday(f.forecastDate) }">
            <div class="fc-day">{{ getDayLabel(f.forecastDate) }}</div>
            <div class="fc-date">{{ formatDate(f.forecastDate) }}</div>
            <div class="fc-icon">{{ getWeatherIcon(f.weatherType) }}</div>
            <div class="fc-weather">{{ f.weatherType }}</div>
            <div class="fc-temp">{{ roundTemp(f.lowTemperature) }}° ~ {{ roundTemp(f.highTemperature) }}°</div>
            <div class="fc-extra">降雨 {{ f.precipitationProbability }}%</div>
            <div class="fc-extra">{{ f.windDirection }}{{ f.windScale }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="two-col" v-if="overview">
      <div class="section-card">
        <div class="section-header">
          <h3>活跃预警 ({{ overview.activeWarnings.length }})</h3>
        </div>
        <div v-if="overview.activeWarnings.length === 0" class="empty-hint">暂无活跃预警</div>
        <div v-for="w in overview.activeWarnings" :key="w.title" class="list-row">
          <el-tag :type="w.severity === '红色' ? 'danger' : w.severity === '橙色' ? 'warning' : 'primary'" size="small">
            {{ w.severity }}
          </el-tag>
          <span class="row-title">{{ w.title }}</span>
          <span class="row-sub">{{ w.impactArea }}</span>
        </div>
      </div>

      <div class="section-card">
        <div class="section-header">
          <h3>风险路段 ({{ overview.riskSegments.length }})</h3>
        </div>
        <div v-if="overview.riskSegments.length === 0" class="empty-hint">暂无风险路段</div>
        <div v-for="r in overview.riskSegments" :key="r.id" class="list-row">
          <el-tag :type="r.riskType === '积水' ? 'danger' : 'warning'" size="small">{{ r.riskType }}</el-tag>
          <span class="row-title">{{ r.name }}</span>
          <span class="row-sub">{{ r.location }}</span>
        </div>
      </div>
    </div>

    <div class="section-card" v-if="overview">
      <div class="section-header">
        <h3>模块建设进度</h3>
      </div>
      <div class="module-grid">
        <div v-for="m in overview.moduleStatus" :key="m.code" class="module-item">
          <div class="mod-head">
            <strong>{{ m.name }}</strong>
            <span class="mod-status">{{ m.status }}</span>
          </div>
          <div class="progress-track">
            <div class="progress-fill" :style="{ width: m.progress + '%' }"></div>
          </div>
          <div class="mod-desc">{{ m.description }}</div>
        </div>
      </div>
    </div>

    <div class="section-card" v-if="overview">
      <div class="section-header">
        <h3>智能出行建议</h3>
      </div>
      <div class="suggestion-grid">
        <div v-for="s in overview.travelSuggestions" :key="s.scenarioCode" class="suggestion-card">
          <div class="sg-head">
            <strong>{{ s.title }}</strong>
            <el-tag size="small" type="success">{{ s.priorityTag }}</el-tag>
          </div>
          <div class="sg-summary">{{ s.summary }}</div>
          <div class="sg-rec">{{ s.recommendation }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-dashboard {
  padding: 0;
}

.stat-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  transition: box-shadow 0.2s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.section-card {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.overview-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
}

.ov-label {
  font-size: 12px;
  color: #909399;
}

.ov-value {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.forecast-scroll {
  overflow-x: auto;
  overflow-y: hidden;
  white-space: nowrap;
  scrollbar-width: thin;
  scrollbar-color: #c0c4cc transparent;
  padding-bottom: 8px;
}

.forecast-scroll::-webkit-scrollbar {
  height: 6px;
}

.forecast-scroll::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.forecast-scroll::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.forecast-scroll::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

.forecast-row {
  display: inline-flex;
  gap: 16px;
  padding: 4px 0;
}

.forecast-card {
  flex-shrink: 0;
  width: 120px;
  padding: 20px 16px;
  background: #f5f7fa;
  border-radius: 10px;
  text-align: center;
  white-space: normal;
  transition: transform 0.2s, box-shadow 0.2s;
}

.forecast-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.forecast-today {
  background: linear-gradient(135deg, #e8f4fd, #d6ecfa);
  border: 2px solid #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.25);
}

.forecast-today .fc-day {
  color: #409eff;
}

.fc-day {
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
  font-size: 15px;
}

.fc-date {
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
}

.fc-icon {
  font-size: 28px;
  margin-bottom: 6px;
}

.fc-weather {
  font-size: 18px;
  color: #409eff;
  margin-bottom: 8px;
  font-weight: 600;
}

.fc-temp {
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
  font-size: 16px;
}

.fc-extra {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.fc-extra:last-child {
  margin-bottom: 0;
}

.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.two-col .section-card {
  margin-bottom: 0;
}

.list-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.list-row:last-child {
  border-bottom: none;
}

.row-title {
  font-weight: 600;
  color: #303133;
}

.row-sub {
  color: #909399;
  font-size: 13px;
}

.empty-hint {
  color: #c0c4cc;
  text-align: center;
  padding: 20px;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.module-item {
  padding: 14px;
  background: #f5f7fa;
  border-radius: 6px;
}

.mod-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.mod-head strong {
  color: #303133;
}

.mod-status {
  font-size: 12px;
  color: #67c23a;
  font-weight: 600;
}

.progress-track {
  width: 100%;
  height: 8px;
  background: #e4e7ed;
  border-radius: 999px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409eff, #67c23a);
  border-radius: inherit;
  transition: width 0.4s ease;
}

.mod-desc {
  font-size: 13px;
  color: #909399;
}

.suggestion-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.suggestion-card {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.sg-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.sg-head strong {
  color: #303133;
}

.sg-summary {
  font-size: 14px;
  color: #606266;
  margin-bottom: 6px;
}

.sg-rec {
  font-size: 13px;
  color: #909399;
}
</style>
