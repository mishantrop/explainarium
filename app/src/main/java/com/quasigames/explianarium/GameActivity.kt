package com.quasigames.explianarium

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.quasigames.explainarium.CatalogSubject
import com.quasigames.explainarium.R
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.collections.Collection

class GameActivity : AppCompatActivity() {
    private var currentWordIdx = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val subjectJSON = intent.getStringExtra("subject")



        val builder = GsonBuilder()
        val gson = builder.create()
        val subject = gson.fromJson(subjectJSON, CatalogSubject::class.java)

        val wordsStat = mutableMapOf<String, Boolean>()
        subject.words.forEach { word -> wordsStat[word] = false }

        render(subject)

        game_correct.setOnClickListener {
            val word = getCurrentWord(subject.words)
            wordsStat[word] = true
            setNextWord(subject)

            println(wordsStat)
        }

        game_incorrect.setOnClickListener {
            setNextWord(subject)
        }
    }

    private fun getCurrentWord(words: Collection<String>): String {
        return words.elementAt(currentWordIdx)
    }

    private fun render(subject: CatalogSubject) {
        game_current_word.text = getCurrentWord(subject.words)
    }

    private fun setNextWord(subject: CatalogSubject) {
        if (currentWordIdx + 1 < subject.words.size) {
            currentWordIdx += 1
            render(subject)
        } else {
            val gameSummaryIntent = Intent(this, GameSummaryActivity::class.java)
            startActivity(gameSummaryIntent)
        }
    }
}