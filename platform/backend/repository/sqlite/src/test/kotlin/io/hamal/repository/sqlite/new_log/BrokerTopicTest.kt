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


class LogBrokerTopicSqliteRepositoryTest {

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
            LogBrokerTopicsSqliteRepository(targetDir).use { }

            assertTrue(FileUtils.exists(targetDir))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "log-broker-topics.db")))
        }

        @Test
        fun `Creates topics table`() {
            LogBrokerTopicsSqliteRepository(testDir).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM sqlite_master WHERE name = 'topics' AND type = 'table'") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Does not create topics table if already exists`() {
            LogBrokerTopicsSqliteRepository(testDir).use {
                it.connection.execute("""INSERT INTO topics (flow_id, group_id, name,instant) VALUES (1, 1,'some-topic',unixepoch());""")
            }

            LogBrokerTopicsSqliteRepository(testDir).use { }
            LogBrokerTopicsSqliteRepository(testDir).use { }
            LogBrokerTopicsSqliteRepository(testDir).use { }
            LogBrokerTopicsSqliteRepository(testDir).use { }

            LogBrokerTopicsSqliteRepository(testDir).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM topics") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Sets journal_mode to wal`() {
            LogBrokerTopicsSqliteRepository(testDir).use {
                it.connection.executeQuery("""SELECT * FROM pragma_journal_mode""") { resultSet ->
                    assertThat(resultSet.getString("journal_mode"), equalTo("wal"))
                }
            }
        }

        @Test
        fun `Sets locking_mode to exclusive`() {
            LogBrokerTopicsSqliteRepository(testDir).use {
                it.connection.executeQuery("""SELECT * FROM pragma_locking_mode""") { resultSet ->
                    assertThat(resultSet.getString("locking_mode"), equalTo("exclusive"))
                }
            }
        }

        @Test
        fun `Sets temp_store to memory`() {
            LogBrokerTopicsSqliteRepository(testDir).use {
                it.connection.executeQuery("""SELECT * FROM pragma_temp_store""") { resultSet ->
                    assertThat(resultSet.getString("temp_store"), equalTo("2"))
                }
            }
        }

        @Test
        fun `Sets synchronous to off`() {
            LogBrokerTopicsSqliteRepository(testDir).use {
                it.connection.executeQuery("""SELECT * FROM pragma_synchronous""") { resultSet ->
                    assertThat(resultSet.getString("synchronous"), equalTo("0"))
                }
            }
        }

    }

    private val testDir = Path("/tmp/hamal/test/broker-topics")
}