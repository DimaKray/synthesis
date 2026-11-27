// Capitalism: вивантажує список усіх зареєстрованих предметів у kubejs/exported/capitalism_items.json
// (потрібно генератору квестів tools/quests, щоб не брати неіснуючих предметів). Робить це при кожному запуску світу.
// Імена змінних з префіксом cap — щоб не конфліктувати з іншими скриптами (Rhino ділить глобальну область).
// Файл пишемо через JsonIO (java.nio.file заборонений фільтром класів KubeJS).
ServerEvents.loaded(event => {
  try {
    let capRegs = Java.loadClass('net.minecraft.core.registries.BuiltInRegistries')
    let capIds = []
    capRegs.ITEM.keySet().forEach(k => capIds.push(String(k)))
    capIds.sort()
    JsonIO.write('kubejs/exported/capitalism_items.json', { count: capIds.length, ids: capIds })
    console.info(`[capitalism] предметів вивантажено: ${capIds.length}`)
  } catch (e) {
    console.error('[capitalism] не вдалось вивантажити предмети: ' + e)
  }
})
