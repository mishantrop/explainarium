package com.quasigames.explainarium.entity

import kotlin.collections.Collection

data class CatalogSubject(
    val id: String,
    val title: String,
    val words: Collection<String>
)
