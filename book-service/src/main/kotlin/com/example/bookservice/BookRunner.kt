package com.example.bookservice

import com.example.bookservice.service.BookService
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class BookRunner(
    private val bookService: BookService
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(BookRunner::class.java)

    override fun run(vararg args: String?) {
        log.info("=== Запуск демонстрации BookService ===")

        val book1 = bookService.create("Clean Code", "Robert Martin", "TECHNOLOGY")
        val book2 = bookService.create("Sapiens", "Yuval Harari", "HISTORY")
        val book3 = bookService.create("Dune", "Frank Herbert", "FICTION")

        log.info("Все книги: {}", bookService.getAll())

        log.info("Книга с id=1: {}", bookService.getById(1L))

        log.info("Книги в категории TECHNOLOGY: {}", bookService.getByCategory("TECHNOLOGY"))

        bookService.update(book2.id, "Sapiens: A Brief History", "Yuval Noah Harari", "HISTORY")
        log.info("После обновления id={}: {}", book2.id, bookService.getById(book2.id))

        bookService.delete(book3.id)
        log.info("После удаления, все книги: {}", bookService.getAll())

        try {
            bookService.create("forbidden book", "Author", "FICTION")
        } catch (e: IllegalArgumentException) {
            log.warn("Ожидаемая ошибка: {}", e.message)
        }

        try {
            bookService.create("Some Book", "Author", "COOKING")
        } catch (e: IllegalArgumentException) {
            log.warn("Ожидаемая ошибка: {}", e.message)
        }

        log.info("=== Демонстрация завершена ===")
    }
}
