package com.jjrapps.bebeagua.domain.usecase

/** Fallback used when there is neither a last used amount nor a configured intake size. */
const val FALLBACK_INTAKE_SIZE_ML = 200

/**
 * Resolves the amount a one-tap log should record: the last amount the user logged, falling back to
 * the first configured size and finally to [FALLBACK_INTAKE_SIZE_ML].
 *
 * Pure so that every entry point (home screen button, home screen widget) agrees on the amount
 * without duplicating the precedence rules.
 */
fun resolveDefaultIntakeSize(lastUsedMl: Int?, configuredSizesMl: List<Int>): Int =
    lastUsedMl ?: configuredSizesMl.firstOrNull() ?: FALLBACK_INTAKE_SIZE_ML
