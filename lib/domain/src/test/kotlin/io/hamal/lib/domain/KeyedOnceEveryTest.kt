package io.hamal.lib.domain

import io.hamal.lib.domain.KeyedOnceEvery
import io.hamal.lib.domain.supplier.InstantSupplier
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

class KeyedOnceEveryEveryTest {

    @Nested
    @DisplayName("DefaultImpl")
    inner class DefaultImplTest {

        @Test
        fun `First time execution`() {
            val testInstance = KeyedOnceEvery.default<String, Int>(5.seconds)

            assertThat(testInstance("key") { 2810 }, equalTo(2810))
        }

        @Test
        fun `Already executed`() {
            val testInstance = KeyedOnceEvery.default<String, Int>(5.seconds)
            testInstance("key") { 2810 }

            assertThat(testInstance("key") { 1212 }, equalTo(2810))
        }

        @Test
        fun `Already executed but time passed in the mean time`() {
            val instantSupplier = InstantSupplier.fake(Instant.now())
            val testInstance = KeyedOnceEvery.default<String, Int>(5.seconds, instantSupplier)
            testInstance("key") { 2810 }

            assertThat(testInstance("key") { 1212 }, equalTo(2810))
            instantSupplier.plus(5.seconds)
            assertThat(testInstance("key") { 1212 }, equalTo(1212))
        }

        @Test
        fun `Different key`() {
            val testInstance = KeyedOnceEvery.default<String, Int>(5.seconds)
            testInstance("key") { 2810 }

            assertThat(testInstance("anotherKey") { 1212 }, equalTo(1212))
        }
    }

    @Nested
    @DisplayName("LruImpl")
    inner class LruImplTest {
        @Test
        fun `First time execution`() {
            val testInstance = KeyedOnceEvery.lru<String, Int>(1, 5.seconds)

            assertThat(testInstance("key") { 2810 }, equalTo(2810))
        }

        @Test
        fun `Already executed`() {
            val testInstance = KeyedOnceEvery.lru<String, Int>(1, 5.seconds)
            testInstance("key") { 2810 }

            assertThat(testInstance("key") { 1212 }, equalTo(2810))
        }

        @Test
        fun `Already executed but time passed in the mean time`() {
            val instantSupplier = InstantSupplier.fake(Instant.now())
            val testInstance = KeyedOnceEvery.lru<String, Int>(1, 5.seconds, instantSupplier)
            testInstance("key") { 2810 }

            assertThat(testInstance("key") { 1212 }, equalTo(2810))
            instantSupplier.plus(5.seconds)
            assertThat(testInstance("key") { 1212 }, equalTo(1212))
        }


        @Test
        fun `Evicts data`() {
            val testInstance = KeyedOnceEvery.lru<String, Int>(1, 5.seconds)
            testInstance("key") { 2810 }

            //evicts key 2810
            assertThat(testInstance("anotherKey") { 1506 }, equalTo(1506))

            assertThat(testInstance("key") { 1212 }, equalTo(1212))
        }
    }
}