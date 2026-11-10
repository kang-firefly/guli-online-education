<template>
  <RouterLink :to="`/course/${course.id}`" class="card">
    <div class="card__media">
      <img v-if="course.cover" :src="course.cover" :alt="course.title" loading="lazy" />
      <span v-else class="card__noimg">暂无封面</span>
      <span class="card__cat">{{ course.subjectTitle || course.subjectParentTitle }}</span>
    </div>

    <div class="card__body">
      <h3 class="card__title">{{ course.title }}</h3>
      <p class="card__teacher">
        {{ course.teacherName }}
        <span class="card__dot" aria-hidden="true">·</span>
        {{ course.lessonNum }} 课时
      </p>

      <div class="card__foot">
        <span class="card__price mono">{{ priceText }}</span>
        <span class="card__buy mono">{{ course.buyCount ?? 0 }} 人学过</span>
      </div>
    </div>
  </RouterLink>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  course: { type: Object, required: true }
})

const priceText = computed(() => {
  const p = Number(props.course.price ?? 0)
  return p === 0 ? '免费' : `¥ ${p.toFixed(2)}`
})
</script>

<style scoped>
.card {
  display: flex;
  flex-direction: column;
  background: var(--paper-2);
  border: 1px solid var(--line);
  transition: border-color var(--dur-base) var(--ease-out),
              transform var(--dur-base) var(--ease-out),
              box-shadow var(--dur-base) var(--ease-out);
}

.card:hover {
  border-color: var(--line-strong);
  transform: translateY(-4px);
  box-shadow: var(--shadow-2);
}

.card__media {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  background: var(--paper-3);
}

.card__media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--dur-slow) var(--ease-out);
}

.card:hover .card__media img { transform: scale(1.04); }

.card__noimg {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  font-size: var(--fs-xs);
  color: var(--muted-2);
}

/* 分类标签：贴着图片左下角，半透明墨底 */
.card__cat {
  position: absolute;
  left: 0;
  bottom: 0;
  padding: 5px 10px;
  background: rgba(13, 42, 32, 0.86);
  color: var(--paper);
  font-size: var(--fs-2xs);
  letter-spacing: 0.12em;
}

.card__body {
  padding: var(--sp-4) var(--sp-4) var(--sp-3);
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
  flex: 1;
}

.card__title {
  font-family: var(--font-serif);
  font-size: var(--fs-lg);
  font-weight: 600;
  line-height: 1.4;
  letter-spacing: 0;
  color: var(--ink);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color var(--dur-fast);
}

.card:hover .card__title { color: var(--green); }

.card__teacher {
  font-size: var(--fs-xs);
  color: var(--muted);
}

.card__dot { margin: 0 4px; color: var(--muted-2); }

.card__foot {
  margin-top: auto;
  padding-top: var(--sp-3);
  border-top: 1px solid var(--line);
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}

.card__price {
  font-size: var(--fs-lg);
  color: var(--green);
}

.card__buy {
  font-size: var(--fs-2xs);
  color: var(--muted-2);
}
</style>
