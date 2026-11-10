import puppeteer from 'puppeteer-core'
const B = 'http://122.51.6.69'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe',
  headless: 'new', args: ['--no-sandbox', '--disable-dev-shm-usage'] })
const p = await b.newPage()
await p.setViewport({ width: 1440, height: 900 })
const errs = []
p.on('pageerror', e => errs.push('pageerror: ' + e.message))
p.on('console', m => { if (m.type() === 'error') errs.push('console: ' + m.text().slice(0, 100)) })

await p.goto(B + '/admin-ui/login', { waitUntil: 'networkidle2', timeout: 45000 })
await p.waitForSelector('input', { timeout: 20000 }).catch(() => {})
await sleep(800)
let inputs = await p.$$('input')
if (inputs.length < 2) {
  await sleep(3000)
  inputs = await p.$$('input')
}
console.log('输入框数量:', inputs.length)
for (const [i, v] of [[0, 'admin'], [1, 'admin123']]) {
  await inputs[i].click({ clickCount: 3 }); await p.keyboard.press('Backspace')
  await inputs[i].type(v, { delay: 20 })
}
await p.click('button[type="submit"]')
await sleep(3000)
console.log('登录后:', p.url())

await p.goto(B + '/admin-ui/banner', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(2000)
await p.screenshot({ path: 'out/deploy-5-banner-list.png' })
console.log('Banner 页标题:', await p.title())
console.log('表格行数:', await p.$$eval('.el-table__body tr', r => r.length).catch(() => 0))

// 打开新增弹窗
await p.evaluate(() => {
  const btn = [...document.querySelectorAll('button')].find(b => b.textContent.includes('新增轮播'))
  btn?.click()
})
await sleep(1200)
await p.screenshot({ path: 'out/deploy-6-banner-dialog.png' })
console.log('弹窗出现:', await p.$eval('.el-dialog', d => d.innerText.slice(0, 30)).catch(() => 'NO'))
console.log(errs.length ? '报错: ' + errs.slice(0, 3).join(' | ') : '无 JS 报错')
await b.close()
