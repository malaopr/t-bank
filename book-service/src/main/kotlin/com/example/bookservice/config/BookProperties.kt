package com.example.bookservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "book")
data class BookProperties(
    var maxEntries: Int = 100,
    var allowedCategories: List<String> = emptyList(),
    var forbiddenTitles: List<String> = emptyList()
)
