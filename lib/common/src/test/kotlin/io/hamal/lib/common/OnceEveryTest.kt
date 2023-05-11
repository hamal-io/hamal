package io.hamal.lib.common

import io.hamal.lib.domain.OnceEvery
import io.hamal.lib.common.supplier.InstantSupplier
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

class OnceEveryTest {

    @Nested
    @DisplayName("DefaultImpl")
    inner class DefaultImplTest {

        @Test
        fun `First time execution`() {
            val testInstance: OnceEvery<Int> = OnceEvery.default(5.seconds, testInstantSupplier)
            val testMonitor = TestMonitor()

            assertThat(testInstance { testMonitor.inc() }, equalTo(1))
            assertThat(testInstance { testMonitor.inc() }, equalTo(1))
            assertThat(testInstance { testMonitor.inc() }, equalTo(1))
            assertThat(testInstance { testMonitor.inc() }, equalTo(1))
        }

        @Test
        fun `Already executed, but enough time passed in the mean time`() {
            val testInstance: OnceEvery<Int> = OnceEvery.default(5.seconds, testInstantSupplier)
            val testMonitor = TestMonitor()
            assertThat(testInstance { testMonitor.inc() }, equalTo(1))
            testInstantSupplier.plus(5.seconds)
            assertThat(testInstance { testMonitor.inc() }, equalTo(2))
            testInstantSupplier.plus(4.seconds)
            assertThat(testInstance { testMonitor.inc() }, equalTo(2))
            testInstantSupplier.plus(10.seconds)
            assertThat(testInstance { testMonitor.inc() }, equalTo(3))
        }


        private val testNow = Instant.now()
        private val testInstantSupplier = InstantSupplier.fake(testNow)

        inner class TestMonitor(private var count: Int = 0) {
            fun inc() = ++count
        }
    }

}