package com.quasigames.explianarium.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateUtils
import android.widget.Toast
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
//    private var wordsStat: MutableMap<String?, Boolean?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_game)
            builder = GsonBuilder()
            val gson = builder?.create()

            subject = gson?.fromJson(intent.getStringExtra("subject"), CatalogSubject::class.java)
            wordsShuffled = subject?.words?.shuffled()

            println("Explainarium: " + wordsShuffled?.size)

            //        wordsStat = mutableMapOf<String?, Boolean?>()
            wordsShuffled?.forEach { word ->
                println("Explainarium: adding word " + word)
//                wordsStat!![word] = null
            }

            render()

            game_correct.setOnClickListener {
                val word = getCurrentWord()
//                wordsStat!![word] = true
                setNextWord()

//                println(wordsStat)
            }

            game_incorrect.setOnClickListener {
                setNextWord()
            }

            initTimer(lifetime)
        } catch (error: Exception) {
            println("Explainarium: " + error)
            println("Explainarium: " + error.message)
            println("Explainarium: " + error.cause)
            error.stackTrace.forEach {x ->
                println("Explainarium: $x")
            }
//            val errorToast = Toast.makeText(this, error.message, Toast.LENGTH_LONG)
//            errorToast.show()
        }
    }

    private fun initTimer(value: Long) {
        try {
            println("Explainarium: init timer $value")
            timer?.cancel()
            timer = object : CountDownTimer(value, 100) {
                override fun onTick(millisUntilFinished: Long) {
                    if (millisUntilFinished <= 0) {
                        game_timer.text = "Время истекло"
                    } else {
                        game_timer.text = formatTime(millisUntilFinished / 1000)
                    }
                }

                override fun onFinish() {
                    println("Explainarium: timer is over")
                    game_timer.text = "Время истекло"

                    finishGame()
                }
            }.start()
        } catch (error: Exception) {
            println("Explainarium: " + error)
            println("Explainarium: " + error.message)
//            val errorToast = Toast.makeText(this, error.message, Toast.LENGTH_LONG)
//            errorToast.show()
        }
    }

    private fun getCurrentWord(): String? {
        try {
            return wordsShuffled?.elementAt(currentWordIdx)
        } catch (error: Exception) {
            println("Explainarium: " + error)
            println("Explainarium: " + error.message)
        }
        return ""
    }

    private fun render() {
        println("Explainarium: getCurrentWord: " + getCurrentWord())
        game_current_word.text = getCurrentWord()
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

//    override fun onSaveInstanceState(outState: Bundle) {
//        println("Explainarium: onSaveInstanceState")
//        super.onSaveInstanceState(outState)
//
//        outState.putLong("lifetime", lifetime)
////
////        outState.putInt("field.width", field.width!!)
////        outState.putInt("field.height", field.height!!)
//
//        outState.putAll(outState)
//    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        println("Explainarium: onRestoreInstanceState")
//        super.onRestoreInstanceState(savedInstanceState)
//
//        timer?.cancel()
//
//        lifetime = savedInstanceState.getLong("lifetime")
////
////        field.init(this, fieldGrid!!, savedInstanceState.getInt("field.width"), savedInstanceState.getInt("field.height"))
//    }

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