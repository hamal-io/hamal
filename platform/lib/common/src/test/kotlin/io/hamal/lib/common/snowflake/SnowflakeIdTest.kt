package io.hamal.lib.common.snowflake

import io.hamal.lib.common.Partition
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class SnowflakeIdTest {

    @Test
    fun `Max id`() {
        val result = SnowflakeId(Long.MAX_VALUE)
        assertThat(result.elapsed(), equalTo(Elapsed(2199023255551)))
        assertThat(result.partition(), equalTo(Partition(63)))
        assertThat(result.sequence(), equalTo(Sequence(65535)))

        val maxInstant = Instant.ofEpochMilli(result.elapsed().value + 1698451200000L)
        assertThat(maxInstant, equalTo(Instant.parse("2093-07-03T15:47:35.551Z")))
    }

    @Test
    fun `partition = 0 - sequence = 0 - elapsed = 0`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(0),
            partitionSource = PartitionSourceImpl(0)
        )

        val result = testInstance.next()
        assertThat(result, equalTo(SnowflakeId(0)))

        assertThat(result.partition(), equalTo(Partition(0)))
        assertThat(result.sequence(), equalTo(Sequence(0)))
        assertThat(result.elapsed(), equalTo(Elapsed(0)))
    }

    @Test
    fun `partition = 0 - sequence = 1 - elapsed = 0`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(0),
            partitionSource = PartitionSourceImpl(0)
        )

        testInstance.next()
        val result = testInstance.next()
        assertThat(result, equalTo(SnowflakeId(1)))

        assertThat(result.partition(), equalTo(Partition(0)))
        assertThat(result.sequence(), equalTo(Sequence(1)))
        assertThat(result.elapsed(), equalTo(Elapsed(0)))
    }

    @Test
    fun `partition = 0 - sequence = 2 - elapsed = 0`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(0),
            partitionSource = PartitionSourceImpl(0)
        )

        testInstance.next()
        testInstance.next()
        val result = testInstance.next()
        assertThat(result, equalTo(SnowflakeId(2)))

        assertThat(result.partition(), equalTo(Partition(0)))
        assertThat(result.sequence(), equalTo(Sequence(2)))
        assertThat(result.elapsed(), equalTo(Elapsed(0)))
    }

    @Test
    fun `partition = 10 - sequence = 0 - elapsed = 0`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(0),
            partitionSource = PartitionSourceImpl(10)
        )

        val result = testInstance.next()
        assertThat(result, equalTo(SnowflakeId(655360)))

        assertThat(result.partition(), equalTo(Partition(10)))
        assertThat(result.sequence(), equalTo(Sequence(0)))
        assertThat(result.elapsed(), equalTo(Elapsed(0)))
    }

    @Test
    fun `partition = 42 - sequence = 0 - elapsed = 123456`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(123456),
            partitionSource = PartitionSourceImpl(42)
        )

        val result = testInstance.next()
        assertThat(result, equalTo(SnowflakeId(517814747136)))

        assertThat(result.partition(), equalTo(Partition(42)))
        assertThat(result.sequence(), equalTo(Sequence(0)))
        assertThat(result.elapsed(), equalTo(Elapsed(123456)))
    }

    @Test
    fun `partition = 42 - sequence = 0 - elapsed = 1099511627776 (~39 years)`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(1099511627776),
            partitionSource = PartitionSourceImpl(42)
        )

        val result = testInstance.next()
        assertThat(result, equalTo(SnowflakeId(4611686018430140416)))

        assertThat(result.partition(), equalTo(Partition(42)))
        assertThat(result.sequence(), equalTo(Sequence(0)))
        assertThat(result.elapsed(), equalTo(Elapsed(1099511627776)))

    }

    @Test
    fun `partition = 42 - sequence = 0 - elapsed = 2199023255550 (~69)`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(2199023255550),
            partitionSource = PartitionSourceImpl(42)
        )

        val result = testInstance.next()
        assertThat(result, equalTo(SnowflakeId(9223372036849139712)))

        assertThat(result.partition(), equalTo(Partition(42)))
        assertThat(result.sequence(), equalTo(Sequence(0)))
        assertThat(result.elapsed(), equalTo(Elapsed(2199023255550)))
    }
}


internal class SnowflakeGeneratorTest {
    @Test
    fun `T-0 Partition 0 - first call in this sequence`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(0),
            partitionSource = PartitionSourceImpl(0)
        )

        val result = testInstance.next()
        assertThat(result, equalTo(SnowflakeId(0)))

        assertThat(result.partition(), equalTo(Partition(0)))
        assertThat(result.sequence(), equalTo(Sequence(0)))
        assertThat(result.elapsed(), equalTo(Elapsed(0)))
    }

    @Test
    fun `T-1 Partition 0 - first call in this sequence`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(1),
            partitionSource = PartitionSourceImpl(0)
        )
        assertThat(
            testInstance.next(),
            equalTo(SnowflakeId(4194304))
        )
    }

    @Test
    fun `T-10 Partition 0 - first call in this sequence`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(10),
            partitionSource = PartitionSourceImpl(0)
        )
        assertThat(
            testInstance.next(), equalTo(
                SnowflakeId(
                    41943040
                )
            )
        )
    }

    @Test
    fun `T-10 Partition 0`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(10),
            partitionSource = PartitionSourceImpl(0)
        )
        assertThat(testInstance.next(), equalTo(SnowflakeId(41943040)))
        assertThat(testInstance.next(), equalTo(SnowflakeId(41943041)))
        assertThat(testInstance.next(), equalTo(SnowflakeId(41943042)))
        assertThat(testInstance.next(), equalTo(SnowflakeId(41943043)))
    }

    @Test
    fun `T-100 Partition 0 - first call in this sequence`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(100),
            partitionSource = PartitionSourceImpl(0)
        )
        assertThat(
            testInstance.next(), equalTo(
                SnowflakeId(
                    419430400
                )
            )
        )
    }

    @Test
    fun `T-0 Partition 1 - first call in this sequence`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(0),
            partitionSource = PartitionSourceImpl(1)
        )
        assertThat(testInstance.next(), equalTo(SnowflakeId(65536)))
    }

    @Test
    fun `T-0 Partition 2 - first call in this sequence`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(0),
            partitionSource = PartitionSourceImpl(2)
        )
        assertThat(testInstance.next(), equalTo(SnowflakeId(131072)))
    }

    @Test
    fun `T-0 Partition 0 - until exhaustion`() {
        val testInstance = SnowflakeGenerator(
            elapsedSource = FixedElapsedSource(0),
            partitionSource = PartitionSourceImpl(0)
        )

        for (i in 0 until 4096) {
            val expectedNext = i.toLong()
            val result = testInstance.next()
            assertThat(result, equalTo(SnowflakeId(expectedNext)))

            assertThat(result.sequence(), equalTo(Sequence(i)))
        }
    }
}


