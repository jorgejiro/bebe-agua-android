package com.jjrapps.bebeagua.ui.changelog

import com.jjrapps.bebeagua.BuildConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChangelogCatalogTest {

    @Test
    fun `catalog is not empty`() {
        assertTrue(ChangelogCatalog.releases.isNotEmpty())
    }

    @Test
    fun `releases are listed newest first`() {
        val codes = ChangelogCatalog.releases.map { it.versionCode }
        assertEquals(codes.sortedDescending(), codes)
    }

    @Test
    fun `version codes are unique`() {
        val codes = ChangelogCatalog.releases.map { it.versionCode }
        assertEquals(codes.size, codes.distinct().size)
    }

    @Test
    fun `current build has an entry with a matching version name`() {
        val current = ChangelogCatalog.releases.find { it.versionCode == BuildConfig.VERSION_CODE }
        assertNotNull(
            "Add a ChangelogCatalog entry for versionCode ${BuildConfig.VERSION_CODE}",
            current
        )
        assertEquals(BuildConfig.VERSION_NAME, current!!.versionName)
    }

    @Test
    fun `every release points at a highlights array`() {
        assertTrue(ChangelogCatalog.releases.all { it.highlightsRes != 0 })
    }
}
