package com.jjrapps.bebeagua.reminder

import com.jjrapps.bebeagua.domain.repository.DailySummaryScheduler
import javax.inject.Inject

class NoOpDailySummaryScheduler @Inject constructor() : DailySummaryScheduler {
    override fun schedule(triggerAtMs: Long) = Unit
    override fun cancel() = Unit
}
