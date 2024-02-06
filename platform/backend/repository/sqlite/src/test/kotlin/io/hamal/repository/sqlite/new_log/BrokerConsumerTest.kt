package io.hamal.repository.sqlite.new_log

import io.hamal.lib.common.util.FileUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.pathString


class LogBrokerConsumerSqliteRepositoryTest {

    @Nested
    inner class ConstructorTest {

        @BeforeEach
        fun setup() {
            FileUtils.delete(testDir)
            FileUtils.createDirectories(testDir)
        }

        @Test
        fun `Creates a directory if path does not exists yet`() {
            val targetDir = testDir.resolve(Path("some-path", "another-path"))
            LogBrokerConsumersSqliteRepository(targetDir).use { }

            assertTrue(FileUtils.exists(targetDir))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "log-broker-consumers.db")))
        }

        @Test
        fun `Creates consumers table`() {
            LogBrokerConsumersSqliteRepository(testDir).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM sqlite_master WHERE name = 'consumers' AND type = 'table'") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Does not create consumers table if already exists`() {
            LogBrokerConsumersSqliteRepository(testDir).use {
                it.connection.execute(
                    """INSERT INTO consumers (group_id,topic_id,next_event_id) VALUES ('some-consumer-id',1234,4321);"""
                )
            }

            LogBrokerConsumersSqliteRepository(testDir).use { }
            LogBrokerConsumersSqliteRepository(testDir).use { }
            LogBrokerConsumersSqliteRepository(testDir).use { }
            LogBrokerConsumersSqliteRepository(testDir).use { }

            LogBrokerConsumersSqliteRepository(testDir).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM consumers") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Sets journal_mode to wal`() {
            LogBrokerConsumersSqliteRepository(testDir).use {
                it.connection.executeQuery("""SELECT * FROM pragma_journal_mode""") { resultSet ->
                    assertThat(resultSet.getString("journal_mode"), equalTo("wal"))
                }
            }
        }

        @Test
        fun `Sets locking_mode to exclusive`() {
            LogBrokerConsumersSqliteRepository(testDir).use {
                it.connection.executeQuery("""SELECT * FROM pragma_locking_mode""") { resultSet ->
                    assertThat(resultSet.getString("locking_mode"), equalTo("exclusive"))
                }
            }
        }

        @Test
        fun `Sets temp_store to memory`() {
            LogBrokerConsumersSqliteRepository(testDir).use {
                it.connection.executeQuery("""SELECT * FROM pragma_temp_store""") { resultSet ->
                    assertThat(resultSet.getString("temp_store"), equalTo("2"))
                }
            }
        }

        @Test
        fun `Sets synchronous to off`() {
            LogBrokerConsumersSqliteRepository(testDir).use {
                it.connection.executeQuery("""SELECT * FROM pragma_synchronous""") { resultSet ->
                    assertThat(resultSet.getString("synchronous"), equalTo("0"))
                }
            }
        }
    }

    private val testDir = Path("/tmp/hamal/test/log-broker-consumer")
}