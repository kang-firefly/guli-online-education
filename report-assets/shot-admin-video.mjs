import puppeteer from 'puppeteer-core'
const B = 'http://122.51.6.69'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe',
  headless: 'new', args: ['--no-sandbox', '--disable-dev-shm-usage'] })
const p = await b.newPage()
await p.setViewport({ width: 1500, height: 950 })
p.on('pageerror', e => console.log('[pageerror]', e.message))

// 登录后台
await p.goto(B + '/admin-ui/login', { waitUntil: 'networkidle2', timeout: 45000 })
await p.waitForSelector('input', { timeout: 20000 }); await sleep(700)
let ins = await p.$$('input')
if (ins.length < 2) { await sleep(2500); ins = await p.$$('input') }
for (const [i, v] of [[0, 'admin'], [1, 'M5xmJTjPDJNadXzA']]) {
  if (!ins[i]) continue
  await ins[i].click({ clickCount: 3 }); await p.keyboard.press('Backspace')
  await ins[i].type(v, { delay: 20 })
}
await p.click('button[type="submit"]'); await sleep(3000)
console.log('登录后:', p.url())

// 进课程管理 → 编辑 Vue 课程（course 1）→ 大纲步骤
await p.goto(B + '/admin-ui/course', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(2000)
// 点第一行（Vue 3 从入门到实战）的"编辑"
await p.evaluate(() => {
  const rows = [...document.querySelectorAll('.el-table__body tr')]
  const row = rows.find(r => r.innerText.includes('Vue 3 从入门到实战'))
  const btns = [...(row || document).querySelectorAll('button')]
  const outline = btns.find(b => b.innerText.trim() === '大纲')
  outline?.click()
})
await sleep(3000)
// 进大纲：优先点"下一步：大纲"，否则点步骤条
await p.evaluate(() => {
  const btn = [...document.querySelectorAll('.el-dialog button')].find(b => b.innerText.includes('下一步'))
  btn?.click()
})
await sleep(2500)
const outlineVisible = await p.evaluate(() => {
  const el = document.querySelector('.pane--outline')
  return el ? getComputedStyle(el).display !== 'none' : false
})
console.log('大纲面板可见:', outlineVisible)
await p.screenshot({ path: 'out/m7-9-admin-outline.png' })
console.log('小节带视频标记数:', await p.$$eval('.section__video', els => els.length).catch(() => 0))

// 点某个小节的视频按钮
await p.evaluate(() => {
  const btns = [...document.querySelectorAll('.section button')]
  const v = btns.find(b => b.querySelector('svg') && b.getAttribute('aria-label') === '设置视频')
  v?.click()
})
await sleep(1500)
await p.screenshot({ path: 'out/m7-10-admin-video-dialog.png' })
console.log('对话框标题:', await p.$eval('.el-dialog__title', el => el.innerText).catch(() => 'none'))
console.log('输入框值:', await p.$eval('.el-dialog input', el => el.value).catch(() => 'none'))
await b.close()
