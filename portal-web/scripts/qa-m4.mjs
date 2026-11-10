/**
 * M4 前台视觉 QA：首页 / 课程列表 / 课程详情 / 名师 / 讲师详情 / 登录
 */
import puppeteer from 'puppeteer-core'
import { fileURLToPath } from 'node:url'
import path from 'node:path'

const CHROME = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe'
const BASE = 'http://127.0.0.1:5175'
const outDir = path.dirname(fileURLToPath(import.meta.url)) + '/../shots'
const sleep = (ms) => new Promise((r) => setTimeout(r, ms))

const browser = await puppeteer.launch({
  executablePath: CHROME,
  headless: 'new',
  args: ['--no-sandbox', '--disable-dev-shm-usage', '--font-render-hinting=none']
})
const page = await browser.newPage()
await page.setViewport({ width: 1440, height: 900 })
page.on('pageerror', (e) => console.log('[pageerror]', e.message))
page.on('console', (m) => { if (m.type() === 'error') console.log('[console]', m.text()) })

async function shot(url, name, wait = 1800, fullPage = false) {
  await page.goto(`${BASE}${url}`, { waitUntil: 'networkidle2' })
  await sleep(wait)
  await page.screenshot({ path: `${outDir}/${name}.png`, fullPage })
  console.log('shot', name)
}

await shot('/', 'p1-home', 2200, true)
await shot('/courses', 'p2-courses')
await shot('/course/1', 'p3-course-detail', 2000)
await shot('/teachers', 'p4-teachers')
await shot('/teacher/1', 'p5-teacher-detail', 2000)
await shot('/login', 'p6-login')

// 窄屏首页
await page.setViewport({ width: 390, height: 844 })
await shot('/', 'p7-home-mobile', 2000)

await browser.close()
