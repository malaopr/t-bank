import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration

data class NormalizedLog(
    val dt: String,
    val id: Int,
    val status: String
)

fun normalize(line: String): NormalizedLog? {
    val text = line.trim()

    val regexA = Regex(
        """(\d{4}-\d{2}-\d{2})\s+(\d{2}:\d{2}).*ID:(\d+).*STATUS:(\w+)""",
        RegexOption.IGNORE_CASE
    )

    val regexB = Regex(
        """TS\s*=\s*(\d{2}/\d{2}/\d{4})-(\d{2}:\d{2}).*status\s*=\s*(\w+).*#(\d+)""",
        RegexOption.IGNORE_CASE
    )

    val regexC = Regex(
        """\[(\d{2}\.\d{2}\.\d{4})\s+(\d{2}:\d{2})].*(sent|delivered).*\(id:(\d+)\)""",
        RegexOption.IGNORE_CASE
    )

    regexA.find(text)?.let {
        val dt = "${it.groupValues[1]} ${it.groupValues[2]}"
        return NormalizedLog(dt, it.groupValues[3].toInt(), it.groupValues[4].lowercase())
    }

    regexB.find(text)?.let {
        val d = it.groupValues[1].split("/")
        val dt = "${d[2]}-${d[1]}-${d[0]} ${it.groupValues[2]}"
        return NormalizedLog(dt, it.groupValues[4].toInt(), it.groupValues[3].lowercase())
    }

    regexC.find(text)?.let {
        val d = it.groupValues[1].split(".")
        val dt = "${d[2]}-${d[1]}-${d[0]} ${it.groupValues[2]}"
        return NormalizedLog(dt, it.groupValues[4].toInt(), it.groupValues[3].lowercase())
    }

    return null
}

fun main() {
    val logs = listOf(
        "2026-01-22 09:14 | ID:042 | STATUS:sent",
        "TS=22/01/2026-09:27; status=delivered; #042",
        "2026-01-22 09:10 | ID:043 | STATUS:sent",
        "2026-01-22 09:18 | ID:043 | STATUS:delivered",
        "TS=22/01/2026-09:05; status=sent; #044",
        "[22.01.2026 09:40] delivered (id:044)",
        "2026-01-22 09:20 | ID:045 | STATUS:sent",
        "[22.01.2026 09:33] delivered (id:045)",
        "   ts=22/01/2026-09:50; STATUS=Sent; #046   ",
        " [22.01.2026 10:05]   DELIVERED   (ID:046) "
    )

    val normalized = mutableListOf<NormalizedLog>()
    val broken = mutableListOf<String>()

    for (line in logs) {
        val n = normalize(line)
        if (n != null) normalized.add(n) else broken.add(line)
    }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val grouped = normalized.groupBy { it.id }

    val durations = mutableListOf<Pair<Int, Long>>()
    val incomplete = mutableListOf<Int>()
    val timeErrors = mutableListOf<Int>()
    val duplicates = mutableMapOf<Int, MutableMap<String, Int>>()

    for ((id, events) in grouped) {
        val sent = events.filter { it.status == "sent" }
        val delivered = events.filter { it.status == "delivered" }

        if (sent.size > 1 || delivered.size > 1) {
            duplicates[id] = mutableMapOf(
                "sent" to sent.size,
                "delivered" to delivered.size
            )
        }

        if (sent.isEmpty() || delivered.isEmpty()) {
            incomplete.add(id)
            continue
        }

        val sentTime = LocalDateTime.parse(sent[0].dt, formatter)
        val deliveredTime = LocalDateTime.parse(delivered[0].dt, formatter)

        if (deliveredTime.isBefore(sentTime)) {
            timeErrors.add(id)
            continue
        }

        val minutes = Duration.between(sentTime, deliveredTime).toMinutes()
        durations.add(id to minutes)
    }

    val sorted = durations.sortedByDescending { it.second }
    val longest = sorted.firstOrNull()
    val violators = sorted.filter { it.second > 20 }

    val deliveredByHour = normalized
        .filter { it.status == "delivered" }
        .groupingBy {
            LocalDateTime.parse(it.dt, formatter).hour
        }
        .eachCount()

    println("Длительность доставки:")
    sorted.forEach { println("ID ${it.first}: ${it.second} минут") }

    println("\nСамый долгий заказ: $longest")

    println("\nНарушение правила (>20 минут):")
    violators.forEach { println("ID ${it.first}") }

    println("\nНеполные заказы: $incomplete")
    println("Ошибки времени: $timeErrors")

    println("\nDelivered по часам:")
    deliveredByHour.toSortedMap().forEach { (hour, count) ->
        println("$hour:00 — $count")
    }

    println("\nДубли:")
    duplicates.forEach { (id, map) ->
        println("ID $id: $map")
    }

    if (broken.isNotEmpty()) {
        println("\nБитые строки:")
        broken.forEach { println(it) }
    }
}
