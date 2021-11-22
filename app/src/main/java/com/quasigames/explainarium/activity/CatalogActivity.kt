package com.quasigames.explainarium.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.quasigames.explainarium.R
import com.quasigames.explainarium.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream

class CatalogActivity : AppCompatActivity() {
    private lateinit var statistics: SharedPreferences

    private fun goToStore() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    private fun initRateNotification() {
        val rateNotification: LinearLayout = findViewById(R.id.rate_notification)
        val rateNotificationButton: Button = findViewById(R.id.rate_button)
        val rateCloseNotificationButton: Button = findViewById(R.id.star_close_button)

        if (StatisticsV1Singleton.canShow(statistics)) {
            rateNotification.visibility = View.VISIBLE

            // Нажатие на кнопку Оценить
            rateNotificationButton.setOnClickListener {
                rateNotification.visibility = View.GONE

                statistics = getSharedPreferences(StatisticsV1Singleton.FILENAME, Context.MODE_PRIVATE)
                val editor = statistics.edit()
                editor.putBoolean(StatisticsV1Singleton.STATISTICS_V1_GAME_AGREED, true).apply()

                AppMetrikaSingleton.reportEvent(
                    applicationContext,
                    "Rater/Rate",
                    HashMap(), // TODO Количество игр
                )

                goToStore()
            }

            // Нажатие на кнопку с крестиком
            rateCloseNotificationButton.setOnClickListener {
                rateNotification.visibility = View.GONE

                AppMetrikaSingleton.reportEvent(
                    applicationContext,
                    "Rater/Dismiss",
                    HashMap(), // TODO Количество игр
                )

                statistics = getSharedPreferences("statistics1", Context.MODE_PRIVATE)
                val editor = statistics.edit()
                editor.putBoolean(StatisticsV1Singleton.STATISTICS_V1_RATE_DISMISSED, true).apply()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statistics = getSharedPreferences(StatisticsV1Singleton.FILENAME, Context.MODE_PRIVATE)
        setContentView(R.layout.activity_catalog)

        try {
            /**
             * Нижняя навигация
             */
            val bottomMenuButtonRules: Button = findViewById(R.id.bottom_menu_help_button)
            bottomMenuButtonRules.setOnClickListener {
                startActivity(Intent(this, RulesActivity::class.java))
            }
            val bottomMenuButtonPrivacyPolicy: Button = findViewById(R.id.bottom_menu_privacy_policy_button)
            bottomMenuButtonPrivacyPolicy.setOnClickListener {
                AppMetrikaSingleton.reportEvent(
                    applicationContext,
                    "Catalog/PrivacyPolicy",
                    HashMap(),
                )
                redirectToPrivacyPolicy()
            }
            val bottomMenuButtonAbout: Button = findViewById(R.id.bottom_menu_about_button)
            bottomMenuButtonAbout.setOnClickListener {
                startActivity(Intent(this, AboutActivity::class.java))
            }

            /**
             * Инициализация каталога
             */
            val catalog = getCatalogFromFile()

            buildCatalogViews(catalog.subjects)

            /**
             * Прверка обновлений
             */
            if (UpdaterSingleton.isEnabled()) {
                checkUpdates()
            }
        } catch (error: Exception) {
            println("Explainarium | Error: $error")
            println("Explainarium | Message: " + error.message)
            println("Explainarium | Cause: " + error.cause)
            error.stackTrace.forEach {x ->
                println("Explainarium | Stack: $x")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initRateNotification()
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
                    AppMetrikaSingleton.reportEvent(
                        applicationContext,
                        "Updater/Click",
                        HashMap(),
                    )
                    goToStore()
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
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
        // val res: Resources = resources
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
                    400,
                )
                subjectCard.addView(subjectImage)
            }

            // Собираем всё вместе
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

    @Suppress("SameParameterValue")
    private fun getAssetsFileContent(context: Context, fileName: String): String =
        context
            .assets
            .open(fileName)
            .bufferedReader()
            .use(BufferedReader::readText)
}
