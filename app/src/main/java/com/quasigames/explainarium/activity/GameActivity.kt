package com.quasigames.explainarium.activity

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.quasigames.explainarium.R
import com.quasigames.explainarium.entity.AppMetrikaSingleton
import com.quasigames.explainarium.entity.CatalogSubject

class GameActivity : AppCompatActivity() {
    private lateinit var builder: GsonBuilder
    private var currentWordIdx = 0
    private var timerLifetime: Long = 120_000
    private lateinit var timer: CountDownTimer
    private var words: MutableList<String> = mutableListOf()
    private lateinit var wordsStat: MutableMap<String?, Boolean?>
    private var isFirstInitialization = true

    private fun getSubjectWords(subjectJSON: String): MutableList<String> {
        builder = GsonBuilder()
        val gson = builder.create()
        val subjectInstance = gson.fromJson(subjectJSON, CatalogSubject::class.java)
        val wordsShuffled = subjectInstance.words.shuffled()
        return wordsShuffled.toMutableList()
    }

    private fun getWordsFromSavedInstanceState(savedInstanceState: Bundle): MutableList<String> {
        val wordsJSON = savedInstanceState.getString("words", "")
        builder = GsonBuilder()
        val gson = builder.create()
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
            val guessWordButton: Button = findViewById(R.id.game_correct)
            val skipWordButton: Button = findViewById(R.id.game_incorrect)

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

            guessWordButton.setOnClickListener {
                handleWordAction(true)
            }

            skipWordButton.setOnClickListener {
                handleWordAction(false)
            }
        } catch (error: Exception) {
            println("Explainarium | Error: $error")
            println("Explainarium | Message: " + error.message)
            println("Explainarium | Cause: " + error.cause)
            error.stackTrace.forEach {x ->
                println("Explainarium | Stack: $x")
            }
        }
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

    override fun onSaveInstanceState(outState: Bundle) {
        builder = GsonBuilder()
        val gson = builder.create()

        outState.run {
            outState.putBoolean("isFirstInitialization", false)
            outState.putLong("timerLifetime", timerLifetime)
            val wordsToSave = words.filter { word -> word !== "" }
            if (gson != null) {
                outState.putString("words", gson.toJson(wordsToSave))
            }
        }

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        timerLifetime = savedInstanceState.getLong("timerLifetime")
        isFirstInitialization = savedInstanceState.getBoolean("isFirstInitialization")

        initTimer(timerLifetime)
    }

    override fun finish() {
        super.finish()
        timer.cancel()
    }

    override fun onResume() {
        super.onResume()
        initTimer(timerLifetime)
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    private fun finishGame(finishReason: String) {
        val gameSummaryIntent = Intent(this, GameSummaryActivity::class.java)
        gameSummaryIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

        val guessedCount = getGuessedCount(wordsStat)
        val skippedCount = getSkippedCount(wordsStat)

        gameSummaryIntent.putExtra("guessedCount", guessedCount)
        gameSummaryIntent.putExtra("skippedCount", skippedCount)
        gameSummaryIntent.putExtra("finishReason", finishReason)

        startActivity(gameSummaryIntent)
    }

    private fun formatTime(totalSecs: Long): String {
        return DateUtils.formatElapsedTime(totalSecs)
    }

    private fun getCurrentWord(): String {
        if (words.size > currentWordIdx) {
            return words.elementAt(currentWordIdx)
        }

        return ":("
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

    private fun goToCatalog() {
        timer.cancel()
        AppMetrikaSingleton.reportEvent(
            applicationContext,
            "Game/GoToCatalog",
            HashMap(),
        )
        val intent = Intent(this, CatalogActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    private fun handleWordAction(isGuessed: Boolean) {
        val word = getCurrentWord()
        wordsStat[word] = isGuessed
        // Типа удаление слова из текущего списка, чтобы оно не повторялось при пересоздании Activity
        words[currentWordIdx] = ""
        setNextWord()
    }

    private fun initTimer(value: Long) {
        val countDownInterval: Long = 100
        val res: Resources = resources

        val gameTimerTextView: TextView = findViewById(R.id.game_timer)

        try {
            timer = object : CountDownTimer(value, countDownInterval) {
                override fun onTick(millisUntilFinished: Long) {
                    timerLifetime -= countDownInterval

                    if (millisUntilFinished <= 0) {
                        gameTimerTextView.text = getString(R.string.game_time_is_over)
                    } else {
                        gameTimerTextView.text = String.format(
                            res.getString(R.string.game_time),
                            formatTime(millisUntilFinished / 1000)
                        )
                    }
                }

                override fun onFinish() {
                    gameTimerTextView.text = getString(R.string.game_time_is_over)

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

    private fun render() {
        val currentWord = getCurrentWord()
        val gameCurrentWordTextView: TextView = findViewById(R.id.game_current_word)
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

        gameCurrentWordTextView.text = currentWord
        gameCurrentWordTextView.textSize = fontSize
    }

    private fun setNextWord() {
        if (currentWordIdx + 1 < words.size) {
            currentWordIdx += 1
            render()
        } else {
            finishGame("words")
        }
    }
}
