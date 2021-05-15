package com.quasigames.explianarium

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.quasigames.explainarium.R
import kotlinx.android.synthetic.main.activity_preparing.*


class PreparingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preparing)

        val subject = intent.getStringExtra("subject")

        prepare_counter_text.text = "4"
        var textGo = "Погнали"

        val gameIntent = Intent(this, GameActivity::class.java)
        gameIntent.putExtra("subject", subject)

        object : CountDownTimer(4000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                var text = (millisUntilFinished / 1000).toString()
                if (text === "0") {
                    prepare_counter_text.text = textGo
                } else {
                    prepare_counter_text.text = text
                }
            }

            override fun onFinish() {
                prepare_counter_text.text = textGo

                startActivity(gameIntent)
            }
        }.start()
    }
}