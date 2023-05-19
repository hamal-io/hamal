package io.hamal.lib.domain

import io.hamal.lib.domain.vo.helper.SerializationFixture.generateTestCases
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class ShardTest {
    @Nested
    @DisplayName("Shard()")
    inner class ConstructorTest {
        @Test
        fun `Tries to create a region id less than 0`() {
            val exception = assertThrows<IllegalArgumentException> {
                Shard(-1)
            }
            assertThat(exception.message, containsString("Shard must be in interval [0,1023]"))
        }

        @Test
        fun `Tries to create a region greater than 1023`() {
            val exception = assertThrows<IllegalArgumentException> {
                Shard(1024)
            }
            assertThat(exception.message, containsString("Shard must be in interval [0,1023]"))
        }

        @Test
        fun `Creates valid region id`() {
            Shard(0)
            Shard(23)
            Shard(1023)
        }
    }

    @Nested
    @DisplayName("Serializer")
    inner class SerializerTest {
        @TestFactory
        fun tests() = generateTestCases(Shard(234), "234")
    }


    @Test
    fun `toString override`() {
        assertThat(Shard(123).toString(), equalTo("Shard(123)"))
    }

}
