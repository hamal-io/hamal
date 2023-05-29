package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.LogBroker
import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString


class DefaultLogBrokerTopicsRepositoryTest {
    @Nested
    inner class ConstructorTest {
        @BeforeEach
        fun setup() {
            FileUtils.delete(Path(testDir))
            FileUtils.createDirectories(Path(testDir))
        }

        @Test
        fun `Creates a directory if path does not exists yet`() {
            val targetDir = Path(testDir, "some-path", "another-path")
            DefaultLogBrokerTopicsRepository(testBrokerTopics(targetDir)).use { }

            assertTrue(FileUtils.exists(targetDir))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "topics.db")))
        }

        @Test
        fun `Creates topics table`() {
            DefaultLogBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM sqlite_master WHERE name = 'topics' AND type = 'table'") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Does not create topics table if already exists`() {
            DefaultLogBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.execute("""INSERT INTO topics (name,instant) VALUES ('some-topic',unixepoch());""")
            }

            DefaultLogBrokerTopicsRepository(testBrokerTopics()).use { }
            DefaultLogBrokerTopicsRepository(testBrokerTopics()).use { }
            DefaultLogBrokerTopicsRepository(testBrokerTopics()).use { }
            DefaultLogBrokerTopicsRepository(testBrokerTopics()).use { }

            DefaultLogBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM topics") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Sets journal_mode to wal`() {
            DefaultLogBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_journal_mode""") { resultSet ->
                    assertThat(resultSet.getString("journal_mode"), equalTo("wal"))
                }
            }
        }

        @Test
        fun `Sets locking_mode to exclusive`() {
            DefaultLogBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_locking_mode""") { resultSet ->
                    assertThat(resultSet.getString("locking_mode"), equalTo("exclusive"))
                }
            }
        }

        @Test
        fun `Sets temp_store to memory`() {
            DefaultLogBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_temp_store""") { resultSet ->
                    assertThat(resultSet.getString("temp_store"), equalTo("2"))
                }
            }
        }

        @Test
        fun `Sets synchronous to off`() {
            DefaultLogBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_synchronous""") { resultSet ->
                    assertThat(resultSet.getString("synchronous"), equalTo("0"))
                }
            }
        }

        private fun testBrokerTopics(path: Path = Path(testDir)) = BrokerTopics(
            logBrokerId = LogBroker.Id(2810),
            path = path
        )
    }

    @Nested
    inner class ResolveTopicTest {
        @BeforeEach
        fun setup() {
            testInstance.clear()
        }

        @AfterEach
        fun after() {
            testInstance.close()
        }

        @Test
        fun `Creates a new entry if topic does not exists`() {
            val result = testInstance.resolveTopic(TopicName("very-first-topic"))
            assertThat(result.id, equalTo(TopicId(1)))
            assertThat(result.logBrokerId, equalTo(LogBroker.Id(345)))
            assertThat(result.name, equalTo(TopicName("very-first-topic")))
            assertThat(result.path, equalTo(Path(testDir)))

            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Bug - able to create realistic topic name`() {
            testInstance.resolveTopic(TopicName("very-first-topic"))

            val result = testInstance.resolveTopic(TopicName("func::created"))
            assertThat(result.id, equalTo(TopicId(2)))
            assertThat(result.logBrokerId, equalTo(LogBroker.Id(345)))
            assertThat(result.name, equalTo(TopicName("func::created")))
            assertThat(result.path, equalTo(Path(testDir)))

            assertThat(testInstance.count(), equalTo(2UL))
        }

        @Test
        fun `Does not creat a new entry if topic already exists`() {
            testInstance.resolveTopic(TopicName("yet-another-topic"))
            testInstance.resolveTopic(TopicName("another-topic"))

            testInstance.resolveTopic(TopicName("some-topic"))

            testInstance.resolveTopic(TopicName("some-more-topic"))
            testInstance.resolveTopic(TopicName("some-more-mor-topic"))


            val result = testInstance.resolveTopic(TopicName("some-topic"))
            assertThat(result.id, equalTo(TopicId(3)))
            assertThat(result.logBrokerId, equalTo(LogBroker.Id(345)))
            assertThat(result.name, equalTo(TopicName("some-topic")))
            assertThat(result.path, equalTo(Path(testDir)))

            assertThat(testInstance.count(), equalTo(5UL))
        }

        private val testInstance = DefaultLogBrokerTopicsRepository(
            BrokerTopics(
                logBrokerId = LogBroker.Id(345),
                path = Path(testDir),
            )
        )
    }

    private val testDir = "/tmp/hamal/test/broker-topics"
}