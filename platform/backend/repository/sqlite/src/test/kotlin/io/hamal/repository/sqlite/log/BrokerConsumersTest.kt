//package io.hamal.repository.sqlite.log
//
//import io.hamal.lib.common.util.FileUtils
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
//class BrokerConsumersSqliteRepositoryTest {
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
//            val targetDir = Path(testDir, "some-path", "another-path")
//            BrokerConsumersSqliteRepository(testBrokerConsumers(targetDir)).use { }
//
//            assertTrue(FileUtils.exists(targetDir))
//            assertTrue(FileUtils.exists(Path(targetDir.pathString, "consumers.db")))
//        }
//
//        @Test
//        fun `Creates consumers table`() {
//            BrokerConsumersSqliteRepository(testBrokerConsumers()).use {
//                it.connection.executeQuery("SELECT COUNT(*) as count FROM sqlite_master WHERE name = 'consumers' AND type = 'table'") { resultSet ->
//                    assertThat(resultSet.getInt("count"), equalTo(1))
//                }
//            }
//        }
//
//        @Test
//        fun `Does not create consumers table if already exists`() {
//            BrokerConsumersSqliteRepository(testBrokerConsumers()).use {
//                it.connection.execute(
//                    """INSERT INTO consumers (group_id,topic_id,next_chunk_id) VALUES ('some-consumer-id',1234,4321);"""
//                )
//            }
//
//            BrokerConsumersSqliteRepository(testBrokerConsumers()).use { }
//            BrokerConsumersSqliteRepository(testBrokerConsumers()).use { }
//            BrokerConsumersSqliteRepository(testBrokerConsumers()).use { }
//            BrokerConsumersSqliteRepository(testBrokerConsumers()).use { }
//
//            BrokerConsumersSqliteRepository(testBrokerConsumers()).use {
//                it.connection.executeQuery("SELECT COUNT(*) as count FROM consumers") { resultSet ->
//                    assertThat(resultSet.getInt("count"), equalTo(1))
//                }
//            }
//        }
//
//        @Test
//        fun `Sets journal_mode to wal`() {
//            BrokerConsumersSqliteRepository(testBrokerConsumers()).use {
//                it.connection.executeQuery("""SELECT * FROM pragma_journal_mode""") { resultSet ->
//                    assertThat(resultSet.getString("journal_mode"), equalTo("wal"))
//                }
//            }
//        }
//
//        @Test
//        fun `Sets locking_mode to exclusive`() {
//            BrokerConsumersSqliteRepository(testBrokerConsumers()).use {
//                it.connection.executeQuery("""SELECT * FROM pragma_locking_mode""") { resultSet ->
//                    assertThat(resultSet.getString("locking_mode"), equalTo("exclusive"))
//                }
//            }
//        }
//
//        @Test
//        fun `Sets temp_store to memory`() {
//            BrokerConsumersSqliteRepository(testBrokerConsumers()).use {
//                it.connection.executeQuery("""SELECT * FROM pragma_temp_store""") { resultSet ->
//                    assertThat(resultSet.getString("temp_store"), equalTo("2"))
//                }
//            }
//        }
//
//        @Test
//        fun `Sets synchronous to off`() {
//            BrokerConsumersSqliteRepository(testBrokerConsumers()).use {
//                it.connection.executeQuery("""SELECT * FROM pragma_synchronous""") { resultSet ->
//                    assertThat(resultSet.getString("synchronous"), equalTo("0"))
//                }
//            }
//        }
//
//        private fun testBrokerConsumers(path: Path = Path(testDir)) = BrokerConsumersSqlite(path)
//    }
//
//    private val testDir = "/tmp/hamal/test/broker-consumers"
//}