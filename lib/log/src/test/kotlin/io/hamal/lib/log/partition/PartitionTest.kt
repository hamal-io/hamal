package io.hamal.lib.log.partition

import io.hamal.lib.log.ToRecord
import io.hamal.lib.log.partition.Partition.Record
import io.hamal.lib.log.segment.DepSegment
import io.hamal.lib.util.Files
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.nio.ByteBuffer
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.pathString

@DisplayName("Partition")
class PartitionTest {

    @Nested
    @DisplayName("open()")
    inner class OpenTest {
        @BeforeEach
        fun setup() {
            Files.delete(Path(testDir))
            Files.createDirectories(Path(testDir))
        }

        @Test
        fun `Creates a directory if path does not exists yet and populates first segment`() {
            val targetDir = Path(testDir, "another-path", "more-nesting")

            Partition.open(Partition.Config(Partition.Id(23), targetDir)).use { }

            assertTrue(Files.exists(targetDir))
            assertTrue(Files.exists(Path(targetDir.pathString, "partition-0023")))
            assertTrue(Files.exists(Path(targetDir.pathString, "partition-0023", "00000000000000000000.db")))
        }
    }

    @Nested
    @DisplayName("append()")
    inner class AppendTest {
        @BeforeEach
        fun setup() {
            testInstance.clear()
        }

        @AfterEach
        fun after() {
            testInstance.close()
        }

        @Test
        fun `Nothing to append`() {
            val result = testInstance.append()
            assertThat(result, hasSize(0))
            assertThat(testInstance.countRecords(), equalTo(0))
        }

        @Test
        fun `Append multiple records to empty partition`() {
            val result = testInstance.append(
                ToRecord(
                    ByteBuffer.wrap("KEY_1".toByteArray()),
                    ByteBuffer.wrap("VALUE_1".toByteArray()),
                    Instant.ofEpochMilli(1)
                ),
                ToRecord(
                    ByteBuffer.wrap("KEY_2".toByteArray()),
                    ByteBuffer.wrap("VALUE_2".toByteArray()),
                    Instant.ofEpochMilli(2)
                ),
                ToRecord(
                    ByteBuffer.wrap("KEY_3".toByteArray()),
                    ByteBuffer.wrap("VALUE_3".toByteArray()),
                    Instant.ofEpochMilli(3)
                )
            )

            assertThat(result, equalTo(listOf(Record.Id(1), Record.Id(2), Record.Id(3))))
            assertThat(testInstance.countRecords(), equalTo(3))

            testInstance.read(Record.Id(1)).let {
                assertThat(it, hasSize(1))
                val record = it.first()
                assertThat(record.id, equalTo(Record.Id(1)))
                assertThat(record.partitionId, equalTo(Partition.Id(1)))
                assertThat(record.segmentId, equalTo(DepSegment.Id(0)))
                assertThat(record.key, equalTo(ByteBuffer.wrap("KEY_1".toByteArray())))
                assertThat(record.value, equalTo(ByteBuffer.wrap("VALUE_1".toByteArray())))
                assertThat(record.instant, equalTo(Instant.ofEpochMilli(1)))
            }

            testInstance.read(Record.Id(3)).let {
                assertThat(it, hasSize(1))
                val record = it.first()
                assertThat(record.id, equalTo(Record.Id(3)))
                assertThat(record.partitionId, equalTo(Partition.Id(1)))
                assertThat(record.segmentId, equalTo(DepSegment.Id(0)))
                assertThat(record.key, equalTo(ByteBuffer.wrap("KEY_3".toByteArray())))
                assertThat(record.value, equalTo(ByteBuffer.wrap("VALUE_3".toByteArray())))
                assertThat(record.instant, equalTo(Instant.ofEpochMilli(3)))
            }

        }

        private val testInstance = Partition.open(Partition.Config(Partition.Id(1), Path(testDir)))
    }

    @Nested
    @DisplayName("read()")
    inner class ReadTest {

        @Test
        fun `Reads multiple records`() {
            givenOneHundredRecords()
            val result = testInstance.read(Record.Id(25), 36)
            assertThat(result, hasSize(36))

            for (id in 0 until 36) {
                assertRecord(result[id], 25 + id)
            }
        }

        private fun assertRecord(record: Record, id: Int) {
            assertThat(record.id, equalTo(Record.Id(id)))
            assertThat(record.partitionId, equalTo(Partition.Id(1)))
            assertThat(record.segmentId, equalTo(DepSegment.Id(0)))
            assertThat(record.key, equalTo(ByteBuffer.wrap("KEY_$id".toByteArray())))
            assertThat(record.value, equalTo(ByteBuffer.wrap("VALUE_$id".toByteArray())))
            assertThat(record.instant, equalTo(Instant.ofEpochMilli(id.toLong())))
        }

        private fun givenOneHundredRecords() {
            testInstance.append(
                IntRange(1, 100).map {
                    ToRecord(
                        ByteBuffer.wrap("KEY_$it".toByteArray()),
                        ByteBuffer.wrap("VALUE_$it".toByteArray()),
                        Instant.ofEpochMilli(it.toLong())
                    )
                }
            )
        }

        private val testInstance = Partition.open(Partition.Config(Partition.Id(1), Path(testDir)))
    }

    private val testDir = "/tmp/hamal/test/partitions"
}