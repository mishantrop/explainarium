package com.quasigames.explainarium.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.content.res.Resources
import android.widget.Button
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.quasigames.explainarium.R
import com.quasigames.explainarium.entity.AppMetrikaSingleton
import com.quasigames.explainarium.entity.CatalogSubject

class GameStartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_start)
        val res: Resources = resources
        val builder = GsonBuilder()
        val gson = builder.create()

        val subjectJSON: String = intent.getStringExtra("subject")!!
        val subject = gson.fromJson(subjectJSON, CatalogSubject::class.java)

        findViewById<TextView>(R.id.game_start_subject).text = subject.title
        findViewById<TextView>(R.id.game_start_count).text = String.format(
            res.getString(R.string.game_start_wordcount), subject.words.size.toString()
        )
        findViewById<Button>(R.id.game_start_start).setOnClickListener {
            goToSubject(gson, subject)
        }
    }

    private fun goToSubject(gson: Gson, subject: CatalogSubject) {
        // Отправка события о старте определённой категории
        AppMetrikaSingleton.reportEvent(
            applicationContext,
            "Game/Start",
            hashMapOf("title" to subject.title),
        )

        val preparingIntent = Intent(this, PreparingActivity::class.java)
        preparingIntent.putExtra("subject", gson.toJson(subject))
        startActivity(preparingIntent)
    }
}