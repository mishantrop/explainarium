package com.quasigames.explianarium.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
//import androidx.cardview.widget.CardView
import com.google.gson.GsonBuilder
import com.quasigames.explainarium.R
import com.quasigames.explianarium.entity.Catalog
import com.quasigames.explianarium.entity.CatalogSubject
import java.io.BufferedReader
import java.io.InputStream

class CatalogActivity : AppCompatActivity() {
    private var catalogLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        catalogLayout = findViewById(R.id.catalog)

        try {
            val assetsList: List<Array<String>?> = listOf(resources.assets.list(""))
            var assetsFilesCount = 0
            assetsList.forEachIndexed{index, filenames ->
                if (index == 0) {
                    filenames?.forEach { filename ->
                        assetsFilesCount = assetsFilesCount + 1
                    }
                }
            }
            val assetsA = Array<String>(assetsFilesCount){""}
            assetsList.forEachIndexed{index, filenames ->
                if (index == 0) {
                    filenames?.forEachIndexed { idx, filename ->
                        assetsA[idx] = filename
                    }
                }
            }

            val preparingIntent = Intent(this, PreparingActivity::class.java)

            println("Explainarium: reading catalog.json")
            val catalogDataJSON = getAssetsFileContent(this, "catalog.json")

            val builder = GsonBuilder()
            val gson = builder.create()
            val catalog = gson.fromJson(catalogDataJSON, Catalog::class.java)

            val subjectCardLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            subjectCardLayoutParams.bottomMargin = 20

            catalog.subjects?.forEach { subject ->
                val subjectCard = LinearLayout(this)
                val subjectButton = Button(this)
                val subjectComplexity = TextView(this)

                // Кнопка
                subjectButton.text = subject.title
                subjectButton.height = 100
                subjectButton.width = 100
                subjectButton.setOnClickListener {
                    goToSubject(preparingIntent, gson, subject)
                }

                // Карточка
                subjectCard.layoutParams = subjectCardLayoutParams
                subjectCard.orientation = LinearLayout.VERTICAL
                subjectCard.setBackgroundColor(Color.WHITE)
                subjectCard.setPadding(0, 10, 0,0)
                subjectCard.setOnClickListener {
                    goToSubject(preparingIntent, gson, subject)
                }

                // Изоброжение
                val imageFilename = "catalog_${subject.id}.jpg"
                if (assetsA.contains(imageFilename)) {
                    val subjectImage = ImageView(this)
                    val ims: InputStream = assets.open( imageFilename)
                    val d = Drawable.createFromStream(ims, null)
                    subjectImage.setImageDrawable(d)
                    subjectImage.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        300
                    )
                    subjectCard.addView(subjectImage)
                }

                // Сложность
                val complexityEmoji = when (subject.complexity) {
                    1 -> "\uD83C\uDF1D\uD83C\uDF1A\uD83C\uDF1A"
                    2 -> "\uD83C\uDF1D\uD83C\uDF1D\uD83C\uDF1A"
                    3 -> "\uD83C\uDF1D\uD83C\uDF1D\uD83C\uDF1D"
                    else -> {
                        "\uD83C\uDF1A\uD83C\uDF1A\uD83C\uDF1A"
                    }
                }
                subjectComplexity.text = "Сложность: $complexityEmoji"
                subjectComplexity.textAlignment = View.TEXT_ALIGNMENT_CENTER

                // Собираем всё вместе
                subjectCard.addView(subjectComplexity)
                subjectCard.addView(subjectButton)

                catalogLayout?.addView(subjectCard)
            }
        } catch (error: Exception) {
            println("Explainarium: " + error.message)
            val errorToast = Toast.makeText(this, error.message, Toast.LENGTH_LONG)
            errorToast.show()
        }
    }

    fun goToSubject(preparingIntent: Intent, gson: Gson, subject: CatalogSubject) {
        preparingIntent.putExtra("subject", gson.toJson(subject))
        startActivity(preparingIntent)
    }

    fun getAssetsFileContent(context: Context, fileName: String): String =
        context
            .assets
            .open(fileName)
            .bufferedReader()
            .use(BufferedReader::readText)
}
