import puppeteer from 'puppeteer-core'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe', headless: 'new', args: ['--no-sandbox'] })
const p = await b.newPage()
await p.setViewport({ width: 1440, height: 900 })
// 未登录点立即报名 → 应跳登录
await p.goto('http://127.0.0.1:5175/course/1', { waitUntil: 'networkidle2' })
await p.evaluate(() => localStorage.clear())
await p.goto('http://127.0.0.1:5175/course/1', { waitUntil: 'networkidle2' })
await sleep(1200)
await p.evaluate(() => {
  const btn = [...document.querySelectorAll('button')].find(x => x.textContent.includes('立即报名'))
  btn?.click()
})
await sleep(1500)
console.log('[guest] enroll =>', p.url())
await b.close()
