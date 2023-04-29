package io.hamal.lib.log.broker

import io.hamal.lib.log.topic.Topic
import io.hamal.lib.log.topic.Topic.Name
import io.hamal.lib.util.Files
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString

@DisplayName("BrokerTopicsRepository")
class BrokerTopicsRepositoryTest {

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
            val targetDir = Path(testDir, "some-path", "another-path")
            BrokerTopicsRepository.open(testBrokerTopics(targetDir)).use { }

            assertTrue(Files.exists(targetDir))
            assertTrue(Files.exists(Path(targetDir.pathString, "topics.db")))
        }

        @Test
        fun `Creates topics table`() {
            BrokerTopicsRepository.open(testBrokerTopics()).use {
                it.executeQueryMany("SELECT COUNT(*) FROM sqlite_master WHERE name = 'topics' AND type = 'table'") { resultSet ->
                    assertThat(resultSet.getInt(1), equalTo(1))
                }
            }
        }

        @Test
        fun `Does not create topics table if already exists`() {
            BrokerTopicsRepository.open(testBrokerTopics()).use {
                it.connection.createStatement().use { statement ->
                    statement.execute(
                        """
                        INSERT INTO topics (name,instant) VALUES ('some-topic',unixepoch());
                    """.trimIndent()
                    )
                }
                it.connection.commit()
            }

            BrokerTopicsRepository.open(testBrokerTopics()).use { }
            BrokerTopicsRepository.open(testBrokerTopics()).use { }
            BrokerTopicsRepository.open(testBrokerTopics()).use { }
            BrokerTopicsRepository.open(testBrokerTopics()).use { }

            BrokerTopicsRepository.open(testBrokerTopics()).use {
                it.executeQueryMany("SELECT COUNT(*) FROM topics") {
                    assertThat(it.getInt(1), equalTo(1))
                }
            }
        }

        @Test
        fun `Sets locking_mode to exclusive`() {
            BrokerTopicsRepository.open(testBrokerTopics()).use {
                it.executeQueryMany("""SELECT * FROM pragma_locking_mode""") {
                    assertThat(it.getString(1), equalTo("exclusive"))
                }
            }
        }

        @Test
        fun `Sets temp_store to memory`() {
            BrokerTopicsRepository.open(testBrokerTopics()).use {
                it.executeQueryMany("""SELECT * FROM pragma_temp_store""") {
                    assertThat(it.getString(1), equalTo("2"))
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

        private val testInstance = BrokerTopicsRepository.open(
            BrokerTopics(
                brokerId = Broker.Id(345),
                path = Path(testDir)
            )
        )
    }

    private val testDir = "/tmp/hamal/test/broker-topics"
}