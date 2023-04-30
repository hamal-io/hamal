package io.hamal.lib.log.broker

import io.hamal.lib.log.topic.Topic
import io.hamal.lib.util.Files
import io.hamal.lib.util.TimeUtils
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.io.path.Path

data class BrokerTopics(
    val brokerId: Broker.Id,
    val path: Path
)

internal class BrokerTopicsRepository private constructor(
    internal val brokerTopics: BrokerTopics,
    internal val lock: Lock,
    internal val connection: Connection
) : AutoCloseable {

    private val topicMapping = io.hamal.lib.KeyedOnce.default<Topic.Name, Topic>()

    companion object {
        fun open(brokerTopics: BrokerTopics): BrokerTopicsRepository {
            val dbPath = ensureDirectoryExists(brokerTopics)
            val result = BrokerTopicsRepository(
                brokerTopics = brokerTopics,
                lock = ReentrantLock(),
                connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
            )
            result.connection.autoCommit = false
            result.setupSqlite()
            result.setupSchema()

            return result
        }

        private fun ensureDirectoryExists(brokerTopics: BrokerTopics): Path {
            return Files.createDirectories(brokerTopics.path)
                .resolve(Path("topics.db"))
        }
    }

    fun resolveTopic(name: Topic.Name): Topic {
        return topicMapping.invoke(name) {
            lock.withLock {
                val id = findTopicId(name) ?: createTopic(name)
                Topic(
                    id = id,
                    brokerId = brokerTopics.brokerId,
                    name = name,
                    path = brokerTopics.path
                )
            }
        }
    }

    fun count() = this.executeQuery("SELECT COUNT(*) from topics") { it.getLong(1).toULong() }

    override fun close() {
        connection.close()
    }
}

internal fun <T> BrokerTopicsRepository.executeQuery(sql: String, fn: (ResultSet) -> T): T {
    require(!connection.isClosed) { "Connection must be open" }
    return connection.createStatement().use { statement ->
        statement.executeQuery(sql).use(fn)
    }
}

internal fun BrokerTopicsRepository.clear() {
    lock.withLock {
        connection.createStatement().use {
            it.execute("DELETE FROM topics")
            it.execute("DELETE FROM sqlite_sequence")
        }
        connection.commit()
    }
}

private fun BrokerTopicsRepository.setupSchema() {
    lock.withLock {
        connection.createStatement().use {
            it.execute(
                """
         CREATE TABLE IF NOT EXISTS topics (
            id INTEGER PRIMARY KEY AUTOINCREMENT ,
            name TEXT NOT NULL ,
            instant DATETIME NOT NULL,
            UNIQUE(name)
        );
        """.trimIndent()
            )
        }
        connection.commit()
    }
}

private fun BrokerTopicsRepository.setupSqlite() {
    lock.withLock {
        connection.createStatement().use {
            it.execute("""PRAGMA locking_mode = exclusive;""")
            it.execute("""PRAGMA temp_store = memory;""")
        }
        connection.commit()
    }
}

private fun BrokerTopicsRepository.findTopicId(name: Topic.Name): Topic.Id? {
    return connection.prepareStatement(
        """SELECT id FROM topics WHERE name = ?"""
    ).use {
        it.setString(1, name.value)
        val resultSet = it.executeQuery()
        if (!resultSet.next()) {
            null
        } else {
            Topic.Id(resultSet.getInt(1))
        }
    }
}

private fun BrokerTopicsRepository.createTopic(name: Topic.Name): Topic.Id {
    return connection.prepareStatement(
        """INSERT INTO topics(name, instant) VALUES(?,?)""",
    ).use {
        it.setString(1, name.value)
        it.setTimestamp(2, Timestamp.from(TimeUtils.now()))
        it.execute()

        executeQuery("select last_insert_rowid();") { rs ->
            Topic.Id(rs.getLong(1).toULong())
        }
    }
}