package org.alberto97.ouilookup.tools

import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

class UpdatePolicyManagerTest {

    @Test
    fun testNeedsUpdate() {
        val time = LocalDateTime.now().minusDays(31).toInstant(ZoneOffset.UTC).toEpochMilli()
        val result = UpdatePolicyManager.isOutdated(time)
        assert(result)
    }

    @Test
    fun testUpToDate() {
        val time = LocalDateTime.now().minusDays(29).toInstant(ZoneOffset.UTC).toEpochMilli()
        val result = UpdatePolicyManager.isOutdated(time)
        assert(!result)
    }
}