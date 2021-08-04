package com.quasigames.explainarium.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.quasigames.explainarium.R
import kotlinx.android.synthetic.main.activity_preparing.*

class PreparingActivity : AppCompatActivity() {
    private var timer: CountDownTimer? = null
    private val initialTimerValueSecs: Long = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preparing)

        val subject = intent.getStringExtra("subject")

        prepare_counter_text.text = (initialTimerValueSecs + 1).toString()
        val textGo = getString(R.string.preparing_go)

        val gameIntent = Intent(this, GameActivity::class.java)
        gameIntent.putExtra("subject", subject)

        timer = object : CountDownTimer(initialTimerValueSecs * 1000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                val text = timeLeft.toString()
                prepare_counter_text.text = if (timeLeft <= 0) textGo else text
            }

            override fun onFinish() {
                prepare_counter_text.text = textGo

                startActivity(gameIntent)
            }
        }.start()
    }

    override fun finish() {
        super.finish()
        timer?.cancel()
    }

    private fun goToCatalog() {
        val intent = Intent(this, CatalogActivity::class.java)
         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goToCatalog()
    }
}