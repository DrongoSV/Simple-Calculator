package com.example.plus

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class WorldTimeActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val pinnedCities = listOf(
        City("Москва", -4),
        City("Новосибирск", 0),
        City("Токио", 2),
        City("Лондон", -6),
        City("Нью-Йорк", -11)
    )

    private val allCities = listOf(
        City("Лайн (Кирибати)", 14),
        City("Окленд (Новая Зеландия, зимой)", 13),
        City("Апиа (Самоа)", 13),
        City("Нукуалофа (Тонга)", 13),
        City("Магадан (Россия)", 12),
        City("Петропавловск-Камчатский (Россия)", 12),
        City("Сува (Фиджи)", 12),
        City("Веллингтон (Новая Зеландия, зимой)", 12),
        City("Сахалин (Россия)", 11),
        City("Владивосток (Россия)", 11),
        City("Сидней (Австралия, зимой)", 11),
        City("Хониара (Соломоновы острова)", 11),
        City("Хабаровск (Россия)", 10),
        City("Благовещенск (Россия)", 10),
        City("Якутск (часть, Россия)", 10),
        City("Мельбурн (Австралия, зимой)", 10),
        City("Порт-Морсби (Папуа-Новая Гвинея)", 10),
        City("Якутск (основная часть, Россия)", 9),
        City("Токио (Япония)", 9),
        City("Сеул (Южная Корея)", 9),
        City("Пхеньян (КНДР)", 9),
        City("Иркутск (Россия)", 8),
        City("Улан-Батор (Монголия)", 8),
        City("Пекин (Китай)", 8),
        City("Сингапур", 8),
        City("Перт (Австралия)", 8),
        City("Куала-Лумпур (Малайзия)", 8),
        City("Новосибирск (Россия)", 7),
        City("Красноярск (Россия)", 7),
        City("Томск (Россия)", 7),
        City("Бангкок (Таиланд)", 7),
        City("Джакарта (Индонезия)", 7),
        City("Ханой (Вьетнам)", 7),
        City("Омск (Россия)", 6),
        City("Барнаул (Россия)", 6),
        City("Астана (Казахстан)", 6),
        City("Дакка (Бангладеш)", 6),
        City("Алматы (Казахстан)", 6),
        City("Бишкек (Киргизия)", 6),
        City("Екатеринбург (Россия)", 5),
        City("Тюмень (Россия)", 5),
        City("Исламабад (Пакистан)", 5),
        City("Ташкент (Узбекистан)", 5),
        City("Мале (Мальдивы)", 5),
        City("Самара (Россия)", 4),
        City("Ульяновск (Россия)", 4),
        City("Баку (Азербайджан)", 4),
        City("Тбилиси (Грузия)", 4),
        City("Дубай (ОАЭ)", 4),
        City("Маскат (Оман)", 4),
        City("Порт-Луи (Маврикий)", 4),
        City("Москва (Россия)", 3),
        City("Санкт-Петербург (Россия)", 3),
        City("Стамбул (Турция)", 3),
        City("Киев (Украина)", 3),
        City("Минск (Беларусь)", 3),
        City("Найроби (Кения)", 3),
        City("Багдад (Ирак)", 3),
        City("Эр-Рияд (Саудовская Аравия)", 3),
        City("Калининград (Россия)", 2),
        City("Киев (Украина, зимой)", 2),
        City("София (Болгария)", 2),
        City("Хартум (Судан)", 2),
        City("Каир (Египет, зимой)", 2),
        City("Кейптаун (ЮАР)", 2),
        City("Хельсинки (Финляндия, зимой)", 2),
        City("Берлин (Германия, зимой)", 1),
        City("Париж (Франция, зимой)", 1),
        City("Мадрид (Испания, зимой)", 1),
        City("Рим (Италия, зимой)", 1),
        City("Стокгольм (Швеция, зимой)", 1),
        City("Алжир (Алжир)", 1),
        City("Лагос (Нигерия)", 1),
        City("Лондон (Великобритания, зимой)", 0),
        City("Лиссабон (Португалия, зимой)", 0),
        City("Дублин (Ирландия, зимой)", 0),
        City("Касабланка (Марокко)", 0),
        City("Рейкьявик (Исландия)", 0),
        City("Аккра (Гана)", 0),
        City("Азорские острова (Португалия)", -1),
        City("Кабо-Верде", -1),
        City("Фернанду-ди-Норонья (Бразилия)", -2),
        City("Южная Георгия и Южные Сандвичевы острова", -2),
        City("Бразилиа (Бразилия)", -3),
        City("Буэнос-Айрес (Аргентина)", -3),
        City("Монтевидео (Уругвай)", -3),
        City("Сантьяго (Чили, зимой)", -3),
        City("Гренландия (большая часть)", -3),
        City("Каракас (Венесуэла)", -4),
        City("Ла-Пас (Боливия)", -4),
        City("Сантьяго (Чили, летом)", -4),
        City("Манаус (Бразилия)", -4),
        City("Оттава (Канада, летом - EDT)", -4),
        City("Нью-Йорк (США, летом - EDT)", -4),
        City("Богота (Колумбия)", -5),
        City("Лима (Перу)", -5),
        City("Нью-Йорк (США, зимой - EST)", -5),
        City("Оттава (Канада, зимой - EST)", -5),
        City("Вашингтон (США, зимой - EST)", -5),
        City("Мехико (Мексика, большая часть)", -6),
        City("Чикаго (США, зимой - CST)", -6),
        City("Коста-Рика", -6),
        City("Гватемала", -6),
        City("Денвер (США, зимой - MST)", -7),
        City("Финикс (США - MST, без летнего времени)", -7),
        City("Калгари (Канада, зимой - MST)", -7),
        City("Лос-Анджелес (США, зимой - PST)", -8),
        City("Сиэтл (США, зимой - PST)", -8),
        City("Ванкувер (Канада, зимой - PST)", -8),
        City("Анкоридж (США, зимой - AKST)", -9),
        City("Гонолулу (США - HST, без летнего времени)", -10),
        City("Папеэте (Французская Полинезия)", -10),
        City("Паго-Паго (Американское Самоа)", -11),
        City("Ниуэ", -11),
        City("Острова Бейкер и Хауленд (США)", -12)
    ).sortedByDescending { it.offset }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_time)

        // Кнопка назад
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Инициализация
        val pinnedContainer = findViewById<LinearLayout>(R.id.pinnedCitiesContainer)
        val allContainer = findViewById<LinearLayout>(R.id.allCitiesContainer)
        val btnShowAll = findViewById<Button>(R.id.btnShowAll)
        val allCitiesScroll = findViewById<ScrollView>(R.id.allCitiesScroll)

        // Добавление закрепленных городов
        pinnedCities.forEach { city ->
            pinnedContainer.addView(createCityView(city))
        }

        // Добавление всех городов
        allCities.forEach { city ->
            allContainer.addView(createCityView(city))
        }

        // Обработчик кнопки показа всех городов
        btnShowAll.setOnClickListener {
            if (allCitiesScroll.visibility == View.VISIBLE) {
                allCitiesScroll.visibility = View.GONE
                btnShowAll.text = "Показать все часовые пояса"
            } else {
                allCitiesScroll.visibility = View.VISIBLE
                btnShowAll.text = "Скрыть часовые пояса"
            }
        }

        // Запуск обновления времени
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                updateAllTimes()
                handler.postDelayed(this, TimeUnit.SECONDS.toMillis(30))
            }
        }
        handler.post(runnable)
    }

    private fun createCityView(city: City): View {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.item_city_time, null)

        val tvCityName = view.findViewById<TextView>(R.id.tvCityName)
        val tvTimeZone = view.findViewById<TextView>(R.id.tvTimeZone)
        val tvTime = view.findViewById<TextView>(R.id.tvTime)

        tvCityName.text = city.name
        tvTimeZone.text = formatOffset(city.offset)
        tvTime.text = calculateTime(city.offset)

        return view
    }

    private fun updateAllTimes() {
        updateTimesInContainer(R.id.pinnedCitiesContainer)
        updateTimesInContainer(R.id.allCitiesContainer)
    }

    private fun updateTimesInContainer(containerId: Int) {
        val container = findViewById<LinearLayout>(containerId)
        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            val tvTime = view.findViewById<TextView>(R.id.tvTime)
            val tvTimeZone = view.findViewById<TextView>(R.id.tvTimeZone)

            val cityName = view.findViewById<TextView>(R.id.tvCityName).text.toString()
            val city = (pinnedCities + allCities).firstOrNull { it.name == cityName }

            city?.let {
                tvTime.text = calculateTime(it.offset)
                tvTimeZone.text = formatOffset(it.offset)
            }
        }
    }

    private fun calculateTime(offset: Int): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.add(Calendar.MILLISECOND, (offset * 3600000).toInt())

        return dateFormat.format(calendar.time)
    }

    private fun formatOffset(offset: Int): String {
        val hours = offset.toInt()
        val minutes = ((offset - hours) * 60).toInt()

        return when {
            offset > 0 -> "UTC+%d:%02d".format(hours, minutes)
            offset < 0 -> "UTC%d:%02d".format(hours, minutes)
            else -> "UTC±0"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    data class City(val name: String, val offset: Int)
}