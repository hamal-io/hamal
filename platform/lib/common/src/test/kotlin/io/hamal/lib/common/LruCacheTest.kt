package io.hamal.lib.common

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class DefaultLruCacheTest {
    @Nested
    inner class PutTest {
        @Test
        fun `Insert value as value does not there`() {
            testInstance.set(someKey, someValue)
            verifySize(1)
            verifySomeValue()
        }

        @Test
        fun `Overwrites existing value`() {
            `Insert value as value does not there`()

            testInstance.set(someKey, anotherValue)
            verifySize(1)
            verifyAnotherValue()
        }

        @Test
        fun `Evicts data if too may insertions happen`() {
            `Insert value as value does not there`()

            IntRange(10, 30).forEach { testInstance.set(it, it) }

            verifySize(20)
            verifyAdditionalData()
        }
    }

    @Nested
    inner class PutIfAbsentTest {
        @Test
        fun `Inserts value as not value there`() {
            testInstance.setIfAbsent(someKey, someValue)
            verifySize(1)
            verifySomeValue()
        }

        @Test
        fun `Does not overwrite existing value`() {
            `Inserts value as not value there`()

            testInstance.setIfAbsent(someKey, anotherValue)
            verifySize(1)
            verifySomeValue()
        }

        @Test
        fun `Evicts data if too may insertions happen`() {
            `Inserts value as not value there`()

            IntRange(10, 30).forEach { testInstance.set(it, it) }

            verifySize(20)
            verifyAdditionalData()
        }
    }

    @Nested
    inner class FindTest {
        @Test
        fun `Nothing there`() {
            val maybeResult = testInstance.find(someKey)
            assertTrue(maybeResult == null)
        }

        @Test
        fun `Nothing found as key does not match`() {
            testInstance.set(anotherKey, anotherValue)

            val maybeResult = testInstance.find(someKey)
            assertTrue(maybeResult == null)
        }

        @Test

        fun `Finds someValue`() {
            testInstance.set(someKey, someValue)
            testInstance.set(anotherKey, anotherValue)

            val maybeResult = testInstance.find(someKey)
            assertTrue(maybeResult != null)
            assertThat(maybeResult, equalTo(someValue))
        }
    }

    @Nested
    inner class ComputeIfAbsentTest {
        @Test

        fun insert() {
            testInstance.computeIfAbsent(someKey) { someValue }
            verifySize(1)
            verifySomeValue()
        }

        @Test

        fun overwrite() {
            insert()
            testInstance.computeIfAbsent(someKey) { anotherValue }
            verifySize(1)
            verifySomeValue()
        }
    }

    @Nested
    inner class SizeTest {
        @Test
        fun `Empty`() {
            val size = testInstance.size()
            assertThat(size, equalTo(0))
        }

        @Test
        fun `Ok`() {
            IntRange(1, 20).forEach {
                testInstance.set(it, it)
                val size = testInstance.size()
                assertThat(size, equalTo(it))
            }
        }

    }

    private fun verifySomeValue() {
        val maybeResult = testInstance.find(someKey)
        assertThat(maybeResult, equalTo(someValue))
    }

    private fun verifyAnotherValue() {
        val maybeResult = testInstance.find(someKey)
        assertThat(maybeResult, equalTo(anotherValue))
    }

    private fun verifySize(expectedSize: Int) {
        assertThat(testInstance.size(), equalTo(expectedSize))
    }

    private fun verifyAdditionalData() {
        IntRange(11, 30).forEach { value: Int ->
            val maybeResult = testInstance.find(value)
            assertThat(maybeResult, equalTo(value))
        }
    }


    private val testInstance: LruCacheImpl<Int, Int> = LruCacheImpl(20)

    private val someKey = 1
    private val someValue = 2
    private val anotherValue = 3
    private val anotherKey = 4
}

