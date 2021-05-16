package com.quasigames.explianarium.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.collections.Collection

import com.quasigames.explianarium.entity.CatalogSubject
import com.quasigames.explainarium.R

class GameActivity : AppCompatActivity() {
    private var currentWordIdx = 0
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val subjectJSON = intent.getStringExtra("subject")

        val builder = GsonBuilder()
        val gson = builder.create()
        val subject = gson.fromJson(subjectJSON, CatalogSubject::class.java)
        val wordsShuffled = subject.words.shuffled()

        val wordsStat = mutableMapOf<String, Boolean?>()
        wordsShuffled.forEach { word -> wordsStat[word] = null }

        render(wordsShuffled)

        game_correct.setOnClickListener {
            val word = getCurrentWord(subject.words)
            wordsStat[word] = true
            setNextWord(wordsShuffled)

            println(wordsStat)
        }

        game_incorrect.setOnClickListener {
            setNextWord(wordsShuffled)
        }

        timer = object : CountDownTimer(10_000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished <= 0) {
                    game_timer.text = "Время истекло"
                } else {
                    game_timer.text = formatTime(millisUntilFinished / 1000)
                }
            }

            override fun onFinish() {
                game_timer.text = "Время истекло"

                finishGame()
            }
        }.start()
    }

    private fun getCurrentWord(words: Collection<String>): String {
        return words.elementAt(currentWordIdx)
    }

    private fun render(wordsShuffled: Collection<String>) {
        game_current_word.text = getCurrentWord(wordsShuffled)
    }

    private fun setNextWord(wordsShuffled: Collection<String>) {
        if (currentWordIdx + 1 < wordsShuffled.size) {
            currentWordIdx += 1
            render(wordsShuffled)
        } else {
            finishGame()
        }
    }

    private fun finishGame() {
        val gameSummaryIntent = Intent(this, GameSummaryActivity::class.java)
        startActivity(gameSummaryIntent)
    }

    private fun formatTime(totalSecs: Long): String {
        return DateUtils.formatElapsedTime(totalSecs)
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