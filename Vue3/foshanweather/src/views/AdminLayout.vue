<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const adminStore = useAdminStore()
const isCollapse = ref(false)

const menuItems = [
  { path: '/admin/dashboard', icon: 'DataAnalysis', title: '数据总览' },
  { path: '/admin/weather', icon: 'Cloudy', title: '气象数据' },
  { path: '/admin/warnings', icon: 'Warning', title: '预警管理' },
  { path: '/admin/travel', icon: 'Location', title: '出行服务' },
  { path: '/admin/users', icon: 'User', title: '用户管理' },
  { path: '/admin/feedback', icon: 'ChatDotRound', title: '反馈处理' },
  { path: '/admin/system', icon: 'Setting', title: '系统管理' },
]

async function handleLogout() {
  await ElMessageBox.confirm('确定退出登录？', '提示', { type: 'warning' })
  adminStore.logout()
  router.push('/login')
}
</script>

<template>
  <el-container class="admin-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="admin-aside">
      <div class="logo-area">
        <span v-if="!isCollapse" class="logo-text">佛山气象出行</span>
        <span v-else class="logo-icon">F</span>
      </div>
      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        router
        background-color="#0f4c81"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ menuItems.find(m => m.path === route.path)?.title || '' }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <span class="admin-name">{{ adminStore.realName || adminStore.username }}</span>
          <el-button type="danger" text @click="handleLogout">退出</el-button>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
}
.admin-aside {
  background: #0f4c81;
  transition: width 0.3s;
  overflow: hidden;
}
.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.logo-icon {
  font-size: 24px;
}
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e4e7ed;
  background: #fff;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.admin-name {
  color: #606266;
  font-size: 14px;
}
.admin-main {
  background: #f0f2f5;
  overflow-y: auto;
}
.el-menu {
  border-right: none;
}
</style>