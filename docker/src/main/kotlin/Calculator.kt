import java.util.Scanner

fun calculate(a: Double, op: String, b: Double): Double = when (op) {
    "+" -> a + b
    "-" -> a - b
    "*" -> a * b
    "/" -> {
        if (b == 0.0) throw ArithmeticException("Division by zero")
        a / b
    }
    "%" -> a % b
    else -> throw IllegalArgumentException("Unknown operator: $op")
}

fun formatResult(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString()
    else value.toString()

fun main() {
    val scanner = Scanner(System.`in`)
    println("=== Kotlin Calculator ===")
    println("Operators: + - * / %")
    println("Type 'exit' to quit")
    println()

    while (true) {
        print("Enter expression (e.g. 10 + 5): ")
        val input = scanner.nextLine().trim()

        if (input.lowercase() == "exit") {
            println("Bye!")
            break
        }

        val parts = input.split("\\s+".toRegex())

        if (parts.size != 3) {
            println("Error: use format  <number> <operator> <number>")
            continue
        }

        try {
            val a = parts[0].toDouble()
            val op = parts[1]
            val b = parts[2].toDouble()
            val result = calculate(a, op, b)
            println("Result: ${formatResult(result)}")
        } catch (e: NumberFormatException) {
            println("Error: invalid number")
        } catch (e: ArithmeticException) {
            println("Error: ${e.message}")
        } catch (e: IllegalArgumentException) {
            println("Error: ${e.message}")
        }

        println()
    }
}
