package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.Broker
import io.hamal.backend.repository.api.log.Chunk
import io.hamal.backend.repository.api.log.Consumer.*
import io.hamal.backend.repository.api.log.Topic
import io.hamal.lib.domain.util.Files
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString

@DisplayName("DefaultBrokerConsumersRepository")
class DefaultBrokerConsumersRepositoryTest {

    @Nested
    @DisplayName("Constructor()")
    inner class ConstructorTest {

        @BeforeEach
        fun setup() {
            Files.delete(Path(testDir))
            Files.createDirectories(Path(testDir))
        }

        @Test
        fun `Creates a directory if path does not exists yet`() {
            val targetDir = Path(testDir, "some-path", "another-path")
            DefaultBrokerConsumersRepository(testBrokerConsumers(targetDir)).use { }

            assertTrue(Files.exists(targetDir))
            assertTrue(Files.exists(Path(targetDir.pathString, "consumers.db")))
        }

        @Test
        fun `Creates consumers table`() {
            DefaultBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM sqlite_master WHERE name = 'consumers' AND type = 'table'") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Does not create consumers table if already exists`() {
            DefaultBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.execute(
                    """INSERT INTO consumers (group_id,topic_id,next_chunk_id) VALUES ('some-group-id',1234,4321);"""
                )
            }

            DefaultBrokerConsumersRepository(testBrokerConsumers()).use { }
            DefaultBrokerConsumersRepository(testBrokerConsumers()).use { }
            DefaultBrokerConsumersRepository(testBrokerConsumers()).use { }
            DefaultBrokerConsumersRepository(testBrokerConsumers()).use { }

            DefaultBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM consumers") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Sets journal_mode to wal`() {
            DefaultBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_journal_mode""") { resultSet ->
                    assertThat(resultSet.getString("journal_mode"), equalTo("wal"))
                }
            }
        }

        @Test
        fun `Sets locking_mode to exclusive`() {
            DefaultBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_locking_mode""") { resultSet ->
                    assertThat(resultSet.getString("locking_mode"), equalTo("exclusive"))
                }
            }
        }

        @Test
        fun `Sets temp_store to memory`() {
            DefaultBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_temp_store""") { resultSet ->
                    assertThat(resultSet.getString("temp_store"), equalTo("2"))
                }
            }
        }

        @Test
        fun `Sets synchronous to off`() {
            DefaultBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_synchronous""") { resultSet ->
                    assertThat(resultSet.getString("synchronous"), equalTo("0"))
                }
            }
        }

        private fun testBrokerConsumers(path: Path = Path(testDir)) = BrokerConsumers(
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
            val result = testInstance.nextChunkId(GroupId("some-group-id"), Topic.Id(42))
            assertThat(result, equalTo(Chunk.Id(0)))
            assertThat(testInstance.count(), equalTo(0UL))
        }

        @Test
        fun `Next chunk id - is last committed chunk id plus 1`() {
            testInstance.commit(GroupId("some-group-id"), Topic.Id(1), Chunk.Id(127))
            val result = testInstance.nextChunkId(GroupId("some-group-id"), Topic.Id(1))
            assertThat(result, equalTo(Chunk.Id(128)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not return next chunk id of different topic`() {
            testInstance.commit(GroupId("some-group-id"), Topic.Id(1), Chunk.Id(127))
            val result = testInstance.nextChunkId(GroupId("some-group-id"), Topic.Id(2))
            assertThat(result, equalTo(Chunk.Id(0)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not return next chunk id of different group`() {
            testInstance.commit(GroupId("some-group-id"), Topic.Id(1), Chunk.Id(127))
            val result = testInstance.nextChunkId(GroupId("different-group-id"), Topic.Id(1))
            assertThat(result, equalTo(Chunk.Id(0)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        private val testInstance = DefaultBrokerConsumersRepository(
            BrokerConsumers(
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
            testInstance.commit(GroupId("some-group"), Topic.Id(123), Chunk.Id(23))
            assertThat(testInstance.count(), equalTo(1UL))

            testInstance.connection.executeQuery("SELECT group_id, topic_id, next_chunk_id FROM consumers") { resultSet ->
                assertThat(resultSet.getString("group_id"), equalTo("some-group"))
                assertThat(resultSet.getLong("topic_id"), equalTo(123L))
                assertThat(resultSet.getLong("next_chunk_id"), equalTo(24))
            }
        }

        @Test
        fun `Committed before`() {
            testInstance.commit(GroupId("some-group"), Topic.Id(123), Chunk.Id(23))

            testInstance.commit(GroupId("some-group"), Topic.Id(123), Chunk.Id(1337))
            assertThat(testInstance.count(), equalTo(1UL))

            testInstance.connection.executeQuery("SELECT group_id, topic_id, next_chunk_id FROM consumers") { resultSet ->
                assertThat(resultSet.getString("group_id"), equalTo("some-group"))
                assertThat(resultSet.getLong("topic_id"), equalTo(123L))
                assertThat(resultSet.getLong("next_chunk_id"), equalTo(1338))
            }
        }

        @Test
        fun `Does not overwrite different topic id `() {
            testInstance.commit(GroupId("some-group"), Topic.Id(23), Chunk.Id(1))
            testInstance.commit(GroupId("some-group"), Topic.Id(34), Chunk.Id(2))

            assertThat(testInstance.count(), equalTo(2UL))
            testInstance.connection.executeQuery("SELECT group_id FROM consumers") { resultSet ->
                assertThat(resultSet.getString("group_id"), equalTo("some-group"))
            }
        }

        @Test
        fun `Does not overwrite different group id`() {
            testInstance.commit(GroupId("some-group"), Topic.Id(23), Chunk.Id(1))
            testInstance.commit(GroupId("another-group"), Topic.Id(23), Chunk.Id(2))

            assertThat(testInstance.count(), equalTo(2UL))
            testInstance.connection.executeQuery("SELECT topic_id FROM consumers") { resultSet ->
                assertThat(resultSet.getLong("topic_id"), equalTo(23L))
            }
        }


        private val testInstance = DefaultBrokerConsumersRepository(
            BrokerConsumers(
                brokerId = Broker.Id(345),
                path = Path(testDir)
            )
        )
    }

    private val testDir = "/tmp/hamal/test/broker-consumers"

}