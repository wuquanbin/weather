<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminDistricts, updateDistrict } from '@/api/admin'
import { ElMessage } from 'element-plus'

const districts = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editForm = ref<any>({})

onMounted(loadData)

async function loadData() {
  loading.value = true
  try { districts.value = await getAdminDistricts() } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

function editDistrict(row: any) {
  editForm.value = { ...row }
  dialogVisible.value = true
}

async function saveDistrict() {
  try {
    await updateDistrict(editForm.value.id, editForm.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('保存失败') }
}
</script>

<template>
  <el-card shadow="hover">
    <template #header>
      <div class="card-header"><span>气象数据管理 - 区域列表</span></div>
    </template>
    <el-table :data="districts" v-loading="loading" stripe border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="code" label="编码" width="100" />
      <el-table-column prop="name" label="名称" width="100" />
      <el-table-column prop="serviceArea" label="服务区域" />
      <el-table-column prop="highlights" label="亮点" />
      <el-table-column prop="transportFocus" label="交通重点" />
      <el-table-column prop="latitude" label="纬度" width="100" />
      <el-table-column prop="longitude" label="经度" width="100" />
      <el-table-column prop="adminCode" label="行政区码" width="100" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" text size="small" @click="editDistrict(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialogVisible" title="编辑区域" width="600px">
    <el-form :model="editForm" label-width="80px">
      <el-form-item label="名称"><el-input v-model="editForm.name" /></el-form-item>
      <el-form-item label="服务区域"><el-input v-model="editForm.serviceArea" /></el-form-item>
      <el-form-item label="亮点"><el-input v-model="editForm.highlights" /></el-form-item>
      <el-form-item label="交通重点"><el-input v-model="editForm.transportFocus" /></el-form-item>
      <el-form-item label="纬度"><el-input v-model="editForm.latitude" type="number" /></el-form-item>
      <el-form-item label="经度"><el-input v-model="editForm.longitude" type="number" /></el-form-item>
      <el-form-item label="行政区码"><el-input v-model="editForm.adminCode" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveDistrict">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>