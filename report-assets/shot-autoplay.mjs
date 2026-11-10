import puppeteer from 'puppeteer-core'
const B = 'http://122.51.6.69'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe',
  headless: 'new', args: ['--no-sandbox', '--disable-dev-shm-usage'] })
const p = await b.newPage()
await p.setViewport({ width: 1440, height: 900 })

await p.goto(B + '/course/4/play', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(5000)
await p.screenshot({ path: 'out/autoplay-t1.png' })
// 再等 6 秒看播放器时间有没有往前走
await sleep(6000)
await p.screenshot({ path: 'out/autoplay-t2.png' })
console.log('截图完成，对比播放器时间')
await b.close()
