const { request } = require('../../utils/request')

Page({
  data: {
    origin: '',
    destination: '',
    mode: 'driving',
    modes: [
      { code: 'driving', name: '驾车' },
      { code: 'walking', name: '步行' },
      { code: 'bicycling', name: '骑行' },
      { code: 'transit', name: '公交' }
    ],
    routeResult: null,
    travelReport: null,
    loading: false,
    districtCode: 'chancheng'
  },

  onOriginInput(e) {
    this.setData({ origin: e.detail.value })
  },

  onDestinationInput(e) {
    this.setData({ destination: e.detail.value })
  },

  onModeChange(e) {
    this.setData({ mode: this.data.modes[e.detail.value].code })
  },

  searchRoute() {
    const { origin, destination, mode } = this.data
    if (!origin || !destination) {
      wx.showToast({ title: '请输入起终点', icon: 'none' })
      return
    }
    this.setData({ loading: true })

    request({
      url: '/api/travel/geocode',
      data: { address: origin }
    }).then(originRes => {
      if (!originRes.success || !originRes.data) throw new Error('起点解析失败')
      return request({
        url: '/api/travel/geocode',
        data: { address: destination }
      }).then(destRes => {
        if (!destRes.success || !destRes.data) throw new Error('终点解析失败')
        return request({
          url: '/api/travel/route/' + mode,
          data: { origin: originRes.data, destination: destRes.data }
        })
      })
    }).then(routeRes => {
      this.setData({
        routeResult: routeRes.success ? routeRes.data : null,
        loading: false
      })
    }).catch(err => {
      this.setData({ loading: false })
      wx.showToast({ title: err.message || '路线查询失败', icon: 'none' })
    })
  },

  getTravelReport() {
    const { districtCode, origin, destination, mode } = this.data
    if (!origin || !destination) return

    request({
      url: '/api/travel/report',
      data: {
        districtCode,
        originAddress: origin,
        destinationAddress: destination,
        departureTime: new Date().toISOString(),
        modeCode: mode
      }
    }).then(res => {
      if (res.success) {
        this.setData({ travelReport: res.data })
      }
    }).catch(() => {})
  }
})
