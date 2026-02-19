# Семинар: Многопоточность и корутины в Kotlin

---

## Часть 1. Потоки (Thread)

### Задание 1. Создание потоков
Создайте 3 потока с именами "Thread-A", "Thread-B", "Thread-C". Каждый поток должен вывести своё имя 5 раз с задержкой 500мс.

```kotlin
object CreateThreads {
    fun run(): List<Thread> {
        val threads = listOf("Thread-A", "Thread-B", "Thread-C").map { name ->
            object : Thread(name) {
                override fun run() {
                    for (i in 1..5) {
                        println("$name - iteration $i")
                        Thread.sleep(500)
                    }
                }
            }
        }
        threads.forEach { it.start() }
        return threads
    }
}
```

### Задание 2. Race condition
Создайте переменную `counter = 0`. Запустите 10 потоков, каждый из которых увеличивает counter на 1000. Выведите финальное значение и объясните результат.

```kotlin
object RaceCondition {
    fun run(): Int {
        var counter = 0
        val threads = (1..10).map {
            Thread { repeat(1000) { counter++ } }.apply { start() }
        }
        threads.forEach { it.join() }
        println("Final counter: $counter")
        return counter
    }
}

fun main() {
    RaceCondition.run()
}
```

### Задание 3. Synchronized
Исправьте задание 2 с помощью `@Synchronized` или `synchronized {}` блока, чтобы результат всегда был 10000.

```kotlin
object SynchronizedCounter {
    fun run(): Int {
        var counter = 0
        List(10) {
            Thread {
                repeat(1000) {
                    synchronized(this) { counter++ }
                }
            }.apply { start() }
        }.forEach { it.join() }
        println("Окончательный счетчик: $counter")
        return counter
    }
}
```

### Задание 4. Deadlock
Создайте пример deadlock с двумя ресурсами и двумя потоками. Затем исправьте его.

```kotlin
object Deadlock {
    val r1 = Any()
    val r2 = Any()

    @JvmStatic
    fun main(args: Array<String>) {
        runFixed()
        Thread.sleep(200)
    }

    fun runDeadlock() {
        Thread { synchronized(r1) { Thread.sleep(100); synchronized(r2) {} } }.start()
        Thread { synchronized(r2) { Thread.sleep(100); synchronized(r1) {} } }.start()
    }

    fun runFixed() {
        Thread { synchronized(r1) { Thread.sleep(100); synchronized(r2) {} } }.start()
        Thread { synchronized(r1) { Thread.sleep(100); synchronized(r2) {} } }.start()
    }
}
```

---

## Часть 2. Executor Framework

### Задание 5. ExecutorService
Используя `Executors.newFixedThreadPool(4)`, выполните 20 задач. Каждая задача выводит свой номер и имя потока, затем спит 200мс.

```kotlin
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val executor = Executors.newFixedThreadPool(4)

    for (i in 1..20) {
        executor.submit {
            println("Задача $i выполняется в потоке ${Thread.currentThread().name}")
            Thread.sleep(200)
        }
    }

    executor.shutdown()
    executor.awaitTermination(5, TimeUnit.SECONDS)
}
```

### Задание 6. Future
Используя ExecutorService и `Callable`, параллельно вычислите факториалы чисел от 1 до 10. Соберите результаты через `Future.get()`.

```kotlin
object ExecutorServiceExample {
    fun run() = mutableListOf<String>().apply {
        java.util.concurrent.Executors.newFixedThreadPool(4).let { executor ->
            (1..20).forEach { i ->
                executor.submit {
                    Thread.sleep(200)
                    val result = "Задача $i в ${Thread.currentThread().name}"
                    println(result)
                    add(result)
                }
            }
            executor.shutdown()
            while (!executor.isTerminated) Thread.sleep(10)
        }
    }
}
```

---

## Часть 3. Корутины

### Задание 7. Первая корутина
Используя `runBlocking` и `launch`, запустите 3 корутины, каждая из которых выводит своё имя 5 раз с `delay(500)`.

```kotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object CoroutineLaunch {
    fun run(): List<String> = runBlocking {
        val results = mutableListOf<String>()
        val jobs = List(3) { index ->
            launch {
                repeat(5) { iteration ->
                    delay(500)
                    val name = "Корутина${index + 1}"
                    results.add("$name: итерация ${iteration + 1}")
                }
            }
        }
        
        jobs.forEach { it.join() }
        results
    }
}
```

### Задание 8. async/await
Используя `async`, параллельно вычислите сумму чисел от 1 до 1_000_000, разбив на 4 части. Соберите результаты через `await()`.

```kotlin
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

object AsyncAwait {
    fun run(): Long = runBlocking {
        val numbersCount = 1_000_000
        val parts = 4
        val partSize = numbersCount / parts
        
        val deferredResults = List(parts) { partIndex ->
            async {
                val start = partIndex * partSize + 1
                val end = if (partIndex == parts - 1) numbersCount
                else (partIndex + 1) * partSize

                (start..end).sumOf { it.toLong() }
            }
        }
        
        deferredResults.sumOf { it.await() }
    }
}
```

### Задание 9. Structured concurrency
Создайте корутину, которая запускает 5 дочерних корутин. Если одна из них падает с исключением, все остальные должны отмениться.

```kotlin
import kotlinx.coroutines.*
import java.io.IOException

object StructuredConcurrency {
    fun run(failingCoroutineIndex: Int): Int = runBlocking {
        val completedJobs = mutableListOf<Int>()
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            println("Перехвачено исключение: ${throwable.message}")
        }

        try {
            withContext(exceptionHandler) {
                coroutineScope {
                    val jobs = List(5) { index ->
                        launch {
                            try {
                                delay(100 * (index + 1))

                                if (index == failingCoroutineIndex) {
                                    throw IOException("Ошибка в корутине $index")
                                }

                                completedJobs.add(index)
                                println("Корутина $index успешно завершена")

                            } catch (e: CancellationException) {
                                println("Корутина $index отменена")
                                throw e 
                            }
                        }
                    }
                    
                    jobs.forEach { it.join() }
                }
            }
        } catch (e: Exception) {
            println("Поймано исключение в родительской корутине: ${e.message}")
        }

        completedJobs.size
    }
}
```

### Задание 10. withContext
Используя `withContext(Dispatchers.IO)`, прочитайте содержимое 3 файлов параллельно и объедините результаты.

```kotlin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

object WithContextIO {
    fun run(filePaths: List<String>): Map<String, String> = runBlocking {
        withContext(Dispatchers.IO) {
            val deferredResults = filePaths.map { filePath ->
                async {
                    filePath to readFileContent(filePath)
                }
            }
            
            deferredResults.awaitAll().toMap()
        }
    }

    private fun readFileContent(filePath: String): String {
        return try {
            File(filePath).readText()
        } catch (e: Exception) {
            "Ошибка чтения файла: ${e.message}"
        }
    }
}
```

---

## Часть 4. Практическое задание

### Задание 11. Многопоточный загрузчик изображений

Напишите программу, которая параллельно скачивает изображения из интернета.

**Требования:**
1. Использовать корутины с `Dispatchers.IO`
2. Скачать 10 изображений с https://picsum.photos/200/300
3. Сохранить в папку `downloads/`
4. Вывести прогресс: "Downloaded 1/10", "Downloaded 2/10", ...
5. В конце вывести статистику: общее время, количество успешных/неуспешных загрузок

```kotlin
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

        println("Загрузка $total изображений.")

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
```

---
