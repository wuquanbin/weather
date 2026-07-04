const { request } = require('../../utils/request')
const app = getApp()

Page({
  data: {
    currentWeather: null,
    forecast: [],
    allForecast: [],
    displayForecast: [],
    showAllForecast: false,
    warnings: [],
    travelSuggestions: [],
    districtCode: 'chancheng',
    districtIndex: 0,
    districtNames: [],
    loading: true
  },

  onLoad() {
    this.initDistricts()
    this.loadData()
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh())
  },

  initDistricts() {
    const districts = app.globalData.districts
    if (districts && districts.length > 0) {
      this.setData({
        districtNames: districts.map(d => d.name),
        districtIndex: districts.findIndex(d => d.code === this.data.districtCode) || 0
      })
    } else {
      // 区域数据可能还没加载完，延迟重试
      setTimeout(() => {
        const d = app.globalData.districts
        if (d && d.length > 0) {
          this.setData({
            districtNames: d.map(item => item.name),
            districtIndex: d.findIndex(item => item.code === this.data.districtCode) || 0
          })
        }
      }, 1500)
    }
  },

  loadData() {
    this.setData({ loading: true })
    const code = this.data.districtCode
    return Promise.all([
      request({ url: '/api/weather/current', data: { districtCode: code } }),
      request({ url: '/api/weather/forecast', data: { districtCode: code } }),
      request({ url: '/api/warnings/active' }),
      request({ url: '/api/travel/suggestions', data: { districtCode: code } })
    ]).then(([weatherRes, forecastRes, warningRes, suggestRes]) => {
      const forecast = forecastRes.success ? forecastRes.data : []
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      const processedForecast = forecast.filter(item => {
        const targetDate = new Date(item.forecastDate)
        targetDate.setHours(0, 0, 0, 0)
        const diffDays = Math.floor((targetDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))
        return diffDays >= -1
      }).map(item => {
        const targetDate = new Date(item.forecastDate)
        targetDate.setHours(0, 0, 0, 0)
        const diffDays = Math.floor((targetDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))
        return {
          ...item,
          highTemperature: Math.round(Number(item.highTemperature)),
          lowTemperature: Math.round(Number(item.lowTemperature)),
          forecastDate: this.formatDate(item.forecastDate),
          weekLabel: this.getDayLabel(item.forecastDate),
          isYesterday: diffDays === -1,
          icon: this.getWeatherIcon(item.weatherType)
        }
      })
      const displayForecast = processedForecast.slice(0, 5)
      const currentWeather = weatherRes.success ? weatherRes.data : null
      if (currentWeather) {
        currentWeather.temperature = Math.round(Number(currentWeather.temperature))
        currentWeather.apparentTemperature = Math.round(Number(currentWeather.apparentTemperature))
        const todayForecast = processedForecast.find(f => f.weekLabel === '今天')
        if (todayForecast) {
          currentWeather.highTemperature = todayForecast.highTemperature
          currentWeather.lowTemperature = todayForecast.lowTemperature
        }
      }
      this.setData({
        currentWeather: currentWeather,
        allForecast: processedForecast,
        displayForecast: displayForecast,
        showAllForecast: false,
        warnings: this.filterWarnings(warningRes.success ? warningRes.data : []),
        travelSuggestions: suggestRes.success ? suggestRes.data : [],
        weatherBgClass: this.getWeatherBgClass(currentWeather?.weatherType),
        loading: false
      })
    }).catch(() => {
      this.setData({ loading: false })
      wx.showToast({ title: '加载失败', icon: 'none' })
    })
  },

  onDistrictChange(e) {
    const districts = app.globalData.districts
    const idx = e.detail.value
    if (districts && districts[idx]) {
      this.setData({
        districtIndex: idx,
        districtCode: districts[idx].code
      })
      this.loadData()
    }
  },

  filterWarnings(warnings) {
    const districtName = this.data.districtNames[this.data.districtIndex] || ''
    if (!districtName) return warnings
    return warnings
      .filter(w => w.impactArea && w.impactArea.includes(districtName))
      .map(w => ({
        ...w,
        title: w.title.replace(/^佛山市/, '')
      }))
  },

  formatDate(dateStr) {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${month}/${day}`
  },

  getDayLabel(dateStr) {
    if (!dateStr) return ''
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const targetDate = new Date(dateStr)
    targetDate.setHours(0, 0, 0, 0)
    
    const diffTime = targetDate.getTime() - today.getTime()
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
    
    if (diffDays === -1) return '昨天'
    if (diffDays === 0) return '今天'
    if (diffDays === 1) return '明天'
    
    const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    return weekDays[targetDate.getDay()]
  },

  getWeatherIcon(weatherType) {
    if (!weatherType) return '☀️'
    const iconMap = {
      '晴': '☀️',
      '多云': '⛅',
      '阴': '☁️',
      '小雨': '🌧️',
      '中雨': '🌧️',
      '大雨': '⛈️',
      '雷阵雨': '⛈️',
      '阵雨': '🌦️',
      '毛毛雨': '🌧️',
      '冻雨': '🌨️',
      '小雪': '❄️',
      '中雪': '❄️',
      '大雪': '❄️',
      '雾': '🌫️',
      '雾凇': '🌫️'
    }
    for (const key in iconMap) {
      if (weatherType.includes(key)) {
        return iconMap[key]
      }
    }
    return '☀️'
  },

  getWeatherBgClass(weatherType) {
    if (!weatherType) return 'bg-sunny'
    if (weatherType.includes('晴')) return 'bg-sunny'
    if (weatherType.includes('多云')) return 'bg-cloudy'
    if (weatherType.includes('阴')) return 'bg-overcast'
    if (weatherType.includes('雨') || weatherType.includes('雷')) return 'bg-rainy'
    if (weatherType.includes('雪')) return 'bg-snowy'
    if (weatherType.includes('雾') || weatherType.includes('霾')) return 'bg-foggy'
    return 'bg-sunny'
  },

  goToWeatherDetail() {
    if (this.data.showAllForecast) {
      this.setData({
        displayForecast: this.data.allForecast.slice(0, 5),
        showAllForecast: false
      })
    } else {
      this.setData({
        displayForecast: this.data.allForecast,
        showAllForecast: true
      })
    }
  }
})
