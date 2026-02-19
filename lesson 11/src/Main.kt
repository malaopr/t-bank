import kotlinx.coroutines.*
import java.io.File
import java.net.URL
import kotlin.system.measureTimeMillis

data class DownloadStats(
    val totalTime: Long,
    val successful: Int,
    val failed: Int
)

object ImageDownloader {
    fun run(urls: List<String>, outputDir: String): DownloadStats = runBlocking {
        File(outputDir).mkdirs()

        var successful = 0
        var failed = 0
        val total = urls.size

        println("Начинаем загрузку $total изображений.")

        val time = measureTimeMillis {
            withContext(Dispatchers.IO) {
                val jobs = urls.mapIndexed { index, url ->
                    launch {
                        try {
                            val fileName = "image_${index + 1}.jpg"
                            val file = File(outputDir, fileName)
                            URL(url).openStream().use { input ->
                                file.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            successful++
                            println("Загружено $successful/$total")
                        } catch (e: Exception) {
                            failed++
                            println("Ошибка загрузки $url: ${e.message}")
                        }
                    }
                }
                jobs.forEach { it.join() }
            }
        }

        println("Успешно: $successful, ошибок: $failed, время: ${time}мс")
        DownloadStats(time, successful, failed)
    }
}

fun main() {
    val urls = List(10) { "https://picsum.photos/200/300?random=$it" }
    val stats = ImageDownloader.run(urls, "downloads")
    println("Статистика: $stats")
}