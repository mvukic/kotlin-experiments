package org.mvukic

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class IntegrationTestsIntegrationTests : IntegrationTestsBase() {

    @Test
    fun test() = runTest {
        assert(true)
    }

}
