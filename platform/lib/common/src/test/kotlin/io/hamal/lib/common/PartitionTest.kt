package io.hamal.lib.common

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PartitionTest {
    @Nested
    inner class ConstructorTest {

        @Test
        fun `Limited to 6 bits`() {
            Partition(63)

            val exception = assertThrows<IllegalArgumentException> {
                Partition(64)
            }
            assertThat(exception.message, containsString("Partition is limited to 6 bits - [0, 63]"))
        }

        @Test
        fun `Partition is not negative`() {
            Partition(1)
            Partition(0)

            val exception = assertThrows<IllegalArgumentException> {
                Partition(-1)
            }
            assertThat(exception.message, containsString("Partition must not be negative - [0, 63]"))
        }

        @Test
        fun `Valid partitions`() {
            Partition(0)
            Partition(23)
            Partition(63)
        }
    }

    @Test
    fun `toString override`() {
        assertThat(Partition(23).toString(), equalTo("Partition(23)"))
    }
}
