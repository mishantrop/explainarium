package com.quasigames.explainarium.activity

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.quasigames.explainarium.BuildConfig
import com.quasigames.explainarium.R
import com.quasigames.explainarium.entity.AppMetrikaSingleton
import com.quasigames.explainarium.entity.StatisticsV1Singleton

class AboutActivity : AppCompatActivity() {
    private lateinit var statistics: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statistics = getSharedPreferences(StatisticsV1Singleton.FILENAME, Context.MODE_PRIVATE)
        setContentView(R.layout.activity_about)

        val textViewVersionCode: TextView = findViewById(R.id.about_version_code)
        val textViewBuildType: TextView = findViewById(R.id.about_build_type)
        val textViewVersionName: TextView = findViewById(R.id.about_version_name)
        val textViewStatisticsV1GameCount: TextView = findViewById(R.id.statistics_v1_game_count)

        val res: Resources = resources

        textViewVersionCode.text = String.format(res.getString(R.string.about_version_code), BuildConfig.VERSION_CODE)
        textViewBuildType.text = String.format(res.getString(R.string.about_build_type), BuildConfig.BUILD_TYPE)
        textViewVersionName.text = String.format(res.getString(R.string.about_version_name), BuildConfig.VERSION_NAME)
        textViewStatisticsV1GameCount.text = String.format(res.getString(R.string.statistics_v1_game_count), StatisticsV1Singleton.getGameCount(statistics))

        actionBar?.setDisplayHomeAsUpEnabled(true)

        AppMetrikaSingleton.reportEvent(
            applicationContext,
            "About",
            HashMap(),
        )
    }
}
