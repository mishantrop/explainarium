package com.quasigames.explainarium

import android.app.Application
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import com.quasigames.explainarium.entity.AppMetrikaSingleton

class Explainarium : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            if (AppMetrikaSingleton.isEnabled()) {
                initAppMetrica()
            }
        } catch (error: Exception) {
            println("Explainarium: " + error.message)
        }
    }

    private fun initAppMetrica() {
        val config = YandexMetricaConfig.newConfigBuilder(AppMetrikaSingleton.apiKey).build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }
}
