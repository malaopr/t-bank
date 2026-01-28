import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

fun main() {
    task1()
    println()
    task2()
    println()
    task3()
}

/*
1) Строки + регулярные выражения
["Name: Ivan, score=17", ...]
Извлечь имя и score, собрать пары, вывести победителя.
*/
fun task1() {
    val lines = listOf(
        "Name: Ivan, score=17",
        "Name: Olga, score=23",
        "Name: Max, score=5"
    )

    val re = Regex("""^Name:\s*([A-Za-z]+)\s*,\s*score=(\d+)\s*$""")

    val pairs: List<Pair<String, Int>> = lines.mapNotNull { s ->
        val m = re.find(s) ?: return@mapNotNull null
        val name = m.groupValues[1]
        val score = m.groupValues[2].toInt()
        name to score
    }

    println("Task 1 pairs: $pairs")

    val best = pairs.maxByOrNull { it.second }
    if (best != null) {
        println("Task 1 best: ${best.first} (${best.second})")
    } else {
        println("Task 1: no valid lines")
    }
}

/*
2) Даты + коллекции
["2026-01-22", ...]
Преобразовать в даты, отсортировать, посчитать сколько в январе 2026.
*/
fun task2() {
    val dateStrings = listOf(
        "2026-01-22",
        "2026-02-01",
        "2025-12-31",
        "2026-01-05"
    )

    val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    val dates = dateStrings.map { LocalDate.parse(it, fmt) }.sorted()

    println("Task 2 sorted dates: ${dates.joinToString { it.format(fmt) }}")

    val countJan2026 = dates.count { it.year == 2026 && it.month == Month.JANUARY }
    println("Task 2 count in Jan 2026: $countJan2026")
}

/*
3) Коллекции + строки
"apple orange apple banana orange apple"
Частоты слов, вывести слова с частотой > 1 по алфавиту.
*/
fun task3() {
    val text = "apple orange apple banana orange apple"

    val words = text.trim().split(Regex("""\s+""")).filter { it.isNotEmpty() }

    val freq = mutableMapOf<String, Int>()
    for (w in words) {
        freq[w] = (freq[w] ?: 0) + 1
    }

    println("Task 3 freq: $freq")

    val repeated = freq
        .filter { (_, c) -> c > 1 }
        .keys
        .sorted()

    println("Task 3 repeated words: ${repeated.joinToString(", ")}")
}


/*
4) Регулярные выражения: проверка формата
 */

fun main() {
    val strings = listOf("A-123", "B-7", "AA-12", "C-001", "D-99x")
    val regex = Regex("^[A-Z]-\\d{1,3}$")

    val filtered = strings.filter { regex.matches(it) }
    println($filtered)
}

/*
5) Строки: нормализация пробелов
 */

fun main() {
    val strings = listOf("  Hello   world  ", "A   B    C", "   one")
    val normalized = strings.map {
        it.trim().replace(Regex("\\s+"), " ")
    }

    println($normalized)
}

/*
6) Даты: разница между двумя датами
 */

import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun main() {
    val datePairs = listOf(
        "2026-01-01" to "2026-01-10",
        "2025-12-31" to "2026-01-01",
        "2026-02-01" to "2026-01-22"
    )

    val differences = datePairs.map { (date1Str, date2Str) ->
        val date1 = LocalDate.parse(date1Str)
        val date2 = LocalDate.parse(date2Str)
        ChronoUnit.DAYS.between(date1, date2)
    }

    println("разница: $differences")
}

/*
7) Коллекции: группировка по ключу
 */

fun main() {
    val strings = listOf("math:Ivan", "bio:Olga", "math:Max", "bio:Ivan", "cs:Olga")
    val subjectStudents = mutableMapOf<String, MutableList<String>>()
    strings.forEach { s ->
        val (subject, student) = s.split(":")
        val list = subjectStudents.getOrPut(subject) { mutableListOf() )
    }
        list.add(student)

    println("словарь: $subjectStudents")
}
}


/*
8) Регулярные выражения + даты: извлечение времени из текста
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    val strings = listOf(
        "Start at 2026/01/22 09:14",
        "No time here",
        "End: 22-01-2026 18:05"
    )

    val pattern1 = Regex("""(\d{4})/(\d{2})/(\d{2}) (\d{2}):(\d{2})""")
    val pattern2 = Regex("""(\d{2})-(\d{2})-(\d{4}) (\d{2}):(\d{2})""")
    val formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val extractedTimes = strings.mapNotNull { s ->
        pattern1.find(s)?.let { match ->
            val (year, month, day, hour, minute) = match.destructured
            LocalDateTime.parse("$year-$month-${day}T${hour}:${minute}").format(formatter1)
        } ?: pattern2.find(s)?.let { match ->
            val (day, month, year, hour, minute) = match.destructured
            LocalDateTime.parse("$year-$month-${day}T${hour}:${minute}").format(formatter1)
        }
    }
    println("извлеченные даты и время: $extractedTimes")
}

