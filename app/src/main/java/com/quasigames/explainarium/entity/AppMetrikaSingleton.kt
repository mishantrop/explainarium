package com.quasigames.explainarium.entity

import android.content.Context
import com.quasigames.explainarium.BuildConfig
import com.yandex.metrica.YandexMetrica

object AppMetrikaSingleton {
    const val apiKey = "230209a3-6d7a-49d7-8fa3-ed5085c8136d"

    fun isEnabled(): Boolean {
         return BuildConfig.BUILD_TYPE == "release"
    }

    fun reportEvent(applicationContext: Context, eventName: String, eventParameters: HashMap<String, Any>?) {
        if (BuildConfig.BUILD_TYPE == "debug") {
            println("Explainarium | Report Event: " + eventName + "; Parameters: " + eventParameters?.size)
            if (eventParameters?.size!! > 0) {
                eventParameters.forEach { parameter ->
                    println("Explainarium | Report Event: $eventName; Parameter: $parameter")
                }

            }
        }

        var eventNameToSend = eventName

        if (BuildConfig.BUILD_TYPE == "debug") {
            eventNameToSend = "debug:$eventNameToSend"
        }

        if (isEnabled()) {
            YandexMetrica
                .getReporter(applicationContext, apiKey)
                .reportEvent(eventNameToSend, eventParameters)
        }
    }
}
