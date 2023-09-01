package io.hamal.repository.sqlite.log

import io.hamal.repository.api.log.BrokerTopicsRepository
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString


class SqliteLogBrokerTopicsRepositoryTest {

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
            SqliteBrokerTopicsRepository(testBrokerTopics(targetDir)).use { }

            assertTrue(FileUtils.exists(targetDir))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "topics.db")))
        }

        @Test
        fun `Creates topics table`() {
            SqliteBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM sqlite_master WHERE name = 'topics' AND type = 'table'") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Does not create topics table if already exists`() {
            SqliteBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.execute("""INSERT INTO topics (name,instant) VALUES ('some-topic',unixepoch());""")
            }

            SqliteBrokerTopicsRepository(testBrokerTopics()).use { }
            SqliteBrokerTopicsRepository(testBrokerTopics()).use { }
            SqliteBrokerTopicsRepository(testBrokerTopics()).use { }
            SqliteBrokerTopicsRepository(testBrokerTopics()).use { }

            SqliteBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM topics") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Sets journal_mode to wal`() {
            SqliteBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_journal_mode""") { resultSet ->
                    assertThat(resultSet.getString("journal_mode"), equalTo("wal"))
                }
            }
        }

        @Test
        fun `Sets locking_mode to exclusive`() {
            SqliteBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_locking_mode""") { resultSet ->
                    assertThat(resultSet.getString("locking_mode"), equalTo("exclusive"))
                }
            }
        }

        @Test
        fun `Sets temp_store to memory`() {
            SqliteBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_temp_store""") { resultSet ->
                    assertThat(resultSet.getString("temp_store"), equalTo("2"))
                }
            }
        }

        @Test
        fun `Sets synchronous to off`() {
            SqliteBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_synchronous""") { resultSet ->
                    assertThat(resultSet.getString("synchronous"), equalTo("0"))
                }
            }
        }

        private fun testBrokerTopics(path: Path = Path(testDir)) = SqliteBrokerTopics(path)
    }

    @Nested
    inner class CreateTopicTest {
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
            val result = testInstance.create(
                CmdId(1),
                BrokerTopicsRepository.TopicToCreate(
                    TopicId(1),
                    TopicName("very-first-topic")
                )
            )

            assertThat(result.id, equalTo(TopicId(1)))
            assertThat(result.name, equalTo(TopicName("very-first-topic")))

            assertThat(testInstance.count {}, equalTo(1UL))
        }

        @Test
        fun `Bug - able to create realistic topic name`() {
            testInstance.create(
                CmdId(1),
                BrokerTopicsRepository.TopicToCreate(
                    TopicId(1),
                    TopicName("very-first-topic")
                )
            )

            val result = testInstance.create(
                CmdId(2),
                BrokerTopicsRepository.TopicToCreate(
                    TopicId(2),
                    TopicName("func::created")
                )
            )
            assertThat(result.id, equalTo(TopicId(2)))
            assertThat(result.name, equalTo(TopicName("func::created")))

            assertThat(testInstance.count {}, equalTo(2UL))
        }

        @Test
        fun `Throws if topic already exists`() {
            testInstance.create(
                CmdId(1),
                BrokerTopicsRepository.TopicToCreate(
                    TopicId(1),
                    TopicName("very-first-topic")
                )
            )

            val throwable = assertThrows<IllegalArgumentException> {
                testInstance.create(
                    CmdId(2),
                    BrokerTopicsRepository.TopicToCreate(
                        TopicId(2),
                        TopicName("very-first-topic")
                    )
                )
            }
            assertThat(
                throwable.message,
                equalTo("Topic already exists")
            )

            assertThat(testInstance.count {}, equalTo(1UL))
        }

        private val testInstance = SqliteBrokerTopicsRepository(
            SqliteBrokerTopics(Path(testDir))
        )
    }

    private val testDir = "/tmp/hamal/test/broker-topics"
}