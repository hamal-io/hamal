package io.hamal.backend.repository.impl.log

import io.hamal.lib.util.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.io.path.Path

data class BrokerConsumers(
    val brokerId: Broker.Id,
    val path: Path
)

internal class BrokerConsumersRepository private constructor(
    internal val brokerConsumers: BrokerConsumers,
    internal val lock: Lock,
    internal val connection: Connection
) : AutoCloseable {
    companion object {
        fun open(brokerConsumers: BrokerConsumers): BrokerConsumersRepository {
            val dbPath = ensureDirectoryExists(brokerConsumers)
            val result = BrokerConsumersRepository(
                brokerConsumers = brokerConsumers,
                lock = ReentrantLock(),
                connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
            )
            result.setupSqlite()
            result.connection.autoCommit = false
            result.setupSchema()

            return result
        }

        private fun ensureDirectoryExists(brokerConsumers: BrokerConsumers): Path {
            return Files.createDirectories(brokerConsumers.path)
                .resolve(Path("consumers.db"))
        }
    }

    fun nextChunkId(groupId: Consumer.GroupId, topicId: Topic.Id): Chunk.Id {
        return lock.withLock {
            connection.prepareStatement(
                """SELECT next_chunk_id FROM consumers WHERE group_id = ? and topic_id = ?""",
            ).use {
                it.setString(1, groupId.value)
                it.setLong(2, topicId.value.toLong())
                val resultSet = it.executeQuery()
                if (!resultSet.next()) {
                    Chunk.Id(0)
                } else {
                    Chunk.Id(resultSet.getInt(1))
                }
            }
        }
    }

    fun commit(groupId: Consumer.GroupId, topicId: Topic.Id, chunkId: Chunk.Id) {
        return lock.withLock {
            connection.prepareStatement(
                """
            INSERT INTO consumers(group_id, topic_id, next_chunk_id)
                  VALUES(?, ?, ?)
                     ON CONFLICT(group_id, topic_id) DO UPDATE SET
                        next_chunk_id=excluded.next_chunk_id
                        WHERE 
                            excluded.group_id  == consumers.group_id AND
                            excluded.topic_id  == consumers.topic_id;
                    """.trimIndent(),
            ).use {
                it.setString(1, groupId.value)
                it.setLong(2, topicId.value.toLong())
                it.setLong(3, chunkId.value.toLong() + 1)
                it.execute()
            }
            connection.commit()
        }
    }

    override fun close() {
        connection.close()
    }


    fun count() = this.executeQuery("SELECT COUNT(*) from consumers") { it.getLong(1).toULong() }
}

internal fun <T> BrokerConsumersRepository.executeQuery(sql: String, fn: (ResultSet) -> T): T {
    require(!connection.isClosed) { "Connection must be open" }
    return connection.createStatement().use { statement ->
        statement.executeQuery(sql).use(fn)
    }
}

internal fun BrokerConsumersRepository.clear() {
    lock.withLock {
        connection.createStatement().use {
            it.execute("DELETE FROM consumers")
        }
        connection.commit()
    }
}

private fun BrokerConsumersRepository.setupSchema() {
    lock.withLock {
        connection.createStatement().use {
            it.execute(
                """
         CREATE TABLE IF NOT EXISTS consumers (
            group_id TEXT NOT NULL ,
            topic_id INTEGER NOT NULL ,
            next_chunk_id INTEGER NOT NULL ,
            PRIMARY KEY (topic_id,group_id)
        );
        """.trimIndent()
            )
        }
        connection.commit()
    }
}

private fun BrokerConsumersRepository.setupSqlite() {
    lock.withLock {
        connection.createStatement().use {
            it.execute("""PRAGMA journal_mode = wal;""")
            it.execute("""PRAGMA locking_mode = exclusive;""")
            it.execute("""PRAGMA temp_store = memory;""")
            it.execute("""PRAGMA synchronous = off;""")
        }
    }
}
