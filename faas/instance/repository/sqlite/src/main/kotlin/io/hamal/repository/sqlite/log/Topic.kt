package io.hamal.repository.sqlite.log

import io.hamal.repository.api.log.*
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.lib.sqlite.Connection
import java.nio.file.Path

// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple partitions, partitioning by key
// keeping track of consumer group ids


// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple segments, roll over etc

class SqliteLogTopicRepository(
    internal val topic: LogTopic,
    internal val path: Path
) : BaseSqliteRepository(
    object : Config {
        override val path: Path get() = path
        override val filename: String get() = String.format("topics/%08d", topic.id.value.value)

    }
), LogTopicRepository {

    private var activeSegment: SqliteLogSegment
    private var activeLogSegmentRepository: SqliteLogSegmentRepository

    init {
        activeSegment = SqliteLogSegment(
            LogSegment.Id(0),
            path = path.resolve(config.filename),
            topicId = topic.id
        )
        activeLogSegmentRepository = SqliteLogSegmentRepository(activeSegment)
    }

    override fun append(cmdId: CmdId, bytes: ByteArray) {
        activeLogSegmentRepository.append(cmdId, bytes)
    }

    override fun read(firstId: LogChunkId, limit: Int): List<LogChunk> {
        return activeLogSegmentRepository.read(firstId, limit)
    }

    override fun count(): ULong {
        return activeLogSegmentRepository.count()
    }

    override fun setupConnection(connection: Connection) {}

    override fun setupSchema(connection: Connection) {}

    override fun clear() {
        activeLogSegmentRepository.clear()
    }

    override fun close() {
        activeLogSegmentRepository.close()
    }
}
