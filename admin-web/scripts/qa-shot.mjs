/**
 * 视觉 QA：用本机 Chrome 打开登录页 → 真登录 → 讲师页，各截一张图
 * 用法: node scripts/qa-shot.mjs
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
page.on('console', (m) => {
  if (m.type() === 'error') console.log('[console error]', m.text())
})
page.on('pageerror', (e) => console.log('[pageerror]', e.message))

// 1) 登录页
await page.goto(`${BASE}/login`, { waitUntil: 'networkidle2' })
await sleep(1200)
await page.screenshot({ path: `${outDir}/01-login.png` })
console.log('shot: 01-login.png')

// 2) 执行登录
await page.type('input[autocomplete="username"]', '', { delay: 0 })
await page.click('input[autocomplete="current-password"]', { clickCount: 3 })
await page.type('input[autocomplete="current-password"]', 'admin123', { delay: 40 })
await page.click('button[type="submit"]')
await sleep(2200)
console.log('after login url =', page.url())

// 3) 讲师页
await page.goto(`${BASE}/teacher`, { waitUntil: 'networkidle2' })
await sleep(1600)
await page.screenshot({ path: `${outDir}/02-teacher.png` })
console.log('shot: 02-teacher.png')

// 4) 新增弹窗
const clicked = await page.evaluate(() => {
  const btns = [...document.querySelectorAll('button')]
  const b = btns.find((x) => x.textContent.replace(/\s/g, '').includes('新增讲师'))
  if (b) { b.click(); return true }
  return false
})
console.log('open dialog:', clicked)
await sleep(900)
await page.screenshot({ path: `${outDir}/03-dialog.png` })
console.log('shot: 03-dialog.png')

// 5) 窄屏（移动端答案）
await page.setViewport({ width: 390, height: 844, deviceScaleFactor: 1 })
await page.goto(`${BASE}/teacher`, { waitUntil: 'networkidle2' })
await sleep(1500)
await page.screenshot({ path: `${outDir}/04-mobile.png`, fullPage: false })
console.log('shot: 04-mobile.png')

await browser.close()
