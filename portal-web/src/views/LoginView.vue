<template>
  <div class="auth">
    <div class="auth__form">
      <div class="auth__inner">
        <span class="eyebrow">Sign in</span>
        <h1 class="auth__title">欢迎回来</h1>
        <p class="auth__desc">用手机号登录，继续你的学习进度。</p>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="onSubmit">
          <el-form-item label="手机号" prop="username">
            <el-input v-model="form.username" size="large" placeholder="11 位手机号" autocomplete="username" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              size="large"
              show-password
              placeholder="6~20 位密码"
              autocomplete="current-password"
              @keyup.enter="onSubmit"
            />
          </el-form-item>

          <button class="submit" type="submit" :disabled="loading">
            {{ loading ? '登录中…' : '登 录' }}
          </button>
        </el-form>

        <p class="auth__switch">
          还没有账号？
          <RouterLink to="/register">免费注册 →</RouterLink>
        </p>
        <p class="auth__demo mono">演示账号 13800000000 / 123456</p>
      </div>
    </div>

    <aside class="auth__aside">
      <div class="auth__quote">
        <span class="eyebrow eyebrow--invert">Guli Academy</span>
        <p class="quote">“听得懂”和“写得出”之间，<br />隔着一整套练习。</p>
        <span class="quote__by">—— 谷粒学苑 · 教学笔记</span>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/portal'
import { auth } from '@/store/auth'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const rules = {
  username: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function onSubmit() {
  const ok = await formRef.value.validate().catch(() => false)
  if (!ok) return
  loading.value = true
  try {
    const d = await login({ username: form.username, password: form.password })
    auth.set(d.token, { id: d.userId, nickname: d.nickname, mobile: d.username })
    ElMessage.success(`欢迎回来，${d.nickname}`)
    router.replace(route.query.redirect || '/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth {
  position: relative;
  z-index: 2;
  display: grid;
  grid-template-columns: 1fr 46%;
  min-height: calc(100vh - var(--header-h));
}

.auth__form { display: grid; place-items: center; padding: var(--sp-8) var(--sp-5); }
.auth__inner { width: min(380px, 100%); }

.auth__title {
  font-size: clamp(32px, 4vw, 46px);
  margin: var(--sp-2) 0 var(--sp-2);
}

.auth__desc {
  font-size: var(--fs-sm);
  color: var(--muted);
  margin-bottom: var(--sp-6);
}

.submit {
  width: 100%;
  height: 50px;
  margin-top: var(--sp-3);
  background: var(--green);
  color: var(--paper);
  border: 0;
  font-family: var(--font-body);
  font-size: var(--fs-md);
  letter-spacing: 0.2em;
  cursor: pointer;
  transition: background var(--dur-fast), opacity var(--dur-fast);
}
.submit:hover { background: var(--green-2); }
.submit:disabled { opacity: 0.6; cursor: not-allowed; }

.auth__switch {
  margin-top: var(--sp-5);
  font-size: var(--fs-sm);
  color: var(--muted);
}
.auth__switch a {
  color: var(--green);
  border-bottom: 1px solid var(--green);
  padding-bottom: 1px;
}

.auth__demo {
  margin-top: var(--sp-4);
  font-size: var(--fs-2xs);
  color: var(--muted-2);
  letter-spacing: 0.06em;
}

.auth__aside {
  background: var(--green-dim);
  display: grid;
  place-items: center;
  padding: var(--sp-8);
  position: relative;
  overflow: hidden;
}

/* 右侧只留一条极细的琥珀竖线作装饰，不再放巨型描边字（大字只露出局部会像噪点） */
.auth__aside::before {
  content: "";
  position: absolute;
  right: var(--sp-7);
  top: 18%;
  bottom: 18%;
  width: 1px;
  background: linear-gradient(to bottom, transparent, rgba(217, 159, 66, 0.5), transparent);
}

.eyebrow--invert { color: rgba(251, 248, 241, 0.5); }

.quote {
  font-family: var(--font-serif);
  font-size: clamp(24px, 2.6vw, 34px);
  line-height: 1.6;
  color: var(--paper);
  margin: var(--sp-4) 0 var(--sp-4);
}

.quote__by {
  font-size: var(--fs-xs);
  color: rgba(251, 248, 241, 0.45);
  letter-spacing: 0.1em;
}

@media (max-width: 900px) {
  .auth { grid-template-columns: 1fr; }
  .auth__aside { display: none; }
}
</style>
