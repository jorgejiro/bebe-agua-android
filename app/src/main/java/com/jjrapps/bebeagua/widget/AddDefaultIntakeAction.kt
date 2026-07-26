package com.jjrapps.bebeagua.widget

import android.content.Context
import android.widget.Toast
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.jjrapps.bebeagua.R
import com.jjrapps.bebeagua.domain.usecase.RecordDefaultIntakeUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Handles a tap on [DrinkWidget]: logs the default intake amount and confirms it with a toast.
 *
 * Glance instantiates action callbacks itself, so dependencies come from the Hilt singleton
 * component instead of constructor injection.
 */
class AddDefaultIntakeAction : ActionCallback {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Dependencies {
        fun recordDefaultIntakeUseCase(): RecordDefaultIntakeUseCase
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val appContext = context.applicationContext
        val recordDefaultIntake = EntryPointAccessors
            .fromApplication(appContext, Dependencies::class.java)
            .recordDefaultIntakeUseCase()

        runCatching { recordDefaultIntake() }
            .onSuccess { recorded ->
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        appContext,
                        appContext.getString(
                            R.string.widget_toast_intake_added,
                            recorded.amountMl,
                            recorded.consumedMl,
                            recorded.goalMl
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .onFailure { Timber.e(it, "Widget intake failed") }
    }
}
