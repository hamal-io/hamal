package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.Broker
import io.hamal.backend.repository.api.log.Topic
import io.hamal.backend.repository.api.log.Topic.Name
import io.hamal.lib.util.Files
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString

@DisplayName("DefaultBrokerTopicsRepository")
class DefaultBrokerTopicsRepositoryTest {

    @Nested
    @DisplayName("constructor()")
    inner class ConstructorTest {
        @BeforeEach
        fun setup() {
            Files.delete(Path(testDir))
            Files.createDirectories(Path(testDir))
        }

        @Test
        fun `Creates a directory if path does not exists yet`() {
            val targetDir = Path(testDir, "some-path", "another-path")
            DefaultBrokerTopicsRepository(testBrokerTopics(targetDir)).use { }

            assertTrue(Files.exists(targetDir))
            assertTrue(Files.exists(Path(targetDir.pathString, "topics.db")))
        }

        @Test
        fun `Creates topics table`() {
            DefaultBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM sqlite_master WHERE name = 'topics' AND type = 'table'") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Does not create topics table if already exists`() {
            DefaultBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.execute("""INSERT INTO topics (name,instant) VALUES ('some-topic',unixepoch());""")
            }

            DefaultBrokerTopicsRepository(testBrokerTopics()).use { }
            DefaultBrokerTopicsRepository(testBrokerTopics()).use { }
            DefaultBrokerTopicsRepository(testBrokerTopics()).use { }
            DefaultBrokerTopicsRepository(testBrokerTopics()).use { }

            DefaultBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM topics") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Sets journal_mode to wal`() {
            DefaultBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_journal_mode""") { resultSet ->
                    assertThat(resultSet.getString("journal_mode"), equalTo("wal"))
                }
            }
        }

        @Test
        fun `Sets locking_mode to exclusive`() {
            DefaultBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_locking_mode""") { resultSet ->
                    assertThat(resultSet.getString("locking_mode"), equalTo("exclusive"))
                }
            }
        }

        @Test
        fun `Sets temp_store to memory`() {
            DefaultBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_temp_store""") { resultSet ->
                    assertThat(resultSet.getString("temp_store"), equalTo("2"))
                }
            }
        }

        @Test
        fun `Sets synchronous to off`() {
            DefaultBrokerTopicsRepository(testBrokerTopics()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_synchronous""") { resultSet ->
                    assertThat(resultSet.getString("synchronous"), equalTo("0"))
                }
            }
        }

        private fun testBrokerTopics(path: Path = Path(testDir)) = BrokerTopics(
            brokerId = Broker.Id(2810),
            path = path
        )
    }

    @Nested
    @DisplayName("resolveTopic()")
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
            val result = testInstance.resolveTopic(Name("very-first-topic"))
            assertThat(result.id, equalTo(Topic.Id(1)))
            assertThat(result.brokerId, equalTo(Broker.Id(345)))
            assertThat(result.name, equalTo(Name("very-first-topic")))
            assertThat(result.path, equalTo(Path(testDir)))

            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Bug - able to create realistic topic name`() {
            testInstance.resolveTopic(Name("very-first-topic"))

            val result = testInstance.resolveTopic(Name("job_definition::created"))
            assertThat(result.id, equalTo(Topic.Id(2)))
            assertThat(result.brokerId, equalTo(Broker.Id(345)))
            assertThat(result.name, equalTo(Name("job_definition::created")))
            assertThat(result.path, equalTo(Path(testDir)))

            assertThat(testInstance.count(), equalTo(2UL))
        }

        @Test
        fun `Does not creat a new entry if topic already exists`() {
            testInstance.resolveTopic(Name("yet-another-topic"))
            testInstance.resolveTopic(Name("another-topic"))

            testInstance.resolveTopic(Name("some-topic"))

            testInstance.resolveTopic(Name("some-more-topic"))
            testInstance.resolveTopic(Name("some-more-mor-topic"))


            val result = testInstance.resolveTopic(Name("some-topic"))
            assertThat(result.id, equalTo(Topic.Id(3)))
            assertThat(result.brokerId, equalTo(Broker.Id(345)))
            assertThat(result.name, equalTo(Name("some-topic")))
            assertThat(result.path, equalTo(Path(testDir)))

            assertThat(testInstance.count(), equalTo(5UL))
        }

        private val testInstance = DefaultBrokerTopicsRepository(
            BrokerTopics(
                brokerId = Broker.Id(345),
                path = Path(testDir),
            )
        )
    }

    private val testDir = "/tmp/hamal/test/broker-topics"
}