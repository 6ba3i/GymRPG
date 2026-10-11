package com.gymrpg.integration.xiaomicn

import org.junit.Assert.assertTrue
import org.junit.Test

class XiaomiHealthKitProbeTest {
    @Test fun packageVisibilityDoesNotImplyAuthorization() {
        assertTrue(XiaomiHealthKitProbe.description(true).contains("unverified"))
    }

    @Test fun packageNotVisibleDoesNotClaimAbsence() {
        assertTrue(XiaomiHealthKitProbe.description(false).contains("not visible or not installed"))
    }
}
