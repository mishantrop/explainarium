package com.quasigames.explianarium.activity

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.quasigames.explainarium.R

class GameSummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game_summary)

        val ugadanoTextView: TextView = findViewById(R.id.game_summary_ugadano)
        val neugadanoTextView: TextView = findViewById(R.id.game_summary_neugadano)
        val gotoCatalogButton: Button = findViewById(R.id.button_goto_catalog)

        val ugadanoCount = intent.getIntExtra("ugadano", 0)
        val neugadanoCount = intent.getIntExtra("neugadano", 0)

        val res: Resources = resources
        ugadanoTextView.text = String.format(res.getString(R.string.game_summary_ugadano), ugadanoCount)
        neugadanoTextView.text = String.format(res.getString(R.string.game_summary_neugadano), neugadanoCount)

        gotoCatalogButton.setOnClickListener {
            goToCatalog()
            finish()
        }
    }

    private fun goToCatalog() {
        val catalogIntent = Intent(this, CatalogActivity::class.java)
        startActivity(catalogIntent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goToCatalog()
    }
}