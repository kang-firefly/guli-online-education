import puppeteer from 'puppeteer-core'
const B = 'http://122.51.6.69'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe',
  headless: 'new', args: ['--no-sandbox', '--disable-dev-shm-usage'] })
const p = await b.newPage()
await p.setViewport({ width: 1500, height: 950 })

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
// 点第一行的"大纲"
const clicked = await p.evaluate(() => {
  const rows = [...document.querySelectorAll('.el-table__body tr')]
  const row = rows.find(r => r.innerText.includes('Vue 3 从入门到实战'))
  if (!row) return 'no row'
  const btns = [...row.querySelectorAll('button')]
  const names = btns.map(b => b.innerText.trim()).filter(Boolean)
  const outline = btns.find(b => b.innerText.trim() === '大纲')
  outline?.click()
  return names.join('|')
})
console.log('行内按钮:', clicked)
await sleep(3000)
// 打印弹窗内可见按钮
const btns = await p.evaluate(() => {
  const dlg = document.querySelector('.el-dialog')
  if (!dlg) return 'no dialog'
  return [...dlg.querySelectorAll('button')].map(b => b.innerText.trim() + (b.disabled ? '(disabled)' : '')).filter(Boolean).join(' | ')
})
console.log('弹窗按钮:', btns)
const panes = await p.evaluate(() => {
  const out = []
  ;['.pane--outline'].forEach(sel => {
    const el = document.querySelector(sel)
    out.push(sel + '=' + (el ? getComputedStyle(el).display : 'missing'))
  })
  return out.join(', ')
})
console.log('面板:', panes)
await p.screenshot({ path: 'out/dbg-outline.png' })
await b.close()
