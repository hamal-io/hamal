@file:OptIn(ExperimentalSerializationApi::class)

package io.hamal.lib.domain.util

import io.hamal.lib.common.*
import io.hamal.lib.common.DefaultPartitionSource
import io.hamal.lib.common.DefaultSequenceSource
import io.hamal.lib.common.SnowflakeGenerator
import io.hamal.lib.common.SnowflakeId.ElapsedSource
import io.hamal.lib.common.SnowflakeId.ElapsedSource.Elapsed
import io.hamal.lib.common.SnowflakeId.PartitionSource.Partition
import io.hamal.lib.common.SnowflakeId.SequenceSource.Sequence
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.Executors

class SnowflakeTest {
    @Nested
    @DisplayName("DefaultElapsedSource")
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
    @DisplayName("PartitionSource")
    inner class PartitionSourceTest {
        @Nested
        @DisplayName("Partition")
        inner class PartitionTest {
            @Test
            fun `Limited to 10 bits`() {
                Partition(1023)

                val exception = assertThrows<IllegalArgumentException> {
                    Partition(1024)
                }
                assertThat(exception.message, containsString("Partition is limited to 10 bits - [0, 1023]"))
            }

            @Test
            fun `Partition is not negative`() {
                Partition(1)
                Partition(0)

                val exception = assertThrows<IllegalArgumentException> {
                    Partition(-1)
                }
                assertThat(exception.message, containsString("Partition must not be negative - [0, 1023]"))
            }
        }

        @Nested
        @DisplayName("DefaultPartitionSource")
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
    @DisplayName("SequenceSource")
    inner class SequenceSourceTest {

        @Nested
        @DisplayName("Sequence")
        inner class SequenceTest {
            @Test
            fun `Limited to 12 bits`() {
                Sequence(4096)

                val exception = assertThrows<IllegalArgumentException> {
                    Sequence(4097)
                }
                assertThat(exception.message, containsString("Sequence is limited to 12 bits - [1, 4096]"))
            }

            @Test
            fun `Sequence is not 0`() {
                val exception = assertThrows<IllegalArgumentException> {
                    Sequence(0)
                }
                assertThat(exception.message, containsString("Sequence must be positive - [1, 4096]"))
            }

            @Test
            fun `Sequence is not negative`() {
                val exception = assertThrows<IllegalArgumentException> {
                    Sequence(-1)
                }
                assertThat(exception.message, containsString("Sequence must be positive - [1, 4096]"))
            }
        }

        @Nested
        @DisplayName("DefaultSequenceSource")
        inner class DefaultSequenceSourceTest {

            @Nested
            @DisplayName("next()")
            inner class NextTest() {

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

                    for (i in 1 until 4097) {
                        val (elapsed, seq) = testInstance.next(elapsedSource::elapsed)
                        assertThat(elapsed, equalTo(Elapsed(1)))
                        assertThat(seq, equalTo(Sequence(i)))
                    }

                    val (resultElapsed, resultSeq) = testInstance.next(elapsedSource::elapsed)
                    assertThat(resultElapsed, equalTo(Elapsed(2)))
                    assertThat(resultSeq, equalTo(Sequence(1)))
                }
            }
        }
    }

    @Nested
    @DisplayName("Id")
    inner class IdTest {
        @Test
        fun `Genesis - never called`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(10)
            )

            val result = testInstance.next()
            assertThat(result, equalTo(SnowflakeId(90074191570665472)))

            assertThat(result.partition(), equalTo(Partition(10)))
            assertThat(result.sequence(), equalTo(Sequence(1)))
            assertThat(result.elapsed(), equalTo(Elapsed(0)))
        }

        @Test
        fun `123456 milli seconds after epoch`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(123456),
                partitionSource = DefaultPartitionSource(42)
            )

            val result = testInstance.next()
            assertThat(result, equalTo(SnowflakeId(378304567722500672)))

            assertThat(result.partition(), equalTo(Partition(42)))
            assertThat(result.sequence(), equalTo(Sequence(1)))
            assertThat(result.elapsed(), equalTo(Elapsed(123456)))
        }

        @Test
        fun `Around 39 years after epoch`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(1099511627776),
                partitionSource = DefaultPartitionSource(42)
            )


            val result = testInstance.next()
            assertThat(result, equalTo(SnowflakeId(378305667234004992)))

            assertThat(result.partition(), equalTo(Partition(42)))
            assertThat(result.sequence(), equalTo(Sequence(1)))
            assertThat(result.elapsed(), equalTo(Elapsed(1099511627776)))

        }

        @Test
        fun `Around 69 years after epoch`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(2199023255550),
                partitionSource = DefaultPartitionSource(42)
            )


            val result = testInstance.next()
            assertThat(result, equalTo(SnowflakeId(378306766745632766)))

            assertThat(result.partition(), equalTo(Partition(42)))
            assertThat(result.sequence(), equalTo(Sequence(1)))
            assertThat(result.elapsed(), equalTo(Elapsed(2199023255550)))
        }

        @Test
        fun `10 years after epoch on Partition 128 and a couple of sequences`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(315569520000),
                partitionSource = DefaultPartitionSource(128)
            )


            for (i in 1 until 2810) {
                testInstance.next()
            }

            val result = testInstance.next()
            assertThat(result, equalTo(SnowflakeId(1159101075524468096)))

            assertThat(result.partition(), equalTo(Partition(128)))
            assertThat(result.sequence(), equalTo(Sequence(2810)))
            assertThat(result.elapsed(), equalTo(Elapsed(315569520000)))
        }

        @Test
        fun `Is Serializable`() {
            val testInstance = SnowflakeId(12345678)
            val encoded = ProtoBuf.encodeToByteArray(testInstance)
            assertThat(encoded.size, equalTo(4))
            val decoded = ProtoBuf.decodeFromByteArray<SnowflakeId>(encoded)
            assertThat(decoded, equalTo(SnowflakeId(12345678)))
        }
    }

    @Nested
    @DisplayName("SnowflakeGenerator")
    inner class SnowflakeGeneratorTest {
        @Test
        fun `T-0 Partition 0 - first call in this sequence`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(0)
            )

            val result = testInstance.next()
            assertThat(result, equalTo(SnowflakeId(2199023255552)))

            assertThat(result.partition(), equalTo(Partition(0)))
            assertThat(result.sequence(), equalTo(Sequence(1)))
            assertThat(result.elapsed(), equalTo(Elapsed(0)))
        }

        @Test
        fun `T-1 Partition 0 - first call in this sequence`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(1),
                partitionSource = DefaultPartitionSource(0)
            )
            assertThat(testInstance.next(), equalTo(SnowflakeId(2199023255553)))
        }

        @Test
        fun `T-10 Partition 0 - first call in this sequence`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(10),
                partitionSource = DefaultPartitionSource(0)
            )
            assertThat(testInstance.next(), equalTo(SnowflakeId(2199023255562)))
        }

        @Test
        fun `T-10 Partition 0`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(10),
                partitionSource = DefaultPartitionSource(0)
            )
            assertThat(testInstance.next(), equalTo(SnowflakeId(2199023255562)))
            assertThat(testInstance.next(), equalTo(SnowflakeId(4398046511114)))
            assertThat(testInstance.next(), equalTo(SnowflakeId(6597069766666)))
            assertThat(testInstance.next(), equalTo(SnowflakeId(8796093022218)))
        }

        @Test
        fun `T-100 Partition 0 - first call in this sequence`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(100),
                partitionSource = DefaultPartitionSource(0)
            )
            assertThat(testInstance.next(), equalTo(SnowflakeId(2199023255652)))
        }

        @Test
        fun `T-0 Partition 1 - first call in this sequence`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(1)
            )
            assertThat(testInstance.next(), equalTo(SnowflakeId(9009398277996544)))
        }

        @Test
        fun `T-0 Partition 2 - first call in this sequence`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(2)
            )
            assertThat(testInstance.next(), equalTo(SnowflakeId(18016597532737536)))
        }

        @Test
        fun `T-0 Partition 0 - until exhaustion`() {
            val testInstance = SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(0)
            )

            for (i in 1 until 4096) {
                val expectedNext = (i).toLong().shl(63 - 22)
                val result = testInstance.next()
                assertThat(result, equalTo(SnowflakeId(expectedNext)))

                assertThat(result.sequence(), equalTo(Sequence(i)))
            }
        }


        @Test
        fun `Multiple generators with different partitions create ids`() {
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

            // all ids in resultTwo must be greater than resultOne - because partition is most significant
            val resultOneMax = resultOne.get().max()
            resultTwo.get().forEach { resultTwoId -> assertThat(resultTwoId, greaterThan(resultOneMax)) }

            // all ids of resultThree must be greater than resultTwo - because partition is most significant
            val resultTwoMax = resultTwo.get().max()
            resultThree.get().forEach { resultThreeId -> assertThat(resultThreeId, greaterThan(resultTwoMax)) }
        }
    }


    @Test
    fun `Multiple generators with different partitions create ids - with unique epoch`() {
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