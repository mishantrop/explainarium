package com.quasigames.explianarium.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quasigames.explainarium.R
import kotlinx.android.synthetic.main.activity_game_summary.*

class GameSummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_summary)

        button_go_to_catalog.setOnClickListener {
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