@file:OptIn(ExperimentalSerializationApi::class)

package io.hamal.lib.common

import io.hamal.lib.common.SnowflakeId.ElapsedSource
import io.hamal.lib.common.SnowflakeId.ElapsedSource.Elapsed
import io.hamal.lib.common.SnowflakeId.SequenceSource.Sequence
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.Executors

class SnowflakeTest {
    @Nested
    inner class DefaultElapsedSourceTest {
        @Test
        fun `With default epoch`() {
            val testInstance = DefaultElapsedSource()
            assertThat(testInstance.epoch, equalTo(1682116276624))
        }

        @Test
        fun `Is monotonic`() {
            val testInstance = DefaultElapsedSource()
            var prev = testInstance.elapsed()
            for (i in (0..1_000_000)) {
                val current = testInstance.elapsed()
                assertThat(prev, lessThanOrEqualTo(current))
                prev = current
            }
        }
    }

    @Nested
    inner class PartitionSourceTest {
        @Nested
        inner class DefaultsPartitionSourceTest {
            @Test
            fun `Simple pass through`() {
                val testInstance = DefaultPartitionSource(137)
                assertThat(testInstance.get(), equalTo(Partition(137)))
                assertThat(testInstance.get(), equalTo(Partition(137)))
                assertThat(testInstance.get(), equalTo(Partition(137)))
                assertThat(testInstance.get(), equalTo(Partition(137)))
            }
        }
    }

    @Nested
    inner class SequenceSourceTest {
        @Nested
        inner class SequenceTest {
            @Test
            fun `Limited to 12 bits`() {
                Sequence(0)
                Sequence(4095)

                val exception = assertThrows<IllegalArgumentException> {
                    Sequence(4096)
                }
                assertThat(exception.message, containsString("Sequence is limited to 12 bits - [0, 4095]"))
            }

            @Test
            fun `Sequence is not negative`() {
                val exception = assertThrows<IllegalArgumentException> {
                    Sequence(-1)
                }
                assertThat(exception.message, containsString("Sequence must not be negative - [0, 4095]"))
            }
        }

        @Nested
        inner class DefaultSequenceSourceTest {
            @Nested
            inner class NextTest {

                @Test
                fun `Requires ElapsedSource to return monotonic time`() {
                    val testInstance = DefaultSequenceSource()

                    testInstance.next { Elapsed(0L) }
                    testInstance.next { Elapsed(1L) }
                    testInstance.next { Elapsed(2L) }
                    testInstance.next { Elapsed(3L) }

                    val exception = assertThrows<IllegalStateException> {
                        testInstance.next { Elapsed(1) }
                    }
                    assertThat(exception.message, containsString("Elapsed must be monotonic"))
                }

                @Test
                fun `Blocks if sequence is exhausted`() {
                    val testInstance = DefaultSequenceSource()
                    val elapsedSource = object : ElapsedSource {
                        var counter = 0
                        var blockingCounter = 0


                        override fun elapsed(): Elapsed {
                            counter++
                            if (counter > 4096) {
                                blockingCounter++
                            }
                            if (blockingCounter > 1024) {
                                return Elapsed(2)
                            }
                            return Elapsed(1)
                        }
                    }

                    for (i in 0 until 4096) {
                        val (elapsed, seq) = testInstance.next(elapsedSource::elapsed)
                        assertThat(elapsed, equalTo(Elapsed(1)))
                        assertThat(seq, equalTo(Sequence(i)))
                    }

                    val (resultElapsed, resultSeq) = testInstance.next(elapsedSource::elapsed)
                    assertThat(resultElapsed, equalTo(Elapsed(2)))
                    assertThat(resultSeq, equalTo(Sequence(0)))
                }
            }
        }
    }

    @Nested
    inner class IdTest {

        @Test
        fun `partition = 0 - sequence = 0 - elapsed = 0`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(0)
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
                partitionSource = DefaultPartitionSource(0)
            )

            testInstance.next()
            val result = testInstance.next()
            assertThat(result, equalTo(SnowflakeId(1024)))

            assertThat(result.partition(), equalTo(Partition(0)))
            assertThat(result.sequence(), equalTo(Sequence(1)))
            assertThat(result.elapsed(), equalTo(Elapsed(0)))
        }

        @Test
        fun `partition = 0 - sequence = 2 - elapsed = 0`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(0)
            )

            testInstance.next()
            testInstance.next()
            val result = testInstance.next()
            assertThat(result, equalTo(SnowflakeId(2048)))

            assertThat(result.partition(), equalTo(Partition(0)))
            assertThat(result.sequence(), equalTo(Sequence(2)))
            assertThat(result.elapsed(), equalTo(Elapsed(0)))
        }

        @Test
        fun `partition = 10 - sequence = 0 - elapsed = 0`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(10)
            )

            val result = testInstance.next()
            assertThat(result, equalTo(SnowflakeId(10)))

            assertThat(result.partition(), equalTo(Partition(10)))
            assertThat(result.sequence(), equalTo(Sequence(0)))
            assertThat(result.elapsed(), equalTo(Elapsed(0)))
        }

        @Test
        fun `partition = 42 - sequence = 0 - elapsed = 123456`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(123456),
                partitionSource = DefaultPartitionSource(42)
            )

            val result = testInstance.next()
            assertThat(result, equalTo(SnowflakeId(517811994666)))

            assertThat(result.partition(), equalTo(Partition(42)))
            assertThat(result.sequence(), equalTo(Sequence(0)))
            assertThat(result.elapsed(), equalTo(Elapsed(123456)))
        }

        @Test
        fun `partition = 42 - sequence = 0 - elapsed = 1099511627776 (~39 years)`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(1099511627776),
                partitionSource = DefaultPartitionSource(42)
            )

            val result = testInstance.next()
            assertThat(result, equalTo(SnowflakeId(4611686018427387946)))

            assertThat(result.partition(), equalTo(Partition(42)))
            assertThat(result.sequence(), equalTo(Sequence(0)))
            assertThat(result.elapsed(), equalTo(Elapsed(1099511627776)))

        }

        @Test
        fun `partition = 42 - sequence = 0 - elapsed = 2199023255550 (~69)`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(2199023255550),
                partitionSource = DefaultPartitionSource(42)
            )


            val result = testInstance.next()
            assertThat(result, equalTo(SnowflakeId(9223372036846387242)))

            assertThat(result.partition(), equalTo(Partition(42)))
            assertThat(result.sequence(), equalTo(Sequence(0)))
            assertThat(result.elapsed(), equalTo(Elapsed(2199023255550)))
        }

        @Test
        fun `Is Serializable`() {
            val testInstance = SnowflakeId(12345678)
            val encoded = ProtoBuf.encodeToByteArray(testInstance)
            assertThat(encoded.size, equalTo(7))
            val decoded = ProtoBuf.decodeFromByteArray<SnowflakeId>(encoded)
            assertThat(decoded, equalTo(SnowflakeId(12345678)))
        }
    }

    @Nested
    inner class SnowflakeGeneratorTest {
        @Test
        fun `T-0 Partition 0 - first call in this sequence`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(0)
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
                partitionSource = DefaultPartitionSource(0)
            )
            assertThat(testInstance.next(), equalTo(SnowflakeId(4194304)))
        }

        @Test
        fun `T-10 Partition 0 - first call in this sequence`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(10),
                partitionSource = DefaultPartitionSource(0)
            )
            assertThat(testInstance.next(), equalTo(SnowflakeId(41943040)))
        }

        @Test
        fun `T-10 Partition 0`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(10),
                partitionSource = DefaultPartitionSource(0)
            )
            assertThat(testInstance.next(), equalTo(SnowflakeId(41943040)))
            assertThat(testInstance.next(), equalTo(SnowflakeId(41944064)))
            assertThat(testInstance.next(), equalTo(SnowflakeId(41945088)))
            assertThat(testInstance.next(), equalTo(SnowflakeId(41946112)))
        }

        @Test
        fun `T-100 Partition 0 - first call in this sequence`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(100),
                partitionSource = DefaultPartitionSource(0)
            )
            assertThat(testInstance.next(), equalTo(SnowflakeId(419430400)))
        }

        @Test
        fun `T-0 Partition 1 - first call in this sequence`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(1)
            )
            assertThat(testInstance.next(), equalTo(SnowflakeId(1)))
        }

        @Test
        fun `T-0 Partition 2 - first call in this sequence`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(2)
            )
            assertThat(testInstance.next(), equalTo(SnowflakeId(2)))
        }

        @Test
        fun `T-0 Partition 0 - until exhaustion`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(0)
            )

            for (i in 0 until 4096) {
                val expectedNext = (i).toLong().shl(10)
                val result = testInstance.next()
                assertThat(result, equalTo(SnowflakeId(expectedNext)))

                assertThat(result.sequence(), equalTo(Sequence(i)))
            }
        }


        @Test
        fun `Multiple generators with different Partitions create ids`() {
            val instanceOne =
                SnowflakeGenerator(DefaultPartitionSource(1))
            val instanceTwo =
                SnowflakeGenerator(DefaultPartitionSource(2))
            val instanceThree =
                SnowflakeGenerator(DefaultPartitionSource(3))

            val resultOne = supplyAsync { IntRange(1, 500_000).map { instanceOne.next() }.toSet() }
            val resultTwo = supplyAsync { IntRange(1, 500_000).map { instanceTwo.next() }.toSet() }
            val resultThree = supplyAsync { IntRange(1, 500_000).map { instanceThree.next() }.toSet() }
            resultOne.join()
            resultTwo.join()
            resultThree.join()

            assertThat(resultOne.get(), hasSize(500_000))
            assertThat(resultTwo.get(), hasSize(500_000))
            assertThat(resultThree.get(), hasSize(500_000))

            val merged = resultOne.get().plus(resultTwo.get()).plus(resultThree.get())
            assertThat(merged, hasSize(1_500_000))
        }
    }


    @Test
    fun `Multiple generators with different Partitions create ids - with unique epoch`() {
        val instanceOne = SnowflakeGenerator(
            partitionSource = DefaultPartitionSource(1),
            elapsedSource = DefaultElapsedSource(0)
        )
        val instanceTwo = SnowflakeGenerator(
            partitionSource = DefaultPartitionSource(2),
            elapsedSource = DefaultElapsedSource(0)
        )
        val instanceThree = SnowflakeGenerator(
            partitionSource = DefaultPartitionSource(3),
            elapsedSource = DefaultElapsedSource(0)
        )

        val resultOne = supplyAsync { IntRange(1, 500_000).map { instanceOne.next() }.toSet() }
        val resultTwo = supplyAsync { IntRange(1, 500_000).map { instanceTwo.next() }.toSet() }
        val resultThree = supplyAsync { IntRange(1, 500_000).map { instanceThree.next() }.toSet() }
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
    fun `Is threadsafe`() {
        val instance = SnowflakeGenerator(
            partitionSource = DefaultPartitionSource(1),
            elapsedSource = DefaultElapsedSource(123456)
        )

        val pool = Executors.newFixedThreadPool(12)
        val result = IntRange(1, 100_000).map {
            pool.submit<SnowflakeId> { instance.next() }
        }.map { it.get() }.toSet()

        assertThat(result, hasSize(100_000))
    }
}