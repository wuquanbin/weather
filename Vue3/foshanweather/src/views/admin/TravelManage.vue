<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminPlaces, createPlace, updatePlace, deletePlace, getAdminRiskSegments, createRiskSegment, updateRiskSegment, deleteRiskSegment, getAdminDistricts } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('places')
const places = ref<any[]>([])
const risks = ref<any[]>([])
const districts = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editType = ref('place')
const form = ref<any>({})

onMounted(async () => {
  districts.value = await getAdminDistricts()
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    if (activeTab.value === 'places') {
      places.value = await getAdminPlaces()
    } else {
      risks.value = await getAdminRiskSegments()
    }
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

function openCreatePlace() {
  isEdit.value = false; editType.value = 'place'
  form.value = { name: '', category: '', address: '', location: '', indoor: false, weatherTags: '', sceneTags: '', recommendLevel: 3, highlight: '', district: null }
  dialogVisible.value = true
}
function openEditPlace(row: any) {
  isEdit.value = true; editType.value = 'place'
  form.value = { ...row, district: row.district || null }
  dialogVisible.value = true
}
function openCreateRisk() {
  isEdit.value = false; editType.value = 'risk'
  form.value = { name: '', location: '', riskType: '', triggerWeatherTags: '', description: '', advice: '', priority: 1, district: null }
  dialogVisible.value = true
}
function openEditRisk(row: any) {
  isEdit.value = true; editType.value = 'risk'
  form.value = { ...row, district: row.district || null }
  dialogVisible.value = true
}

async function save() {
  try {
    const api = editType.value === 'place'
      ? (isEdit.value ? updatePlace(form.value.id, form.value) : createPlace(form.value))
      : (isEdit.value ? updateRiskSegment(form.value.id, form.value) : createRiskSegment(form.value))
    await api
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
}

async function handleDelete(type: string, id: number) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  try {
    if (type === 'place') await deletePlace(id); else await deleteRiskSegment(id)
    ElMessage.success('已删除'); loadData()
  } catch { ElMessage.error('删除失败') }
}
</script>

<template>
  <el-card shadow="hover">
    <template #header>
      <div class="card-header">
        <span>出行服务管理</span>
        <el-button type="primary" @click="activeTab === 'places' ? openCreatePlace() : openCreateRisk()">
          {{ activeTab === 'places' ? '新增地点' : '新增风险路段' }}
        </el-button>
      </div>
    </template>
    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane label="出行地点" name="places">
        <el-table :data="places" v-loading="loading" stripe border>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="name" label="名称" width="140" />
          <el-table-column prop="category" label="分类" width="90" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="weatherTags" label="适配天气" width="120" />
          <el-table-column prop="recommendLevel" label="推荐等级" width="90" />
          <el-table-column prop="indoor" label="室内" width="60">
            <template #default="{ row }">{{ row.indoor ? '是' : '否' }}</template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text size="small" @click="openEditPlace(row)">编辑</el-button>
              <el-button type="danger" text size="small" @click="handleDelete('place', row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="风险路段" name="risks">
        <el-table :data="risks" v-loading="loading" stripe border>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="name" label="路段名称" width="160" />
          <el-table-column prop="riskType" label="风险类型" width="90" />
          <el-table-column prop="triggerWeatherTags" label="触发天气" width="120" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="advice" label="建议" />
          <el-table-column prop="priority" label="优先级" width="80" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text size="small" @click="openEditRisk(row)">编辑</el-button>
              <el-button type="danger" text size="small" @click="handleDelete('risk', row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="(isEdit ? '编辑' : '新增') + (editType === 'place' ? '出行地点' : '风险路段')" width="600px">
    <el-form :model="form" label-width="90px">
      <template v-if="editType === 'place'">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="form.category" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="坐标"><el-input v-model="form.location" placeholder="经度,纬度" /></el-form-item>
        <el-form-item label="室内"><el-switch v-model="form.indoor" /></el-form-item>
        <el-form-item label="适配天气"><el-input v-model="form.weatherTags" placeholder="逗号分隔" /></el-form-item>
        <el-form-item label="场景标签"><el-input v-model="form.sceneTags" placeholder="逗号分隔" /></el-form-item>
        <el-form-item label="推荐等级"><el-input-number v-model="form.recommendLevel" :min="1" :max="5" /></el-form-item>
        <el-form-item label="亮点"><el-input v-model="form.highlight" type="textarea" /></el-form-item>
      </template>
      <template v-else>
        <el-form-item label="路段名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="位置"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="风险类型"><el-input v-model="form.riskType" placeholder="积水/拥堵/施工" /></el-form-item>
        <el-form-item label="触发天气"><el-input v-model="form.triggerWeatherTags" placeholder="逗号分隔" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="建议"><el-input v-model="form.advice" type="textarea" /></el-form-item>
        <el-form-item label="优先级"><el-input-number v-model="form.priority" :min="1" :max="10" /></el-form-item>
      </template>
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