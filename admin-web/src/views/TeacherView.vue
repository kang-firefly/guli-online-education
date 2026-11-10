<template>
  <div class="teacher">
    <!-- 页头：左侧大标题 + 细线，右侧说明与主操作 -->
    <header class="page-head">
      <div class="page-head__left">
        <h1 class="page-head__title">讲师</h1>
        <p class="page-head__desc">
          维护平台讲师档案。讲师是课程归属的主体，被课程引用时不可删除。
        </p>
      </div>
      <div class="page-head__right">
        <span class="page-head__count mono">{{ total }} <i>位</i></span>
        <el-button type="primary" :icon="Plus" @click="openCreate">新增讲师</el-button>
      </div>
    </header>

    <!-- 筛选条：一条发丝线托住的表单行 -->
    <section class="filter" aria-label="筛选条件">
      <el-input
        v-model="query.name"
        class="filter__input"
        placeholder="按姓名搜索"
        clearable
        :prefix-icon="Search"
        @keyup.enter="onSearch"
        @clear="onSearch"
      />
      <el-select v-model="query.level" class="filter__select" placeholder="全部级别" clearable>
        <el-option label="讲师" :value="1" />
        <el-option label="高级讲师" :value="2" />
      </el-select>
      <el-button @click="onSearch">查询</el-button>
      <el-button text @click="onReset">重置</el-button>
    </section>

    <!-- 列表 -->
    <section class="table-wrap reveal reveal-2">
      <el-table
        v-loading="loading"
        :data="rows"
        row-key="id"
        :show-overflow-tooltip="true"
        @row-dblclick="openEdit"
      >
        <el-table-column label="讲师" :min-width="isNarrow ? 190 : 280">
          <template #default="{ row }">
            <div class="cell-teacher">
              <span class="avatar">
                <img v-if="row.avatar" :src="row.avatar" :alt="row.name" />
                <template v-else>{{ row.name?.slice(0, 1) }}</template>
              </span>
              <div class="cell-teacher__text">
                <span class="cell-teacher__name">{{ row.name }}</span>
                <span class="cell-teacher__intro">{{ row.intro || '暂无简介' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column v-if="!isNarrow" label="级别" width="120">
          <template #default="{ row }">
            <el-tag :type="row.level === 2 ? 'warning' : 'info'" disable-transitions>
              {{ row.level === 2 ? '高级讲师' : '讲师' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column v-if="!isNarrow" label="资历" min-width="220">
          <template #default="{ row }">
            <span class="cell-muted">{{ row.career || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column v-if="!isNarrow" label="排序" width="90" align="right">
          <template #default="{ row }">
            <span class="mono cell-muted">{{ row.sort ?? 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column v-if="!isNarrow" label="创建时间" width="170">
          <template #default="{ row }">
            <span class="mono cell-muted">{{ row.gmtCreate || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" :width="isNarrow ? 96 : 160" align="right">
          <template #default="{ row }">
            <template v-if="isNarrow">
              <el-button text size="small" :icon="Edit" aria-label="编辑" @click="openEdit(row)" />
              <el-button text size="small" :icon="Delete" class="danger-text" aria-label="删除" @click="onDelete(row)" />
            </template>
            <template v-else>
              <el-button text size="small" @click="openEdit(row)">编辑</el-button>
              <el-button text size="small" class="danger-text" @click="onDelete(row)">删除</el-button>
            </template>
          </template>
        </el-table-column>

        <template #empty>
          <div class="empty">
            <span class="empty__mark">无</span>
            <p class="empty__title">还没有讲师</p>
            <p class="empty__desc">先添加一位讲师，再为 TA 创建课程。</p>
            <el-button type="primary" :icon="Plus" @click="openCreate">新增讲师</el-button>
          </div>
        </template>
      </el-table>

      <div class="pager">
        <span class="pager__meta mono">
          第 {{ query.page }} / {{ Math.max(1, Math.ceil(total / query.size)) }} 页
        </span>
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="query.size"
          hide-on-single-page
          @current-change="fetchList"
        />
      </div>
    </section>

    <!-- 新增 / 编辑 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑讲师' : '新增讲师'"
      width="560px"
      align-center
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="讲师姓名" prop="name">
            <el-input v-model="form.name" maxlength="20" show-word-limit placeholder="如：张明远" />
          </el-form-item>

          <el-form-item label="级别" prop="level">
            <el-radio-group v-model="form.level">
              <el-radio-button :value="1">讲师</el-radio-button>
              <el-radio-button :value="2">高级讲师</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </div>

        <el-form-item label="资历">
          <el-input
            v-model="form.career"
            type="textarea"
            :rows="2"
            maxlength="500"
            show-word-limit
            placeholder="如：12 年 Java 研发经验，主导过千万级中台建设"
          />
        </el-form-item>

        <el-form-item label="简介">
          <el-input
            v-model="form.intro"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            placeholder="一句话说明这位讲师擅长什么"
          />
        </el-form-item>

        <div class="form-grid">
          <el-form-item label="头像">
            <div class="upload">
              <span class="upload__preview">
                <img v-if="form.avatar" :src="form.avatar" alt="头像预览" />
                <span v-else class="upload__placeholder">无</span>
              </span>
              <el-upload
                :show-file-list="false"
                :http-request="uploadAvatar"
                accept="image/*"
                :before-upload="beforeUpload"
              >
                <el-button :loading="uploading" :icon="Upload">
                  {{ form.avatar ? '重新上传' : '上传头像' }}
                </el-button>
              </el-upload>
            </div>
          </el-form-item>

          <el-form-item label="排序（越小越靠前）">
            <el-input-number v-model="form.sort" :min="0" :max="9999" controls-position="right" />
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">
          {{ isEdit ? '保存修改' : '创建讲师' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Upload, Edit, Delete } from '@element-plus/icons-vue'
import {
  pageTeachers,
  createTeacher,
  updateTeacher,
  removeTeacher,
  uploadImage
} from '@/api/teacher'

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, name: '', level: null })

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const uploading = ref(false)
const formRef = ref(null)

const emptyForm = () => ({
  id: null,
  name: '',
  level: 1,
  career: '',
  intro: '',
  avatar: '',
  sort: 0
})
const form = reactive(emptyForm())

// 窄屏隐藏次要列，避免横向滚动看不到操作列
const isNarrow = ref(window.innerWidth <= 768)
const syncNarrow = () => { isNarrow.value = window.innerWidth <= 768 }
onMounted(() => window.addEventListener('resize', syncNarrow))
onUnmounted(() => window.removeEventListener('resize', syncNarrow))

const rules = {
  name: [{ required: true, message: '请输入讲师姓名', trigger: 'blur' }],
  level: [{ required: true, message: '请选择级别', trigger: 'change' }]
}

async function fetchList() {
  loading.value = true
  try {
    const data = await pageTeachers({
      page: query.page,
      size: query.size,
      name: query.name || undefined,
      level: query.level ?? undefined
    })
    rows.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function onSearch() {
  query.page = 1
  fetchList()
}

function onReset() {
  query.name = ''
  query.level = null
  query.page = 1
  fetchList()
}

function openCreate() {
  isEdit.value = false
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, emptyForm(), row)
  dialogVisible.value = true
}

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  const lt5m = file.size / 1024 / 1024 <= 5
  if (!isImage) ElMessage.error('只能上传图片文件')
  if (!lt5m) ElMessage.error('图片不能超过 5MB')
  return isImage && lt5m
}

async function uploadAvatar(option) {
  uploading.value = true
  try {
    const url = await uploadImage(option.file, 'avatar')
    form.avatar = url
    ElMessage.success('头像已上传')
    option.onSuccess?.(url)
  } catch (e) {
    option.onError?.(e)
  } finally {
    uploading.value = false
  }
}

async function onSubmit() {
  const ok = await formRef.value.validate().catch(() => false)
  if (!ok) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateTeacher({ ...form })
      ElMessage.success('修改成功')
    } else {
      await createTeacher({ ...form })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitting.value = false
  }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确认删除讲师「${row.name}」？名下有课程时无法删除。`,
      '删除讲师',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }
  await removeTeacher(row.id)
  ElMessage.success('已删除')
  if (rows.value.length === 1 && query.page > 1) query.page -= 1
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.teacher {
  display: flex;
  flex-direction: column;
  gap: var(--sp-5);
}

/* ---------------- 页头 ---------------- */
.page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: var(--sp-6);
  padding-bottom: var(--sp-4);
  border-bottom: 1px solid var(--line-strong);
  animation: rise var(--dur-slow) var(--ease-out) both;
}

.page-head__title {
  font-size: var(--fs-4xl);
  line-height: 1;
  letter-spacing: -0.03em;
  margin-bottom: var(--sp-2);
}

.page-head__desc {
  font-size: var(--fs-sm);
  color: var(--muted);
  max-width: 46ch;
  line-height: 1.8;
  text-wrap: pretty;
}

.page-head__right {
  display: flex;
  align-items: center;
  gap: var(--sp-5);
  flex: none;
}

.page-head__count {
  font-size: var(--fs-2xl);
  font-family: var(--font-display);
  color: var(--green);
  line-height: 1;
}
.page-head__count i {
  font-style: normal;
  font-size: var(--fs-xs);
  color: var(--muted);
  margin-left: 4px;
  letter-spacing: 0.1em;
}

/* ---------------- 筛选条 ---------------- */
.filter {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  padding-bottom: var(--sp-4);
  border-bottom: 1px solid var(--line);
}

.filter__input { width: 240px; }
.filter__select { width: 150px; }

/* ---------------- 表格 ---------------- */
.table-wrap { min-height: 320px; }

.cell-teacher {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  min-width: 0;
}

.avatar {
  width: 38px;
  height: 38px;
  flex: none;
  border-radius: var(--r-pill);
  overflow: hidden;
  display: grid;
  place-items: center;
  background: var(--paper-3);
  border: 1px solid var(--line);
  font-family: var(--font-serif);
  font-size: var(--fs-md);
  color: var(--green);
}
.avatar img { width: 100%; height: 100%; object-fit: cover; }

.cell-teacher__text {
  display: flex;
  flex-direction: column;
  min-width: 0;
  line-height: 1.45;
}

.cell-teacher__name {
  font-family: var(--font-serif);
  font-size: var(--fs-lg);
  font-weight: 600;
  color: var(--ink);
  letter-spacing: 0.01em;
}

.cell-teacher__intro {
  font-size: var(--fs-xs);
  color: var(--muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 42ch;
}

.cell-muted { color: var(--muted); font-size: var(--fs-sm); }

.danger-text { color: var(--danger) !important; }

/* 操作列按钮收紧，避免窄屏被折叠成省略号 */
:deep(.el-table .cell .el-button.is-text) {
  padding: 4px 3px;
  margin-left: 0;
}
:deep(.el-table .cell .el-button.is-text + .el-button.is-text) {
  margin-left: 6px;
}

/* ---------------- 空态 ---------------- */
.empty {
  padding: var(--sp-8) 0 var(--sp-7);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--sp-2);
}

.empty__mark {
  font-family: var(--font-serif);
  font-size: 40px;
  color: var(--line-strong);
  line-height: 1;
  margin-bottom: var(--sp-2);
}

.empty__title {
  font-family: var(--font-serif);
  font-size: var(--fs-lg);
  color: var(--ink-2);
}

.empty__desc {
  font-size: var(--fs-sm);
  color: var(--muted);
  margin-bottom: var(--sp-3);
}

/* ---------------- 分页 ---------------- */
.pager {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: var(--sp-4);
}

.pager__meta {
  font-size: var(--fs-xs);
  color: var(--muted);
  letter-spacing: 0.08em;
}

/* ---------------- 弹窗表单 ---------------- */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--sp-4);
}

.upload {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
}

.upload__preview {
  width: 48px;
  height: 48px;
  border-radius: var(--r-pill);
  overflow: hidden;
  display: grid;
  place-items: center;
  border: 1px dashed var(--line-strong);
  background: var(--paper-3);
  color: var(--muted-2);
  font-size: var(--fs-xs);
  flex: none;
}
.upload__preview img { width: 100%; height: 100%; object-fit: cover; }

@media (max-width: 768px) {
  .page-head { flex-direction: column; align-items: flex-start; }
  .page-head__right { width: 100%; justify-content: space-between; }
  .filter { flex-wrap: wrap; }
  .filter__input, .filter__select { width: 100%; }
  .form-grid { grid-template-columns: 1fr; }
}
</style>
