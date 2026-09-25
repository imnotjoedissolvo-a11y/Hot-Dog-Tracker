package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.HotDogRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HotDogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HotDogRepository = run {
        val db = AppDatabase.getDatabase(application)
        HotDogRepository(db.hotDogDao(), application)
    }

    val count: StateFlow<Int> = repository.countFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    private val _confettiTrigger = MutableStateFlow(0)
    val confettiTrigger: StateFlow<Int> = _confettiTrigger

    private val _milestoneEvent = MutableSharedFlow<Int>()
    val milestoneEvent: SharedFlow<Int> = _milestoneEvent.asSharedFlow()

    init {
        // Sync widget on launch with stored count
        viewModelScope.launch {
            val initial = repository.getTodayCount()
            repository.syncWidgetWithCurrentCount(initial)
        }
    }

    fun increment() {
        viewModelScope.launch {
            val newCount = repository.increment()
            if (newCount > 0 && newCount % 50 == 0) {
                _confettiTrigger.value = newCount
                _milestoneEvent.emit(newCount)
            }
        }
    }

    fun reset() {
        viewModelScope.launch {
            repository.reset()
        }
    }
}
