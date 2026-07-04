<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminWarnings, createWarning, updateWarning, deleteWarning } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { WarningNoticeItem } from '@/types/weather'

const warnings = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref<any>({
  warningType: '', severity: '', title: '', content: '',
  issuedAt: '', expiresAt: '', status: 'active', impactArea: '', defenseGuidance: '',
})

onMounted(loadData)

async function loadData() {
  loading.value = true
  try { warnings.value = await getAdminWarnings() } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

function openCreate() {
  isEdit.value = false
  form.value = { warningType: '', severity: '', title: '', content: '', issuedAt: '', expiresAt: '', status: 'active', impactArea: '', defenseGuidance: '' }
  dialogVisible.value = true
}

function openEdit(row: any) {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  try {
    if (isEdit.value) {
      await updateWarning(form.value.id, form.value)
    } else {
      await createWarning(form.value)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该预警？', '提示', { type: 'warning' })
  try { await deleteWarning(id); ElMessage.success('已删除'); loadData() } catch { ElMessage.error('删除失败') }
}

function severityType(s: string) {
  if (s === '红色') return 'danger'
  if (s === '橙色') return 'warning'
  if (s === '黄色') return ''
  return 'info'
}
</script>

<template>
  <el-card shadow="hover">
    <template #header>
      <div class="card-header">
        <span>预警信息管理</span>
        <el-button type="primary" @click="openCreate">新增预警</el-button>
      </div>
    </template>
    <el-table :data="warnings" v-loading="loading" stripe border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="级别" width="80">
        <template #default="{ row }">
          <el-tag :type="severityType(row.severity)" size="small">{{ row.severity }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="warningType" label="类型" width="90" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
            {{ row.status === 'active' ? '生效' : '已过期' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="impactArea" label="影响区域" />
      <el-table-column prop="issuedAt" label="发布时间" width="160" />
      <el-table-column prop="expiresAt" label="到期时间" width="160" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" text size="small" @click="openEdit(row)">编辑</el-button>
          <el-button type="danger" text size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑预警' : '新增预警'" width="650px">
    <el-form :model="form" label-width="90px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="预警类型">
            <el-select v-model="form.warningType" placeholder="选择类型">
              <el-option label="暴雨" value="暴雨" />
              <el-option label="台风" value="台风" />
              <el-option label="高温" value="高温" />
              <el-option label="雷雨大风" value="雷雨大风" />
              <el-option label="寒冷" value="寒冷" />
              <el-option label="大雾" value="大雾" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="预警级别">
            <el-select v-model="form.severity" placeholder="选择级别">
              <el-option label="蓝色" value="蓝色" />
              <el-option label="黄色" value="黄色" />
              <el-option label="橙色" value="橙色" />
              <el-option label="红色" value="红色" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
      <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="3" /></el-form-item>
      <el-form-item label="影响区域"><el-input v-model="form.impactArea" /></el-form-item>
      <el-form-item label="防御指引"><el-input v-model="form.defenseGuidance" type="textarea" :rows="2" /></el-form-item>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="发布时间"><el-input v-model="form.issuedAt" type="datetime-local" /></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="到期时间"><el-input v-model="form.expiresAt" type="datetime-local" /></el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="状态">
        <el-select v-model="form.status">
          <el-option label="生效" value="active" />
          <el-option label="已过期" value="expired" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="save">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>