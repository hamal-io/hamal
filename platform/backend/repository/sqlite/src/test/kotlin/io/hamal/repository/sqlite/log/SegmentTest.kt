//package io.hamal.repository.sqlite.log
//
//import io.hamal.lib.common.util.FileUtils
//import io.hamal.lib.domain.vo.TopicId
//import io.hamal.repository.api.log.Segment
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.equalTo
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Nested
//import org.junit.jupiter.api.Test
//import java.nio.file.Path
//import kotlin.io.path.Path
//import kotlin.io.path.pathString
//
//
//class SegmentSqliteRepositoryTest {
//
//    @Nested
//    inner class ConstructorTest {
//
//        @BeforeEach
//        fun setup() {
//            FileUtils.delete(Path(testDir))
//            FileUtils.createDirectories(Path(testDir))
//        }
//
//        @Test
//        fun `Creates a directory if path does not exists yet`() {
//            val targetDir = Path(testDir, "partition-001", "another-path")
//            SegmentSqliteRepository(testSegment(targetDir)).use { }
//
//            assertTrue(FileUtils.exists(targetDir))
//            assertTrue(FileUtils.exists(Path(targetDir.pathString, "00000000000000002810.db")))
//        }
//
//        @Test
//        fun `Creates chunks table`() {
//            SegmentSqliteRepository(testSegment()).connection
//                .executeQuery("SELECT COUNT(*) as count FROM sqlite_master WHERE name = 'chunks' AND type = 'table'") { resultSet ->
//                    assertThat(resultSet.getInt("count"), equalTo(1))
//                }
//        }
//
//        @Test
//        fun `Does not create chunks table if already exists`() {
//            SegmentSqliteRepository(testSegment()).use {
//                it.connection.execute("""INSERT INTO chunks (cmd_id, bytes,instant) VALUES (1,'some-bytes',unixepoch());""")
//            }
//
//
//            SegmentSqliteRepository(testSegment()).use { }
//            SegmentSqliteRepository(testSegment()).use {}
//            SegmentSqliteRepository(testSegment()).use {}
//            SegmentSqliteRepository(testSegment()).use {}
//
//            SegmentSqliteRepository(testSegment()).use {
//                it.connection.executeQuery("SELECT COUNT(*) as count FROM chunks") { resultSet ->
//                    assertThat(resultSet.getInt("count"), equalTo(1))
//                }
//            }
//        }
//
//        @Test
//        fun `Sets journal_mode to wal`() {
//            SegmentSqliteRepository(testSegment()).use {
//                it.connection.executeQuery("""SELECT journal_mode FROM pragma_journal_mode""") { resultSet ->
//                    assertThat(resultSet.getString("journal_mode"), equalTo("wal"))
//                }
//            }
//        }
//
//        @Test
//        fun `Sets locking_mode to exclusive`() {
//            SegmentSqliteRepository(testSegment()).use {
//                it.connection.executeQuery("""SELECT locking_mode FROM pragma_locking_mode""") { resultSet ->
//                    assertThat(resultSet.getString("locking_mode"), equalTo("exclusive"))
//                }
//            }
//        }
//
//        @Test
//        fun `Sets temp_store to memory`() {
//            SegmentSqliteRepository(testSegment()).use {
//                it.connection.executeQuery("""SELECT temp_store FROM pragma_temp_store""") { resultSet ->
//                    assertThat(resultSet.getString("temp_store"), equalTo("2"))
//                }
//            }
//        }
//
//        @Test
//        fun `Sets synchronous to off`() {
//            SegmentSqliteRepository(testSegment()).use {
//                it.connection.executeQuery("""SELECT synchronous FROM pragma_synchronous""") { resultSet ->
//                    assertThat(resultSet.getString("synchronous"), equalTo("0"))
//                }
//            }
//        }
//
//        private fun testSegment(path: Path = Path(testDir)) = SegmentSqlite(
//            id = Segment.Id(2810),
//            topicId = TopicId(1506),
//            path = path
//        )
//    }
//
//    private val testDir = "/tmp/hamal/test/segments"
//}