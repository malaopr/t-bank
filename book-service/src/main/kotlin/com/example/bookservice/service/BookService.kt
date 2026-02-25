package com.example.bookservice.service

import com.example.bookservice.config.BookProperties
import com.example.bookservice.model.Book
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookProperties: BookProperties
) {

    private val log = LoggerFactory.getLogger(BookService::class.java)
    private val books: MutableMap<Long, Book> = mutableMapOf()
    private var idCounter: Long = 1

    fun create(title: String, author: String, category: String): Book {
        log.info("Попытка создать книгу: title='{}', author='{}', category='{}'", title, author, category)

        if (books.size >= bookProperties.maxEntries) {
            log.warn("Достигнут лимит записей: {}", bookProperties.maxEntries)
            throw IllegalStateException("Достигнут лимит записей: ${bookProperties.maxEntries}")
        }

        if (bookProperties.forbiddenTitles.any { title.lowercase().contains(it.lowercase()) }) {
            log.warn("Попытка добавить книгу с запрещённым названием: '{}'", title)
            throw IllegalArgumentException("Название книги содержит запрещённое слово: $title")
        }

        if (bookProperties.allowedCategories.isNotEmpty() &&
            !bookProperties.allowedCategories.contains(category.uppercase())) {
            log.warn("Категория '{}' не разрешена. Допустимые: {}", category, bookProperties.allowedCategories)
            throw IllegalArgumentException("Категория '$category' не разрешена. Допустимые: ${bookProperties.allowedCategories}")
        }

        val book = Book(id = idCounter++, title = title, author = author, category = category.uppercase())
        books[book.id] = book
        log.info("Книга успешно создана: {}", book)
        return book
    }

    fun getAll(): List<Book> {
        log.debug("Получение всех книг. Всего: {}", books.size)
        return books.values.toList()
    }

    fun getById(id: Long): Book? {
        val book = books[id]
        if (book == null) log.warn("Книга с id={} не найдена", id)
        else log.debug("Найдена книга: {}", book)
        return book
    }

    fun getByCategory(category: String): List<Book> {
        log.info("Фильтрация книг по категории: '{}'", category)
        return books.values.filter { it.category == category.uppercase() }
    }

    fun update(id: Long, title: String, author: String, category: String): Book? {
        log.info("Обновление книги id={}", id)
        val existing = books[id] ?: run {
            log.warn("Книга с id={} не найдена для обновления", id)
            return null
        }

        if (bookProperties.forbiddenTitles.any { title.lowercase().contains(it.lowercase()) }) {
            log.warn("Попытка обновить книгу с запрещённым названием: '{}'", title)
            throw IllegalArgumentException("Название книги содержит запрещённое слово: $title")
        }

        val updated = existing.copy(title = title, author = author, category = category.uppercase())
        books[id] = updated
        log.info("Книга обновлена: {}", updated)
        return updated
    }

    fun delete(id: Long): Boolean {
        val removed = books.remove(id)
        return if (removed != null) {
            log.info("Книга с id={} удалена", id)
            true
        } else {
            log.warn("Книга с id={} не найдена для удаления", id)
            false
        }
    }
}
