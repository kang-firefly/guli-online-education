import puppeteer from 'puppeteer-core'
const B = 'http://122.51.6.69'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe',
  headless: 'new', args: ['--no-sandbox', '--disable-dev-shm-usage'] })
const p = await b.newPage()
await p.setViewport({ width: 1440, height: 1400 })
p.on('pageerror', e => console.log('[err]', e.message))

await p.goto(B + '/course/1', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(2500)
await p.screenshot({ path: 'out/deploy-7-course-outline.png', fullPage: true })
console.log('前台详情页章节数:', await p.$$eval('.chapter', e => e.length).catch(() => 'n/a'))
console.log('前台小节行数:', await p.$$eval('.section', e => e.length).catch(() => 'n/a'))
await b.close()
