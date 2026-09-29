package com.jjrapps.bebeagua.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.jjrapps.bebeagua.domain.repository.WidgetUpdater
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Refreshes [DrinkWideWidget] only. The 1x1 [DrinkWidget] renders no data and is never invalidated.
 */
@Singleton
class GlanceWidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context
) : WidgetUpdater {

    override suspend fun refresh() {
        try {
            DrinkWideWidget.updateAll(context)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.w(e, "Widget refresh failed")
        }
    }
}
