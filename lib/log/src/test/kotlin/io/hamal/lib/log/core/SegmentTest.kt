package io.hamal.lib.log.core

import io.hamal.lib.log.core.Segment.*
import io.hamal.lib.util.Files
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.nio.ByteBuffer
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.pathString

@DisplayName("Segment")
class SegmentTest {

    @Nested
    @DisplayName("open()")
    inner class OpenTest {

        @BeforeEach
        fun setup() {
            Files.delete(Path(testDir))
            Files.createDirectories(Path(testDir))
        }

        @Test
        fun `Creates a directory if path does not exists yet`() {
            val targetDir = Path(testDir, "partition-001", "another-path")
            Segment.open(Config(Id(2810), targetDir)).use { }
            assertTrue(Files.exists(targetDir))
            assertTrue(Files.exists(Path(targetDir.pathString, "00000000000000002810.db")))
        }

        @Test
        fun `Creates records table`() {
            Segment.open(testConfig)
                .executeQuery("SELECT COUNT(*) FROM sqlite_master WHERE name = 'records' AND type = 'table'") { resultSet ->
                    assertThat(resultSet.getInt(1), equalTo(1))
                }
        }

        @Test
        fun `Does not create records table if already exists`() {
            Segment.open(testConfig).use {
                it.connection.beginRequest()
                it.connection.createStatement().use { statement ->
                    statement.execute(
                        """
                        INSERT INTO records (key, value,instant) VALUES ('key', 'value',unixepoch());
                    """.trimIndent()
                    )
                }
                it.connection.commit()
            }

            Segment.open(testConfig).use { }
            Segment.open(testConfig).use { }
            Segment.open(testConfig).use { }
            Segment.open(testConfig).use { }

            Segment.open(testConfig)
                .executeQuery("SELECT COUNT(*) FROM records") {
                    assertThat(it.getInt(1), equalTo(1))
                }
        }

        @Test
        fun `Sets journal_mode to wal`() {
            Segment.open(testConfig)
                .executeQuery("""SELECT * FROM pragma_journal_mode""") {
                    assertThat(it.getString(1), equalTo("wal"))
                }
        }

        @Test
        fun `Sets locking_mode to exclusive`() {
            Segment.open(testConfig)
                .executeQuery("""SELECT * FROM pragma_locking_mode""") {
                    assertThat(it.getString(1), equalTo("exclusive"))
                }
        }

        @Test
        fun `Sets temp_store to memory`() {
            Segment.open(testConfig)
                .executeQuery("""SELECT * FROM pragma_temp_store""") {
                    assertThat(it.getString(1), equalTo("2"))
                }
        }

        @Test
        fun `Sets synchronous to off`() {
            Segment.open(testConfig)
                .executeQuery("""SELECT * FROM pragma_synchronous""") {
                    assertThat(it.getString(1), equalTo("0"))
                }
        }

        private val testConfig = Config(Id(2810), Path(testDir))
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
        fun `Key,Value and Instant do not have to be unique`() {
            testInstance.append(
                ToRecord(
                    ByteBuffer.wrap("SomeKey".toByteArray()),
                    ByteBuffer.wrap("SomeValue".toByteArray()),
                    Instant.ofEpochMilli(1)
                )
            )

            testInstance.append(
                ToRecord(
                    ByteBuffer.wrap("SomeKey".toByteArray()),
                    ByteBuffer.wrap("SomeValue".toByteArray()),
                    Instant.ofEpochMilli(1)
                )
            )

            assertThat(testInstance.countRecords(), equalTo(2))

            testInstance.read(Id(1), 2).let {
                assertThat(it, hasSize(2))
                val record = it.first()
                assertThat(record.key, equalTo(ByteBuffer.wrap("SomeKey".toByteArray())))
                assertThat(record.value, equalTo(ByteBuffer.wrap("SomeValue".toByteArray())))
                assertThat(record.instant, equalTo(Instant.ofEpochMilli(1)))
            }
        }

        @Test
        fun `Append single record`() {
            val result = testInstance.append(
                ToRecord(
                    ByteBuffer.wrap("KEY".toByteArray()),
                    ByteBuffer.wrap("VALUE".toByteArray()),
                    Instant.ofEpochMilli(2810)
                )
            )
            assertThat(result, equalTo(listOf(Id(1))))

            assertThat(testInstance.countRecords(), equalTo(1))

            testInstance.read(Id(1)).let {
                assertThat(it, hasSize(1))
                val record = it.first()
                assertThat(record.id, equalTo(Record.Id(1)))
                assertThat(record.key, equalTo(ByteBuffer.wrap("KEY".toByteArray())))
                assertThat(record.value, equalTo(ByteBuffer.wrap("VALUE".toByteArray())))
                assertThat(record.instant, equalTo(Instant.ofEpochMilli(2810)))
            }
        }

        @Test
        fun `Append multiple records to empty segment`() {
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

            assertThat(
                result, equalTo(
                    listOf(
                        Id(1), Id(2), Id(3)
                    )
                )
            )
            assertThat(testInstance.countRecords(), equalTo(3))


            testInstance.read(Id(1)).let {
                assertThat(it, hasSize(1))
                val record = it.first()
                assertThat(record.id, equalTo(Record.Id(1)))
                assertThat(record.key, equalTo(ByteBuffer.wrap("KEY_1".toByteArray())))
                assertThat(record.value, equalTo(ByteBuffer.wrap("VALUE_1".toByteArray())))
                assertThat(record.instant, equalTo(Instant.ofEpochMilli(1)))
            }

            testInstance.read(Id(3)).let {
                assertThat(it, hasSize(1))
                val record = it.first()
                assertThat(record.id, equalTo(Record.Id(3)))
                assertThat(record.key, equalTo(ByteBuffer.wrap("KEY_3".toByteArray())))
                assertThat(record.value, equalTo(ByteBuffer.wrap("VALUE_3".toByteArray())))
                assertThat(record.instant, equalTo(Instant.ofEpochMilli(3)))
            }

        }

        @Test
        fun `Append multiple records to segment which already contains records`() {
            givenThreeRecords()
            val now = Instant.now()

            val result = testInstance.append(
                ToRecord(
                    ByteBuffer.wrap("Hamal".toByteArray()),
                    ByteBuffer.wrap("Rockz".toByteArray()),
                    now
                ),
                ToRecord(
                    ByteBuffer.wrap("Hamal".toByteArray()),
                    ByteBuffer.wrap("Rockz".toByteArray()),
                    now
                )
            )
            assertThat(result, equalTo(listOf(Id(4), Id(5))))
            assertThat(testInstance.countRecords(), equalTo(5))
        }

        private fun givenThreeRecords() {
            testInstance.append(
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
        }

        private val testInstance = Segment.open(Config(Id(1), Path(testDir)))

    }

    @Nested
    @DisplayName("read()")
    inner class ReadTest {
        @BeforeEach
        fun setup() {
            testInstance.clear()
        }

        @AfterEach
        fun after() {
            testInstance.close()
        }

        @Test
        fun `Tries to read from empty segment`() {
            val result = testInstance.read(Id(20))
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Tries to read outside of segment range`() {
            givenOneHundredRecords()

            val result = testInstance.read(Id(200), 100)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Tries to read with a limit of 0`() {
            givenOneHundredRecords()

            val result = testInstance.read(Id(23), 0)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Tries to read with a negative limit`() {
            givenOneHundredRecords()

            val result = testInstance.read(Id(23), -20)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Read exactly one record`() {
            givenOneHundredRecords()

            val result = testInstance.read(Id(69))
            assertThat(result, hasSize(1))
            assertRecord(result.first(), 69)
        }

        @Test
        fun `Reads multiple records`() {
            givenOneHundredRecords()
            val result = testInstance.read(Id(25), 36)
            assertThat(result, hasSize(36))

            for (id in 0 until 36) {
                assertRecord(result[id], 25 + id)
            }
        }

        @Test
        fun `Read is only partially covered by segment`() {
            givenOneHundredRecords()

            val result = testInstance.read(Id(90), 40)
            assertThat(result, hasSize(11))

            for (id in 0 until 11) {
                assertRecord(result[id], 90 + id)
            }
        }

        private fun assertRecord(record: Record, id: Int) {
            assertThat(record.id, equalTo(Record.Id(id)))
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

        private val testInstance = Segment.open(Config(Id(10000), Path(testDir)))
    }

    private val testDir = "/tmp/hamal/test/segments"
}