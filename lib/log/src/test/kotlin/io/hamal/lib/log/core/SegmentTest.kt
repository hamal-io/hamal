package io.hamal.lib.log.core

import io.hamal.lib.log.core.Segment.Config
import io.hamal.lib.util.Files
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import kotlin.io.path.Path
import kotlin.io.path.pathString

class SegmentTest {
    @BeforeEach
    fun setup() {
        Files.delete(Path(testDir))
        Files.createDirectories(Path(testDir))
    }

    @Nested
    @DisplayName("open()")
    inner class OpenTest {
        @Test
        fun `Creates a directory if path does not exists yet`() {
            val targetDir = Path(testDir, "partition-001", "another-path")
            Segment.open(Config(targetDir, RecordOffset(2810))).use { }
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
                it.connection.createStatement().use { statement ->
                    statement.execute(
                        """
                        INSERT INTO records (id,key, value,instant) VALUES (1,'key', 'value',unixepoch());
                    """.trimIndent()
                    )
                }
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

        private val testConfig = Config(Path(testDir), RecordOffset(2810))
    }

    private val testDir = "/tmp/hamal/test/segments"
}