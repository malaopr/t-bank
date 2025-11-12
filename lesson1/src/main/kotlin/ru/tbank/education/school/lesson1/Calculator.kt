package ru.tbank.education.school.lesson1
import kotlin.math.*

fun calculate(a: Double, b: Double = 0.0, operation: OperationType = OperationType.ADD): Double? {
    return when (operation) {
        OperationType.ADD -> {
            a + b
        }

        OperationType.SUBTRACT -> {
            a - b
        }

        OperationType.MULTIPLY -> {
            a * b
        }

        OperationType.DIVIDE -> {
            if (b==0.0) null else a/b
        }

        OperationType.SIN -> {
            sin(a)
        }

        OperationType.COS -> {
            cos(a)
        }

        OperationType.SQRT -> {
            a.takeIf {it >= 0}?.let { sqrt(it)}
        }
    }
}

@Suppress("ReturnCount")
fun String.calculate(): Double? {
    val parts = trim().split(" ")

    if (parts.size == 3) {
        val a = parts[0].toDoubleOrNull() ?: return null
        val operationStr = parts[1]
        val b = parts[2].toDoubleOrNull() ?: return null

        return when (operationStr) {
            "+" -> calculate(a, b, OperationType.ADD)
            "-" -> calculate(a, b, OperationType.SUBTRACT)
            "*" -> calculate(a, b, OperationType.MULTIPLY)
            "/" -> calculate(a, b, OperationType.DIVIDE)
            else -> null
        }
    }

    if (parts.size == 2) {
        val operationStr = parts[0].uppercase()
        val number = parts[1].toDoubleOrNull() ?: return null

        return when (operationStr) {
            "SIN" -> calculate(number, operation = OperationType.SIN)
            "COS" -> calculate(number, operation = OperationType.COS)
            "SQRT" -> calculate(number, operation = OperationType.SQRT)
            else -> null
        }
    }

    return null
}

fun printResult(result: Double?) {
    result?.let {
        println("Результат: $it")
    } ?: println("Ошибка")
}