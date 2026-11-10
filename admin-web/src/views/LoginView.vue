<template>
  <div class="login">
    <!-- 左：墨绿书脊式版块，底部对齐，留大量空白 -->
    <section class="login__brand">
      <div class="brand__top">
        <span class="eyebrow eyebrow--invert">GULI ACADEMY</span>
        <span class="brand__year mono">2026</span>
      </div>

      <div class="brand__body">
        <h1 class="brand__title reveal reveal-1">谷粒<br />学苑</h1>
        <hr class="brand__rule reveal reveal-2" />
        <p class="brand__desc reveal reveal-3">
          在线教育平台 · 后台管理端<br />
          维护讲师、课程分类与课程大纲，<br />
          让每一节课都有据可循。
        </p>
      </div>

      <footer class="brand__foot reveal reveal-4">
        <span class="mono">v1.0 / M2</span>
        <span>讲师管理模块</span>
      </footer>

      <!-- 超大描边水印，破格元素 -->
      <span class="brand__watermark" aria-hidden="true">学</span>
    </section>

    <!-- 右：表单区，细线 + 小标签，大量留白 -->
    <section class="login__form">
      <div class="form__inner">
        <header class="form__head reveal reveal-2">
          <span class="eyebrow">01 — Sign in</span>
          <h2>登录后台</h2>
          <p class="form__sub">请使用管理员账号登录，继续管理平台内容。</p>
        </header>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="form__body reveal reveal-3"
          label-position="top"
          @submit.prevent="onSubmit"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              size="large"
              placeholder="请输入用户名"
              autocomplete="username"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              size="large"
              show-password
              placeholder="请输入密码"
              autocomplete="current-password"
              :prefix-icon="Lock"
              @keyup.enter="onSubmit"
            />
          </el-form-item>

          <el-button
            class="form__submit"
            type="primary"
            size="large"
            native-type="submit"
            :loading="loading"
          >
            {{ loading ? '正在登录…' : '登 录' }}
          </el-button>
        </el-form>

        <p class="form__hint reveal reveal-4">
          演示账号 <span class="mono">admin</span> / <span class="mono">admin123</span>
        </p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { auth } from '@/store/auth'

const router = useRouter()
const route = useRoute()

const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: 'admin', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function onSubmit() {
  if (loading.value) return
  const ok = await formRef.value.validate().catch(() => false)
  if (!ok) return

  loading.value = true
  try {
    const data = await login({ username: form.username, password: form.password })
    auth.set(data.token, {
      userId: data.userId,
      username: data.username,
      nickname: data.nickname,
      avatar: data.avatar
    })
    ElMessage.success(`欢迎回来，${data.nickname || data.username}`)
    router.replace(route.query.redirect || '/teacher')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login {
  position: relative;
  z-index: 2;
  min-height: 100vh;
  display: grid;
  grid-template-columns: 46fr 54fr;
  background: var(--paper);
}

/* ---------------- 左侧品牌版块 ---------------- */
.login__brand {
  position: relative;
  overflow: hidden;
  padding: var(--sp-6) var(--sp-7) var(--sp-7) var(--sp-7);
  background: var(--green-dim);
  color: var(--paper-2);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.brand__top {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  padding-bottom: var(--sp-4);
  border-bottom: 1px solid var(--line-invert);
}

.eyebrow--invert { color: rgba(255, 253, 247, 0.62); }
.brand__year { font-size: var(--fs-xs); color: rgba(255, 253, 247, 0.5); }

.brand__title {
  font-family: var(--font-display);
  font-size: clamp(40px, 5.2vw, 76px);
  line-height: 1.02;
  letter-spacing: -0.03em;
  color: var(--paper-2);
  margin-bottom: var(--sp-5);
}

.brand__rule {
  width: 96px;
  height: 2px;
  border: 0;
  background: var(--amber);
  margin: 0 0 var(--sp-5);
}

.brand__desc {
  font-size: var(--fs-md);
  line-height: 1.9;
  color: rgba(255, 253, 247, 0.72);
  max-width: 30ch;
}

.brand__foot {
  display: flex;
  gap: var(--sp-4);
  align-items: center;
  font-size: var(--fs-xs);
  color: rgba(255, 253, 247, 0.45);
  letter-spacing: 0.08em;
  padding-top: var(--sp-4);
  border-top: 1px solid var(--line-invert);
}

/* 破格：巨型描边字压在版块右下角，只作氛围不抢正文 */
.brand__watermark {
  position: absolute;
  right: -18%;
  bottom: -22%;
  font-family: var(--font-serif);
  font-size: 40rem;
  line-height: 1;
  color: transparent;
  -webkit-text-stroke: 1px rgba(255, 253, 247, 0.05);
  pointer-events: none;
  user-select: none;
}

/* ---------------- 右侧表单 ---------------- */
.login__form {
  display: grid;
  place-items: center;
  padding: var(--sp-7) var(--sp-6);
  position: relative;
}

/* 左侧那根的延伸：一条极细竖线，把两块版面对齐 */
.login__form::before {
  content: "";
  position: absolute;
  left: 0;
  top: 12%;
  bottom: 12%;
  width: 1px;
  background: linear-gradient(to bottom, transparent, var(--line-strong), transparent);
}

.form__inner {
  width: min(372px, 100%);
}

.form__head h2 {
  font-size: var(--fs-3xl);
  margin: var(--sp-2) 0 var(--sp-2);
}

.form__sub {
  font-size: var(--fs-sm);
  color: var(--muted);
  margin-bottom: var(--sp-6);
  line-height: 1.8;
}

.form__body :deep(.el-form-item) {
  margin-bottom: var(--sp-5);
}

.form__submit {
  width: 100%;
  height: 46px;
  letter-spacing: 0.18em;
  font-size: var(--fs-md);
  margin-top: var(--sp-2);
}

.form__hint {
  margin-top: var(--sp-5);
  font-size: var(--fs-xs);
  color: var(--muted-2);
  letter-spacing: 0.04em;
}
.form__hint .mono {
  color: var(--ink-2);
  background: var(--paper-3);
  padding: 1px 6px;
  border-radius: var(--r-xs);
}

/* 响应式：小屏收起品牌版块，只留表单 */
@media (max-width: 900px) {
  .login { grid-template-columns: 1fr; }
  .login__brand { display: none; }
  .login__form::before { display: none; }
  .login__form { padding: var(--sp-8) var(--sp-5); }
}
</style>
