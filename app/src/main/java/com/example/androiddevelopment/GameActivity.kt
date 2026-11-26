package com.example.androiddevelopment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private var playerScore = 0
    private var phoneScore = 0
    private val options = listOf("Камень", "Ножницы", "Бумага")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val tvScore = findViewById<TextView>(R.id.tvScore)
        val tvChoices = findViewById<TextView>(R.id.tvChoices)
        val tvResult = findViewById<TextView>(R.id.tvGameResult)

        val btnRock = findViewById<Button>(R.id.btnRock)
        val btnScissors = findViewById<Button>(R.id.btnScissors)
        val btnPaper = findViewById<Button>(R.id.btnPaper)
        val btnBack = findViewById<Button>(R.id.btnGameBack)

        fun playRound(playerChoice: String) {
            val phoneChoice = options.random()
            tvChoices.text = "Вы: $playerChoice  |  Телефон: $phoneChoice"

            val playerWon = (playerChoice == "Камень" && phoneChoice == "Ножницы") ||
                    (playerChoice == "Ножницы" && phoneChoice == "Бумага") ||
                    (playerChoice == "Бумага" && phoneChoice == "Камень")

            when {
                playerChoice == phoneChoice -> {
                    tvResult.text = "Ничья!"
                }
                playerWon -> {
                    playerScore++
                    tvResult.text = "Вы победили!"
                }
                else -> {
                    phoneScore++
                    tvResult.text = "Вы проиграли! (вибрация)"
                    vibratePhone(450)
                    Log.d("GameActivity", "Проигрыш -> сработал вибросигнал")
                }
            }

            tvScore.text = "Счёт: Вы $playerScore : $phoneScore Телефон"
        }

        btnRock.setOnClickListener { playRound("Камень") }
        btnScissors.setOnClickListener { playRound("Ножницы") }
        btnPaper.setOnClickListener { playRound("Бумага") }
        btnBack.setOnClickListener { finish() }
    }

    private fun vibratePhone(durationMs: Long) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }
}