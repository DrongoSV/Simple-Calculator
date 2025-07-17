package com.example.plus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ImageButton

class ConverterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_converter)

        // Кнопка назад
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Переход к BMR калькулятору
        findViewById<LinearLayout>(R.id.bmrContainer).setOnClickListener {
            startActivity(Intent(this, BMRActivity::class.java))
        }
        // В методе onCreate ConverterActivity.kt:
        findViewById<LinearLayout>(R.id.percentageContainer).setOnClickListener {
            startActivity(Intent(this, PercentageActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.worldTimeContainer).setOnClickListener {
            startActivity(Intent(this, WorldTimeActivity::class.java))
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}