import puppeteer from 'puppeteer-core'
const B = 'http://122.51.6.69'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({
  executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe',
  headless: 'new', args: ['--no-sandbox', '--disable-dev-shm-usage']
})
const p = await b.newPage()
await p.setViewport({ width: 1440, height: 900 })
const errs = []
p.on('pageerror', e => errs.push('pageerror: ' + e.message))
p.on('console', m => { if (m.type() === 'error') errs.push('console: ' + m.text().slice(0, 120)) })

// 1) 前台首页
await p.goto(B + '/', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(2500)
await p.screenshot({ path: 'out/deploy-1-portal.png' })
console.log('前台标题:', await p.title())
console.log('前台卡片数:', await p.$$eval('.card', els => els.length).catch(() => 0))

// 2) 前台课程列表
await p.goto(B + '/courses', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(2000)
await p.screenshot({ path: 'out/deploy-2-courses.png' })

// 3) 后台登录页
await p.goto(B + '/admin-ui/login', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(1500)
await p.screenshot({ path: 'out/deploy-3-admin-login.png' })
console.log('后台标题:', await p.title())

// 4) 登录后台
try {
  const inputs = await p.$$('input')
  // 页面可能已预填，先全选清空再输入
  for (const [idx, val] of [[0, 'admin'], [1, 'admin123']]) {
    await inputs[idx].click({ clickCount: 3 })
    await p.keyboard.press('Backspace')
    await inputs[idx].type(val, { delay: 25 })
  }
  await p.click('button[type="submit"]')
  await sleep(3500)
  await p.screenshot({ path: 'out/deploy-4-admin-home.png' })
  console.log('登录后 URL:', p.url())
} catch (e) { console.log('登录脚本失败:', e.message) }

if (errs.length) console.log('页面报错:', errs.slice(0, 4))
else console.log('无 JS 报错')
await b.close()
