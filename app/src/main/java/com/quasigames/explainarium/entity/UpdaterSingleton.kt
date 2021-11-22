package com.quasigames.explainarium.entity

import com.google.gson.GsonBuilder
import com.quasigames.explainarium.BuildConfig
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object UpdaterSingleton {
    fun isEnabled(): Boolean {
         return BuildConfig.BUILD_TYPE == "release"
    }

    fun isUpdateAvailable(updateInfo: UpdateInfo): Boolean {
        return updateInfo.VERSION_CODE > BuildConfig.VERSION_CODE
    }

    fun isValidResponse(responseText: String): Boolean {
        val builder = GsonBuilder()
        val gson = builder.create()
        var isValid = false

        try {
            val updateInfo = gson.fromJson(responseText, UpdateInfo::class.java)
            isValid = updateInfo.VERSION_CODE > 0
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        return isValid
    }

    fun getUpdateInfoObject(responseText: String): UpdateInfo {
        val builder = GsonBuilder()
        val gson = builder.create()
        var updateInfo = UpdateInfo()

        try {
            updateInfo = gson.fromJson(responseText, UpdateInfo::class.java)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        return updateInfo
    }

    fun fetchUpdateInfo(updateInfoUri: String): String {
        val inputStream: InputStream
        var result = ""
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null

        try {
            val url = URL(updateInfoUri)
            connection = url.openConnection() as HttpURLConnection

            connection.setRequestProperty("Accept-Encoding", "identity")

            connection.setRequestProperty("Accept-Charset", "utf-8")
            connection.setRequestProperty("Accept", "application/json")

            connection.connect()

            inputStream = connection.inputStream

            reader = BufferedReader(inputStream.reader())
            result = reader.readText()
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            connection?.disconnect()
            reader?.close()
        }

        return result
    }
}
