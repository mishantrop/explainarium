package com.quasigames.explainarium

import android.content.Intent
import android.os.Bundle
import android.view.*
//import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.quasigames.explianarium.activity.CatalogActivity

class MainActivity : AppCompatActivity() {
//    private var catalogLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

//        setContentView(R.layout.activity_main)

        try {
            val catalogIntent = Intent(this, CatalogActivity::class.java)
            startActivity(catalogIntent)
        } catch (error: Exception) {
            println("xxx" + error.message)

            val errorToast = Toast.makeText(this, error.message, Toast.LENGTH_LONG)
            errorToast.show()
        }
    }
}
