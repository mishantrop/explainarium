package com.quasigames.explainarium.entity

import android.content.Context
import android.content.SharedPreferences

object StatisticsV1Singleton {
    private const val STATISTICS_V1_GAME_COUNT = "game_count"
    const val FILENAME = "statistics1"
    const val STATISTICS_V1_RATE_DISMISSED = "rater_dismissed"
    const val STATISTICS_V1_GAME_AGREED = "rater_agreed"

    fun incGameCount(statistics: SharedPreferences) {
        /**
         * По-простому считаем количество сыгранных игр
         * Если их количество кратно трём, или стоит флаг, что игру не оценили,
         * то на главном экране показываем плашечку.
         * Если плашечку закрывали, то не нужно показывать плашечку.
         */
        val editor = statistics.edit()
        var prevGameCount = 0
        if (statistics.contains(STATISTICS_V1_GAME_COUNT)){
            // Получаем число из настроек
            prevGameCount = statistics.getInt(STATISTICS_V1_GAME_COUNT, 0)
        }
        editor.putInt(STATISTICS_V1_GAME_COUNT, prevGameCount + 1).apply()
    }

    fun getGameCount(statistics: SharedPreferences): Int {
        var gameCount = 0

        if (statistics.contains(STATISTICS_V1_GAME_COUNT)){
            // Получаем число из настроек
            gameCount = statistics.getInt(STATISTICS_V1_GAME_COUNT, 0)
        } else {
            val editor = statistics.edit()
            editor.putInt(STATISTICS_V1_GAME_COUNT, 0).apply()
        }

        return gameCount
    }

    private fun isRaterAgreed(statistics: SharedPreferences): Boolean {
        var isAgreed = false

        if (statistics.contains(STATISTICS_V1_GAME_AGREED)){
            isAgreed = statistics.getBoolean(STATISTICS_V1_GAME_AGREED, false)
        }

        return isAgreed
    }

    private fun isRaterDismissed(statistics: SharedPreferences): Boolean {
        var isAgreed = false

        if (statistics.contains(STATISTICS_V1_RATE_DISMISSED)){
            isAgreed = statistics.getBoolean(STATISTICS_V1_RATE_DISMISSED, false)
        }

        return isAgreed
    }

    fun canShow(statistics: SharedPreferences): Boolean {
        val isRaterAgreed = isRaterAgreed(statistics)
        val isRaterDismissed = isRaterDismissed(statistics)
        val gameCount = getGameCount(statistics)

        if (isRaterAgreed) {
            return false
        }

        if (isRaterDismissed && gameCount > 0 && gameCount % 10 == 0) {
            return true
        }

        if (gameCount > 0 && gameCount % 3 == 0) {
            return true
        }

        return false
    }
}
