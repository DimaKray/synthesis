// Capitalism: вивантажує список усіх зареєстрованих предметів у kubejs/exported/capitalism_items.txt
// (потрібно генератору квестів tools/quests, щоб не брати неіснуючих предметів). Робить це при кожному запуску світу.
// Імена змінних з префіксом cap — щоб не конфліктувати з іншими скриптами (Rhino ділить глобальну область).
ServerEvents.loaded(event => {
  try {
    let capRegs = Java.loadClass('net.minecraft.core.registries.BuiltInRegistries')
    let capFiles = Java.loadClass('java.nio.file.Files')
    let capPaths = Java.loadClass('java.nio.file.Paths')
    let capIds = []
    capRegs.ITEM.keySet().forEach(k => capIds.push(String(k)))
    capIds.sort()
    let capDir = capPaths.get('kubejs', 'exported')
    capFiles.createDirectories(capDir)
    capFiles.writeString(capDir.resolve('capitalism_items.txt'), capIds.join('\n'))
    console.info(`[capitalism] предметів вивантажено: ${capIds.length}`)
  } catch (e) {
    console.error('[capitalism] не вдалось вивантажити предмети: ' + e)
  }
})
