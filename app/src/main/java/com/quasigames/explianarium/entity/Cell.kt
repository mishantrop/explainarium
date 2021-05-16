package com.quasigames.explianarium.entity

import android.widget.Button

class Cell(private var id: Int, private var button: Button, onClick: (id: Int) -> Unit) {
    var char: String? = null

    init {
        button.setOnClickListener {
            onClick(id)
        }
    }

    fun setSize(width: Int, height: Int) {
        button.layoutParams?.width = width
        button.layoutParams?.height = height
        button.setPadding(0,0,0,0)
    }
}
