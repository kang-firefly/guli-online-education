<template>
  <div class="pay">
    <div class="pay__inner" v-if="order">
      <div class="pay__head">
        <span class="eyebrow">Checkout</span>
        <h1 class="pay__title">订单确认</h1>
        <p class="pay__tip">演示环境：点击「模拟支付」即视为支付成功，不产生真实交易。</p>
      </div>

      <div class="card">
        <div class="card__course">
          <img :src="order.cover" class="card__cover" alt="" />
          <div class="card__info">
            <h2 class="card__title">{{ order.courseTitle }}</h2>
            <span class="card__no mono">订单号 {{ order.orderNo }}</span>
          </div>
        </div>

        <dl class="card__rows">
          <div class="row"><dt>课程金额</dt><dd class="mono">¥ {{ Number(order.amount).toFixed(2) }}</dd></div>
          <div class="row"><dt>下单时间</dt><dd class="mono">{{ order.gmtCreate?.replace('T', ' ') }}</dd></div>
          <div class="row" v-if="order.payTime">
            <dt>支付时间</dt><dd class="mono">{{ order.payTime.replace('T', ' ') }}</dd>
          </div>
          <div class="row">
            <dt>状态</dt>
            <dd><span class="badge" :class="statusClass">{{ statusText }}</span></dd>
          </div>
        </dl>

        <!-- 待支付 -->
        <div class="card__actions" v-if="order.status === 0">
          <button class="btn" type="button" :disabled="paying" @click="onPay">
            {{ paying ? '支付中…' : `模拟支付 ¥${Number(order.amount).toFixed(2)}` }}
          </button>
          <button class="btn-ghost" type="button" :disabled="paying" @click="onCancel">取消订单</button>
        </div>

        <!-- 已支付 -->
        <div class="card__actions" v-else-if="order.status === 1">
          <button class="btn" type="button" @click="goStudy">开始学习</button>
          <RouterLink :to="`/course/${order.courseId}`" class="btn-ghost">返回课程</RouterLink>
        </div>

        <!-- 已取消 -->
        <div class="card__actions" v-else>
          <RouterLink :to="`/course/${order.courseId}`" class="btn">重新选择课程</RouterLink>
        </div>
      </div>
    </div>

    <div class="pay__empty" v-else-if="loading">加载中…</div>
    <div class="pay__empty" v-else>订单不存在或无权查看</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrder, payOrder, cancelOrder } from '@/api/portal'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(true)
const paying = ref(false)

const orderNo = computed(() => route.params.orderNo)

const statusText = computed(() => ({ 0: '待支付', 1: '已支付', 2: '已取消' }[order.value?.status] ?? '—'))
const statusClass = computed(() => ({ 0: 'badge--warn', 1: 'badge--ok', 2: 'badge--muted' }[order.value?.status]))

async function load() {
  loading.value = true
  try {
    order.value = await getOrder(orderNo.value)
  } catch (e) {
    order.value = null
  } finally {
    loading.value = false
  }
}

async function onPay() {
  paying.value = true
  try {
    order.value = await payOrder(orderNo.value)
    ElMessage.success('支付成功，课程已解锁')
  } finally {
    paying.value = false
  }
}

async function onCancel() {
  try {
    await ElMessageBox.confirm('取消后需要重新下单，确认取消？', '取消订单', {
      confirmButtonText: '取消订单',
      cancelButtonText: '再想想',
      type: 'warning'
    })
  } catch {
    return
  }
  await cancelOrder(orderNo.value)
  ElMessage.success('订单已取消')
  await load()
}

function goStudy() {
  router.push({ name: 'play', params: { id: order.value.courseId } })
}

onMounted(load)
</script>

<style scoped>
.pay { min-height: 100vh; background: var(--paper); padding: var(--sp-6) var(--sp-5); }
.pay__inner { max-width: 720px; margin: 0 auto; }
.pay__head { margin-bottom: var(--sp-5); }
.pay__title {
  font-family: var(--font-display);
  font-size: 40px;
  margin: 6px 0 var(--sp-2);
  color: var(--ink);
}
.pay__tip { font-size: var(--fs-xs); color: var(--muted); }

.card { border: 1px solid var(--line); background: var(--paper-2); }
.card__course {
  display: flex;
  gap: var(--sp-4);
  padding: var(--sp-4);
  border-bottom: 1px solid var(--line);
}
.card__cover { width: 140px; height: 80px; object-fit: cover; border: 1px solid var(--line); flex-shrink: 0; }
.card__info { min-width: 0; display: flex; flex-direction: column; justify-content: center; gap: 6px; }
.card__title { font-family: var(--font-display); font-size: var(--fs-lg); margin: 0; color: var(--ink); }
.card__no { color: var(--muted-2); font-size: var(--fs-2xs); }

.card__rows { margin: 0; padding: var(--sp-4); display: flex; flex-direction: column; gap: 10px; }
.row { display: flex; justify-content: space-between; align-items: baseline; }
.row dt { font-size: var(--fs-xs); color: var(--muted); }
.row dd { margin: 0; font-size: var(--fs-sm); color: var(--ink); }

.badge {
  display: inline-block;
  padding: 2px 10px;
  font-size: var(--fs-2xs);
  border: 1px solid var(--line-strong);
}
.badge--warn { color: #b5761a; border-color: #e0c9a0; background: #fdf7ea; }
.badge--ok { color: var(--green); border-color: var(--green-2); background: #eef4f0; }
.badge--muted { color: var(--muted); }

.card__actions {
  display: flex;
  gap: var(--sp-3);
  padding: var(--sp-4);
  border-top: 1px solid var(--line);
}
.btn, .btn-ghost { padding: 12px 28px; font-size: var(--fs-sm); cursor: pointer; text-decoration: none; }
.btn { background: var(--green); color: var(--paper-2); border: 1px solid var(--green); }
.btn:hover { background: var(--green-2); }
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-ghost { background: transparent; color: var(--ink-2); border: 1px solid var(--line-strong); }
.btn-ghost:hover { border-color: var(--green); color: var(--green); }

.pay__empty { text-align: center; color: var(--muted); padding: var(--sp-6); }
</style>
