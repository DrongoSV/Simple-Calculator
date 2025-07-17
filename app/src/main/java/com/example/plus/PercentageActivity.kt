package com.example.plus

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class PercentageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_percentage)

        // Кнопка назад
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Инициализация элементов
        val etPercent1 = findViewById<EditText>(R.id.etPercent1)
        val etNumber1 = findViewById<EditText>(R.id.etNumber1)
        val btnCalculate1 = findViewById<Button>(R.id.btnCalculate1)
        val tvResult1 = findViewById<TextView>(R.id.tvResult1)

        val etPart = findViewById<EditText>(R.id.etPart)
        val etWhole = findViewById<EditText>(R.id.etWhole)
        val btnCalculate2 = findViewById<Button>(R.id.btnCalculate2)
        val tvResult2 = findViewById<TextView>(R.id.tvResult2)

        val etPercentAdd = findViewById<EditText>(R.id.etPercentAdd)
        val etNumberAdd = findViewById<EditText>(R.id.etNumberAdd)
        val btnCalculate3 = findViewById<Button>(R.id.btnCalculate3)
        val tvResult3 = findViewById<TextView>(R.id.tvResult3)

        val etPercentSubtract = findViewById<EditText>(R.id.etPercentSubtract)
        val etNumberSubtract = findViewById<EditText>(R.id.etNumberSubtract)
        val btnCalculate4 = findViewById<Button>(R.id.btnCalculate4)
        val tvResult4 = findViewById<TextView>(R.id.tvResult4)

        // Форматирование чисел
        val df = DecimalFormat("#.##")

        // Операция 1: Сколько составляет X% от числа Y
        btnCalculate1.setOnClickListener {
            if (etPercent1.text.toString().isEmpty() || etNumber1.text.toString().isEmpty()) {
                Toast.makeText(this, "Заполните оба поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val percent = etPercent1.text.toString().toDouble()
            val number = etNumber1.text.toString().toDouble()
            val result = (percent / 100) * number

            tvResult1.text = "${df.format(percent)}% от ${df.format(number)} = ${df.format(result)}"
        }

        // Операция 2: Сколько % составляет число X от числа Y
        btnCalculate2.setOnClickListener {
            if (etPart.text.toString().isEmpty() || etWhole.text.toString().isEmpty()) {
                Toast.makeText(this, "Заполните оба поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val part = etPart.text.toString().toDouble()
            val whole = etWhole.text.toString().toDouble()

            if (whole == 0.0) {
                Toast.makeText(this, "Второе число не может быть нулем", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = (part / whole) * 100
            tvResult2.text = "${df.format(part)} от ${df.format(whole)} = ${df.format(result)}%"
        }

        // Операция 3: Прибавить X% к числу Y
        btnCalculate3.setOnClickListener {
            if (etPercentAdd.text.toString().isEmpty() || etNumberAdd.text.toString().isEmpty()) {
                Toast.makeText(this, "Заполните оба поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val percent = etPercentAdd.text.toString().toDouble()
            val number = etNumberAdd.text.toString().toDouble()
            val result = number + (number * (percent / 100))

            tvResult3.text = "${df.format(percent)}% от ${df.format(number)} + ${df.format(number)} = ${df.format(result)}"
        }

        // Операция 4: Вычесть X% из числа Y
        btnCalculate4.setOnClickListener {
            if (etPercentSubtract.text.toString().isEmpty() || etNumberSubtract.text.toString().isEmpty()) {
                Toast.makeText(this, "Заполните оба поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val percent = etPercentSubtract.text.toString().toDouble()
            val number = etNumberSubtract.text.toString().toDouble()
            val result = number - (number * (percent / 100))

            tvResult4.text = "${df.format(number)} - ${df.format(percent)}% = ${df.format(result)}"
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}