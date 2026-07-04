<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminFeedbacks, updateFeedback } from '@/api/admin'
import { ElMessage } from 'element-plus'
import type { Feedback } from '@/types/weather'

const feedbacks = ref<Feedback[]>([])
const loading = ref(false)
const replyDialogVisible = ref(false)
const currentFeedback = ref<Feedback | null>(null)
const replyText = ref('')

onMounted(loadData)

async function loadData() {
  loading.value = true
  try { feedbacks.value = await getAdminFeedbacks() } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

function openReply(row: Feedback) {
  currentFeedback.value = row
  replyText.value = row.reply || ''
  replyDialogVisible.value = true
}

async function submitReply() {
  if (!currentFeedback.value) return
  try {
    await updateFeedback(currentFeedback.value.id, { status: 'replied', reply: replyText.value })
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    loadData()
  } catch { ElMessage.error('回复失败') }
}

async function markResolved(id: number) {
  try {
    await updateFeedback(id, { status: 'resolved' })
    ElMessage.success('已标记为已处理')
    loadData()
  } catch { ElMessage.error('操作失败') }
}

function statusType(s: string) {
  if (s === 'pending') return 'warning'
  if (s === 'replied') return 'primary'
  return 'success'
}
function statusLabel(s: string) {
  const map: Record<string, string> = { pending: '待处理', replied: '已回复', resolved: '已解决' }
  return map[s] || s
}
</script>

<template>
  <el-card shadow="hover">
    <template #header>用户反馈管理</template>
    <el-table :data="feedbacks" v-loading="loading" stripe border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="feedbackType" label="类型" width="100" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="reply" label="回复" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="提交时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" text size="small" @click="openReply(row)">回复</el-button>
          <el-button v-if="row.status !== 'resolved'" type="success" text size="small" @click="markResolved(row.id)">标记解决</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="replyDialogVisible" title="回复反馈" width="500px">
    <div v-if="currentFeedback" style="margin-bottom: 16px">
      <p><strong>用户反馈:</strong> {{ currentFeedback.content }}</p>
      <p v-if="currentFeedback.imageUrls"><strong>附图:</strong> {{ currentFeedback.imageUrls }}</p>
    </div>
    <el-input v-model="replyText" type="textarea" :rows="4" placeholder="输入回复内容" />
    <template #footer>
      <el-button @click="replyDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitReply">提交回复</el-button>
    </template>
  </el-dialog>
</template>