package com.quasigames.explainarium

import android.app.Application
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

class Explainarium : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            if (!BuildConfig.DEBUG) {
                val appMetricaApiKey = "230209a3-6d7a-49d7-8fa3-ed5085c8136d"
                val config = YandexMetricaConfig.newConfigBuilder(appMetricaApiKey).build()

                YandexMetrica.activate(applicationContext, config)
                YandexMetrica.enableActivityAutoTracking(this)
            }
        } catch (error: Exception) {
            println("Explainarium: " + error.message)
        }
    }
}
