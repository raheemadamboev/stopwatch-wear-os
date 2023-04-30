package xyz.teamgravity.stopwatch.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(StopwatchState.RESET)
    val state: StateFlow<StopwatchState> = _state.asStateFlow()

    private val _time = MutableStateFlow("00:00:00:000")
    val time: StateFlow<String> = _time.asStateFlow()

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime.asStateFlow()

    init {
        observe()
    }

    private fun observe() {
        observeElapsedTime()
        observeState()
    }

    private fun observeElapsedTime() {
        viewModelScope.launch {
            elapsedTime.collectLatest { milli ->
                _time.emit(TimeUtil.formatStopwatch(milli))
            }
        }
    }

    private fun observeState() {
        state.flatMapLatest { state ->
            TimeUtil.getStopwatch(state == StopwatchState.RUNNING)
        }.onEach { diff ->
            _elapsedTime.update { it + diff }
        }.launchIn(viewModelScope)
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    fun onRun() {
        _state.update { state ->
            when (state) {
                StopwatchState.RUNNING -> StopwatchState.PAUSED
                StopwatchState.PAUSED,
                StopwatchState.RESET,
                -> StopwatchState.RUNNING
            }
        }
    }

    fun onReset() {
        viewModelScope.launch {
            _state.emit(StopwatchState.RESET)
            _elapsedTime.emit(0L)
        }
    }
}