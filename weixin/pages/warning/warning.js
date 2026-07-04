const { get } = require('../../utils/request')
const app = getApp()

// 预警级别从高到低，索引越小级别越高
const SEVERITY_ORDER = ['红色', '橙色', '黄色', '蓝色']

Page({
  data: {
    warnings: [],
    loading: true,
    filterSeverity: '全部',
    severities: ['全部', '红色', '橙色', '黄色', '蓝色'],
    filteredWarnings: [],
    minNotifyLevel: '',
  },

  onLoad() {
    // 读取用户设置的最低预警级别
    const notifyWarning = wx.getStorageSync('notifyWarning')
    const minLevel = wx.getStorageSync('notifyLevel')
    if (notifyWarning !== false && minLevel) {
      this.setData({ minNotifyLevel: minLevel })
    }
    this.loadWarnings()
  },

  onShow() {
    // 每次显示时重新读取设置，以防用户在"我的"页面修改了
    const notifyWarning = wx.getStorageSync('notifyWarning')
    const minLevel = wx.getStorageSync('notifyLevel')
    const newMinLevel = (notifyWarning !== false && minLevel) ? minLevel : ''
    if (newMinLevel !== this.data.minNotifyLevel) {
      this.setData({ minNotifyLevel: newMinLevel })
      this.applyFilter()
    }
  },

  onPullDownRefresh() {
    this.loadWarnings().then(() => wx.stopPullDownRefresh())
  },

  async loadWarnings() {
    this.setData({ loading: true })
    try {
      const res = await get('/api/warnings/active')
      const warnings = res.success ? res.data : []
      this.setData({ warnings })
      this.applyFilter()
    } catch (e) {
      console.error('加载预警失败:', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  onSeverityChange(e) {
    const idx = e.detail.value
    this.setData({ filterSeverity: this.data.severities[idx] })
    this.applyFilter()
  },

  applyFilter() {
    const { warnings, filterSeverity, minNotifyLevel } = this.data
    let result = warnings

    // 根据用户设置的最低预警级别过滤，只显示 >= 最低级别的预警
    if (minNotifyLevel) {
      const minIdx = SEVERITY_ORDER.indexOf(minNotifyLevel)
      if (minIdx >= 0) {
        result = result.filter(w => {
          const wIdx = SEVERITY_ORDER.indexOf(w.severity)
          return wIdx >= 0 && wIdx <= minIdx
        })
      }
    }

    // 再应用手动筛选
    if (filterSeverity !== '全部') {
      result = result.filter(w => w.severity === filterSeverity)
    }

    this.setData({ filteredWarnings: result })
  },
})
