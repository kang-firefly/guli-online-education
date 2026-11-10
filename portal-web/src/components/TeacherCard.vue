<template>
  <RouterLink :to="`/teacher/${teacher.id}`" class="tcard">
    <span class="tcard__avatar">
      <img v-if="teacher.avatar" :src="teacher.avatar" :alt="teacher.name" loading="lazy" />
      <template v-else>{{ teacher.name?.slice(0, 1) }}</template>
    </span>

    <div class="tcard__body">
      <div class="tcard__head">
        <h3 class="tcard__name">{{ teacher.name }}</h3>
        <span class="tcard__level">{{ teacher.level === 2 ? '高级讲师' : '讲师' }}</span>
      </div>
      <p class="tcard__career">{{ teacher.career || '暂无资历说明' }}</p>
      <p class="tcard__intro">{{ teacher.intro || '' }}</p>
    </div>
  </RouterLink>
</template>

<script setup>
defineProps({
  teacher: { type: Object, required: true }
})
</script>

<style scoped>
.tcard {
  display: flex;
  gap: var(--sp-4);
  padding: var(--sp-4);
  background: var(--paper-2);
  border: 1px solid var(--line);
  transition: border-color var(--dur-base) var(--ease-out),
              transform var(--dur-base) var(--ease-out);
}

.tcard:hover {
  border-color: var(--line-strong);
  transform: translateY(-3px);
}

.tcard__avatar {
  width: 64px;
  height: 64px;
  flex: none;
  border-radius: 50%;
  overflow: hidden;
  display: grid;
  place-items: center;
  background: var(--paper-3);
  border: 1px solid var(--line);
  font-family: var(--font-serif);
  font-size: var(--fs-xl);
  color: var(--green);
}

.tcard__avatar img { width: 100%; height: 100%; object-fit: cover; }

.tcard__body { min-width: 0; }

.tcard__head {
  display: flex;
  align-items: baseline;
  gap: var(--sp-2);
}

.tcard__name {
  font-family: var(--font-serif);
  font-size: var(--fs-lg);
  font-weight: 600;
  letter-spacing: 0.02em;
}

.tcard__level {
  font-size: var(--fs-2xs);
  letter-spacing: 0.1em;
  color: var(--amber);
  border: 1px solid rgba(189, 125, 30, 0.4);
  padding: 1px 5px;
  border-radius: var(--r-xs);
}

.tcard__career {
  margin-top: 4px;
  font-size: var(--fs-xs);
  color: var(--muted);
  display: -webkit-box;
  -webkit-line-clamp: 1;
  line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tcard__intro {
  margin-top: 6px;
  font-size: var(--fs-xs);
  color: var(--ink-2);
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
