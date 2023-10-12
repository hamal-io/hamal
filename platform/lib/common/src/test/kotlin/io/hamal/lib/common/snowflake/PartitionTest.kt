package io.hamal.lib.common.snowflake

import io.hamal.lib.common.Partition
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class PartitionSourceImplTest {
    @Test
    fun `Simple pass through`() {
        val testInstance = PartitionSourceImpl(137)
        assertThat(testInstance.get(), equalTo(Partition(137)))
        assertThat(testInstance.get(), equalTo(Partition(137)))
        assertThat(testInstance.get(), equalTo(Partition(137)))
        assertThat(testInstance.get(), equalTo(Partition(137)))
    }
}
