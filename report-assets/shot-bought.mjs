import puppeteer from 'puppeteer-core'
const B = 'http://122.51.6.69'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({
  executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe',
  headless: 'new', args: ['--no-sandbox', '--disable-dev-shm-usage']
})
const p = await b.newPage()
await p.setViewport({ width: 1440, height: 900 })
p.on('pageerror', e => console.log('[pageerror]', e.message))

// UI 登录（测试账号已购买 course 1）
await p.goto(B + '/login', { waitUntil: 'networkidle2', timeout: 45000 })
await p.waitForSelector('input', { timeout: 15000 })
await sleep(600)
const inputs = await p.$$('input')
for (const [i, v] of [[0, '13920525700'], [1, 'test1234']]) {
  await inputs[i].click({ clickCount: 3 }); await p.keyboard.press('Backspace')
  await inputs[i].type(v, { delay: 20 })
}
await p.click('button[type="submit"]')
await sleep(3000)
console.log('登录后 URL:', p.url())

// 详情页（应显示"继续学习"）
await p.goto(B + '/course/1', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(2000)
console.log('详情页按钮 =', await p.$eval('.hero__buy .btn', el => el.innerText.trim()).catch(() => 'none'))
await p.screenshot({ path: 'out/m7-4-detail-bought.png' })

// 播放页（应解锁，显示 B 站播放器）
await p.goto(B + '/course/1/play', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(4500)
console.log('播放页 iframe 数 =', await p.$$eval('iframe', els => els.length))
console.log('锁图标数 =', await p.$$eval('.item__lock', els => els.length))
await p.screenshot({ path: 'out/m7-5-play-bought.png' })

await b.close()
