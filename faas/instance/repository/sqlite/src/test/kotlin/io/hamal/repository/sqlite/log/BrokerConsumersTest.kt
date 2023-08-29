package io.hamal.repository.sqlite.log

import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogChunkId
import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.domain.vo.TopicId
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


class SqliteLogBrokerConsumersRepositoryTest {
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
            SqliteLogBrokerConsumersRepository(testBrokerConsumers(targetDir)).use { }

            assertTrue(FileUtils.exists(targetDir))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "consumers.db")))
        }

        @Test
        fun `Creates consumers table`() {
            SqliteLogBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM sqlite_master WHERE name = 'consumers' AND type = 'table'") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Does not create consumers table if already exists`() {
            SqliteLogBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.execute(
                    """INSERT INTO consumers (group_id,topic_id,next_chunk_id) VALUES ('some-group-id',1234,4321);"""
                )
            }

            SqliteLogBrokerConsumersRepository(testBrokerConsumers()).use { }
            SqliteLogBrokerConsumersRepository(testBrokerConsumers()).use { }
            SqliteLogBrokerConsumersRepository(testBrokerConsumers()).use { }
            SqliteLogBrokerConsumersRepository(testBrokerConsumers()).use { }

            SqliteLogBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM consumers") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Sets journal_mode to wal`() {
            SqliteLogBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_journal_mode""") { resultSet ->
                    assertThat(resultSet.getString("journal_mode"), equalTo("wal"))
                }
            }
        }

        @Test
        fun `Sets locking_mode to exclusive`() {
            SqliteLogBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_locking_mode""") { resultSet ->
                    assertThat(resultSet.getString("locking_mode"), equalTo("exclusive"))
                }
            }
        }

        @Test
        fun `Sets temp_store to memory`() {
            SqliteLogBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_temp_store""") { resultSet ->
                    assertThat(resultSet.getString("temp_store"), equalTo("2"))
                }
            }
        }

        @Test
        fun `Sets synchronous to off`() {
            SqliteLogBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_synchronous""") { resultSet ->
                    assertThat(resultSet.getString("synchronous"), equalTo("0"))
                }
            }
        }

        private fun testBrokerConsumers(path: Path = Path(testDir)) = SqliteBrokerConsumers(path)
    }

    @Nested
    inner class NextChunkTopicIdTest {

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
            val result = testInstance.nextChunkId(GroupId("some-group-id"), TopicId(42))
            assertThat(result, equalTo(LogChunkId(0)))
            assertThat(testInstance.count(), equalTo(0UL))
        }

        @Test
        fun `Next chunk id - is last committed chunk id plus 1`() {
            testInstance.commit(GroupId("some-group-id"), TopicId(1), LogChunkId(127))
            val result = testInstance.nextChunkId(GroupId("some-group-id"), TopicId(1))
            assertThat(result, equalTo(LogChunkId(128)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not return next chunk id of different topic`() {
            testInstance.commit(GroupId("some-group-id"), TopicId(1), LogChunkId(127))
            val result = testInstance.nextChunkId(GroupId("some-group-id"), TopicId(2))
            assertThat(result, equalTo(LogChunkId(0)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not return next chunk id of different group`() {
            testInstance.commit(GroupId("some-group-id"), TopicId(1), LogChunkId(127))
            val result = testInstance.nextChunkId(GroupId("different-group-id"), TopicId(1))
            assertThat(result, equalTo(LogChunkId(0)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        private val testInstance = SqliteLogBrokerConsumersRepository(
            SqliteBrokerConsumers(Path(testDir))
        )
    }

    @Nested
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
            testInstance.commit(GroupId("some-group"), TopicId(123), LogChunkId(23))
            assertThat(testInstance.count(), equalTo(1UL))

            testInstance.connection.executeQuery("SELECT group_id, topic_id, next_chunk_id FROM consumers") { resultSet ->
                assertThat(resultSet.getString("group_id"), equalTo("some-group"))
                assertThat(resultSet.getLong("topic_id"), equalTo(123L))
                assertThat(resultSet.getLong("next_chunk_id"), equalTo(24))
            }
        }

        @Test
        fun `Committed before`() {
            testInstance.commit(GroupId("some-group"), TopicId(123), LogChunkId(23))

            testInstance.commit(GroupId("some-group"), TopicId(123), LogChunkId(1337))
            assertThat(testInstance.count(), equalTo(1UL))

            testInstance.connection.executeQuery("SELECT group_id, topic_id, next_chunk_id FROM consumers") { resultSet ->
                assertThat(resultSet.getString("group_id"), equalTo("some-group"))
                assertThat(resultSet.getLong("topic_id"), equalTo(123L))
                assertThat(resultSet.getLong("next_chunk_id"), equalTo(1338))
            }
        }

        @Test
        fun `Does not overwrite different topic id `() {
            testInstance.commit(GroupId("some-group"), TopicId(23), LogChunkId(1))
            testInstance.commit(GroupId("some-group"), TopicId(34), LogChunkId(2))

            assertThat(testInstance.count(), equalTo(2UL))
            testInstance.connection.executeQuery("SELECT group_id FROM consumers") { resultSet ->
                assertThat(resultSet.getString("group_id"), equalTo("some-group"))
            }
        }

        @Test
        fun `Does not overwrite different group id`() {
            testInstance.commit(GroupId("some-group"), TopicId(23), LogChunkId(1))
            testInstance.commit(GroupId("another-group"), TopicId(23), LogChunkId(2))

            assertThat(testInstance.count(), equalTo(2UL))
            testInstance.connection.executeQuery("SELECT topic_id FROM consumers") { resultSet ->
                assertThat(resultSet.getLong("topic_id"), equalTo(23L))
            }
        }


        private val testInstance = SqliteLogBrokerConsumersRepository(
            SqliteBrokerConsumers(Path(testDir))
        )
    }

    private val testDir = "/tmp/hamal/test/broker-consumers"

}