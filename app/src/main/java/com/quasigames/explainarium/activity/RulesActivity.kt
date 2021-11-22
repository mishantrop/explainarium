package com.quasigames.explainarium.activity

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.quasigames.explainarium.BuildConfig
import com.quasigames.explainarium.R
import com.quasigames.explainarium.entity.AppMetrikaSingleton

class RulesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)

        val textViewRules: TextView = findViewById(R.id.rules_text)

        val res: Resources = resources

        textViewRules.text = String.format(res.getString(R.string.rules_text), BuildConfig.VERSION_CODE)

        AppMetrikaSingleton.reportEvent(
            applicationContext,
            "Rules",
            HashMap(),
        )
    }
}
