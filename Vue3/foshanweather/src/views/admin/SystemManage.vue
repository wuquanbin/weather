<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getSystemParams, updateSystemParam, getOperationLogs } from '@/api/admin'
import { ElMessage } from 'element-plus'
import type { SystemParam, OperationLog } from '@/types/weather'

const activeTab = ref('params')
const params = ref<SystemParam[]>([])
const logs = ref<OperationLog[]>([])
const loading = ref(false)
const editDialogVisible = ref(false)
const editForm = ref<SystemParam | null>(null)

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    if (activeTab.value === 'params') params.value = await getSystemParams()
    else logs.value = await getOperationLogs()
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

function editParam(row: SystemParam) {
  editForm.value = { ...row }
  editDialogVisible.value = true
}

async function saveParam() {
  if (!editForm.value) return
  try {
    await updateSystemParam(editForm.value.id, { paramValue: editForm.value.paramValue, description: editForm.value.description })
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    loadData()
  } catch { ElMessage.error('保存失败') }
}

function groupLabel(g: string) {
  const map: Record<string, string> = { weather: '气象', notification: '通知', amap: '地图', system: '系统' }
  return map[g] || g
}
</script>

<template>
  <el-card shadow="hover">
    <template #header>系统管理</template>
    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane label="系统参数" name="params">
        <el-table :data="params" v-loading="loading" stripe border>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="paramKey" label="参数键" width="200" />
          <el-table-column prop="paramValue" label="参数值" />
          <el-table-column prop="description" label="说明" />
          <el-table-column label="分组" width="100">
            <template #default="{ row }">
              <el-tag size="small">{{ groupLabel(row.group) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text size="small" @click="editParam(row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="操作日志" name="logs">
        <el-table :data="logs" v-loading="loading" stripe border>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="operatorName" label="操作人" width="100" />
          <el-table-column prop="operationType" label="操作类型" width="100" />
          <el-table-column prop="module" label="模块" width="100" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="ip" label="IP" width="130" />
          <el-table-column prop="createdAt" label="时间" width="160" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>

  <el-dialog v-model="editDialogVisible" title="编辑参数" width="500px">
    <el-form v-if="editForm" :model="editForm" label-width="80px">
      <el-form-item label="参数键"><el-input v-model="editForm.paramKey" disabled /></el-form-item>
      <el-form-item label="参数值"><el-input v-model="editForm.paramValue" /></el-form-item>
      <el-form-item label="说明"><el-input v-model="editForm.description" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveParam">保存</el-button>
    </template>
  </el-dialog>
</template>