package com.example.simondice.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GameViewModel(
    private val dataStore: DataStore<Preferences>
): ViewModel() {

    private val _sequence: MutableLiveData<Int> = MutableLiveData()
    val sequence: LiveData<Int> = _sequence

    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message

    private val _score: MutableLiveData<Int> = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private var list: MutableList<Int> = mutableListOf()

    private var isSequencePlaying = false
    private var isUserPlaying = false
    private var currentTurn = 0

    val highestScore = dataStore.data.map { it[intPreferencesKey("high_score")] ?: 0 }.asLiveData()

    fun startGame() {
        if(!isSequencePlaying && !isUserPlaying) {
            nextSequence()
        }
    }

    fun nextSequence() {
        viewModelScope.launch {
            list.add((1..4).random())
            isSequencePlaying = true
            isSequencePlaying = false
            _message.value = "Espera..."
            delay(1200)
            list.forEach { number ->
                _sequence.value = number
                delay(600)
                _sequence.value = 0
                delay(300)
            }
            _message.value = "Tu turno!!!"
            isSequencePlaying = false
            isUserPlaying = true
        }
    }

    fun imageClicked(image: Int) {
        if(!isSequencePlaying && list.size > 0) {
            printImage(image)
            val isTurnCorrect = list[currentTurn] == image
            if(!isTurnCorrect) {
                _message.value = "JAJAJA perdiste!!"
                resetGame()
            } else {
                if(list.size - 1 == currentTurn) {
                    currentTurn = 0
                    _score.value = _score.value?.plus(1)
                    saveHighestScore()
                    nextSequence()
                } else {
                    currentTurn++
                }
            }
        }
    }

    fun printImage(image: Int) {
        viewModelScope.launch {
            _sequence.value = image
            delay(300)
            _sequence.value = 0
        }
    }

    fun resetGame() {
        currentTurn = 0
        list = mutableListOf()
        _score.value = 0
        isUserPlaying = false
    }

    fun saveHighestScore() {
        val highestScore = highestScore.value ?: 0
        if(_score.value!! > highestScore) {
            viewModelScope.launch {
                dataStore.edit { it[intPreferencesKey("high_score")] = score.value!! }
            }
        }
    }

    // define ViewModel factory in
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            dataStore: DataStore<Preferences>
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GameViewModel(dataStore) as T
            }
        }
    }
}