const { get, post, put } = require('../../utils/request')
const app = getApp()

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    favorites: [],
    showFeedback: false,
    feedbackContent: '',
    feedbackSubmitting: false,
    notifyWarning: true,
    notifyCommute: false,
    notifyLevel: '橙色',
    levels: ['蓝色', '黄色', '橙色', '红色'],
    levelIndex: 2,
  },

  onLoad() {
    this.restoreSession()
  },

  onShow() {
    if (this.data.isLoggedIn) {
      this.loadFavorites()
    }
  },

  restoreSession() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    if (token && userInfo) {
      app.globalData.token = token
      app.globalData.userInfo = userInfo
      this.setData({ isLoggedIn: true, userInfo })
      this.loadFavorites()
    }
  },

  async onWechatLogin() {
    try {
      const loginRes = await new Promise((resolve, reject) => {
        wx.login({ success: resolve, fail: reject })
      })
      const res = await post('/api/wechat/login', { code: loginRes.code })
      if (!res.success) throw new Error(res.message)
      const data = res.data
      const userInfo = {
        id: data.userId,
        nickname: data.nickname,
        avatarUrl: data.avatarUrl,
      }
      app.globalData.token = data.token
      app.globalData.userInfo = userInfo
      wx.setStorageSync('token', data.token)
      wx.setStorageSync('userInfo', userInfo)
      this.setData({ isLoggedIn: true, userInfo })
      this.loadFavorites()
      wx.showToast({ title: '登录成功', icon: 'success' })
    } catch (e) {
      wx.showToast({ title: '登录失败', icon: 'none' })
      console.error('登录失败:', e)
    }
  },

  onLogout() {
    app.globalData.token = ''
    app.globalData.userInfo = null
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
    this.setData({
      isLoggedIn: false,
      userInfo: null,
      favorites: [],
    })
    wx.showToast({ title: '已退出登录', icon: 'success' })
  },

  async loadFavorites() {
    try {
      const res = await get('/api/wechat/favorites')
      const favorites = (res.success) ? res.data : []
      this.setData({ favorites })
    } catch (e) {
      console.error('加载收藏失败:', e)
    }
  },

  async onDeleteFavorite(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定删除该收藏地点？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await get('/api/wechat/favorites/' + id)
            this.loadFavorites()
            wx.showToast({ title: '已删除', icon: 'success' })
          } catch (e) {
            this.loadFavorites()
          }
        }
      },
    })
  },

  toggleFeedback() {
    this.setData({ showFeedback: !this.data.showFeedback })
  },

  onFeedbackInput(e) {
    this.setData({ feedbackContent: e.detail.value })
  },

  async submitFeedback() {
    if (!this.data.feedbackContent.trim()) {
      wx.showToast({ title: '请输入反馈内容', icon: 'none' })
      return
    }
    if (!this.data.isLoggedIn) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    this.setData({ feedbackSubmitting: true })
    try {
      await post('/api/wechat/feedback', {
        content: this.data.feedbackContent.trim(),
      })
      wx.showToast({ title: '提交成功', icon: 'success' })
      this.setData({ feedbackContent: '', showFeedback: false })
    } catch (e) {
      wx.showToast({ title: '提交失败', icon: 'none' })
    } finally {
      this.setData({ feedbackSubmitting: false })
    }
  },

  onNotifyWarningChange(e) {
    this.setData({ notifyWarning: e.detail.value })
    wx.setStorageSync('notifyWarning', e.detail.value)
  },

  onNotifyCommuteChange(e) {
    this.setData({ notifyCommute: e.detail.value })
    wx.setStorageSync('notifyCommute', e.detail.value)
  },

  onLevelChange(e) {
    const idx = e.detail.value
    this.setData({
      levelIndex: idx,
      notifyLevel: this.data.levels[idx],
    })
    wx.setStorageSync('notifyLevel', this.data.levels[idx])
  },

  clearCache() {
    wx.showModal({
      title: '提示',
      content: '确定清除本地缓存？',
      success(res) {
        if (res.confirm) {
          wx.clearStorageSync()
          wx.showToast({ title: '已清除', icon: 'success' })
        }
      },
    })
  },
})
