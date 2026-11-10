<template>
  <div class="teachers">
    <div class="wrap">
      <header class="head">
        <span class="eyebrow">Faculty</span>
        <h1 class="head__title">名师阵容</h1>
        <p class="head__desc">
          他们写过大流量的系统，也带过零基础的班。{{ total }} 位讲师，按级别排序。
        </p>
      </header>

      <div v-if="loading" class="grid">
        <div v-for="n in 4" :key="n" class="skeleton" />
      </div>

      <div v-else class="grid">
        <TeacherCard v-for="t in rows" :key="t.id" :teacher="t" class="reveal" />
      </div>

      <div v-if="total > size" class="pager">
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
import { ref, onMounted } from 'vue'
import { pageTeachers } from '@/api/portal'
import TeacherCard from '@/components/TeacherCard.vue'

const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(8)
const loading = ref(true)

async function fetchList() {
  loading.value = true
  try {
    const d = await pageTeachers({ page: page.value, size: size.value })
    rows.value = d.records || []
    total.value = d.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(fetchList)
</script>

<style scoped>
.teachers {
  position: relative;
  z-index: 2;
  padding: var(--sp-8) 0 var(--sp-9);
}

.head {
  padding-bottom: var(--sp-5);
  border-bottom: 1px solid var(--line-strong);
  margin-bottom: var(--sp-6);
}

.head__title {
  font-size: clamp(32px, 4vw, var(--fs-4xl));
  margin: var(--sp-2) 0 var(--sp-2);
}

.head__desc { font-size: var(--fs-sm); color: var(--muted); max-width: 56ch; }

.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--sp-4);
}

.skeleton {
  height: 132px;
  background: linear-gradient(100deg, var(--paper-3) 30%, #f7f2e7 50%, var(--paper-3) 70%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite linear;
}
@keyframes shimmer {
  from { background-position: 200% 0; }
  to { background-position: -200% 0; }
}

.pager {
  margin-top: var(--sp-6);
  padding-top: var(--sp-5);
  border-top: 1px solid var(--line);
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 860px) {
  .grid { grid-template-columns: 1fr; }
}
</style>
