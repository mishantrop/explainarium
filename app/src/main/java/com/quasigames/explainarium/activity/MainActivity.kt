package com.quasigames.explainarium.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val catalogIntent = Intent(this, CatalogActivity::class.java)
            startActivity(catalogIntent)
        } catch (error: Exception) {
            println("Explainarium | Error: $error")
            println("Explainarium | Message: " + error.message)
            println("Explainarium | Cause: " + error.cause)
            error.stackTrace.forEach {x ->
                println("Explainarium | Stack: $x")
            }
        }
    }
}
