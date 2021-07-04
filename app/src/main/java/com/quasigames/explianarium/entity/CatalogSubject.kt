package com.quasigames.explianarium.entity

import kotlin.collections.Collection

data class CatalogSubject(
    val id: String,
    val title: String,
    val complexity: Int,
    val words: Collection<String>
)
