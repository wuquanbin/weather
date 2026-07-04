<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminUsers, getAdminAdmins, createAdmin, updateAdmin, deleteAdmin } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { User, AdminUser } from '@/types/weather'

const activeTab = ref('users')
const users = ref<User[]>([])
const admins = ref<AdminUser[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref<any>({ username: '', password: '', realName: '', phone: '', email: '', role: 'DATA_ADMIN', status: 1 })

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    if (activeTab.value === 'users') users.value = await getAdminUsers()
    else admins.value = await getAdminAdmins()
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

function openCreateAdmin() {
  isEdit.value = false
  form.value = { username: '', password: '', realName: '', phone: '', email: '', role: 'DATA_ADMIN', status: 1 }
  dialogVisible.value = true
}
function openEditAdmin(row: AdminUser) {
  isEdit.value = true
  form.value = { ...row, password: '' }
  dialogVisible.value = true
}

async function saveAdmin() {
  try {
    if (isEdit.value) await updateAdmin(form.value.id, form.value)
    else await createAdmin(form.value)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
}

async function handleDeleteAdmin(id: number) {
  await ElMessageBox.confirm('确定删除该管理员？', '提示', { type: 'warning' })
  try { await deleteAdmin(id); ElMessage.success('已删除'); loadData() } catch { ElMessage.error('删除失败') }
}

function roleLabel(r: string) {
  const map: Record<string, string> = { SUPER_ADMIN: '超级管理员', DATA_ADMIN: '数据运维', CONTENT_ADMIN: '内容运营' }
  return map[r] || r
}
</script>

<template>
  <el-card shadow="hover">
    <template #header>用户与权限管理</template>
    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane label="小程序用户" name="users">
        <el-table :data="users" v-loading="loading" stripe border>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="nickname" label="昵称" />
          <el-table-column prop="openid" label="OpenID" />
          <el-table-column prop="districtCode" label="所在区域" width="100" />
          <el-table-column prop="phone" label="手机" width="120" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="lastLoginTime" label="最后登录" width="160" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="管理员" name="admins">
        <div style="margin-bottom: 12px">
          <el-button type="primary" @click="openCreateAdmin">新增管理员</el-button>
        </div>
        <el-table :data="admins" v-loading="loading" stripe border>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="username" label="账号" width="120" />
          <el-table-column prop="realName" label="姓名" width="100" />
          <el-table-column label="角色" width="120">
            <template #default="{ row }">{{ roleLabel(row.role) }}</template>
          </el-table-column>
          <el-table-column prop="phone" label="手机" width="120" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text size="small" @click="openEditAdmin(row)">编辑</el-button>
              <el-button type="danger" text size="small" @click="handleDeleteAdmin(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑管理员' : '新增管理员'" width="500px">
    <el-form :model="form" label-width="80px">
      <el-form-item label="账号"><el-input v-model="form.username" :disabled="isEdit" /></el-form-item>
      <el-form-item label="密码"><el-input v-model="form.password" type="password" :placeholder="isEdit ? '留空不修改' : '请输入密码'" /></el-form-item>
      <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
      <el-form-item label="角色">
        <el-select v-model="form.role">
          <el-option label="超级管理员" value="SUPER_ADMIN" />
          <el-option label="数据运维" value="DATA_ADMIN" />
          <el-option label="内容运营" value="CONTENT_ADMIN" />
        </el-select>
      </el-form-item>
      <el-form-item label="手机"><el-input v-model="form.phone" /></el-form-item>
      <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="form.status">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveAdmin">保存</el-button>
    </template>
  </el-dialog>
</template>