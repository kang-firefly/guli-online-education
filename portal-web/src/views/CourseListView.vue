<template>
  <div class="list">
    <div class="wrap">
      <header class="head">
        <div>
          <span class="eyebrow">Course Library</span>
          <h1 class="head__title">全部课程</h1>
          <p class="head__desc">按方向筛选，或直接按价格、热度排序。</p>
        </div>

        <el-select v-model="sort" class="head__sort" @change="reload">
          <el-option label="最新上线" value="latest" />
          <el-option label="最热" value="hot" />
          <el-option label="价格从低到高" value="price_asc" />
          <el-option label="价格从高到低" value="price_desc" />
        </el-select>
      </header>

      <nav class="chips" aria-label="分类筛选">
        <button class="chip" :class="{ 'is-active': !subjectId && !parentId }" @click="clearFilter">全部</button>
        <template v-for="p in subjects" :key="p.id">
          <button class="chip" :class="{ 'is-active': parentId === p.id && !subjectId }" @click="filterParent(p.id)">
            {{ p.title }}
          </button>
          <button
            v-for="c in p.children"
            :key="c.id"
            class="chip chip--sub"
            :class="{ 'is-active': subjectId === c.id }"
            @click="filterSubject(c)"
          >
            {{ c.title }}
          </button>
        </template>
      </nav>

      <div v-if="loading" class="grid">
        <div v-for="n in 8" :key="n" class="skeleton" />
      </div>

      <div v-else-if="rows.length" class="grid">
        <CourseCard v-for="c in rows" :key="c.id" :course="c" />
      </div>

      <div v-else class="empty">
        <span class="empty__mark">空</span>
        <p class="empty__title">这个分类下还没有课程</p>
        <p class="empty__desc">换一个分类看看，或者回到全部课程。</p>
        <button class="btn" @click="clearFilter">看全部课程</button>
      </div>

      <div v-if="total > size" class="pager">
        <span class="pager__meta mono">共 {{ total }} 门 · 第 {{ page }} 页</span>
        <el-pagination
          v-model:current-page="page"
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="size"
          @current-change="fetchList"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { pageCourses, getIndex } from '@/api/portal'
import CourseCard from '@/components/CourseCard.vue'

const route = useRoute()
const router = useRouter()

const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(12)
const loading = ref(true)
const subjects = ref([])

const sort = ref(route.query.sort || 'latest')
const parentId = ref(route.query.parent ? Number(route.query.parent) : null)
const subjectId = ref(route.query.subject ? Number(route.query.subject) : null)

async function fetchList() {
  loading.value = true
  try {
    const d = await pageCourses({
      page: page.value,
      size: size.value,
      sort: sort.value,
      subjectParentId: parentId.value ?? undefined,
      subjectId: subjectId.value ?? undefined
    })
    rows.value = d.records || []
    total.value = d.total || 0
  } finally {
    loading.value = false
  }
}

function reload() {
  page.value = 1
  fetchList()
}

function clearFilter() {
  parentId.value = null
  subjectId.value = null
  router.replace({ path: '/courses' })
  reload()
}

function filterParent(id) {
  parentId.value = id
  subjectId.value = null
  router.replace({ path: '/courses', query: { parent: id } })
  reload()
}

function filterSubject(c) {
  subjectId.value = c.id
  parentId.value = null
  router.replace({ path: '/courses', query: { subject: c.id } })
  reload()
}

// 从首页分类点进来时，路由参数变化要重新拉取
watch(
  () => route.query,
  (q) => {
    parentId.value = q.parent ? Number(q.parent) : null
    subjectId.value = q.subject ? Number(q.subject) : null
    reload()
  }
)

onMounted(async () => {
  const d = await getIndex()
  subjects.value = d.subjects || []
  await fetchList()
})
</script>

<style scoped>
.list {
  position: relative;
  z-index: 2;
  padding: var(--sp-8) 0 var(--sp-9);
}

.head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: var(--sp-6);
  padding-bottom: var(--sp-5);
  border-bottom: 1px solid var(--line-strong);
}

.head__title {
  font-size: clamp(32px, 4vw, var(--fs-4xl));
  margin: var(--sp-2) 0 var(--sp-2);
}

.head__desc { font-size: var(--fs-sm); color: var(--muted); }

.head__sort { width: 170px; }

/* 分类 chips：文字标签 + 下划线，不用圆角胶囊 */
.chips {
  display: flex;
  flex-wrap: wrap;
  gap: var(--sp-4);
  padding: var(--sp-5) 0 var(--sp-6);
}

.chip {
  border: 0;
  background: transparent;
  padding: 4px 0;
  font-family: var(--font-body);
  font-size: var(--fs-md);
  color: var(--ink-2);
  cursor: pointer;
  border-bottom: 1px solid transparent;
  transition: color var(--dur-fast), border-color var(--dur-fast);
}

.chip:hover { color: var(--green); }
.chip.is-active {
  color: var(--green);
  border-color: var(--amber);
}

.chip--sub {
  font-size: var(--fs-sm);
  color: var(--muted);
}

.grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--sp-5);
}

.skeleton {
  aspect-ratio: 3 / 4;
  background: linear-gradient(100deg, var(--paper-3) 30%, #f7f2e7 50%, var(--paper-3) 70%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite linear;
}
@keyframes shimmer {
  from { background-position: 200% 0; }
  to { background-position: -200% 0; }
}

.empty {
  padding: var(--sp-9) 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--sp-2);
}
.empty__mark {
  font-family: var(--font-serif);
  font-size: 48px;
  color: var(--line-strong);
  line-height: 1;
}
.empty__title { font-family: var(--font-serif); font-size: var(--fs-xl); color: var(--ink-2); }
.empty__desc { font-size: var(--fs-sm); color: var(--muted); margin-bottom: var(--sp-3); }

.btn {
  height: 42px;
  padding: 0 var(--sp-5);
  background: var(--green);
  color: var(--paper);
  border: 0;
  font-family: var(--font-body);
  font-size: var(--fs-sm);
  cursor: pointer;
}
.btn:hover { background: var(--green-2); }

.pager {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: var(--sp-7);
  padding-top: var(--sp-5);
  border-top: 1px solid var(--line);
}

.pager__meta {
  font-size: var(--fs-xs);
  color: var(--muted);
  letter-spacing: 0.08em;
}

@media (max-width: 1100px) {
  .grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 560px) {
  .grid { grid-template-columns: 1fr; }
  .head { flex-direction: column; align-items: flex-start; }
}
</style>
