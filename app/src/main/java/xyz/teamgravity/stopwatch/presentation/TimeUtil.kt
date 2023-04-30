package xyz.teamgravity.stopwatch.presentation

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtil {

    private val stopwatchFormatter: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern("HH:mm:ss:SSS", Locale.getDefault()) }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    fun formatStopwatch(milli: Long): String {
        return stopwatchFormatter.format(LocalTime.ofNanoOfDay(milli * 1_000_000))
    }

    fun getStopwatch(running: Boolean): Flow<Long> {
        return flow {
            var start = System.currentTimeMillis()
            while (running) {
                val current = System.currentTimeMillis()
                val diff = if (current > start) current - start else 0
                emit(diff)
                start = System.currentTimeMillis()
                delay(10L)
            }
        }
    }
}