package com.quasigames.explainarium.entity

import com.quasigames.explainarium.BuildConfig
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object UpdaterSingleton {
    fun isEnabled(): Boolean {
         return BuildConfig.BUILD_TYPE == "release"
    }

    fun isUpdateRequired(updateInfo: UpdateInfo): Boolean {
        return updateInfo.VERSION_CODE > BuildConfig.VERSION_CODE
    }

    fun fetchUpdateInfo(myURL: String): String {
        val inputStream: InputStream
        var result = ""

        try {
            val url = URL(myURL)
            val conn = url.openConnection() as HttpURLConnection

            conn.setRequestProperty("Accept-Encoding", "identity") // TODO try gzip

            conn.setRequestProperty("Accept-Charset", "utf-8")
            conn.setRequestProperty("Accept", "application/json")

            conn.connect()

            inputStream = conn.inputStream

            val reader = BufferedReader(inputStream.reader())
            result = reader.readText()
            reader.close()
        } catch(err:Error) {
            print("Error when executing get request: "+err.localizedMessage)
        }

        return result
    }
}
