package com.example.plus

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyListView: ListView
    private lateinit var clearHistoryButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var sharedPreferences: SharedPreferences
    private var historyList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Установка русской локали для корректного отображения даты
        Locale.setDefault(Locale("ru"))

        historyListView = findViewById(R.id.historyListView)
        clearHistoryButton = findViewById(R.id.btnClearHistory)
        backButton = findViewById(R.id.btnBack)
        sharedPreferences = getSharedPreferences("CalculatorHistory", Context.MODE_PRIVATE)

        loadHistory()

        val adapter = HistoryAdapter(this, historyList)
        historyListView.adapter = adapter

        // Обработчик кнопки "назад"
        backButton.setOnClickListener {
            finish()
        }

        clearHistoryButton.setOnClickListener {
            showClearHistoryConfirmationDialog(adapter)
        }
    }

    private fun loadHistory() {
        val historySet = sharedPreferences.getStringSet("history", null)
        historySet?.let {
            historyList.clear()
            historyList.addAll(it.toList().reversed()) // Показываем последние записи первыми
        }
    }

    private fun saveHistory() {
        val editor = sharedPreferences.edit()
        editor.putStringSet("history", historyList.toSet())
        editor.apply()
    }

    private fun clearHistory(adapter: HistoryAdapter) {
        historyList.clear()
        val editor = sharedPreferences.edit()
        editor.remove("history")
        editor.apply()
        adapter.clear()
        adapter.notifyDataSetChanged()
    }

    private fun removeHistoryItem(position: Int) {
        if (position < historyList.size) {
            historyList.removeAt(position)
            saveHistory()
        }
    }

    private fun showClearHistoryConfirmationDialog(adapter: HistoryAdapter) {
        AlertDialog.Builder(this)
            .setTitle("Удалить все")
            .setMessage("Вы уверены, что хотите удалить всю историю?")
            .setPositiveButton("Да") { _, _ ->
                clearHistory(adapter)
            }
            .setNegativeButton("Нет", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(position: Int, adapter: HistoryAdapter) {
        AlertDialog.Builder(this)
            .setTitle("Удалить запись")
            .setMessage("Вы уверены, что хотите удалить эту запись?")
            .setPositiveButton("Да") { _, _ ->
                removeHistoryItem(position)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Нет", null)
            .show()
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Расчет", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    inner class HistoryAdapter(context: Context, private val items: List<String>) :
        ArrayAdapter<String>(context, R.layout.history_item, items) {

        private val dateFormat = SimpleDateFormat("d MMMM yyyyг. HH:mm", Locale.getDefault())

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.history_item, parent, false)

            val calculationText = view.findViewById<TextView>(R.id.calculationText)
            val dateText = view.findViewById<TextView>(R.id.dateText)
            val deleteButton = view.findViewById<ImageButton>(R.id.btnDelete)
            val copyButton = view.findViewById<ImageButton>(R.id.btnCopy) // Новая кнопка копирования

            val item = items[position]
            val parts = item.split("|")

            if (parts.size == 2) {
                calculationText.text = parts[0]
                try {
                    val timestamp = parts[1].toLong()
                    dateText.text = dateFormat.format(Date(timestamp))
                } catch (e: Exception) {
                    dateText.text = ""
                }
            } else {
                calculationText.text = item
                dateText.text = ""
            }

            // Обработка копирования при нажатии на кнопку копирования
            copyButton.setOnClickListener {
                copyToClipboard(calculationText.text.toString())
            }

            // Обработка копирования при долгом нажатии на элемент
            view.setOnLongClickListener {
                copyToClipboard(calculationText.text.toString())
                true
            }

            // Обработка удаления с подтверждением
            deleteButton.setOnClickListener {
                showDeleteConfirmationDialog(position, this)
            }

            return view
        }
    }
}