package com.quasigames.explainarium.activity

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.quasigames.explainarium.R
import com.quasigames.explainarium.entity.AppMetrikaSingleton

class GameSummaryActivity : AppCompatActivity() {
    private fun goToCatalog() {
        val intent = Intent(this, CatalogActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game_summary)
        val res: Resources = resources

        val opinionTextView: TextView = findViewById(R.id.game_summary_opinion)
        val guessedTextView: TextView = findViewById(R.id.game_summary_guessed)
        val skippedTextView: TextView = findViewById(R.id.game_summary_skipped)
        val gotoCatalogButton: Button = findViewById(R.id.button_goto_catalog)

        val guessedCount = intent.getIntExtra("guessedCount", 0)
        val skippedCount = intent.getIntExtra("skippedCount", 0)
        var finishReason = intent.getStringExtra("finishReason")
        if (finishReason == null) {
            finishReason = "undefined"
        }

        if (guessedCount == 0 && skippedCount == 0) {
            opinionTextView.text = res.getString(R.string.game_summary_zero_guessed_and_skipped)
        } else {
            opinionTextView.text = res.getString(R.string.game_summary_normalno)
        }

        guessedTextView.text = String.format(res.getString(R.string.game_summary_guessed), guessedCount)
        skippedTextView.text = String.format(res.getString(R.string.game_summary_skipped), skippedCount)

        gotoCatalogButton.setOnClickListener {
            goToCatalog()
        }

        AppMetrikaSingleton.reportEvent(
            applicationContext,
            "Game/Summary",
            hashMapOf("guessedCount" to guessedCount, "skippedCount" to skippedCount, "finishReason" to finishReason),
        )
    }

    override fun onBackPressed() {
        goToCatalog()
    }
}
