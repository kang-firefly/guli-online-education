<template>
  <div class="course">
    <header class="page-head">
      <div class="page-head__left">
        <h1 class="page-head__title">课程</h1>
        <p class="page-head__desc">
          三步发布：填基本信息 → 编排章节小节 → 确认发布。信息或大纲不完整时不允许发布。
        </p>
      </div>
      <div class="page-head__right">
        <span class="page-head__count mono">{{ total }} <i>门</i></span>
        <el-button type="primary" :icon="Plus" @click="openCreate">新建课程</el-button>
      </div>
    </header>

    <section class="filter" aria-label="筛选条件">
      <el-input
        v-model="query.title"
        class="filter__input"
        placeholder="按标题搜索"
        clearable
        :prefix-icon="Search"
        @keyup.enter="onSearch"
        @clear="onSearch"
      />
      <el-select v-model="query.teacherId" class="filter__select" placeholder="全部讲师" clearable>
        <el-option v-for="t in teachers" :key="t.id" :label="t.name" :value="t.id" />
      </el-select>
      <el-select v-model="query.subjectId" class="filter__select" placeholder="全部分类" clearable>
        <el-option-group v-for="p in subjects" :key="p.id" :label="p.title">
          <el-option v-for="c in p.children" :key="c.id" :label="c.title" :value="c.id" />
        </el-option-group>
      </el-select>
      <el-select v-model="query.status" class="filter__select filter__select--sm" placeholder="全部状态" clearable>
        <el-option label="已发布" :value="1" />
        <el-option label="未发布" :value="0" />
      </el-select>
      <el-button @click="onSearch">查询</el-button>
      <el-button text @click="onReset">重置</el-button>
    </section>

    <section class="table-wrap reveal reveal-2">
      <el-table v-loading="loading" :data="rows" row-key="id">
        <el-table-column label="课程" min-width="260">
          <template #default="{ row }">
            <div class="cell-course">
              <span class="cover">
                <img v-if="row.cover" :src="row.cover" :alt="row.title" />
                <span v-else class="cover__empty">无</span>
              </span>
              <div class="cell-course__text">
                <span class="cell-course__title">{{ row.title }}</span>
                <span class="cell-course__meta">
                  {{ row.subjectParentTitle }} / {{ row.subjectTitle }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="讲师" width="100">
          <template #default="{ row }">
            <span class="cell-muted">{{ row.teacherName || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="价格" width="100" align="right">
          <template #default="{ row }">
            <span class="mono price">{{ Number(row.price) === 0 ? '免费' : '¥ ' + Number(row.price).toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="课时" width="70" align="right">
          <template #default="{ row }">
            <span class="mono cell-muted">{{ row.lessonNum }}</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'warning' : 'info'" disable-transitions>
              {{ row.status === 1 ? '已发布' : '未发布' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" width="172">
          <template #default="{ row }">
            <span class="mono cell-muted">{{ row.gmtCreate }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="212" align="right">
          <template #default="{ row }">
            <el-button text size="small" @click="openEdit(row)">编辑</el-button>
            <el-button text size="small" @click="openOutline(row)">大纲</el-button>
            <el-button
              text
              size="small"
              @click="row.status === 1 ? onOffline(row) : onPublish(row)"
            >
              {{ row.status === 1 ? '下架' : '发布' }}
            </el-button>
            <el-button text size="small" class="danger-text" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>

        <template #empty>
          <div class="empty">
            <span class="empty__mark">空</span>
            <p class="empty__title">还没有课程</p>
            <p class="empty__desc">点右上角「新建课程」，按三步向导发布第一门课。</p>
          </div>
        </template>
      </el-table>

      <div class="pager">
        <span class="pager__meta mono">
          第 {{ query.page }} / {{ Math.max(1, Math.ceil(total / query.size)) }} 页
        </span>
        <el-pagination
          v-model:current-page="query.page"
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="query.size"
          hide-on-single-page
          @current-change="fetchList"
        />
      </div>
    </section>

    <!-- ============ 三步向导 ============ -->
    <el-dialog
      v-model="wizardVisible"
      :title="courseId ? '编辑课程' : '新建课程'"
      width="900px"
      align-center
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-steps :active="step" finish-status="success" class="steps">
        <el-step title="基本信息" description="标题 / 讲师 / 分类 / 封面" />
        <el-step title="课程大纲" description="章节与小节" />
        <el-step title="确认发布" description="核对后发布" />
      </el-steps>

      <!-- ① 基本信息 -->
      <div v-show="step === 0" class="pane">
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="课程标题" prop="title">
            <el-input v-model="form.title" maxlength="50" show-word-limit placeholder="如：Vue 3 从入门到实战" />
          </el-form-item>

          <div class="grid-3">
            <el-form-item label="讲师" prop="teacherId">
              <el-select v-model="form.teacherId" placeholder="选择讲师" class="full">
                <el-option v-for="t in teachers" :key="t.id" :label="`${t.name}${t.level === 2 ? ' · 高级' : ''}`" :value="t.id" />
              </el-select>
            </el-form-item>

            <el-form-item label="一级分类" prop="parentId">
              <el-select v-model="form.parentId" placeholder="选择一级" class="full" @change="form.subjectId = null">
                <el-option v-for="p in subjects" :key="p.id" :label="p.title" :value="p.id" />
              </el-select>
            </el-form-item>

            <el-form-item label="二级分类" prop="subjectId">
              <el-select v-model="form.subjectId" placeholder="选择二级" class="full" :disabled="!form.parentId">
                <el-option
                  v-for="c in (subjects.find((p) => p.id === form.parentId)?.children || [])"
                  :key="c.id"
                  :label="c.title"
                  :value="c.id"
                />
              </el-select>
            </el-form-item>
          </div>

          <div class="grid-3">
            <el-form-item label="价格（元，0 为免费）" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" :step="10" controls-position="right" class="full" />
            </el-form-item>

            <el-form-item label="课时数">
              <el-input-number v-model="form.lessonNum" :min="0" :max="9999" controls-position="right" class="full" />
            </el-form-item>

            <el-form-item label="课程封面">
              <div class="upload">
                <span class="upload__preview upload__preview--wide">
                  <img v-if="form.cover" :src="form.cover" alt="封面预览" />
                  <span v-else class="upload__tip">16:9</span>
                </span>
                <el-upload :show-file-list="false" :http-request="uploadCover" accept="image/*" :before-upload="beforeUpload">
                  <el-button :loading="uploading" :icon="Upload" size="small">
                    {{ form.cover ? '重传' : '上传' }}
                  </el-button>
                </el-upload>
              </div>
            </el-form-item>
          </div>

          <el-form-item label="课程简介" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="4"
              maxlength="5000"
              show-word-limit
              placeholder="课程讲什么、适合谁、学完能做什么"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- ② 大纲 -->
      <div v-show="step === 1" class="pane pane--outline">
        <div class="outline">
          <section class="outline__col">
            <header class="outline__head">
              <span class="eyebrow">Chapters</span>
              <el-button text size="small" :icon="Plus" @click="openChapterDialog(null)">加章节</el-button>
            </header>

            <ul v-if="chapters.length" class="chapters">
              <li
                v-for="c in chapters"
                :key="c.id"
                class="chapter"
                :class="{ 'is-active': c.id === activeChapterId }"
                @click="activeChapterId = c.id"
              >
                <div class="chapter__main">
                  <span class="chapter__title">{{ c.title }}</span>
                  <span class="chapter__meta mono">{{ c.sections.length }} 节</span>
                </div>
                <div class="chapter__ops">
                  <el-button text size="small" :icon="Edit" aria-label="编辑章节" @click.stop="openChapterDialog(c)" />
                  <el-button text size="small" :icon="Delete" class="danger-text" aria-label="删除章节" @click.stop="onDeleteChapter(c)" />
                </div>
              </li>
            </ul>
            <p v-else class="outline__empty">还没有章节，先加一章。</p>
          </section>

          <section class="outline__col">
            <header class="outline__head">
              <span class="eyebrow">Sections</span>
              <el-button
                text
                size="small"
                :icon="Plus"
                :disabled="!activeChapter"
                @click="openSectionDialog(null)"
              >
                加小节
              </el-button>
            </header>

            <ul v-if="activeChapter && activeChapter.sections.length" class="sections">
              <li v-for="s in activeChapter.sections" :key="s.id" class="section">
                <span class="section__dot" aria-hidden="true" />
                <span class="section__title">{{ s.title }}</span>
                <span v-if="s.hasVideo" class="section__video" title="已配置视频">●</span>
                <span class="section__dur mono">{{ s.duration || '—' }}</span>
                <el-button
                  text size="small" :icon="VideoCamera" aria-label="设置视频"
                  :class="{ 'has-video': s.hasVideo }"
                  @click="openVideoDialog(s)"
                />
                <el-button text size="small" :icon="Edit" aria-label="编辑小节" @click="openSectionDialog(s)" />
                <el-button text size="small" :icon="Delete" class="danger-text" aria-label="删除小节" @click="onDeleteSection(s)" />
              </li>
            </ul>
            <p v-else class="outline__empty">
              {{ activeChapter ? '这一章还没有小节。' : '先在左边选中一个章节。' }}
            </p>
          </section>
        </div>
      </div>

      <!-- ③ 确认 -->
      <div v-show="step === 2" class="pane">
        <div class="confirm">
          <div class="confirm__cover">
            <img v-if="form.cover" :src="form.cover" alt="" />
            <span v-else>无封面</span>
          </div>
          <div class="confirm__info">
            <h3 class="confirm__title">{{ form.title }}</h3>
            <p class="confirm__meta">
              {{ subjectParentTitle }} / {{ subjectTitle }} · {{ teacherName }} ·
              <span class="mono">{{ Number(form.price) === 0 ? '免费' : '¥ ' + Number(form.price).toFixed(2) }}</span>
            </p>
            <p class="confirm__desc">{{ form.description || '（未填写简介）' }}</p>
            <div class="confirm__stats">
              <span class="mono">{{ chapters.length }}</span> 章 ·
              <span class="mono">{{ totalSections }}</span> 节
            </div>
            <p class="confirm__hint" :class="{ 'is-bad': blockers.length }">
              {{ blockers.length ? '还差：' + blockers.join('、') : '信息完整，可以发布。' }}
            </p>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="wizard-foot">
          <div class="wizard-foot__left">
            <el-button v-if="step > 0" text @click="step -= 1">上一步</el-button>
          </div>
          <div class="wizard-foot__right">
            <el-button @click="wizardVisible = false">取消</el-button>
            <el-button v-if="step === 0" :loading="saving" @click="saveBasic(false)">保存草稿</el-button>
            <el-button v-if="step < 2" type="primary" :loading="saving" @click="nextStep">
              {{ step === 0 ? '下一步：大纲' : '下一步：确认' }}
            </el-button>
            <el-button v-if="step === 2" type="primary" :disabled="blockers.length > 0" :loading="publishing" @click="onPublishInWizard">
              发布课程
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>

    <!-- 章节 / 小节 编辑弹窗 -->
    <el-dialog v-model="outlineDialog.visible" :title="outlineDialog.title" width="420px" align-center append-to-body>
      <el-form label-position="top">
        <el-form-item label="标题">
          <el-input v-model="outlineDialog.form.title" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item v-if="outlineDialog.type === 'section'" label="时长（如 12:30）">
          <el-input v-model="outlineDialog.form.duration" maxlength="20" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="outlineDialog.form.sort" :min="0" :max="999" controls-position="right" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="outlineDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="outlineDialog.saving" @click="submitOutline">保存</el-button>
      </template>
    </el-dialog>
  </div>

    <!-- 视频链接对话框 -->
    <el-dialog v-model="videoDialog.visible" title="设置视频链接" width="540px" align-center>
      <el-form label-width="88px">
        <el-form-item label="小节">
          <span class="video-section-name">{{ videoDialog.title }}</span>
        </el-form-item>
        <el-form-item label="B 站链接">
          <el-input
            v-model="videoDialog.url"
            placeholder="https://www.bilibili.com/video/BV1xx411c7mD?p=1"
            clearable
          />
          <p class="video-hint">
            只支持 bilibili.com 的完整视频链接（不支持 b23.tv 短链）；<b>留空即清除视频</b>。
          </p>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="videoDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="videoDialog.saving" @click="onSaveVideo">保存</el-button>
      </template>
    </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Upload, Edit, Delete, VideoCamera } from '@element-plus/icons-vue'
import {
  pageCourses, getCourse, createCourse, updateCourse, publishCourse,
  offlineCourse, removeCourse, createChapter, updateChapter, removeChapter,
  createSection, updateSection, removeSection, setSectionVideo, listChapters
} from '@/api/course'
import { subjectTree } from '@/api/subject'
import { allTeachers, uploadImage } from '@/api/teacher'

/* ---------------- 列表 ---------------- */
const loading = ref(false)
const rows = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, title: '', teacherId: null, subjectId: null, status: null })

const teachers = ref([])
const subjects = ref([])

async function fetchList() {
  loading.value = true
  try {
    const data = await pageCourses({
      page: query.page,
      size: query.size,
      title: query.title || undefined,
      teacherId: query.teacherId ?? undefined,
      subjectId: query.subjectId ?? undefined,
      status: query.status ?? undefined
    })
    rows.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function onSearch() { query.page = 1; fetchList() }
function onReset() {
  Object.assign(query, { page: 1, title: '', teacherId: null, subjectId: null, status: null })
  fetchList()
}

async function loadRefs() {
  const [ts, ss] = await Promise.all([allTeachers(), subjectTree()])
  teachers.value = ts || []
  subjects.value = ss || []
}

/* ---------------- 向导 ---------------- */
const wizardVisible = ref(false)
const step = ref(0)
const courseId = ref(null)
const saving = ref(false)
const publishing = ref(false)
const uploading = ref(false)
const formRef = ref(null)

const emptyForm = () => ({
  title: '', teacherId: null, parentId: null, subjectId: null,
  price: 0, lessonNum: 0, cover: '', description: ''
})
const form = reactive(emptyForm())

const rules = {
  title: [{ required: true, message: '请输入课程标题', trigger: 'blur' }],
  teacherId: [{ required: true, message: '请选择讲师', trigger: 'change' }],
  parentId: [{ required: true, message: '请选择一级分类', trigger: 'change' }],
  subjectId: [{ required: true, message: '请选择二级分类', trigger: 'change' }],
  price: [{ required: true, message: '请填写价格', trigger: 'blur' }]
}

const teacherName = computed(() => teachers.value.find((t) => t.id === form.teacherId)?.name || '—')
const subjectParentTitle = computed(() => subjects.value.find((p) => p.id === form.parentId)?.title || '—')
const subjectTitle = computed(
  () => subjects.value.find((p) => p.id === form.parentId)?.children.find((c) => c.id === form.subjectId)?.title || '—'
)

const chapters = ref([])
const activeChapterId = ref(null)
const activeChapter = computed(() => chapters.value.find((c) => c.id === activeChapterId.value) || null)
const totalSections = computed(() => chapters.value.reduce((n, c) => n + c.sections.length, 0))

const blockers = computed(() => {
  const b = []
  if (!form.title) b.push('课程标题')
  if (!form.teacherId) b.push('讲师')
  if (!form.subjectId) b.push('分类')
  if (!form.cover) b.push('封面')
  if (!form.description) b.push('简介')
  if (!chapters.value.length) b.push('至少一个章节')
  if (!totalSections.value) b.push('至少一个小节')
  return b
})

function resetWizard() {
  Object.assign(form, emptyForm())
  chapters.value = []
  activeChapterId.value = null
  step.value = 0
  courseId.value = null
}

function openCreate() {
  resetWizard()
  wizardVisible.value = true
}

async function openEdit(row) {
  resetWizard()
  await loadDetail(row.id)
  wizardVisible.value = true
}

async function openOutline(row) {
  resetWizard()
  await loadDetail(row.id)
  wizardVisible.value = true
  step.value = 1
}

async function loadDetail(id) {
  const d = await getCourse(id)
  courseId.value = d.id
  Object.assign(form, {
    title: d.title,
    teacherId: d.teacherId,
    parentId: d.subjectParentId,
    subjectId: d.subjectId,
    price: Number(d.price),
    lessonNum: d.lessonNum,
    cover: d.cover || '',
    description: d.description || ''
  })
  // 大纲必须走后台接口，否则拿不到每节的 videoUrl
  chapters.value = (await listChapters(id)) || []
  activeChapterId.value = chapters.value[0]?.id ?? null
}

function beforeUpload(file) {
  const okType = file.type.startsWith('image/')
  const okSize = file.size / 1024 / 1024 <= 5
  if (!okType) ElMessage.error('只能上传图片文件')
  if (!okSize) ElMessage.error('图片不能超过 5MB')
  return okType && okSize
}

async function uploadCover(option) {
  uploading.value = true
  try {
    form.cover = await uploadImage(option.file, 'cover')
    ElMessage.success('封面已上传')
    option.onSuccess?.()
  } catch (e) {
    option.onError?.(e)
  } finally {
    uploading.value = false
  }
}

/** 保存基本信息；返回是否成功 */
async function saveBasic(silent) {
  const ok = await formRef.value.validate().catch(() => false)
  if (!ok) return false
  saving.value = true
  try {
    const payload = {
      id: courseId.value,
      title: form.title,
      teacherId: form.teacherId,
      subjectId: form.subjectId,
      price: form.price,
      lessonNum: form.lessonNum,
      cover: form.cover,
      description: form.description
    }
    if (courseId.value) {
      await updateCourse(payload)
    } else {
      courseId.value = await createCourse(payload)
    }
    if (silent) ElMessage.success('草稿已保存')
    return true
  } finally {
    saving.value = false
  }
}

async function nextStep() {
  if (step.value === 0) {
    const ok = await saveBasic(true)
    if (!ok) return
    if (!chapters.value.length) chapters.value = []
    activeChapterId.value = chapters.value[0]?.id ?? null
    step.value = 1
    return
  }
  if (step.value === 1) {
    if (!courseId.value) {
      ElMessage.warning('请先完成基本信息')
      step.value = 0
      return
    }
    await refreshChapters()
    step.value = 2
  }
}

async function refreshChapters() {
  if (!courseId.value) return
  // 必须用后台接口：前台 /api/course/{id} 不返回 videoUrl
  chapters.value = (await listChapters(courseId.value)) || []
  if (!chapters.value.find((c) => c.id === activeChapterId.value)) {
    activeChapterId.value = chapters.value[0]?.id ?? null
  }
}

/* ---------------- 视频链接 ---------------- */
const videoDialog = reactive({ visible: false, saving: false, sectionId: null, title: '', url: '' })

function openVideoDialog(section) {
  videoDialog.sectionId = section.id
  videoDialog.title = section.title
  videoDialog.url = section.videoUrl || ''
  videoDialog.visible = true
}

async function onSaveVideo() {
  if (!videoDialog.sectionId) return
  const url = videoDialog.url.trim()
  videoDialog.saving = true
  try {
    await setSectionVideo(videoDialog.sectionId, url)
    ElMessage.success(url ? '视频链接已保存' : '已清除视频链接')
    videoDialog.visible = false
    await refreshChapters()
  } catch (e) {
    // 失败原因由 http 层统一提示
  } finally {
    videoDialog.saving = false
  }
}

/* ---------------- 大纲编辑 ---------------- */
const outlineDialog = reactive({
  visible: false, type: 'chapter', title: '', saving: false, form: { id: null, title: '', duration: '', sort: 0 }
})

function openChapterDialog(chapter) {
  if (!courseId.value) {
    ElMessage.warning('请先保存基本信息')
    return
  }
  outlineDialog.type = 'chapter'
  outlineDialog.title = chapter ? '编辑章节' : '新增章节'
  outlineDialog.form = { id: chapter?.id ?? null, title: chapter?.title ?? '', sort: chapter?.sort ?? chapters.value.length + 1 }
  outlineDialog.visible = true
}

function openSectionDialog(section) {
  if (!activeChapter.value) return
  outlineDialog.type = 'section'
  outlineDialog.title = section ? '编辑小节' : '新增小节'
  outlineDialog.form = {
    id: section?.id ?? null,
    title: section?.title ?? '',
    duration: section?.duration ?? '',
    sort: section?.sort ?? activeChapter.value.sections.length + 1
  }
  outlineDialog.visible = true
}

async function submitOutline() {
  const f = outlineDialog.form
  if (!f.title?.trim()) {
    ElMessage.warning('标题不能为空')
    return
  }
  outlineDialog.saving = true
  try {
    if (outlineDialog.type === 'chapter') {
      const payload = { id: f.id, courseId: courseId.value, title: f.title.trim(), sort: f.sort }
      if (f.id) await updateChapter(payload)
      else {
        const id = await createChapter(payload)
        activeChapterId.value = id
      }
    } else {
      const payload = {
        id: f.id, courseId: courseId.value, chapterId: activeChapter.value.id,
        title: f.title.trim(), duration: f.duration, sort: f.sort
      }
      if (f.id) await updateSection(payload)
      else await createSection(payload)
    }
    outlineDialog.visible = false
    await refreshChapters()
  } finally {
    outlineDialog.saving = false
  }
}

async function onDeleteChapter(chapter) {
  try {
    await ElMessageBox.confirm(`删除「${chapter.title}」，其下 ${chapter.sections.length} 个小节会一并删除。`, '删除章节', {
      confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
    })
  } catch { return }
  await removeChapter(chapter.id)
  ElMessage.success('已删除')
  await refreshChapters()
}

async function onDeleteSection(section) {
  try {
    await ElMessageBox.confirm(`删除小节「${section.title}」？`, '删除小节', {
      confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
    })
  } catch { return }
  await removeSection(section.id)
  ElMessage.success('已删除')
  await refreshChapters()
}

/* ---------------- 发布 / 下架 / 删除 ---------------- */
async function onPublishInWizard() {
  publishing.value = true
  try {
    await publishCourse(courseId.value)
    ElMessage.success('课程已发布')
    wizardVisible.value = false
    fetchList()
  } finally {
    publishing.value = false
  }
}

async function onPublish(row) {
  try {
    await publishCourse(row.id)
    ElMessage.success('课程已发布')
    fetchList()
  } catch { /* 缺失信息已在全局提示 */ }
}

async function onOffline(row) {
  try {
    await ElMessageBox.confirm(`下架「${row.title}」后前台将不可见，确认？`, '下架课程', {
      confirmButtonText: '下架', cancelButtonText: '取消', type: 'warning'
    })
  } catch { return }
  await offlineCourse(row.id)
  ElMessage.success('已下架')
  fetchList()
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`删除课程「${row.title}」？其大纲与简介会一并清理。`, '删除课程', {
      confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
    })
  } catch { return }
  await removeCourse(row.id)
  ElMessage.success('已删除')
  if (rows.value.length === 1 && query.page > 1) query.page -= 1
  fetchList()
}

onMounted(async () => {
  await loadRefs()
  await fetchList()
})
</script>

<style scoped>
.course { display: flex; flex-direction: column; gap: var(--sp-5); }

/* 页头（与讲师页一致） */
.page-head {
  display: flex; align-items: flex-end; justify-content: space-between;
  gap: var(--sp-6); padding-bottom: var(--sp-4);
  border-bottom: 1px solid var(--line-strong);
  animation: rise var(--dur-slow) var(--ease-out) both;
}
.page-head__title { font-size: var(--fs-4xl); line-height: 1; letter-spacing: -0.03em; margin-bottom: var(--sp-2); }
.page-head__desc { font-size: var(--fs-sm); color: var(--muted); max-width: 50ch; line-height: 1.8; text-wrap: pretty; }
.page-head__right { display: flex; align-items: center; gap: var(--sp-5); flex: none; }
.page-head__count { font-size: var(--fs-2xl); font-family: var(--font-display); color: var(--green); line-height: 1; }
.page-head__count i { font-style: normal; font-size: var(--fs-xs); color: var(--muted); margin-left: 4px; letter-spacing: 0.1em; }

/* 筛选 */
.filter { display: flex; align-items: center; gap: var(--sp-3); padding-bottom: var(--sp-4); border-bottom: 1px solid var(--line); }
.filter__input { width: 220px; }
.filter__select { width: 160px; }
.filter__select--sm { width: 130px; }

/* 表格 */
.cell-course { display: flex; align-items: center; gap: var(--sp-3); min-width: 0; }
.cover {
  width: 64px; height: 40px; flex: none; overflow: hidden;
  border: 1px solid var(--line); background: var(--paper-3);
  display: grid; place-items: center; border-radius: var(--r-xs);
}
.cover img { width: 100%; height: 100%; object-fit: cover; }
.cover__empty { font-size: 10px; color: var(--muted-2); letter-spacing: 0.1em; }
.cell-course__text { display: flex; flex-direction: column; min-width: 0; line-height: 1.45; }
.cell-course__title { font-family: var(--font-serif); font-size: var(--fs-lg); font-weight: 600; color: var(--ink); }
.cell-course__meta { font-size: var(--fs-xs); color: var(--muted); }

.cell-muted { color: var(--muted); font-size: var(--fs-sm); }
.price { color: var(--ink); font-size: var(--fs-md); }
.danger-text { color: var(--danger) !important; }

/* 操作列按钮收紧，避免被折叠成省略号或裁切 */
:deep(.el-table .cell .el-button.is-text) { padding: 4px 2px; margin-left: 0; }
:deep(.el-table .cell .el-button.is-text + .el-button.is-text) { margin-left: 4px; }

.pager { display: flex; align-items: center; justify-content: space-between; padding-top: var(--sp-4); }
.pager__meta { font-size: var(--fs-xs); color: var(--muted); letter-spacing: 0.08em; }

.empty { padding: var(--sp-8) 0 var(--sp-7); display: flex; flex-direction: column; align-items: center; gap: var(--sp-2); }
.empty__mark { font-family: var(--font-serif); font-size: 40px; color: var(--line-strong); line-height: 1; }
.empty__title { font-family: var(--font-serif); font-size: var(--fs-lg); color: var(--ink-2); }
.empty__desc { font-size: var(--fs-sm); color: var(--muted); }

/* 向导 */
.steps { margin: var(--sp-2) 0 var(--sp-6); }
.pane { min-height: 300px; }
.grid-3 { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: var(--sp-4); }
.full { width: 100%; }

.upload { display: flex; align-items: center; gap: var(--sp-2); }
.upload__preview {
  width: 68px; height: 40px; flex: none; overflow: hidden;
  border: 1px dashed var(--line-strong); background: var(--paper-3);
  display: grid; place-items: center; border-radius: var(--r-xs);
}
.upload__preview img { width: 100%; height: 100%; object-fit: cover; }
.upload__tip { font-size: 10px; color: var(--muted-2); letter-spacing: 0.08em; }

/* 大纲 */
.pane--outline { min-height: 340px; }
.outline { display: grid; grid-template-columns: 1fr 1fr; gap: var(--sp-5); }
.outline__col { border: 1px solid var(--line); background: var(--paper-2); }
.outline__head {
  display: flex; align-items: center; justify-content: space-between;
  padding: var(--sp-3) var(--sp-4); border-bottom: 1px solid var(--line);
  background: var(--paper-3);
}
.outline__empty { padding: var(--sp-6) var(--sp-4); font-size: var(--fs-sm); color: var(--muted-2); text-align: center; }

.chapters, .sections { list-style: none; margin: 0; padding: var(--sp-2); max-height: 320px; overflow: auto; }

.chapter {
  display: flex; align-items: center; gap: var(--sp-2);
  padding: var(--sp-3); cursor: pointer; border-left: 2px solid transparent;
  transition: background var(--dur-fast) var(--ease-out);
}
.chapter:hover { background: var(--paper-3); }
.chapter.is-active { background: var(--paper-3); border-left-color: var(--amber); }
.chapter__main { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.chapter__title { font-family: var(--font-serif); font-size: var(--fs-md); font-weight: 600; }
.chapter__meta { font-size: var(--fs-2xs); color: var(--muted); }
.chapter__ops { display: flex; opacity: 0.55; transition: opacity var(--dur-fast); }
.chapter:hover .chapter__ops { opacity: 1; }

.section {
  display: flex; align-items: center; gap: var(--sp-2);
  padding: var(--sp-2) var(--sp-3); border-bottom: 1px solid var(--line);
}
.section:last-child { border-bottom: 0; }
.section__dot { width: 3px; height: 3px; border-radius: 50%; background: var(--muted-2); flex: none; }
.section__title { flex: 1; font-size: var(--fs-sm); color: var(--ink-2); min-width: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.section__dur { font-size: var(--fs-2xs); color: var(--muted); }

/* 确认页 */
.confirm { display: grid; grid-template-columns: 240px 1fr; gap: var(--sp-6); align-items: start; }
.confirm__cover {
  aspect-ratio: 16 / 10; border: 1px solid var(--line); background: var(--paper-3);
  display: grid; place-items: center; color: var(--muted-2); font-size: var(--fs-xs); overflow: hidden;
}
.confirm__cover img { width: 100%; height: 100%; object-fit: cover; }
.confirm__title { font-size: var(--fs-2xl); margin-bottom: var(--sp-2); }
.confirm__meta { font-size: var(--fs-sm); color: var(--muted); margin-bottom: var(--sp-4); }
.confirm__desc {
  font-size: var(--fs-sm); color: var(--ink-2); line-height: 1.9;
  padding: var(--sp-3) 0; border-top: 1px solid var(--line); border-bottom: 1px solid var(--line);
  max-height: 140px; overflow: auto; white-space: pre-wrap;
}
.confirm__stats { margin-top: var(--sp-3); font-size: var(--fs-sm); color: var(--muted); }
.confirm__stats .mono { color: var(--green); font-size: var(--fs-lg); }
.confirm__hint { margin-top: var(--sp-3); font-size: var(--fs-sm); color: var(--success); }
.confirm__hint.is-bad { color: var(--danger); }

.wizard-foot { display: flex; align-items: center; justify-content: space-between; }
.wizard-foot__right { display: flex; gap: var(--sp-2); }

@media (max-width: 768px) {
  .page-head { flex-direction: column; align-items: flex-start; }
  .page-head__right { width: 100%; justify-content: space-between; }
  .filter { flex-wrap: wrap; }
  .filter__input, .filter__select, .filter__select--sm { width: 100%; }
  .grid-3 { grid-template-columns: 1fr; }
  .outline { grid-template-columns: 1fr; }
  .confirm { grid-template-columns: 1fr; }
}

/* ---------- 视频链接 ---------- */
.section__video {
  color: #2f8f5b;
  font-size: 8px;
  line-height: 1;
  margin-left: -2px;
}
.section .el-button.has-video { color: #2f8f5b; }

.video-section-name {
  font-size: 13px;
  color: var(--ink);
}
.video-hint {
  margin: 6px 0 0;
  font-size: 11px;
  line-height: 1.6;
  color: var(--muted-2);
}
.video-hint b { color: #b5761a; font-weight: 600; }
</style>
