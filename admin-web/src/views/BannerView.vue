<template>
  <div class="banner">
    <header class="page-head">
      <div class="page-head__left">
        <h1 class="page-head__title">首页运营</h1>
        <p class="page-head__desc">
          维护前台首页的轮播图。排序号越小越靠前；删除后前台立即不再展示。
        </p>
      </div>
      <div class="page-head__right">
        <span class="page-head__count mono">{{ rows.length }} <i>张</i></span>
        <el-button type="primary" :icon="Plus" @click="openCreate">新增轮播</el-button>
      </div>
    </header>

    <section class="table-wrap reveal reveal-2">
      <el-table
        v-loading="loading"
        :data="rows"
        row-key="id"
        empty-text="还没有轮播图，点右上角新增"
        @row-dblclick="openEdit"
      >
        <el-table-column label="预览" width="190">
          <template #default="{ row }">
            <span class="thumb">
              <img :src="row.imageUrl" :alt="row.title" loading="lazy" />
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="title" label="标题" min-width="200" />

        <el-table-column label="跳转链接" min-width="200">
          <template #default="{ row }">
            <span class="mono muted">{{ row.linkUrl || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="排序" width="90" align="right">
          <template #default="{ row }">
            <span class="mono">{{ row.sort ?? 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="onRemove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <!-- 新增 / 编辑 -->
    <el-dialog
      v-model="dialog"
      :title="form.id ? '编辑轮播' : '新增轮播'"
      width="580px"
      align-center
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" maxlength="50" show-word-limit placeholder="如 Vue 3 从入门到实战" />
        </el-form-item>

        <el-form-item label="轮播图片" prop="imageUrl">
          <div class="upload">
            <el-upload
              :show-file-list="false"
              accept="image/*"
              :before-upload="beforeUpload"
              :http-request="doUpload"
            >
              <div v-if="form.imageUrl" class="upload__preview">
                <img :src="form.imageUrl" alt="预览" />
                <span class="upload__mask">点击替换</span>
              </div>
              <div v-else class="upload__empty">
                <el-icon><Plus /></el-icon>
                <span>上传图片</span>
              </div>
            </el-upload>
            <p class="upload__tip">
              建议比例 16:6，单张 ≤ 5MB，jpg / png / webp
              <template v-if="uploading"> · <b>上传中…</b></template>
            </p>
          </div>
        </el-form-item>

        <el-form-item label="跳转链接">
          <el-input v-model="form.linkUrl" placeholder="如 /course/1，可留空" maxlength="200" />
        </el-form-item>

        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" controls-position="right" />
          <span class="form-hint">越小越靠前</span>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listBanners,
  createBanner,
  updateBanner,
  removeBanner,
  uploadBannerImage
} from '@/api/banner'

const rows = ref([])
const loading = ref(false)
const dialog = ref(false)
const saving = ref(false)
const uploading = ref(false)
const formRef = ref(null)

const form = reactive({ id: null, title: '', imageUrl: '', linkUrl: '', sort: 0 })

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请上传轮播图片', trigger: 'change' }]
}

async function load() {
  loading.value = true
  try {
    rows.value = await listBanners()
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.id = null
  form.title = ''
  form.imageUrl = ''
  form.linkUrl = ''
  form.sort = rows.value.length
}

function openCreate() {
  resetForm()
  dialog.value = true
}

function openEdit(row) {
  form.id = row.id
  form.title = row.title
  form.imageUrl = row.imageUrl
  form.linkUrl = row.linkUrl || ''
  form.sort = row.sort ?? 0
  dialog.value = true
}

function beforeUpload(file) {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片不能超过 5MB')
    return false
  }
  return true
}

async function doUpload(options) {
  uploading.value = true
  try {
    form.imageUrl = await uploadBannerImage(options.file)
    ElMessage.success('图片已上传')
  } finally {
    uploading.value = false
  }
}

async function onSubmit() {
  const ok = await formRef.value.validate().catch(() => false)
  if (!ok) return
  saving.value = true
  try {
    if (form.id) {
      await updateBanner(form.id, { ...form })
      ElMessage.success('已保存')
    } else {
      await createBanner({ ...form })
      ElMessage.success('已新增')
    }
    dialog.value = false
    await load()
  } finally {
    saving.value = false
  }
}

async function onRemove(row) {
  try {
    await ElMessageBox.confirm(
      `确认删除「${row.title}」？删除后前台首页不再展示。`,
      '删除轮播',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }
  await removeBanner(row.id)
  ElMessage.success('已删除')
  await load()
}

onMounted(load)
</script>

<style scoped>
.banner { display: flex; flex-direction: column; gap: var(--sp-5); }

/* 页头 */
.page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: var(--sp-5);
  padding-bottom: var(--sp-4);
  border-bottom: 1px solid var(--line);
}
.page-head__title {
  font-family: var(--font-display);
  font-size: 40px;
  line-height: 1;
  letter-spacing: -0.02em;
  margin: 0 0 var(--sp-2);
}
.page-head__desc {
  font-size: var(--fs-sm);
  color: var(--muted);
  max-width: 52ch;
  line-height: 1.7;
}
.page-head__right { display: flex; align-items: center; gap: var(--sp-4); }
.page-head__count { font-size: var(--fs-xl); color: var(--ink); }
.page-head__count i { font-style: normal; font-size: var(--fs-xs); color: var(--muted); }

.table-wrap {
  background: var(--paper-2);
  border: 1px solid var(--line);
  padding: var(--sp-2) var(--sp-3);
}

/* 缩略图 */
.thumb {
  display: block;
  width: 160px;
  height: 60px;
  overflow: hidden;
  background: var(--paper-3);
  border: 1px solid var(--line);
}
.thumb img { width: 100%; height: 100%; object-fit: cover; }

.mono { font-family: var(--font-mono); font-size: var(--fs-xs); }
.muted { color: var(--muted); }

/* 上传 */
.upload { display: flex; flex-direction: column; gap: 6px; }
.upload__empty {
  width: 260px;
  height: 98px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border: 1px dashed var(--line-strong);
  background: var(--paper);
  color: var(--muted);
  font-size: var(--fs-xs);
  cursor: pointer;
  transition: border-color var(--dur-fast), color var(--dur-fast);
}
.upload__empty:hover { border-color: var(--green); color: var(--green); }

.upload__preview {
  position: relative;
  width: 260px;
  height: 98px;
  overflow: hidden;
  border: 1px solid var(--line);
  cursor: pointer;
}
.upload__preview img { width: 100%; height: 100%; object-fit: cover; }
.upload__mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(18, 58, 44, 0.55);
  color: #fff;
  font-size: var(--fs-xs);
  opacity: 0;
  transition: opacity var(--dur-fast);
}
.upload__preview:hover .upload__mask { opacity: 1; }

.upload__tip { font-size: var(--fs-2xs); color: var(--muted-2); margin: 0; }
.form-hint { margin-left: var(--sp-3); font-size: var(--fs-2xs); color: var(--muted-2); }

@media (max-width: 900px) {
  .page-head { flex-direction: column; align-items: flex-start; }
  .page-head__title { font-size: 30px; }
}
</style>
