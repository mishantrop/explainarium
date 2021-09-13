package com.quasigames.explainarium.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.quasigames.explainarium.BuildConfig
import com.quasigames.explainarium.R
import com.quasigames.explainarium.entity.AppMetrikaSingleton
import com.quasigames.explainarium.entity.UpdaterSingleton
import com.quasigames.explainarium.entity.Catalog
import com.quasigames.explainarium.entity.CatalogSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

            if (UpdaterSingleton.isEnabled()) {
                checkUpdates()
            }
        } catch (error: Exception) {
            println("Explainarium: " + error.message)
            Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun checkUpdates() {
        val res: Resources = resources

        lifecycleScope.launch(Dispatchers.IO) {
            val responseText = UpdaterSingleton.fetchUpdateInfo(res.getString(R.string.update_uri))

            withContext(Dispatchers.Main) {
                processUpdateInfoResponse(responseText)
            }
        }
    }

    private fun processUpdateInfoResponse(responseText: String) {
        if (!UpdaterSingleton.isValidResponse(responseText)) {
            return
        }
        val updateInfo = UpdaterSingleton.getUpdateInfoObject(responseText)

        if (UpdaterSingleton.isUpdateAvailable(updateInfo)) {
            try {
                val updateNotification: LinearLayout = findViewById(R.id.update_notification)
                val updateNotificationButton: Button = findViewById(R.id.update_button)
                updateNotification.visibility = View.VISIBLE

                updateNotificationButton.setOnClickListener {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
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
                AppMetrikaSingleton.reportEvent(
                    applicationContext,
                    "Catalog/PrivacyPolicy",
                    HashMap(),
                )
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
        val orientation = this.resources.configuration.orientation

        val builder = GsonBuilder()
        val gson = builder.create()
        val res: Resources = resources
        val catalogLayout: GridLayout = findViewById(R.id.catalog)
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            catalogLayout.columnCount = 2
        } else {
            catalogLayout.columnCount = 3
        }
        catalogLayout.removeAllViewsInLayout()

        val catalogImagesFilenames = getImagesFilenames()

        subjects?.forEach { subject ->
            val subjectCard = LinearLayout(this)
            val subjectButton = Button(this)
            val subjectWordsCount = TextView(this)

            // Кнопка
            subjectButton.text = subject.title
            subjectButton.height = 100
            subjectButton.width = 100
            subjectButton.textSize = 11.0F
            subjectButton.setOnClickListener {
                goToSubject(gson, subject)
            }
            subjectButton.background = ContextCompat.getDrawable(this, R.color.cardBackgroundLight)

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

            // Количество слов
            subjectWordsCount.text = String.format(res.getString(R.string.catalog_subject_wordscount), subject.words.size)
            subjectWordsCount.gravity = Gravity.CENTER_HORIZONTAL

            // Собираем всё вместе
            subjectCard.addView(subjectWordsCount)
            subjectCard.addView(subjectButton)

            if (isImageExists) {
                catalogLayout.addView(subjectCard)
            }
        }
    }

    private fun goToSubject(gson: Gson, subject: CatalogSubject) {
        // Отправка события о старте определённой категории
        AppMetrikaSingleton.reportEvent(
            applicationContext,
            "Game/Start",
            hashMapOf("title" to subject.title),
        )

        val preparingIntent = Intent(this, PreparingActivity::class.java)
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
