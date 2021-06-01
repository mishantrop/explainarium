package com.quasigames.explianarium.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.quasigames.explainarium.R
import com.quasigames.explianarium.entity.CatalogSubject
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {
    private var builder: GsonBuilder? = null
    private var currentWordIdx = 0
    private var lifetime: Long = 120_000
    private var timer: CountDownTimer? = null
    private var words: MutableList<String> = mutableListOf()
    private var wordsStat: MutableMap<String?, Boolean?>? = null
    private var isFirstInitialization = true

    private fun getWordsFromSubject(subjectJSON: String): MutableList<String> {
        builder = GsonBuilder()
        val gson = builder?.create()
        val subjectInstance = gson?.fromJson(subjectJSON, CatalogSubject::class.java)
        val wordsShuffled = subjectInstance?.words
        val words2 = wordsShuffled?.toMutableList()

        if (words2 != null) {
            return words2
        }

        return mutableListOf()
    }

    private fun getWordsFormSavedInstanceState(savedInstanceState: Bundle): MutableList<String> {
        val wordsJSON = savedInstanceState.getString("words")
        if (wordsJSON != null) {
            builder = GsonBuilder()
            val gson = builder?.create()
            if (gson != null) {
                val wordsType = object : TypeToken<List<String>>() {}.type
                return gson.fromJson(wordsJSON, wordsType)
            }
        }
        return mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_game)

            if (savedInstanceState != null) {
                isFirstInitialization = savedInstanceState.getBoolean("isFirstInitialization")
            }
            if (isFirstInitialization) {
                words = getWordsFromSubject(intent.getStringExtra("subject")!!)
            } else if (savedInstanceState != null) {
//                currentWordIdx = savedInstanceState.getInt("currentWordIdx")
                words = getWordsFormSavedInstanceState(savedInstanceState)
            }

            wordsStat = mutableMapOf()

            render()

            game_correct.setOnClickListener {
                val word = getCurrentWord()
                wordsStat!![word] = true
                words[currentWordIdx] = ""
                setNextWord()
            }

            game_incorrect.setOnClickListener {
                val word = getCurrentWord()
                wordsStat!![word] = false
                words[currentWordIdx] = ""
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
//            println("Explainarium | init timer $value")
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
//                    println("Explainarium | Time is over")
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

    private fun getCurrentWord(): String {
        if (words.size > currentWordIdx) {
            return words.elementAt(currentWordIdx)
        }

        return ":("
    }

    private fun xxx(x: String) {
        println("Explainarium $x")
    }

    private fun render() {
        val currentWord = getCurrentWord()

        xxx(currentWordIdx.toString())
        xxx(currentWord)

        val fontSize = when (currentWord.length) {
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
        if (currentWordIdx + 1 < words.size) {
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
        builder = GsonBuilder()
        val gson = builder?.create()
        timer?.cancel()

        outState.run {
            outState.putBoolean("isFirstInitialization", false)
//            outState.putInt("currentWordIdx", currentWordIdx)
            outState.putLong("lifetime", lifetime)
            val wordsToSave = words.filter { word -> word !== "" }
            if (gson != null) {
                outState.putString("words", gson.toJson(wordsToSave))
                xxx(gson.toJson(wordsToSave))
            }
        }

//        outState.putAll(outState)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        lifetime = savedInstanceState.getLong("lifetime")
        isFirstInitialization = savedInstanceState.getBoolean("isFirstInitialization")

        initTimer(lifetime)

        super.onRestoreInstanceState(savedInstanceState)
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