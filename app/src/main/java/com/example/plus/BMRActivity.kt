package com.example.plus

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class BMRActivity : AppCompatActivity() {

    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var etAge: EditText
    private lateinit var radioMale: RadioButton
    private lateinit var radioFemale: RadioButton
    private lateinit var spinnerActivity: Spinner
    private lateinit var btnCalculate: Button
    private lateinit var tvBMR: TextView
    private lateinit var tvMaintenance: TextView
    private lateinit var tvLoss: TextView
    private lateinit var tvGain: TextView

    // Коэффициенты активности
    private val activityCoefficients = mapOf(
        0 to 1.2,   // Минимальный
        1 to 1.375, // Низкий
        2 to 1.55,  // Умеренный
        3 to 1.7,   // Высокий
        4 to 1.9    // Экстремальный
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmr)

        // Инициализация элементов
        etHeight = findViewById(R.id.etHeight)
        etWeight = findViewById(R.id.etWeight)
        etAge = findViewById(R.id.etAge)
        radioMale = findViewById(R.id.radioMale)
        radioFemale = findViewById(R.id.radioFemale)
        spinnerActivity = findViewById(R.id.spinnerActivity)
        btnCalculate = findViewById(R.id.btnCalculate)
        tvBMR = findViewById(R.id.tvBMR)
        tvMaintenance = findViewById(R.id.tvMaintenance)
        tvLoss = findViewById(R.id.tvLoss)
        tvGain = findViewById(R.id.tvGain)

        // Устанавливаем мужской пол по умолчанию
        radioMale.isChecked = true

        // Настройка выпадающего списка активности
        ArrayAdapter.createFromResource(
            this,
            R.array.activity_levels,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerActivity.adapter = adapter
        }

        // Кнопка назад
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Обработка нажатия на кнопку расчета
        btnCalculate.setOnClickListener {
            calculateBMR()
        }
    }

    private fun calculateBMR() {
        // Получаем значения из полей ввода
        val heightStr = etHeight.text.toString()
        val weightStr = etWeight.text.toString()
        val ageStr = etAge.text.toString()

        if (heightStr.isEmpty() || weightStr.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val height = heightStr.toDouble()
        val weight = weightStr.toDouble()
        val age = ageStr.toInt()
        val activityLevel = spinnerActivity.selectedItemPosition
        val activityCoefficient = activityCoefficients[activityLevel] ?: 1.2

        // Рассчитываем базовый BMR
        val bmr: Double = if (radioMale.isChecked) {
            (10 * weight) + (6.25 * height) - (5 * age) + 5
        } else {
            (10 * weight) + (6.25 * height) - (5 * age) - 161
        }

        // Рассчитываем суточную норму калорий
        val maintenanceCalories = bmr * activityCoefficient

        // Рассчитываем калории для разных целей
        val lossCalories = maintenanceCalories * 0.85  // -15%
        val gainCalories = maintenanceCalories * 1.15  // +15%

        // Отображаем результаты
        tvBMR.text = "BMR: ${bmr.roundToInt()} ккал"
        tvMaintenance.text = "Поддержание: ${maintenanceCalories.roundToInt()} ккал"
        tvLoss.text = "Снижение: ${lossCalories.roundToInt()} ккал"
        tvGain.text = "Набор: ${gainCalories.roundToInt()} ккал"
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}