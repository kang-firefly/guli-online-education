import puppeteer from 'puppeteer-core'
const B = 'http://122.51.6.69'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe',
  headless: 'new', args: ['--no-sandbox', '--disable-dev-shm-usage'] })
const p = await b.newPage()
await p.setViewport({ width: 1440, height: 950 })
p.on('pageerror', e => console.log('[pageerror]', e.message))

// 注册一个全新账号（保证没买过 course 2）
const mobile = '137' + String(Date.now()).slice(-8)
await p.goto(B + '/register', { waitUntil: 'networkidle2', timeout: 45000 })
await p.waitForSelector('input', { timeout: 15000 }); await sleep(600)
const ins = await p.$$('input')
console.log('注册页输入框数:', ins.length)
for (const [i, v] of [[0, '订单测试'], [1, mobile], [2, 'test1234'], [3, 'test1234']]) {
  if (!ins[i]) continue
  await ins[i].click({ clickCount: 3 }); await p.keyboard.press('Backspace')
  await ins[i].type(v, { delay: 15 })
}
await p.click('button[type="submit"]')
await sleep(3000)
console.log('注册后 URL:', p.url())

// 打开 course 2 详情 → 点购买
await p.goto(B + '/course/2', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(2000)
console.log('按钮:', await p.$eval('.hero__buy .btn', el => el.innerText.trim()).catch(() => 'none'))
await p.click('.hero__buy .btn')
await sleep(3500)
console.log('下单后 URL:', p.url())
await p.screenshot({ path: 'out/m7-7-order-pay.png' })

// 点模拟支付
await p.evaluate(() => {
  const btn = [...document.querySelectorAll('button')].find(b => b.textContent.includes('模拟支付'))
  btn?.click()
})
await sleep(3000)
await p.screenshot({ path: 'out/m7-8-order-paid.png' })
console.log('支付后状态:', await p.$eval('.badge', el => el.innerText).catch(() => 'none'))
await b.close()
