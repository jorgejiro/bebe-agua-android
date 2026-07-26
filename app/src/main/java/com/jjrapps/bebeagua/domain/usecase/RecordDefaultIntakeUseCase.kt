package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

/** Outcome of a one-tap log, with the day totals needed to confirm it to the user. */
data class RecordedIntake(
    val amountMl: Int,
    val consumedMl: Int,
    val goalMl: Int
)

/**
 * Logs the default intake amount and reschedules the next reminder, mirroring what the home screen
 * button does. Used by the home screen widget, which has no ViewModel of its own.
 */
class RecordDefaultIntakeUseCase @Inject constructor(
    private val getDefaultIntakeSize: GetDefaultIntakeSizeUseCase,
    private val addIntake: AddIntakeUseCase,
    private val scheduleReminders: ScheduleRemindersUseCase,
    private val intakeRepository: IntakeRepository,
    private val settingsRepository: SettingsRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(): RecordedIntake {
        val amountMl = getDefaultIntakeSize()
        addIntake(amountMl)
        scheduleReminders()

        val today = LocalDate.now(clock)
        return RecordedIntake(
            amountMl = amountMl,
            consumedMl = intakeRepository.observeTotalForDate(today).first(),
            goalMl = settingsRepository.observeSettings().first().dailyGoalMl
        )
    }
}
