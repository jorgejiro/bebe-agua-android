package com.jjrapps.bebeagua.data.local.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class IntakeDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: IntakeDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.intakeDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun observeIntakesForDate_returnsInsertedIntake() = runTest {
        val date = LocalDate.of(2025, 6, 1)
        dao.insert(entity(amountMl = 250, date = date))

        dao.observeIntakesForDate(date).test {
            val intakes = awaitItem()
            assertEquals(1, intakes.size)
            assertEquals(250, intakes.first().amountMl)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeIntakesForDate_doesNotReturnOtherDates() = runTest {
        val target = LocalDate.of(2025, 6, 1)
        val other = LocalDate.of(2025, 6, 2)
        dao.insert(entity(amountMl = 200, date = other))

        dao.observeIntakesForDate(target).test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeTotalForDate_sumsAllAmountsForTheDay() = runTest {
        val date = LocalDate.of(2025, 6, 1)
        dao.insert(entity(amountMl = 200, date = date))
        dao.insert(entity(amountMl = 350, date = date))

        dao.observeTotalForDate(date).test {
            assertEquals(550, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeTotalForDate_returnsZeroWhenNoIntakes() = runTest {
        dao.observeTotalForDate(LocalDate.of(2025, 6, 1)).test {
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun delete_removesIntakeFromQuery() = runTest {
        val date = LocalDate.of(2025, 6, 1)
        val id = dao.insert(entity(amountMl = 200, date = date))
        dao.delete(id)

        dao.observeIntakesForDate(date).test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getDailyTotalsBetween_aggregatesIntakesPerDay() = runTest {
        val d1 = LocalDate.of(2025, 1, 1)
        val d2 = LocalDate.of(2025, 1, 2)
        dao.insert(entity(amountMl = 500, date = d1, ts = 1_000L))
        dao.insert(entity(amountMl = 300, date = d1, ts = 2_000L))
        dao.insert(entity(amountMl = 400, date = d2, ts = 3_000L))

        val totals = dao.getDailyTotalsBetween(d1, d2)

        assertEquals(2, totals.size)
        assertEquals(800, totals.first { it.localDate == d1.toString() }.totalMl)
        assertEquals(400, totals.first { it.localDate == d2.toString() }.totalMl)
    }

    @Test
    fun getLastIntakeSizeMl_returnsNullWhenTableIsEmpty() = runTest {
        assertNull(dao.getLastIntakeSizeMl())
    }

    @Test
    fun getLastIntakeSizeMl_returnsMostRecentAmount() = runTest {
        val now = System.currentTimeMillis()
        val date = LocalDate.now()
        dao.insert(entity(amountMl = 200, date = date, ts = now - 10_000L))
        dao.insert(entity(amountMl = 350, date = date, ts = now))

        assertEquals(350, dao.getLastIntakeSizeMl())
    }

    @Test
    fun observeLastIntakeSizeMl_emitsMostRecentAmount() = runTest {
        val now = System.currentTimeMillis()
        val date = LocalDate.now()

        dao.observeLastIntakeSizeMl().test {
            assertNull(awaitItem())

            dao.insert(entity(amountMl = 200, date = date, ts = now - 10_000L))
            assertEquals(200, awaitItem())

            dao.insert(entity(amountMl = 350, date = date, ts = now))
            assertEquals(350, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun entity(amountMl: Int, date: LocalDate, ts: Long = System.currentTimeMillis()) =
        IntakeEntity(
            amountMl = amountMl,
            timestampEpochMs = ts,
            timezoneId = "Europe/Madrid",
            localDate = date.toString()
        )
}
