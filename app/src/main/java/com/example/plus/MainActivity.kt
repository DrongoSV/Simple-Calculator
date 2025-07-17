package com.example.plus

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var calculationEditText: EditText
    private lateinit var resultTextView: TextView
    private var lastNumeric = false
    private var stateError = false
    private var lastDot = false
    private var memoryValue = 0.0
    private var isUpdatingFromWatcher = false
    private lateinit var sharedPreferences: SharedPreferences
    private val historyList = mutableListOf<String>()
    private var lastCursorPosition = 0

    // Добавим константу для разделителя
    private val THOUSAND_SEPARATOR = ' '

    // Список операторов для умной вставки
    private val operators = setOf("+", "-", "*", "/", "^")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calculationEditText = findViewById(R.id.calculationEditText)
        resultTextView = findViewById(R.id.resultTextView)
        sharedPreferences = getSharedPreferences("CalculatorHistory", Context.MODE_PRIVATE)
        loadHistory()

        // Установим начальное положение курсора
        calculationEditText.setSelection(0)

        // TextWatcher для динамического расчета
        calculationEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isUpdatingFromWatcher) {
                    calculateDynamic()
                }
            }
        })

        // Кнопки управления курсором
        findViewById<Button>(R.id.btnMoveLeft).setOnClickListener { moveCursor(-1) }
        findViewById<Button>(R.id.btnMoveRight).setOnClickListener { moveCursor(1) }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { deleteAtCursor() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clearAll() }

        // Цифровые кнопки
        findViewById<Button>(R.id.btn0).setOnClickListener { insertAtCursor("0") }
        findViewById<Button>(R.id.btn00).setOnClickListener { insertAtCursor("00") }
        findViewById<Button>(R.id.btn1).setOnClickListener { insertAtCursor("1") }
        findViewById<Button>(R.id.btn2).setOnClickListener { insertAtCursor("2") }
        findViewById<Button>(R.id.btn3).setOnClickListener { insertAtCursor("3") }
        findViewById<Button>(R.id.btn4).setOnClickListener { insertAtCursor("4") }
        findViewById<Button>(R.id.btn5).setOnClickListener { insertAtCursor("5") }
        findViewById<Button>(R.id.btn6).setOnClickListener { insertAtCursor("6") }
        findViewById<Button>(R.id.btn7).setOnClickListener { insertAtCursor("7") }
        findViewById<Button>(R.id.btn8).setOnClickListener { insertAtCursor("8") }
        findViewById<Button>(R.id.btn9).setOnClickListener { insertAtCursor("9") }
        findViewById<Button>(R.id.btnDecimal).setOnClickListener { insertDecimal() }

        // Операторы
        findViewById<Button>(R.id.btnAdd).setOnClickListener { smartInsertOperator("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { smartInsertOperator("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { smartInsertOperator("*") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { smartInsertOperator("/") }
        findViewById<Button>(R.id.btnExponent).setOnClickListener { smartInsertOperator("^") }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { calculatePercentage() }
        findViewById<Button>(R.id.btnBracket).setOnClickListener { toggleBrackets() }

        // Специальные функции
        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculateResult() }

        // Функции памяти
        findViewById<Button>(R.id.btnMC).setOnClickListener { clearMemory() }
        findViewById<Button>(R.id.btnMPlus).setOnClickListener { addToMemory() }
        findViewById<Button>(R.id.btnMMinus).setOnClickListener { subtractFromMemory() }
        findViewById<Button>(R.id.btnMR).setOnClickListener { recallMemory() }

        // Навигационные кнопки
        findViewById<ImageButton>(R.id.btnHistory).setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnConverter).setOnClickListener {
            val intent = Intent(this, ConverterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun calculateDynamic() {
        if (stateError) return

        val expression = calculationEditText.text.toString().replace(THOUSAND_SEPARATOR.toString(), "")
        if (expression.isNotEmpty()) {
            try {
                // Проверяем, заканчивается ли выражение на оператор
                val endsWithOperator = expression.takeLast(1) in operators

                // Вычисляем только если выражение завершено
                if (!endsWithOperator) {
                    val result = ExpressionBuilder(expression).build().evaluate()
                    resultTextView.text = formatResult(result)
                } else {
                    resultTextView.text = ""
                }
            } catch (ex: Exception) {
                // Не показываем ошибку для частичных выражений
                resultTextView.text = ""
            }
        } else {
            resultTextView.text = "0"
        }
    }

    private fun formatResult(value: Double): String {
        return if (value % 1 == 0.0)
            value.toLong().toString()
        else
            value.toString()
    }

    private fun insertDecimal() {
        if (lastNumeric && !stateError && !lastDot) {
            insertAtCursor(".")
        }
    }

    private fun insertAtCursor(value: String) {
        val currentText = calculationEditText.text.toString().replace(THOUSAND_SEPARATOR.toString(), "")
        val selectionStart = calculationEditText.selectionStart
        val selectionEnd = calculationEditText.selectionEnd

        // Блокируем TextWatcher
        isUpdatingFromWatcher = true



        if (stateError) {
            calculationEditText.setText(value)
            stateError = false
            lastCursorPosition = value.length
        } else {
            // Удаляем выделенный текст (если есть)
            val textWithoutSelection = if (selectionStart != selectionEnd) {
                StringBuilder(currentText)
                    .delete(selectionStart, selectionEnd)
                    .toString()
            } else {
                currentText
            }

            // Вставляем новое значение
            val newText = StringBuilder(textWithoutSelection)
                .insert(selectionStart, value)
                .toString()

            calculationEditText.setText(newText)
            formatNumbersWithSpaces() // Форматируем числа
            lastCursorPosition = selectionStart + value.length
        }

        // Явно устанавливаем курсор
        calculationEditText.setSelection(lastCursorPosition)

        // Обновляем флаги состояния
        updateFlagsFromText()

        isUpdatingFromWatcher = false // Разблокируем TextWatcher
    }

    private fun smartInsertOperator(operator: String) {
        val currentText = calculationEditText.text.toString()
        val selectionStart = calculationEditText.selectionStart
        val selectionEnd = calculationEditText.selectionEnd

        isUpdatingFromWatcher = true // Блокируем TextWatcher

        // Если есть выделение - заменяем выделенный текст
        if (selectionStart != selectionEnd) {
            insertAtCursor(operator)
            isUpdatingFromWatcher = false
            return
        }

        // Замена предыдущего оператора
        if (selectionStart > 0 && currentText[selectionStart - 1].toString() in operators) {
            val newText = StringBuilder(currentText)
                .replace(selectionStart - 1, selectionStart, operator)
                .toString()

            calculationEditText.setText(newText)
            lastCursorPosition = selectionStart
            calculationEditText.setSelection(lastCursorPosition)
        } else {
            insertAtCursor(operator)
        }

        updateFlagsFromText()
        isUpdatingFromWatcher = false // Разблокируем TextWatcher
    }

    // Добавим новую функцию форматирования
    private fun formatNumbersWithSpaces() {
        val text = calculationEditText.text.toString()
        if (text.isEmpty()) return

        // Сохраняем позицию курсора
        val cursorPos = calculationEditText.selectionStart

        // Форматируем текст
        val formattedText = formatExpression(text)

        // Устанавливаем отформатированный текст
        isUpdatingFromWatcher = true
        calculationEditText.setText(formattedText)

        // Восстанавливаем позицию курсора
        calculationEditText.setSelection(cursorPos + (formattedText.length - text.length))
        isUpdatingFromWatcher = false
    }

    // Функция для форматирования выражения
    private fun formatExpression(expression: String): String {
        return try {
            // Разбиваем выражение на части
            val parts = expression.split("(?<=[-+*/^()])|(?=[-+*/^()])".toRegex())

            parts.joinToString("") { part ->
                if (part.matches("\\d+".toRegex())) {
                    formatNumber(part)
                } else {
                    part
                }
            }
        } catch (e: Exception) {
            expression // В случае ошибки возвращаем оригинал
        }
    }

    // Функция для форматирования числа
    private fun formatNumber(number: String): String {
        // Проверяем, содержит ли число десятичную часть
        if (number.contains('.')) {
            val parts = number.split('.')
            return "${formatNumber(parts[0])}.${parts[1]}"
        }
        val cleanNumber = number.replace(THOUSAND_SEPARATOR.toString(), "")
        if (cleanNumber.length <= 3) return cleanNumber

        return buildString {
            var count = 0
            for (i in cleanNumber.length - 1 downTo 0) {
                append(cleanNumber[i])
                count++
                if (count % 3 == 0 && i > 0) {
                    append(THOUSAND_SEPARATOR)
                }
            }
        }.reversed()
    }

    private fun deleteAtCursor() {
        val currentText = calculationEditText.text.toString()
        val selectionStart = calculationEditText.selectionStart
        val selectionEnd = calculationEditText.selectionEnd

        isUpdatingFromWatcher = true // Блокируем TextWatcher

        if (selectionStart > 0 && selectionStart == selectionEnd) {
            val newText = StringBuilder(currentText)
                .deleteCharAt(selectionStart - 1)
                .toString()

            calculationEditText.setText(newText)
            lastCursorPosition = selectionStart - 1
            calculationEditText.setSelection(lastCursorPosition)
            updateFlagsFromText()
        } else if (selectionStart != selectionEnd) {
            // Удаление выделенного текста
            val newText = StringBuilder(currentText)
                .delete(selectionStart, selectionEnd)
                .toString()

            calculationEditText.setText(newText)
            lastCursorPosition = selectionStart
            calculationEditText.setSelection(lastCursorPosition)
            updateFlagsFromText()
        }

        isUpdatingFromWatcher = false // Разблокируем TextWatcher
    }

    private fun moveCursor(steps: Int) {
        val currentPosition = calculationEditText.selectionStart
        val newPosition = (currentPosition + steps)
            .coerceIn(0, calculationEditText.text.length)

        calculationEditText.setSelection(newPosition)
        lastCursorPosition = newPosition
    }

    private fun updateFlagsFromText() {
        val text = calculationEditText.text.toString()
        if (text.isNotEmpty()) {
            lastNumeric = text.last().isDigit()
            lastDot = text.endsWith(".")
        } else {
            lastNumeric = false
            lastDot = false
        }
    }

    private fun clearAll() {
        isUpdatingFromWatcher = true // Блокируем TextWatcher
        calculationEditText.setText("")
        resultTextView.text = "0"
        lastNumeric = false
        stateError = false
        lastDot = false
        lastCursorPosition = 0
        calculationEditText.setSelection(0)
        isUpdatingFromWatcher = false // Разблокируем TextWatcher
    }

    private fun calculateResult() {
        if (lastNumeric && !stateError) {
            val expression = calculationEditText.text.toString().replace(THOUSAND_SEPARATOR.toString(), "")
            try {
                val result = ExpressionBuilder(expression).build().evaluate()
                val resultText = formatResult(result)
                resultTextView.text = resultText

                // Сохраняем в историю
                saveToHistory("$expression = $resultText")

                // Обновляем поле ввода результатом
                isUpdatingFromWatcher = true
                calculationEditText.setText(resultText)
                formatNumbersWithSpaces() // Форматируем результат
                isUpdatingFromWatcher = false

                lastNumeric = true
                lastDot = resultText.contains('.')
                lastCursorPosition = resultText.length
                calculationEditText.setSelection(lastCursorPosition)
            } catch (ex: Exception) {
                resultTextView.text = "Error"
                stateError = true
                lastNumeric = false
            }
        }
    }

    private fun calculatePercentage() {
        if (lastNumeric && !stateError) {
            try {
                isUpdatingFromWatcher = true
                insertAtCursor("/100")
                isUpdatingFromWatcher = false
                calculateResult()
            } catch (ex: Exception) {
                resultTextView.text = "Error"
                stateError = true
            }
        }
    }

    private fun toggleBrackets() {
        val currentText = calculationEditText.text.toString()
        isUpdatingFromWatcher = true

        if (stateError) {
            calculationEditText.setText("(")
            stateError = false
            lastCursorPosition = 1
            calculationEditText.setSelection(1)
        } else {
            when {
                currentText.isEmpty() -> insertAtCursor("(")
                currentText.count { it == '(' } > currentText.count { it == ')' } ->
                    insertAtCursor(")")
                else -> insertAtCursor("(")
            }
        }

        isUpdatingFromWatcher = false
    }

    // Функции памяти
    private fun clearMemory() {
        memoryValue = 0.0
    }

    private fun addToMemory() {
        try {
            val currentValue = resultTextView.text.toString().toDouble()
            memoryValue += currentValue
        } catch (e: NumberFormatException) {
            // Обработка неверного числа
        }
    }

    private fun subtractFromMemory() {
        try {
            val currentValue = resultTextView.text.toString().toDouble()
            memoryValue -= currentValue
        } catch (e: NumberFormatException) {
            // Обработка неверного числа
        }
    }

    private fun recallMemory() {
        isUpdatingFromWatcher = true
        insertAtCursor(memoryValue.toString())
        isUpdatingFromWatcher = false
    }

    // Функции истории
    private fun saveToHistory(record: String) {
        val currentTime = System.currentTimeMillis()
        historyList.add("$record|$currentTime")
        while (historyList.size > 100) {
            historyList.removeAt(0)
        }
        saveHistory()
    }

    private fun saveHistory() {
        val editor = sharedPreferences.edit()
        editor.putStringSet("history", historyList.toSet())
        editor.apply()
    }

    private fun loadHistory() {
        val historySet = sharedPreferences.getStringSet("history", null)
        historySet?.let {
            historyList.clear()
            historyList.addAll(it)
        }
    }
}