import puppeteer from 'puppeteer-core'
const B = 'http://122.51.6.69'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe',
  headless: 'new', args: ['--no-sandbox', '--disable-dev-shm-usage'] })
const p = await b.newPage()
await p.setViewport({ width: 1500, height: 950 })
p.on('pageerror', e => console.log('[pageerror]', e.message))

await p.goto(B + '/admin-ui/login', { waitUntil: 'networkidle2', timeout: 45000 })
await p.waitForSelector('input', { timeout: 20000 }); await sleep(800)
let ins = await p.$$('input')
for (const [i, v] of [[0, 'admin'], [1, 'M5xmJTjPDJNadXzA']]) {
  await ins[i].click({ clickCount: 3 }); await p.keyboard.press('Backspace')
  await ins[i].type(v, { delay: 20 })
}
await p.click('button[type="submit"]'); await sleep(3000)

await p.goto(B + '/admin-ui/course', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(2500)
await p.evaluate(() => {
  const rows = [...document.querySelectorAll('.el-table__body tr')]
  const row = rows.find(r => r.innerText.includes('Vue 3 从入门到实战'))
  ;[...row.querySelectorAll('button')].find(b => b.innerText.trim() === '大纲')?.click()
})
await sleep(4000)

// 点第一小节的摄像机
const ok = await p.evaluate(() => {
  const btn = document.querySelector('.section button[aria-label="设置视频"]')
  if (!btn) return false
  btn.click(); return true
})
console.log('摄像机按钮存在:', ok)
await sleep(1800)
await p.screenshot({ path: 'out/m7-10-admin-video-dialog.png' })
const dlgTitle = await p.evaluate(() => {
  const t = [...document.querySelectorAll('.el-dialog__title')].map(e => e.innerText)
  return t.join(' / ')
})
const inputVal = await p.evaluate(() => {
  const dialogs = [...document.querySelectorAll('.el-dialog')]
  const target = dialogs.find(d => d.innerText.includes('设置视频链接'))
  return target ? target.querySelector('input')?.value : 'not found'
})
console.log('对话框:', dlgTitle)
console.log('输入框预填值:', inputVal)
await b.close()
