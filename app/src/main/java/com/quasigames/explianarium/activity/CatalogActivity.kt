package com.quasigames.explianarium.activity

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.quasigames.explainarium.R
import com.quasigames.explianarium.entity.Catalog
import com.quasigames.explianarium.entity.CatalogSubject
import java.io.BufferedReader
import java.io.InputStream

class CatalogActivity : AppCompatActivity() {
    private val isDev = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        try {
            val catalog = getCatalogFromFile()

            buildCatalogViews(catalog.subjects)
        } catch (error: Exception) {
            println("Explainarium: " + error.message)
            val errorToast = Toast.makeText(this, error.message, Toast.LENGTH_LONG)
            errorToast.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_privacy_policy -> {
                redirectToPrivacyPolicy()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun redirectToPrivacyPolicy() {
        val url = "https://quasi-art.ru/quasigames/explainarium/privacy-policy"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun getCatalogFromFile(): Catalog {
        println("Explainarium: reading catalog.json")
        val catalogFilename = if (isDev) "catalog_dev.json" else "catalog.json"
        val catalogDataJSON = getAssetsFileContent(this, catalogFilename)

        val builder = GsonBuilder()
        val gson = builder.create()

        return gson.fromJson(catalogDataJSON, Catalog::class.java)
    }

    private fun getAssetsFilenames(): Array<String> {
        val assetsList: List<Array<String>?> = listOf(resources.assets.list(""))
        var assetsFilesCount = 0
        assetsList.forEachIndexed{index, filenames ->
            if (index == 0) {
                filenames?.forEach { _ ->
                    assetsFilesCount += 1
                }
            }
        }
        val assetsA = Array(assetsFilesCount){""}
        assetsList.forEachIndexed{index, filenames ->
            if (index == 0) {
                filenames?.forEachIndexed { idx, filename ->
                    assetsA[idx] = filename
                }
            }
        }

        return assetsA
    }

    private fun buildCatalogViews(subjects: Collection<CatalogSubject>?) {
        val builder = GsonBuilder()
        val gson = builder.create()
        val catalogLayout: LinearLayout = findViewById(R.id.catalog)
        catalogLayout.removeAllViewsInLayout()
        val preparingIntent = Intent(this, PreparingActivity::class.java)

        val assetsA = getAssetsFilenames()

        val subjectCardLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        subjectCardLayoutParams.bottomMargin = 20

        subjects?.forEach { subject ->
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
//            subjectCard.setBackgroundColor(Color.WHITE)
            subjectCard.background = ContextCompat.getDrawable(this, R.drawable.rounded_edge)
            subjectCard.setPadding(0, 10, 0,0)
            subjectCard.setOnClickListener {
                goToSubject(preparingIntent, gson, subject)
            }

            // Изображение
            val imageFilename = "catalog_${subject.id}.png"
            if (assetsA.contains(imageFilename)) {
                val subjectImage = ImageView(this)
                val ims: InputStream = assets.open( imageFilename)
                val d = Drawable.createFromStream(ims, null)
                subjectImage.setImageDrawable(d)
                subjectImage.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    500
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
            val res: Resources = resources
//            subjectComplexity.text = "Сложность: $complexityEmoji"
            subjectComplexity.text = String.format(res.getString(R.string.catalog_subject_complexity), complexityEmoji)
            subjectComplexity.textAlignment = View.TEXT_ALIGNMENT_CENTER

            // Собираем всё вместе
            subjectCard.addView(subjectComplexity)
            subjectCard.addView(subjectButton)

            catalogLayout.addView(subjectCard)
        }
    }

    private fun goToSubject(preparingIntent: Intent, gson: Gson, subject: CatalogSubject) {
        preparingIntent.putExtra("subject", gson.toJson(subject))
        startActivity(preparingIntent)
    }

    private fun getAssetsFileContent(context: Context, fileName: String): String =
        context
            .assets
            .open(fileName)
            .bufferedReader()
            .use(BufferedReader::readText)
}
