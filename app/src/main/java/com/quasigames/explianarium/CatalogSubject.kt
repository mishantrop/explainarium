package com.quasigames.explainarium

import kotlin.collections.Collection

data class CatalogSubject(
    val id: Int,
    val title: String,
    val words: Collection<String>
)
