package io.hamal.lib.log.broker

import io.hamal.lib.log.consumer.DepConsumer
import io.hamal.lib.log.segment.Chunk
import io.hamal.lib.log.topic.Topic
import io.hamal.lib.util.Files
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString

@DisplayName("BrokerConsumersRepository")
class BrokerConsumersRepositoryTest {

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
            DepBrokerConsumersRepository.open(testBrokerConsumers(targetDir)).use { }

            assertTrue(Files.exists(targetDir))
            assertTrue(Files.exists(Path(targetDir.pathString, "consumers.db")))
        }

        @Test
        fun `Creates consumers table`() {
            DepBrokerConsumersRepository.open(testBrokerConsumers()).use {
                it.executeQuery("SELECT COUNT(*) FROM sqlite_master WHERE name = 'consumers' AND type = 'table'") { resultSet ->
                    assertThat(resultSet.getInt(1), equalTo(1))
                }
            }
        }

        @Test
        fun `Does not create consumers table if already exists`() {
            DepBrokerConsumersRepository.open(testBrokerConsumers()).use {
                it.connection.createStatement().use { statement ->
                    statement.execute(
                        """
                        INSERT INTO consumers (group_id,topic_id,next_chunk_id) VALUES ('some-group-id',1234,4321);
                    """.trimIndent()
                    )
                }
                it.connection.commit()
            }

            DepBrokerConsumersRepository.open(testBrokerConsumers()).use { }
            DepBrokerConsumersRepository.open(testBrokerConsumers()).use { }
            DepBrokerConsumersRepository.open(testBrokerConsumers()).use { }
            DepBrokerConsumersRepository.open(testBrokerConsumers()).use { }

            DepBrokerConsumersRepository.open(testBrokerConsumers()).use {
                it.executeQuery("SELECT COUNT(*) FROM consumers") {
                    assertThat(it.getInt(1), equalTo(1))
                }
            }
        }

        @Test
        fun `Sets journal_mode to wal`() {
            DepBrokerConsumersRepository.open(testBrokerConsumers())
                .executeQuery("""SELECT * FROM pragma_journal_mode""") {
                    assertThat(it.getString(1), equalTo("wal"))
                }
        }

        @Test
        fun `Sets locking_mode to exclusive`() {
            DepBrokerConsumersRepository.open(testBrokerConsumers()).use {
                it.executeQuery("""SELECT * FROM pragma_locking_mode""") {
                    assertThat(it.getString(1), equalTo("exclusive"))
                }
            }
        }

        @Test
        fun `Sets temp_store to memory`() {
            DepBrokerConsumersRepository.open(testBrokerConsumers()).use {
                it.executeQuery("""SELECT * FROM pragma_temp_store""") {
                    assertThat(it.getString(1), equalTo("2"))
                }
            }
        }

        @Test
        fun `Sets synchronous to off`() {
            DepBrokerConsumersRepository.open(testBrokerConsumers())
                .executeQuery("""SELECT * FROM pragma_synchronous""") {
                    assertThat(it.getString(1), equalTo("0"))
                }
        }

        private fun testBrokerConsumers(path: Path = Path(testDir)) = DepBrokerConsumers(
            brokerId = Broker.Id(2810),
            path = path
        )

    }

    @Nested
    @DisplayName("nextChunkId()")
    inner class NextChunkIdTest {

        @BeforeEach
        fun setup() {
            testInstance.clear()
        }

        @AfterEach
        fun after() {
            testInstance.close()
        }

        @Test
        fun `Returns chunk id 0 if no entry exists for group id and topic id`() {
            val result = testInstance.nextChunkId(DepConsumer.GroupId("some-group-id"), Topic.Id(42))
            assertThat(result, equalTo(Chunk.Id(0)))
            assertThat(testInstance.count(), equalTo(0UL))
        }

        @Test
        fun `Next chunk id - is last committed chunk id plus 1`() {
            testInstance.commit(DepConsumer.GroupId("some-group-id"), Topic.Id(1), Chunk.Id(127))
            val result = testInstance.nextChunkId(DepConsumer.GroupId("some-group-id"), Topic.Id(1))
            assertThat(result, equalTo(Chunk.Id(128)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not return next chunk id of different topic`() {
            testInstance.commit(DepConsumer.GroupId("some-group-id"), Topic.Id(1), Chunk.Id(127))
            val result = testInstance.nextChunkId(DepConsumer.GroupId("some-group-id"), Topic.Id(2))
            assertThat(result, equalTo(Chunk.Id(0)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not return next chunk id of different group`() {
            testInstance.commit(DepConsumer.GroupId("some-group-id"), Topic.Id(1), Chunk.Id(127))
            val result = testInstance.nextChunkId(DepConsumer.GroupId("different-group-id"), Topic.Id(1))
            assertThat(result, equalTo(Chunk.Id(0)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        private val testInstance = DepBrokerConsumersRepository.open(
            DepBrokerConsumers(
                brokerId = Broker.Id(345),
                path = Path(testDir)
            )
        )
    }

    @Nested
    @DisplayName("commit()")
    inner class CommitTest {

        @BeforeEach
        fun setup() {
            testInstance.clear()
        }

        @AfterEach
        fun after() {
            testInstance.close()
        }

        @Test
        fun `Never committed before`() {
            testInstance.commit(DepConsumer.GroupId("some-group"), Topic.Id(123), Chunk.Id(23))
            assertThat(testInstance.count(), equalTo(1UL))

            testInstance.executeQuery("SELECT group_id, topic_id, next_chunk_id FROM consumers") { resultSet ->
                assertThat(resultSet.getString("group_id"), equalTo("some-group"))
                assertThat(resultSet.getLong("topic_id"), equalTo(123L))
                assertThat(resultSet.getLong("next_chunk_id"), equalTo(24))
            }
        }

        @Test
        fun `Committed before`() {
            testInstance.commit(DepConsumer.GroupId("some-group"), Topic.Id(123), Chunk.Id(23))

            testInstance.commit(DepConsumer.GroupId("some-group"), Topic.Id(123), Chunk.Id(1337))
            assertThat(testInstance.count(), equalTo(1UL))

            testInstance.executeQuery("SELECT group_id, topic_id, next_chunk_id FROM consumers") { resultSet ->
                assertThat(resultSet.getString("group_id"), equalTo("some-group"))
                assertThat(resultSet.getLong("topic_id"), equalTo(123L))
                assertThat(resultSet.getLong("next_chunk_id"), equalTo(1338))
            }
        }

        @Test
        fun `Does not overwrite different topic id `() {
            testInstance.commit(DepConsumer.GroupId("some-group"), Topic.Id(23), Chunk.Id(1))
            testInstance.commit(DepConsumer.GroupId("some-group"), Topic.Id(34), Chunk.Id(2))

            assertThat(testInstance.count(), equalTo(2UL))
            testInstance.executeQuery("SELECT group_id FROM consumers") { resultSet ->
                assertThat(resultSet.getString("group_id"), equalTo("some-group"))
            }
        }

        @Test
        fun `Does not overwrite different group id`() {
            testInstance.commit(DepConsumer.GroupId("some-group"), Topic.Id(23), Chunk.Id(1))
            testInstance.commit(DepConsumer.GroupId("another-group"), Topic.Id(23), Chunk.Id(2))

            assertThat(testInstance.count(), equalTo(2UL))
            testInstance.executeQuery("SELECT topic_id FROM consumers") { resultSet ->
                assertThat(resultSet.getLong("topic_id"), equalTo(23L))
            }
        }


        private val testInstance = DepBrokerConsumersRepository.open(
            DepBrokerConsumers(
                brokerId = Broker.Id(345),
                path = Path(testDir)
            )
        )
    }

    private val testDir = "/tmp/hamal/test/broker-consumers"
}