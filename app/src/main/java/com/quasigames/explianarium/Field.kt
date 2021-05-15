package com.quasigames.explainarium

import android.annotation.SuppressLint
import android.content.Context
import android.widget.GridLayout
import android.widget.Toast

class Field {
    private var context: Context? = null
    private var grid: GridLayout? = null
    private var successToast: Toast? = null

    var maxWidth: Int = 8
    var maxHeight: Int = 8
    private var viewportHeight = 0
    private var viewportWidth = 0
    var width: Int? = null
    var height: Int? = null

    private var isEnabled = true
    private val matchTargetCount = 2
    private val cells = mutableMapOf<Int, Cell>()
    private val emojis = mutableListOf(
        "\uD83D\uDC7D", // 👽 Alien
        "\uD83D\uDC3B", // 🐻 Bear
        "\uD83D\uDCA3", // 💣 Bomb
        "\uD83D\uDC31", // 🐱 Cat
        "\uD83D\uDC2E", // 🐮 Cow Face
        "\uD83D\uDC36", // 🐶 Dog
        "\uD83D\uDC2C", // 🐬 Dolphin
        "\uD83D\uDC32", // 🐲 Dragon
        "\uD83D\uDC18", // 🐘 Elephant
        "\uD83D\uDD25", // 🔥 Fire
        "\uD83C\uDF40", // 🍀 Four Leaf Clover
        "\uD83D\uDC38", // 🐸 Frog
        "\uD83D\uDC8E", // 💎 Gem Stone
        "\uD83C\uDF1F", // 🌟 Glowing Star
        "\uD83D\uDC1D", // 🐝 Honeybee
        "\uD83D\uDC0E", // 🐎 Horse
        "\uD83C\uDFE0", // 🏠 House
        "\uD83C\uDF83", // 🎃 Jack-O-Lantern
        "\uD83D\uDC1E", // 🐞 Lady Beetle
        "\uD83D\uDDFF", // 🗿 Moai
        "\uD83D\uDC35", // 🐵 Monkey Face
        "\uD83D\uDC27", // 🐧 Octopus
        "\uD83D\uDC79", // 👹 Ogre
        "\uD83D\uDC3C", // 🐼 Panda
        "\uD83D\uDC37", // 🐷 Pig
        "\uD83C\uDF08", // 🌈 Rainbow
        "\uD83C\uDF4E", // 🍎 Red Apple
        "\uD83C\uDF85", // 🎅 Santa Claus
        "\uD83D\uDC22", // 🐢 Turtle
        "\uD83C\uDF0B", // 🌋 Volcano
        "\uD83D\uDC3A", // 🐺 Wolf
        "\uD83C\uDF81"  // 🎁 Wrapped Gift
    )

    @SuppressLint("ShowToast")
    fun init(context: Context, grid: GridLayout) {
//        fun init(context: Context, grid: GridLayout, width: Int, height: Int) {
//        this.context = context
//        val pairsCount = (width * height) / matchTargetCount
//        if (pairsCount > emojis.size) {
//            throw Exception("I do not have emojis enough (total: ${emojis.size}) to create $pairsCount pairs")
//        }
//        if (width % matchTargetCount > 0 && height % matchTargetCount > 0) {
//            throw Exception("Count of cells must be even")
//        }
//        if (width > maxWidth || height > maxHeight) {
//            throw Exception("Maximum size: ${maxWidth}x${maxHeight}")
//        }
//
//        this.width = width
//        this.height = height
//
//        successToast = Toast.makeText(context, "Ты молодец \uD83C\uDF1A", Toast.LENGTH_LONG)
//        this.grid = grid
//
//        reset()
//
//        grid.columnCount = this.width!!
//        grid.rowCount = this.height!!

//        render()
    }

    fun setViewportSize(width: Int, height: Int) {
        viewportWidth = width
        viewportHeight = height

//        calcButtonsSize()
    }

//    private fun calcButtonsSize() {
//        val screenWidthPx = Resources.getSystem().displayMetrics.widthPixels
//        val buttonWidth = screenWidthPx / width!!
//        val buttonHeight = viewportHeight / height!!
//        for ((_, cell) in cells) {
//            cell.setSize(buttonWidth, buttonHeight)
//        }
//    }

//    private fun getChars(pairsCount: Int): MutableList<String> {
//        val chars = mutableListOf<String>()
//        emojis.shuffle()
//        val pairsLastIndex = pairsCount - 1
//        for (i in 0..pairsLastIndex) {
//            chars.add(emojis[i])
//            chars.add(emojis[i])
//        }
//        chars.shuffle()
//        return chars
//    }

//    private fun hideUnmatchedCells() {
//        val unmatchedCells = cells.filter { (_, cell) -> !cell.isMatched }
//        for ((_, cell) in unmatchedCells) {
//            cell.hide()
//        }
//    }

//    private fun initCells() {
//        grid?.removeAllViews()
//        cells.clear()
//
//        var cellId = 1
//        for (w in 1..width!!) {
//            for (h in 1..height!!) {
//                val button = Button(context)
//                val cell = Cell(
//                    cellId,
//                    button,
//                    onClick = { id -> onCellClick(id) }
//                )
//                cells[cellId] = cell
//                grid?.addView(button)
//                cellId++
//            }
//        }
//
//        initChars()
//        calcButtonsSize()
//    }

//    private fun initChars() {
//        val pairsCount = (width!! * height!!) / 2
//        val chars = getChars(pairsCount)
//        var charIndex = 0
//        for ((_, cell) in cells) {
//            cell.char = chars[charIndex]
//            charIndex++
//        }
//    }

    private fun onCellClick(cellId: Int) {
        if (!isEnabled) {
            return
        }
//        showCell(cellId)
//        val openedCells = cells.filter { (_, cell) -> cell.isVisible && !cell.isMatched }
//        val openedCellsChars = mutableSetOf<String>()
//        if (openedCells.size == matchTargetCount) {
//            for ((_, cell) in openedCells) {
//                openedCellsChars.add(cell.char!!)
//            }
//
//            val isMatched = openedCellsChars.size == 1
//            if (isMatched) {
//                for ((_, cell) in openedCells) {
////                    cell.setMatched()
//                }
//            } else {
//                isEnabled = false
//                Handler().postDelayed(
//                    {
////                        hideUnmatchedCells()
//                        isEnabled = true
//                    },
//                    1000
//                )
//            }
//        }

//        val matchedCells = cells.filter { (_, cell) -> cell.isMatched }
//        if (matchedCells.size == cells.size) {
//            isEnabled = false
//            successToast?.show()
//            Handler().postDelayed(
//                {
//                    reset()
//                    isEnabled = true
//                    render()
//                },
//                1000
//            )
//
//        }
    }

//    private fun render() {
//        for ((_, cell) in cells) {
//            cell.render()
//        }
//    }

//    private fun reset() {
//        initCells()
//    }

//    private fun showCell(cellId: Int) {
//        for ((id, cell) in cells) {
//            if (id == cellId) {
//                cell.show()
//            }
//        }
//    }

//    fun setSize(width: Int, height: Int) {
//        init(context!!, grid!!, width, height)
//    }
}
