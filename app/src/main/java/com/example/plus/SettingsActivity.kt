package com.example.plus

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import androidx.preference.PreferenceManager
import java.io.File


class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Кнопка назад
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Настройка переключателя темы
        val themeSwitch = findViewById<Switch>(R.id.themeSwitch)
        themeSwitch.isChecked = isDarkThemeEnabled()

        // Обработчик переключения темы
        findViewById<LinearLayout>(R.id.themeContainer).setOnClickListener {
            themeSwitch.isChecked = !themeSwitch.isChecked
            toggleTheme(themeSwitch.isChecked)
        }

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            toggleTheme(isChecked)
        }

        // Поделиться приложением
        findViewById<LinearLayout>(R.id.shareContainer).setOnClickListener {
            shareApp()
        }

        // Сообщить о проблеме
        findViewById<LinearLayout>(R.id.reportContainer).setOnClickListener {
            showReportDialog()
        }

        // Официальная страница
        findViewById<LinearLayout>(R.id.repoContainer).setOnClickListener {
            openRepository()
        }

        // Отображение версии приложения
        val versionText = findViewById<TextView>(R.id.tvVersion)
        versionText.text = getString(R.string.app_version, getAppVersion())
    }

    private fun isDarkThemeEnabled(): Boolean {
        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun toggleTheme(isDark: Boolean) {
        val mode = if (isDark) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        AppCompatDelegate.setDefaultNightMode(mode)

        // Сохраняем выбор темы
        sharedPreferences.edit().putBoolean("dark_theme", isDark).apply()
    }

    private fun shareApp() {
        val shareText = """
        Простой калькулятор - лучший калькулятор, который легко выполняет базовые операции.
        
        https://github.com/zyrouge/symphony/releases/tag/v2024.12.115
    """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Попробуй этот калькулятор")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        try {
            startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
        } catch (e: Exception) {
            Toast.makeText(this, "Не удалось открыть диалог", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ServiceCast")
    private fun showReportDialog() {
        val supportEmail = "support@example.com" // Замените на реальный email

        AlertDialog.Builder(this)
            .setTitle(R.string.report_dialog_title)
            .setMessage(getString(R.string.report_dialog_message, supportEmail))
            .setPositiveButton(R.string.copy_email) { _, _ ->
                // Копирование email в буфер обмена
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Support Email", supportEmail)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, R.string.email_copied, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.ok, null)
            .show()
    }


    private fun openRepository() {
        val repoUrl = getString(R.string.repo_url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repoUrl))
        startActivity(intent)
    }

    private fun getAppVersion(): String {
        return try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            pInfo.versionName ?: "2.8.9" // Обработка null значения
        } catch (e: Exception) {
            "2.8.9"
        }
    }

    // При закрытии настроек
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}