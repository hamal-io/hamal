package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.*
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.CommandId
import java.nio.file.Path


// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple segments, roll over etc

class DefaultLogShardRepository(
    internal val logShard: LogShard
) : BaseRepository(object : Config {
    override val path: Path get() = logShard.path
    override val filename: String get() = String.format("shard-%04d", logShard.id.value.toLong())
    override val shard: Shard get() = logShard.id

}), LogShardRepository {

    internal var activeSegment: LogSegment
    internal var activeLogSegmentRepository: LogSegmentRepository

    init {
        activeSegment = LogSegment(
            LogSegment.Id(0),
            shard = logShard.id,
            path = logShard.path.resolve(config.filename),
            topicId = logShard.topicId
        )
        activeLogSegmentRepository = DefaultLogSegmentRepository(activeSegment)
    }

    override fun append(commandId: CommandId, bytes: ByteArray) {
        activeLogSegmentRepository.append(commandId, bytes)
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