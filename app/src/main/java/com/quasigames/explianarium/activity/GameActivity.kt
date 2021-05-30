package com.quasigames.explianarium.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_game.*

import com.quasigames.explianarium.entity.CatalogSubject
import com.quasigames.explainarium.R

class GameActivity : AppCompatActivity() {
    private var builder: GsonBuilder? = null
    private var currentWordIdx = 0
    private var lifetime: Long = 120_000
    private var timer: CountDownTimer? = null
    private var subject: CatalogSubject? = null
    private var wordsShuffled: List<String>? = null
    private var wordsStat: MutableMap<String?, Boolean?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_game)

            builder = GsonBuilder()
            val gson = builder?.create()

            subject = gson?.fromJson(intent.getStringExtra("subject"), CatalogSubject::class.java)
            wordsShuffled = subject?.words?.toList()
//            wordsShuffled = subject?.words?.shuffled()

            println("Explainarium | Count of words: " + wordsShuffled?.size)

            wordsStat = mutableMapOf<String?, Boolean?>()
            wordsShuffled?.forEach { word ->
                wordsStat!![word] = null
            }

            render()

            game_correct.setOnClickListener {
                val word = getCurrentWord()
                wordsStat!![word] = true
                setNextWord()
            }

            game_incorrect.setOnClickListener {
                val word = getCurrentWord()
                wordsStat!![word] = false
                setNextWord()
            }

            initTimer(lifetime)
        } catch (error: Exception) {
            println("Explainarium | Error: $error")
            println("Explainarium | Message: " + error.message)
            println("Explainarium | Cause: " + error.cause)
            error.stackTrace.forEach {x ->
                println("Explainarium | Stack: $x")
            }
        }
    }

    private fun initTimer(value: Long) {
        val countDownInterval: Long = 100
        try {
            println("Explainarium | init timer $value")
            timer?.cancel()
            timer = object : CountDownTimer(value, countDownInterval) {
                override fun onTick(millisUntilFinished: Long) {
                    lifetime -= countDownInterval

                    if (millisUntilFinished <= 0) {
                        game_timer.text = getString(R.string.game_time_is_over)
                    } else {
                        game_timer.text = formatTime(millisUntilFinished / 1000)
                    }
                }

                override fun onFinish() {
                    println("Explainarium | Time is over")
                    game_timer.text = getString(R.string.game_time_is_over)

                    finishGame()
                }
            }.start()
        } catch (error: Exception) {
            println("Explainarium | Error: $error")
            println("Explainarium | Message: " + error.message)
            println("Explainarium | Cause: " + error.cause)
            error.stackTrace.forEach {x ->
                println("Explainarium | Stack: $x")
            }
        }
    }

    private fun getCurrentWord(): String? {
        return wordsShuffled?.elementAt(currentWordIdx)
    }

//    private fun getTimerHeight() {
//        game_timer.measuredHeight
//    }
//
//    private fun getButtonsHeight() {
//        game_buttons.measuredHeight
//    }

    private fun render() {
        val currentWord = getCurrentWord()

        val fontSize = when (currentWord?.length) {
            1 -> 64.0F
            2 -> 64.0F
            3 -> 64.0F
            4 -> 64.0F
            5 -> 48.0F
            6 -> 48.0F
            7 -> 48.0F
            8 -> 48.0F
            9 -> 42.0F
            10 -> 42.0F
            11 -> 42.0F
            12 -> 42.0F
            13 -> 42.0F
            14 -> 42.0F
            15 -> 32.0F
            16 -> 32.0F
            17 -> 32.0F
            18 -> 24.0F
            19 -> 24.0F
            20 -> 24.0F
            21 -> 24.0F
            22 -> 24.0F
            else -> {
                24.0F
            }
        }

        game_current_word.text = currentWord
        game_current_word.textSize = fontSize
    }

    private fun setNextWord() {
        if (currentWordIdx + 1 < wordsShuffled?.size!!) {
            currentWordIdx += 1
            render()
        } else {
            finishGame()
        }
    }

    private fun finishGame() {
        val builder = GsonBuilder()
        val gson = builder.create()

        val gameSummaryIntent = Intent(this, GameSummaryActivity::class.java)
        gameSummaryIntent.putExtra("subject", gson.toJson("Hello"))
        startActivity(gameSummaryIntent)
    }

    private fun formatTime(totalSecs: Long): String {
        return DateUtils.formatElapsedTime(totalSecs)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong("lifetime", lifetime)

        timer?.cancel()

        outState.putAll(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        lifetime = savedInstanceState.getLong("lifetime")

        initTimer(lifetime)
    }

    override fun finish() {
        super.finish()
        timer?.cancel()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val catalogIntent = Intent(this, CatalogActivity::class.java)
        startActivity(catalogIntent)
    }
}