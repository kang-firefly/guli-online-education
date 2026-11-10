/**
 * M3 视觉 QA：分类树、课程列表、三步向导（基本信息 / 大纲 / 确认）
 */
import puppeteer from 'puppeteer-core'
import { fileURLToPath } from 'node:url'
import path from 'node:path'

const CHROME = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe'
const BASE = 'http://127.0.0.1:5173'
const outDir = path.dirname(fileURLToPath(import.meta.url)) + '/../shots'
const sleep = (ms) => new Promise((r) => setTimeout(r, ms))

const browser = await puppeteer.launch({
  executablePath: CHROME,
  headless: 'new',
  args: ['--no-sandbox', '--disable-dev-shm-usage', '--font-render-hinting=none']
})
const page = await browser.newPage()
await page.setViewport({ width: 1440, height: 900, deviceScaleFactor: 1 })
page.on('pageerror', (e) => console.log('[pageerror]', e.message))
page.on('console', (m) => { if (m.type() === 'error') console.log('[console]', m.text()) })

// 登录
await page.goto(`${BASE}/login`, { waitUntil: 'networkidle2' })
await page.click('input[autocomplete="current-password"]', { clickCount: 3 })
await page.type('input[autocomplete="current-password"]', 'admin123', { delay: 30 })
await page.click('button[type="submit"]')
await sleep(2000)

// 1) 课程分类
await page.goto(`${BASE}/subject`, { waitUntil: 'networkidle2' })
await sleep(1500)
await page.screenshot({ path: `${outDir}/05-subject.png` })
console.log('shot 05-subject')

// 2) 课程列表
await page.goto(`${BASE}/course`, { waitUntil: 'networkidle2' })
await sleep(1500)
await page.screenshot({ path: `${outDir}/06-course.png` })
console.log('shot 06-course')

// 3) 打开新建向导 → 基本信息
await page.evaluate(() => {
  const b = [...document.querySelectorAll('button')].find((x) => x.textContent.includes('新建课程'))
  b?.click()
})
await sleep(1200)
await page.screenshot({ path: `${outDir}/07-wizard-step1.png` })
console.log('shot 07-wizard-step1')

// 4) 大纲（直接点已有课程行上的「大纲」，跳过表单填写，稳定可复现）
await page.evaluate(() => {
  const close = document.querySelector('.el-dialog__headerbtn')
  close?.click()
})
await sleep(800)
await page.evaluate(() => {
  const rows = [...document.querySelectorAll('.el-table__row')]
  const row = rows.find((r) => r.textContent.includes('Vue 3 从入门到实战'))
  const btn = [...(row?.querySelectorAll('button') || [])].find((b) => b.textContent.trim() === '大纲')
  btn?.click()
})
await sleep(2000)
await page.screenshot({ path: `${outDir}/09-wizard-outline.png` })
console.log('shot 09-wizard-outline')

// 5) 确认页（下一步：确认）
await page.evaluate(() => {
  const b = [...document.querySelectorAll('.el-dialog button')].find((x) => x.textContent.includes('下一步：确认'))
  b?.click()
})
await sleep(1600)
await page.screenshot({ path: `${outDir}/10-wizard-confirm.png` })
console.log('shot 10-wizard-confirm')

await browser.close()
