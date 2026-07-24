package com.jjrapps.bebeagua.ui.changelog

import androidx.annotation.ArrayRes
import com.jjrapps.bebeagua.R
import java.time.LocalDate

/**
 * A released version of the app. The highlights live in a localized string array so the
 * changelog is translated like any other UI text; the screen resolves [highlightsRes].
 */
data class ChangelogRelease(
    val versionName: String,
    val versionCode: Int,
    val releaseDate: LocalDate,
    @param:ArrayRes val highlightsRes: Int
)

/**
 * Static catalog of releases, newest first.
 *
 * When bumping the version in `build.gradle.kts`, add an entry here, the matching
 * `string-array` in `values/strings.xml` and `values-es/strings.xml`, and a section in
 * `CHANGELOG.md`.
 */
object ChangelogCatalog {

    val releases: List<ChangelogRelease> = listOf(
        ChangelogRelease(
            versionName = "1.1.0",
            versionCode = 6,
            releaseDate = LocalDate.of(2026, 7, 24),
            highlightsRes = R.array.changelog_1_1_0
        ),
        ChangelogRelease(
            versionName = "1.0",
            versionCode = 5,
            releaseDate = LocalDate.of(2026, 5, 26),
            highlightsRes = R.array.changelog_1_0
        ),
        ChangelogRelease(
            versionName = "1.0.3",
            versionCode = 4,
            releaseDate = LocalDate.of(2026, 5, 9),
            highlightsRes = R.array.changelog_1_0_3
        ),
        ChangelogRelease(
            versionName = "1.0.2",
            versionCode = 3,
            releaseDate = LocalDate.of(2026, 5, 9),
            highlightsRes = R.array.changelog_1_0_2
        ),
        ChangelogRelease(
            versionName = "1.0.1",
            versionCode = 2,
            releaseDate = LocalDate.of(2026, 5, 6),
            highlightsRes = R.array.changelog_1_0_1
        )
    )
}
