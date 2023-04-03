package io.hamal.lib.meta.util

import io.hamal.lib.meta.Maybe
import io.hamal.lib.meta.get
import io.hamal.lib.meta.isAbsent
import io.hamal.lib.meta.isPresent
import io.hamal.lib.meta.util.LruCache.DefaultImpl
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

@Nested
class LruCacheTest {

    @Nested
    inner class DefaultImplTest {

        @Nested
        @DisplayName("put()")
        inner class PutTest {
            @Test
            fun `Insert value as value does not there`() {
                testInstance.put(someKey, someValue)
                verifySize(1)
                verifySomeValue()
            }

            @Test
            fun `Overwrites existing value`() {
                `Insert value as value does not there`()

                testInstance.put(someKey, anotherValue)
                verifySize(1)
                verifyAnotherValue()
            }

            @Test
            fun `Evicts data if too may insertions happen`() {
                `Insert value as value does not there`()

                IntRange(10, 30).forEach { testInstance.put(it, it) }

                verifySize(20)
                verifyAdditionalData()
            }
        }

        @Nested
        @DisplayName("putIfAbsent()")
        inner class PutIfAbsentTest {
            @Test
            fun `Inserts value as not value there`() {
                testInstance.putIfAbsent(someKey, someValue)
                verifySize(1)
                verifySomeValue()
            }

            @Test
            fun `Does not overwrite existing value`() {
                `Inserts value as not value there`()

                testInstance.putIfAbsent(someKey, anotherValue)
                verifySize(1)
                verifySomeValue()
            }

            @Test
            fun `Evicts data if too may insertions happen`() {
                `Inserts value as not value there`()

                IntRange(10, 30).forEach { testInstance.put(it, it) }

                verifySize(20)
                verifyAdditionalData()
            }
        }

        @Nested
        @DisplayName("find()")
        inner class FindTest {
            @Test
            fun `Nothing there`() {
                val maybeResult = testInstance.find(someKey)
                assertTrue(maybeResult.isAbsent())
            }

            @Test
            fun `Nothing found as key does not match`() {
                testInstance.put(anotherKey, anotherValue)

                val maybeResult = testInstance.find(someKey)
                assertTrue(maybeResult.isAbsent())
            }

            @Test
            @DisplayName("found")
            fun `Finds someValue`() {
                testInstance.put(someKey, someValue)
                testInstance.put(anotherKey, anotherValue)

                val maybeResult = testInstance.find(someKey)
                assertTrue(maybeResult.isPresent())
                assertThat(maybeResult, equalTo(Maybe.some(someValue)))
            }
        }


        @Nested
        @DisplayName("computeIfAbsent()")
        inner class ComputeIfAbsentTest {
            @Test
            @DisplayName("no value there before")
            fun insert() {
                testInstance.computeIfAbsent(someKey) { someValue }
                verifySize(1)
                verifySomeValue()
            }

            @Test
            @DisplayName("overwrite already existing value")
            fun overwrite() {
                insert()
                testInstance.computeIfAbsent(someKey) { anotherValue }
                verifySize(1)
                verifySomeValue()
            }
        }

        @Nested
        @DisplayName("size()")
        inner class SizeTest {
            @Test
            fun `Empty`() {
                val size = testInstance.size()
                assertThat(size, equalTo(0))
            }

            @Test
            fun `Ok`() {
                IntRange(1, 20).forEach {
                    testInstance.put(it, it)
                    val size = testInstance.size()
                    assertThat(size, equalTo(it))
                }
            }

        }

        private fun verifySomeValue() {
            val maybeResult = testInstance.find(someKey)
            assertTrue(maybeResult.isPresent())
            assertThat(maybeResult.get(), equalTo(someValue))
        }

        private fun verifyAnotherValue() {
            val maybeResult = testInstance.find(someKey)
            assertTrue(maybeResult.isPresent())
            assertThat(maybeResult.get(), equalTo(anotherValue))
        }

        private fun verifySize(expectedSize: Int) {
            assertThat(testInstance.size(), equalTo(expectedSize))
        }

        private fun verifyAdditionalData() {
            IntRange(11, 30).forEach { value: Int ->
                val maybeResult = testInstance.find(value)
                assertTrue(maybeResult.isPresent())
                assertThat(maybeResult.get(), equalTo(value))
            }
        }


        private val testInstance: DefaultImpl<Int, Int> = DefaultImpl(20)

        private val someKey = 1
        private val someValue = 2
        private val anotherValue = 3
        private val anotherKey = 4
    }

}