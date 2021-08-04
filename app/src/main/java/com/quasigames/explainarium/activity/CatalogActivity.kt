package com.quasigames.explainarium.activity

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
import androidx.gridlayout.widget.GridLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.quasigames.explainarium.R
import com.quasigames.explainarium.entity.Catalog
import com.quasigames.explainarium.entity.CatalogSubject
import java.io.BufferedReader
import java.io.InputStream

class CatalogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_catalog)

        try {
            val catalog = getCatalogFromFile()

            buildCatalogViews(catalog.subjects)
        } catch (error: Exception) {
            println("Explainarium: " + error.message)
            val errorToast = Toast.makeText(this, error.message, Toast.LENGTH_LONG)
            errorToast.show()
        }
    }

//    override fun onBackPressed() {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        startActivity(intent)
//    }

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
            R.id.action_about -> {
                val aboutIntent = Intent(this, AboutActivity::class.java)
                startActivity(aboutIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun redirectToPrivacyPolicy() {
        val res: Resources = resources
        val url = res.getString(R.string.action_privacy_policy_uri)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun getCatalogFromFile(): Catalog {
        println("Explainarium: reading catalog.json")
        val catalogFilename = "catalog.json"
        val catalogDataJSON = getAssetsFileContent(this, "catalog/$catalogFilename")

        val builder = GsonBuilder()
        val gson = builder.create()

        return gson.fromJson(catalogDataJSON, Catalog::class.java)
    }

    private fun getImagesFilenames(): Array<String> {
        val catalogImagesPath = "catalog/images"
        val assetsList: List<Array<String>?> = listOf(resources.assets.list(catalogImagesPath))

        // Выясняем количество файлов в каталоге, чтобы создать массив нужного размера
        var assetsFilesCount = 0
        assetsList.forEachIndexed{_, filenames ->
            filenames?.forEach { _ ->
                assetsFilesCount += 1
            }
        }

        // Заполнение массива именами файлов изображений
        val assetsA = Array(assetsFilesCount){""}
        assetsList.forEachIndexed{_, filenames ->
            filenames?.forEachIndexed { idx, filename ->
                assetsA[idx] = filename
            }
        }

        return assetsA
    }

    private fun buildCatalogViews(subjects: Collection<CatalogSubject>?) {
        val builder = GsonBuilder()
        val gson = builder.create()
        val res: Resources = resources
        val catalogLayout: GridLayout = findViewById(R.id.catalog)
        catalogLayout.removeAllViewsInLayout()
//        val preparingIntent = Intent(this, PreparingActivity::class.java)

        val catalogImagesFilenames = getImagesFilenames()
        val isComplexityVisible = false

        subjects?.forEach { subject ->
            val subjectCard = LinearLayout(this)
            val subjectButton = Button(this)
            val subjectComplexity = TextView(this)
            val subjectWordsCount = TextView(this)

            // Кнопка
            subjectButton.text = subject.title
            subjectButton.height = 100
            subjectButton.width = 100
            subjectButton.textSize = 11.0F
            subjectButton.setOnClickListener {
                goToSubject(gson, subject)
            }

            // Карточка
            val subjectCardLayoutParams: GridLayout.LayoutParams = GridLayout.LayoutParams()
            val spec = GridLayout.spec(GridLayout.UNDEFINED, 1.0f)
            subjectCardLayoutParams.columnSpec = spec
            subjectCardLayoutParams.topMargin = 10
            subjectCardLayoutParams.rightMargin = 10
            subjectCardLayoutParams.bottomMargin = 10
            subjectCardLayoutParams.leftMargin = 10
            subjectCard.layoutParams = subjectCardLayoutParams
            subjectCard.orientation = LinearLayout.VERTICAL
//            subjectCard.setBackgroundColor(Color.WHITE)
            subjectCard.background = ContextCompat.getDrawable(this, R.drawable.rounded_edge)
            subjectCard.setPadding(0, 0, 0,0)
            subjectCard.setOnClickListener {
                goToSubject(gson, subject)
            }

            // Изображение
            val imageFilename = "catalog/images/${subject.id}.png"
            val isImageExists = catalogImagesFilenames.contains("${subject.id}.png")
            if (isImageExists) {
                val subjectImage = ImageView(this)
                val ims: InputStream = assets.open( imageFilename)
                val d = Drawable.createFromStream(ims, null)
                subjectImage.setImageDrawable(d)
                subjectImage.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    320
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
            subjectComplexity.text = String.format(res.getString(R.string.catalog_subject_complexity), complexityEmoji)
            subjectComplexity.gravity = Gravity.CENTER_HORIZONTAL

            // Количество слов
            subjectWordsCount.text = String.format(res.getString(R.string.catalog_subject_wordscount), subject.words.size)
            subjectWordsCount.gravity = Gravity.CENTER_HORIZONTAL

            // Собираем всё вместе
            if (isComplexityVisible) {
                subjectCard.addView(subjectComplexity)
            }
            subjectCard.addView(subjectWordsCount)
            subjectCard.addView(subjectButton)

            if (isImageExists) {
                catalogLayout.addView(subjectCard)
            }
        }
    }

    private fun goToSubject(gson: Gson, subject: CatalogSubject) {
        val preparingIntent = Intent(this, PreparingActivity::class.java)
        preparingIntent.putExtra("subject", gson.toJson(subject))
        // preparingIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(preparingIntent)
    }

    private fun getAssetsFileContent(context: Context, fileName: String): String =
        context
            .assets
            .open(fileName)
            .bufferedReader()
            .use(BufferedReader::readText)
}
