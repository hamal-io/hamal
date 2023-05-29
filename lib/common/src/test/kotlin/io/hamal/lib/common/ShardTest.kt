package io.hamal.lib.common

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ShardTest {
    @Nested
    inner class ConstructorTest {

        @Test
        fun `Limited to 10 bits`() {
            Shard(1023)

            val exception = assertThrows<IllegalArgumentException> {
                Shard(1024)
            }
            assertThat(exception.message, containsString("Shard is limited to 10 bits - [0, 1023]"))
        }

        @Test
        fun `Shard is not negative`() {
            Shard(1)
            Shard(0)

            val exception = assertThrows<IllegalArgumentException> {
                Shard(-1)
            }
            assertThat(exception.message, containsString("Shard must not be negative - [0, 1023]"))
        }

        @Test
        fun `Valid shards`() {
            Shard(0)
            Shard(23)
            Shard(1023)
        }
    }

    @Test
    fun `toString override`() {
        assertThat(Shard(123).toString(), equalTo("Shard(123)"))
    }
}
