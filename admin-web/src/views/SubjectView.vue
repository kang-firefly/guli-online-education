<template>
  <div class="subject">
    <header class="page-head">
      <div class="page-head__left">
        <h1 class="page-head__title">课程分类</h1>
        <p class="page-head__desc">
          只支持两级：一级是方向（前端 / 后端 / 数据库），二级是具体技术。课程必须挂在二级分类下。
        </p>
      </div>
      <div class="page-head__right">
        <span class="page-head__count mono">{{ leafCount }} <i>个二级</i></span>
        <el-button type="primary" :icon="Plus" @click="openCreate(null)">新增一级分类</el-button>
      </div>
    </header>

    <section class="table-wrap reveal reveal-2">
      <el-table
        v-loading="loading"
        :data="tree"
        row-key="id"
        default-expand-all
        :tree-props="{ children: 'children' }"
        :indent="22"
      >
        <el-table-column label="分类名称" min-width="260">
          <template #default="{ row }">
            <span class="name" :class="{ 'name--child': row.parentId }">{{ row.title }}</span>
          </template>
        </el-table-column>

        <el-table-column label="层级" width="120">
          <template #default="{ row }">
            <el-tag :type="row.parentId ? 'info' : 'warning'" disable-transitions>
              {{ row.parentId ? '二级' : '一级' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="子分类" width="110" align="right">
          <template #default="{ row }">
            <span class="mono cell-muted">{{ row.parentId ? '—' : row.children.length }}</span>
          </template>
        </el-table-column>

        <el-table-column label="排序" width="90" align="right">
          <template #default="{ row }">
            <span class="mono cell-muted">{{ row.sort ?? 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="ID" width="90" align="right">
          <template #default="{ row }">
            <span class="mono cell-muted">{{ row.id }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="230" align="right">
          <template #default="{ row }">
            <el-button v-if="!row.parentId" text size="small" @click="openCreate(row)">加子类</el-button>
            <el-button text size="small" @click="openEdit(row)">编辑</el-button>
            <el-button text size="small" class="danger-text" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>

        <template #empty>
          <div class="empty">
            <span class="empty__mark">空</span>
            <p class="empty__title">还没有分类</p>
            <p class="empty__desc">先建一个一级分类，再往下面加二级分类。</p>
          </div>
        </template>
      </el-table>
    </section>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑分类' : '新增分类'"
      width="460px"
      align-center
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="上级分类" prop="parentId">
          <el-select v-model="form.parentId" :disabled="isEdit && hasChildren" class="full">
            <el-option label="作为一级分类" :value="0" />
            <el-option
              v-for="p in firstLevels"
              :key="p.id"
              :label="`${p.title}（二级）`"
              :value="p.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="分类名称" prop="title">
          <el-input v-model="form.title" maxlength="20" show-word-limit placeholder="如：Vue" />
        </el-form-item>

        <el-form-item label="排序（越小越前）">
          <el-input-number v-model="form.sort" :min="0" :max="9999" controls-position="right" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">
          {{ isEdit ? '保存修改' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { subjectTree, createSubject, updateSubject, removeSubject } from '@/api/subject'

const loading = ref(false)
const tree = ref([])

const firstLevels = computed(() => tree.value)
const leafCount = computed(() => tree.value.reduce((n, r) => n + r.children.length, 0))

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const hasChildren = ref(false)
const formRef = ref(null)

const emptyForm = () => ({ id: null, title: '', parentId: 0, sort: 0 })
const form = reactive(emptyForm())

const rules = {
  title: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  parentId: [{ required: true, message: '请选择上级分类', trigger: 'change' }]
}

async function fetchTree() {
  loading.value = true
  try {
    tree.value = await subjectTree()
  } finally {
    loading.value = false
  }
}

function openCreate(parent) {
  isEdit.value = false
  hasChildren.value = false
  Object.assign(form, emptyForm())
  form.parentId = parent ? parent.id : 0
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  hasChildren.value = (row.children || []).length > 0
  Object.assign(form, { id: row.id, title: row.title, parentId: row.parentId, sort: row.sort })
  dialogVisible.value = true
}

async function onSubmit() {
  const ok = await formRef.value.validate().catch(() => false)
  if (!ok) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateSubject({ ...form })
      ElMessage.success('修改成功')
    } else {
      await createSubject({ ...form })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchTree()
  } finally {
    submitting.value = false
  }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确认删除分类「${row.title}」？有线下的子分类或被课程引用时无法删除。`,
      '删除分类',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }
  await removeSubject(row.id)
  ElMessage.success('已删除')
  fetchTree()
}

onMounted(fetchTree)
</script>

<style scoped>
.subject {
  display: flex;
  flex-direction: column;
  gap: var(--sp-5);
}

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
  max-width: 48ch;
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

.name {
  font-family: var(--font-serif);
  font-size: var(--fs-lg);
  font-weight: 600;
  color: var(--ink);
}

/* 二级分类缩进后字号略降，层级一眼可辨 */
.name--child {
  font-size: var(--fs-md);
  font-weight: 400;
  color: var(--ink-2);
}

.cell-muted { color: var(--muted); font-size: var(--fs-sm); }
.danger-text { color: var(--danger) !important; }

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
}
.empty__title { font-family: var(--font-serif); font-size: var(--fs-lg); color: var(--ink-2); }
.empty__desc { font-size: var(--fs-sm); color: var(--muted); }

.full { width: 100%; }

@media (max-width: 768px) {
  .page-head { flex-direction: column; align-items: flex-start; }
  .page-head__right { width: 100%; justify-content: space-between; }
}
</style>
