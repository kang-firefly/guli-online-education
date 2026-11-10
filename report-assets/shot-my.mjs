import puppeteer from 'puppeteer-core'
const B = 'http://122.51.6.69'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe',
  headless: 'new', args: ['--no-sandbox', '--disable-dev-shm-usage'] })
const p = await b.newPage()
await p.setViewport({ width: 1440, height: 1000 })
p.on('pageerror', e => console.log('[pageerror]', e.message))

await p.goto(B + '/login', { waitUntil: 'networkidle2', timeout: 45000 })
await p.waitForSelector('input', { timeout: 15000 }); await sleep(600)
const inputs = await p.$$('input')
for (const [i, v] of [[0, '13920525700'], [1, 'test1234']]) {
  await inputs[i].click({ clickCount: 3 }); await p.keyboard.press('Backspace')
  await inputs[i].type(v, { delay: 20 })
}
await p.click('button[type="submit"]'); await sleep(3000)

await p.goto(B + '/me', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(2000)
await p.screenshot({ path: 'out/m7-6-mycourses.png', fullPage: true })
console.log('我的课程条目数:', await p.$$eval('.course', els => els.length).catch(() => 0))
console.log('课程标题:', await p.$eval('.course__title', el => el.innerText).catch(() => 'none'))
await b.close()
