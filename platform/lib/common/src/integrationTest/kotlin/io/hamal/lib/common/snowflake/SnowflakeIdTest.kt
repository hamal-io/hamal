package io.hamal.lib.common.snowflake

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

internal class SnowflakeIdTest {

    @Test
    fun `Multiple generators with different Partitions create ids`() {
        val instanceOne = SnowflakeGenerator(PartitionSourceImpl(125))
        val instanceTwo = SnowflakeGenerator(PartitionSourceImpl(126))
        val instanceThree = SnowflakeGenerator(PartitionSourceImpl(127))

        val resultOne = CompletableFuture.supplyAsync { IntRange(1, 500_000).map { instanceOne.next() }.toSet() }
        val resultTwo = CompletableFuture.supplyAsync { IntRange(1, 500_000).map { instanceTwo.next() }.toSet() }
        val resultThree = CompletableFuture.supplyAsync { IntRange(1, 500_000).map { instanceThree.next() }.toSet() }
        resultOne.join()
        resultTwo.join()
        resultThree.join()

        assertThat(resultOne.get(), hasSize(500_000))
        assertThat(resultTwo.get(), hasSize(500_000))
        assertThat(resultThree.get(), hasSize(500_000))

        val merged = resultOne.get().plus(resultTwo.get()).plus(resultThree.get())
        assertThat(merged, hasSize(1_500_000))
    }

    @Test
    fun `Multiple generators with different Partitions create ids - with unique epoch`() {
        val instanceOne = SnowflakeGenerator(
            partitionSource = PartitionSourceImpl(1),
            elapsedSource = ElapsedSourceImpl(0)
        )
        val instanceTwo = SnowflakeGenerator(
            partitionSource = PartitionSourceImpl(2),
            elapsedSource = ElapsedSourceImpl(0)
        )
        val instanceThree = SnowflakeGenerator(
            partitionSource = PartitionSourceImpl(3),
            elapsedSource = ElapsedSourceImpl(0)
        )

        val resultOne = CompletableFuture.supplyAsync { IntRange(1, 500_000).map { instanceOne.next() }.toSet() }
        val resultTwo = CompletableFuture.supplyAsync { IntRange(1, 500_000).map { instanceTwo.next() }.toSet() }
        val resultThree = CompletableFuture.supplyAsync { IntRange(1, 500_000).map { instanceThree.next() }.toSet() }
        resultOne.join()
        resultTwo.join()
        resultThree.join()

        assertThat(resultOne.get(), hasSize(500_000))
        assertThat(resultTwo.get(), hasSize(500_000))
        assertThat(resultThree.get(), hasSize(500_000))

        val merged = resultOne.get().plus(resultTwo.get()).plus(resultThree.get())
        assertThat(merged, hasSize(1_500_000))
    }

    @Test
    fun `Is concurrent safe`() {
        val instance = SnowflakeGenerator(
            partitionSource = PartitionSourceImpl(1),
            elapsedSource = ElapsedSourceImpl(123456)
        )

        val pool = Executors.newFixedThreadPool(12)
        val result = IntRange(1, 4096).map {
            pool.submit<SnowflakeId> { instance.next() }
        }.map { it.get() }.toSet()

        assertThat(result, hasSize(4096))
    }
}