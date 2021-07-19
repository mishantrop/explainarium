package com.quasigames.explainarium.com.quasigames.explainarium.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.quasigames.explainarium.activity.CatalogActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        try {
            val catalogIntent = Intent(this, CatalogActivity::class.java)
            startActivity(catalogIntent)
        } catch (error: Exception) {
            println("Explainarium" + error.message)

            val errorToast = Toast.makeText(this, error.message, Toast.LENGTH_LONG)
            errorToast.show()
        }
    }
}
