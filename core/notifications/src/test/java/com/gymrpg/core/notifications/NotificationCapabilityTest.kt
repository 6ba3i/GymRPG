package com.gymrpg.core.notifications

import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationCapabilityTest {
    @Test fun unsupportedApiUsesStandardFallback() {
        assertTrue(NotificationCapability.status(35, true, null).contains("standard workout notification fallback"))
    }

    @Test fun deniedNotificationPermissionDoesNotClaimPromotion() {
        assertTrue(NotificationCapability.status(37, false, true).contains("permission unavailable"))
    }

    @Test fun promotedEligibilityDoesNotClaimIslandDisplay() {
        assertTrue(NotificationCapability.status(37, true, true).contains("OEM display unverified"))
    }
}
