App({
  globalData: {
    userInfo: null,
    districtCode: 'chancheng',
    districts: []
  },
  onLaunch() {
    this.loadDistricts()
  },
  loadDistricts() {
    const { request } = require('./utils/request')
    request({ url: '/api/meta/districts' }).then(res => {
      if (res.success) {
        this.globalData.districts = res.data
      }
    }).catch(() => {})
  }
})
