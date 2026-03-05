package ru.tbank.education.school.lesson10.practise

import java.net.HttpURLConnection
import java.net.URL

val BASE = "http://localhost:8080/api/notes"

fun request(url: String, method: String, body: String? = null): Pair<Int, String> {
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.requestMethod = method

    if (body != null) {
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        connection.outputStream.use { os ->
            os.write(body.toByteArray())
            os.flush()
        }
    }

    val responseCode = connection.responseCode

    val responseBody = try {
        if (responseCode in 200..299) {
            connection.inputStream.bufferedReader().readText()
        } else {
            connection.errorStream?.bufferedReader()?.readText() ?: "Ошибка $responseCode"
        }
    } catch (e: Exception) {
        "Ошибка чтения ответа: ${e.message}"
    }

    connection.disconnect()
    return Pair(responseCode, responseBody)
}

fun main() {
    println("=== 1. GET /api/notes — все заметки ===")
    val (code1, body1) = request(BASE, "GET")
    println("Код: $code1")
    println("Тело: $body1")

    println("\n=== 2. POST /api/notes — создать заметку ===")
    val newNote = """{"title":"Домашка","content":"Сделать задание по сетям","tag":"учёба"}"""
    val (code2, body2) = request(BASE, "POST", newNote)
    println("Код: $code2")
    println("Тело: $body2")

    println("\n=== 3. GET /api/notes/1 — одна заметка ===")
    val (code3, body3) = request("$BASE/1", "GET")
    println("Код: $code3")
    println("Тело: $body3")

    println("\n=== 4. PUT /api/notes/1 — обновить заметку ===")
    val updatedNote = """{"title":"Покупки (обновлено)","content":"Молоко, хлеб, яйца, сыр","tag":"личное"}"""
    val (code4, body4) = request("$BASE/1", "PUT", updatedNote)
    println("Код: $code4")
    println("Тело: $body4")

    println("\n=== 5. GET /api/notes?tag=учёба — фильтр по тегу ===")
    val (code5, body5) = request("$BASE?tag=учёба", "GET")
    println("Код: $code5")
    println("Тело: $body5")

    println("\n=== 6. DELETE /api/notes/1 — удалить заметку ===")
    val (code6, body6) = request("$BASE/1", "DELETE")
    println("Код: $code6")
    println("Тело: $body6")

    println("\n=== 7. GET /api/notes/999 — несуществующая заметка ===")
    val (code7, body7) = request("$BASE/999", "GET")
    println("Код: $code7")
    println("Тело: $body7")

    println("\n=== 8. GET /api/notes — финальное состояние ===")
    val (code8, body8) = request(BASE, "GET")
    println("Код: $code8")
    println("Тело: $body8")
}