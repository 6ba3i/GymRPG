package com.gymrpg

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoPreferencesTest {
    @Test fun failedReadReportsDefaultsAsFallback() = runBlocking {
        val preferences = DemoPreferences(FailingStore(readFailure = IOException("read")))
        assertEquals(DemoSettings(), preferences.settings.first())
        assertTrue(preferences.error.value!!.contains("could not be read"))
    }

    @Test fun failedSaveReportsErrorAndSuccessfulRetryClearsIt() = runBlocking {
        val store = FailingStore(writeFailure = IOException("write"))
        val preferences = DemoPreferences(store)
        preferences.save(DemoSettings(fullGlass = true))
        assertNotNull(preferences.error.value)
        store.writeFailure = null
        preferences.save(DemoSettings(fullGlass = true))
        assertNull(preferences.error.value)
    }

    @Test fun cancellationIsNotConvertedToPersistenceError() = runBlocking {
        val preferences = DemoPreferences(FailingStore(writeFailure = CancellationException("cancelled")))
        try {
            preferences.save(DemoSettings())
            throw AssertionError("Cancellation should propagate")
        } catch (_: CancellationException) {
            assertNull(preferences.error.value)
        }
    }

    private class FailingStore(
        val readFailure: Throwable? = null,
        var writeFailure: Throwable? = null,
    ) : DataStore<Preferences> {
        private var current = emptyPreferences()
        override val data: Flow<Preferences> = flow {
            readFailure?.let { throw it }
            emit(current)
        }
        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
            writeFailure?.let { throw it }
            current = transform(current)
            return current
        }
    }
}
