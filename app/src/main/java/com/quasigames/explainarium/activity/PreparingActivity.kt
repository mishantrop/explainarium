package com.quasigames.explainarium.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.quasigames.explainarium.R

class PreparingActivity : AppCompatActivity() {
    private lateinit var timer: CountDownTimer
    private val initialTimerValueSecs: Long = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preparing)
        val prepareCounterText: TextView = findViewById(R.id.prepare_counter_text)

        val subject = intent.getStringExtra("subject")

        prepareCounterText.text = (initialTimerValueSecs + 1).toString()
        val textGo = getString(R.string.preparing_go)

        val gameIntent = Intent(this, GameActivity::class.java)
        gameIntent.putExtra("subject", subject)

        timer = object : CountDownTimer(initialTimerValueSecs * 1000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                val text = timeLeft.toString()
                prepareCounterText.text = if (timeLeft <= 0) textGo else text
            }

            override fun onFinish() {
                prepareCounterText.text = textGo

                startActivity(gameIntent)
            }
        }.start()
    }

    override fun finish() {
        super.finish()
        timer.cancel()
    }

    private fun goToCatalog() {
        val intent = Intent(this, CatalogActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        goToCatalog()
    }
}
