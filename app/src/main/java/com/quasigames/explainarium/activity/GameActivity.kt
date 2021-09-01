package com.quasigames.explainarium.activity

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.quasigames.explainarium.R
import com.quasigames.explainarium.entity.AppMetrikaSingleton
import com.quasigames.explainarium.entity.CatalogSubject
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {
    private var builder: GsonBuilder? = null
    private var currentWordIdx = 0
    private var lifetime: Long = 120_000
    private var timer: CountDownTimer? = null
    private var words: MutableList<String> = mutableListOf()
    private var wordsStat: MutableMap<String?, Boolean?>? = null
    private var isFirstInitialization = true

    private fun getSubjectWords(subjectJSON: String): MutableList<String> {
        builder = GsonBuilder()
        val gson = builder?.create()
        val subjectInstance = gson?.fromJson(subjectJSON, CatalogSubject::class.java)
        val wordsShuffled = subjectInstance?.words?.shuffled()
        val words2 = wordsShuffled?.toMutableList()

        if (words2 != null) {
            return words2
        }

        return mutableListOf()
    }

    private fun getWordsFromSavedInstanceState(savedInstanceState: Bundle): MutableList<String> {
        val wordsJSON = savedInstanceState.getString("words", "")
        builder = GsonBuilder()
        val gson = builder?.create()
        if (gson != null) {
            val wordsType = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(wordsJSON, wordsType)
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
                words = getSubjectWords(intent.getStringExtra("subject")!!)
            } else if (savedInstanceState != null) {
                words = getWordsFromSavedInstanceState(savedInstanceState)
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
        val res: Resources = resources

        try {
            timer?.cancel()
            timer = object : CountDownTimer(value, countDownInterval) {
                override fun onTick(millisUntilFinished: Long) {
                    lifetime -= countDownInterval

                    if (millisUntilFinished <= 0) {
                        game_timer.text = getString(R.string.game_time_is_over)
                    } else {
                        game_timer.text = String.format(
                            res.getString(R.string.game_time),
                            formatTime(millisUntilFinished / 1000)
                        )
                    }
                }

                override fun onFinish() {
                    game_timer.text = getString(R.string.game_time_is_over)

                    finishGame("timer")
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

    private fun render() {
        val currentWord = getCurrentWord()

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
            finishGame("words")
        }
    }

    private fun getGuessedCount(wordsStatistics: MutableMap<String?, Boolean?>): Int {
        var guessed = 0

        wordsStatistics.forEach { isGuessed ->
            if (isGuessed.value == true) {
                guessed += 1
            }
        }

        return guessed
    }

    private fun getSkippedCount(wordsStatistics: MutableMap<String?, Boolean?>): Int {
        var skipped = 0

        wordsStatistics.forEach { isGuessed ->
            if (isGuessed.value == false) {
                skipped += 1
            }
        }

        return skipped
    }

    private fun finishGame(finishReason: String) {
        val gameSummaryIntent = Intent(this, GameSummaryActivity::class.java)
        gameSummaryIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

        val guessedCount = getGuessedCount(wordsStat!!)
        val skippedCount = getSkippedCount(wordsStat!!)

        gameSummaryIntent.putExtra("guessedCount", guessedCount)
        gameSummaryIntent.putExtra("skippedCount", skippedCount)
        gameSummaryIntent.putExtra("finishReason", finishReason)

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
            outState.putLong("lifetime", lifetime)
            val wordsToSave = words.filter { word -> word !== "" }
            if (gson != null) {
                outState.putString("words", gson.toJson(wordsToSave))
            }
        }

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

    private fun goToCatalog() {
        timer?.cancel()
        AppMetrikaSingleton.reportEvent(
            applicationContext,
            "Game/GoToCatalog",
            HashMap(),
        )
        val intent = Intent(this, CatalogActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppMetrikaSingleton.reportEvent(
            applicationContext,
            "Game/BackPressed",
            HashMap(),
        )
        goToCatalog()
    }
}
