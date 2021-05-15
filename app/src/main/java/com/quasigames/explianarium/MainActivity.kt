package com.quasigames.explainarium

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.gson.GsonBuilder
import com.quasigames.explianarium.PreparingActivity

class MainActivity : AppCompatActivity() {
    private var catalogLayout: LinearLayout? = null
//    private var isLightMode = true
//    private var isDarkMode = !isLightMode
//    private val field = Field()
//    private val gameplay = Gameplay()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        catalogLayout = findViewById(R.id.catalog)

        val catalogDataJSON = """
            {
                "subjects": [
                    {
                        "id": 1,
                        "title": "Спорт",
                        "words": ["бита", "футбол", "велосипед", "лыжи"]
                    },
                    {
                        "id": 2,
                        "title": "Мультфильмы",
                        "words": ["Король Лев","Шрек","Теремок","Кот в сапогах"]
                    },
                    {
                        "id": 3,
                        "title": "Алкоголь",
                        "words": ["Пиво","Виски","Водка","Мартини"]
                    }
                ]
            }
            """

//        val fileContent = javaClass.getResource("ru.json")?.readText()
//        println("XXX fileContent $fileContent")

        val builder = GsonBuilder()
        val gson = builder.create()
        val catalog = gson.fromJson(catalogDataJSON, Catalog::class.java)

        catalog.subjects?.forEach { subject ->
            val subjectCard = CardView(this)
            val subjectImage = ImageButton(this)
            val subjectButton = Button(this)

            subjectButton.text = subject.title
            subjectButton.height = 100
            subjectButton.width = 100
            subjectButton.setOnClickListener {
                val preparingIntent = Intent(this, PreparingActivity::class.java)
                preparingIntent.putExtra("subject", gson.toJson(subject))
                startActivity(preparingIntent)
            }

            subjectCard.addView(subjectImage)
            subjectCard.addView(subjectButton)

            subjectCard.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            catalogLayout?.addView(subjectCard)
//            catalogLayout?.invalidate()
        }
        // val intent = Intent(this, AdActivity::class.java)
         startActivity(intent)

//        val rToast = Toast.makeText(this, "Hello world", Toast.LENGTH_LONG)
//        rToast.show()

        try {
//            field.init(this, fieldGrid!!)
//            gameplay.init(field)
//
////            val vto: ViewTreeObserver = mainLayoutToolbar.viewTreeObserver
////            vto.addOnGlobalLayoutListener {
////                val mainLayoutToolbarHeight = mainLayoutToolbar.height
////                val actionbarHeight = getActionbarHeight()
////                field.setViewportSize(0, mainLayout.height - mainLayoutToolbarHeight - actionbarHeight)
////            }
        } catch (error: Exception) {
            println("xxx" + error.message)
//            val errorToast = Toast.makeText(this, error.message, Toast.LENGTH_LONG)
//            errorToast.show()
        }
    }


//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_ad, menu)
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_show_ad -> {
////                setTheme(1)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

//    private fun getActionbarHeight(): Int {
//        val tv = TypedValue()
//        var actionBarHeight = 0
//        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
//            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
//        }
//
//        return actionBarHeight
//    }

//    private fun setTheme(value: Boolean) {
//        val mainLayout = findViewById<LinearLayout>(R.id.activity_main_layout)
//
//        isDarkMode = value
//
//        if (value) {
//            mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundDark))
//        } else {
//            mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundLight))
//        }
//    }
}
