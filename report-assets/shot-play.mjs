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

// ① 免费课播放页（course 4，有视频）
await p.goto(B + '/course/4/play', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(4000)
await p.screenshot({ path: 'out/m7-1-play-free.png' })
const frameCount = await p.$$eval('iframe', els => els.length)
const itemCount = await p.$$eval('.item', els => els.length)
const activeText = await p.$eval('.item.is-active', el => el.innerText.replace(/\n/g, ' ')).catch(() => 'none')
console.log('免费课：iframe数=', frameCount, '小节数=', itemCount, '当前=', activeText)

// ② 付费课播放页（course 1，未登录 → 锁定）
await p.goto(B + '/course/1/play', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(2500)
await p.screenshot({ path: 'out/m7-2-play-locked.png' })
const lockedText = await p.$eval('.locked-mask__text', el => el.innerText).catch(() => 'none')
const lockIcons = await p.$$eval('.item__lock', els => els.length)
console.log('付费课未登录：锁定文案=', lockedText, '列表锁图标=', lockIcons)

// ③ 课程详情页按钮（未登录，应付费课显示"登录后购买"）
await p.goto(B + '/course/1', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(2000)
const btnText = await p.$eval('.hero__buy .btn', el => el.innerText.trim()).catch(() => 'none')
console.log('详情页按钮（未登录，付费课）=', btnText)
await p.screenshot({ path: 'out/m7-3-detail-btn.png' })

await b.close()
