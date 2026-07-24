package com.jjrapps.bebeagua.ui.changelog

import android.content.res.Configuration
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

/**
 * Guards the pairing between [ChangelogCatalog] and the localized string arrays: a release with a
 * missing or empty highlights array would render as a blank card.
 */
@RunWith(AndroidJUnit4::class)
class ChangelogResourcesTest {

    private fun resourcesFor(language: String) =
        InstrumentationRegistry.getInstrumentation().targetContext.let { context ->
            val config = Configuration(context.resources.configuration).apply {
                setLocale(Locale.forLanguageTag(language))
            }
            context.createConfigurationContext(config).resources
        }

    private fun assertHighlightsPresent(language: String) {
        val resources = resourcesFor(language)
        ChangelogCatalog.releases.forEach { release ->
            val highlights = resources.getStringArray(release.highlightsRes)
            assertTrue(
                "Missing $language highlights for version ${release.versionName}",
                highlights.isNotEmpty()
            )
            assertTrue(
                "Blank $language highlight in version ${release.versionName}",
                highlights.none { it.isBlank() }
            )
        }
    }

    @Test
    fun englishHighlightsArePresent() = assertHighlightsPresent("en")

    @Test
    fun spanishHighlightsArePresent() = assertHighlightsPresent("es")
}
