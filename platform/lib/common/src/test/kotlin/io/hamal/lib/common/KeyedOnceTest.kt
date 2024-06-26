package io.hamal.lib.common

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class KeyedOnceTest {
    @Nested
    inner class DefaultImplTest {

        @Test
        fun `First time execution`() {
            val testInstance = KeyedOnce.default<String, Int>()

            assertThat(testInstance("key") { 2810 }, equalTo(2810))
        }

        @Test
        fun `Already executed`() {
            val testInstance = KeyedOnce.default<String, Int>()
            testInstance("key") { 2810 }

            assertThat(testInstance("key") { 1212 }, equalTo(2810))
        }

        @Test
        fun `Different key`() {
            val testInstance = KeyedOnce.default<String, Int>()
            testInstance("key") { 2810 }

            assertThat(testInstance("anotherKey") { 1212 }, equalTo(1212))
        }
    }

    @Nested
    inner class LruImplTest {
        @Test
        fun `First time execution`() {
            val testInstance = KeyedOnce.lru<String, Int>(1)

            assertThat(testInstance("key") { 2810 }, equalTo(2810))
        }

        @Test
        fun `Already executed`() {
            val testInstance = KeyedOnce.lru<String, Int>(1)
            testInstance("key") { 2810 }

            assertThat(testInstance("key") { 1212 }, equalTo(2810))
        }

        @Test
        fun `Evicts data`() {
            val testInstance = KeyedOnce.lru<String, Int>(1)
            testInstance("key") { 2810 }

            //evicts key 2810
            assertThat(testInstance("anotherKey") { 1506 }, equalTo(1506))

            assertThat(testInstance("key") { 1212 }, equalTo(1212))
        }
    }
}