package com.example.simondice.ui

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.simondice.R
import com.google.android.material.imageview.ShapeableImageView

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "simon_says")

class MainActivity : AppCompatActivity() {

    private lateinit var maxScore: TextView
    private lateinit var score: TextView
    private lateinit var textMessage: TextView
    private lateinit var button: Button

    private lateinit var goku: ShapeableImageView
    private lateinit var vegeta: ShapeableImageView
    private lateinit var jiren: ShapeableImageView
    private lateinit var freezer: ShapeableImageView

    private val viewModel: GameViewModel by viewModels {
        GameViewModel.provideFactory(dataStore = dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        init()
        setUpListeners()
        setUpObservers()
    }

    fun init() {
        button = findViewById(R.id.start_button)
        goku = findViewById(R.id.goku)
        vegeta = findViewById(R.id.vegeta)
        jiren = findViewById(R.id.jiren)
        freezer = findViewById(R.id.freezer)
        maxScore = findViewById(R.id.text_max_score)
        score = findViewById(R.id.text_score)
        textMessage = findViewById(R.id.text_message)
    }


    private fun setUpListeners() {
        button.setOnClickListener {
            viewModel.startGame()
        }
        goku.setOnClickListener {
            viewModel.imageClicked(1)
        }
        vegeta.setOnClickListener {
            viewModel.imageClicked(2)
        }
        jiren.setOnClickListener {
            viewModel.imageClicked(3)
        }
        freezer.setOnClickListener {
            viewModel.imageClicked(4)
        }
    }

    private fun setUpObservers() {
        viewModel.sequence.observe(this) { number ->
            when(number){
                1 -> {
                    goku.alpha = 0.3f
                    vegeta.alpha = 1f
                    jiren.alpha = 1f
                    freezer.alpha = 1f
                }
                2 -> {
                    vegeta.alpha = 0.3f
                    goku.alpha = 1f
                    jiren.alpha = 1f
                    freezer.alpha = 1f
                }
                3 -> {
                    jiren.alpha = 0.3f
                    goku.alpha = 1f
                    vegeta.alpha = 1f
                    freezer.alpha = 1f
                }
                4 -> {
                    freezer.alpha = 0.3f
                    goku.alpha = 1f
                    vegeta.alpha = 1f
                    jiren.alpha = 1f
                }
                0 -> {
                    freezer.alpha = 1f
                    goku.alpha = 1f
                    vegeta.alpha = 1f
                    jiren.alpha = 1f
                }
            }
        }

        viewModel.message.observe(this) { str ->
            textMessage.text = str
        }
        viewModel.score.observe(this){ str ->
            score.text = "Puntos: $str"
        }
        viewModel.highestScore.observe(this){ str ->
            maxScore.text = "Maxima Puntuación: $str"
        }
    }
}