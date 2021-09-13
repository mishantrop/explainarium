package com.quasigames.explainarium.entity

import com.quasigames.explainarium.BuildConfig

data class UpdateInfo(
    val VERSION_CODE: Int,
) {
    constructor() : this(BuildConfig.VERSION_CODE)
}
