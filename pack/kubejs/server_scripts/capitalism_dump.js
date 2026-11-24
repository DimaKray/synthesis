// Capitalism: вивантажує список усіх зареєстрованих предметів у kubejs/exported/capitalism_items.txt
// (потрібно генератору квестів tools/quests, щоб не брати неіснуючих предметів). Робить це при кожному запуску світу.
ServerEvents.loaded(event => {
  try {
    const BuiltInRegistries = Java.loadClass('net.minecraft.core.registries.BuiltInRegistries')
    const Files = Java.loadClass('java.nio.file.Files')
    const Paths = Java.loadClass('java.nio.file.Paths')
    const ids = []
    BuiltInRegistries.ITEM.keySet().forEach(k => ids.push(String(k)))
    ids.sort()
    const dir = Paths.get('kubejs', 'exported')
    Files.createDirectories(dir)
    Files.writeString(dir.resolve('capitalism_items.txt'), ids.join('\n'))
    console.info(`[capitalism] предметів вивантажено: ${ids.length}`)
  } catch (e) {
    console.error('[capitalism] не вдалось вивантажити предмети: ' + e)
  }
})
