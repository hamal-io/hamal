package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.*
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.CmdId
import java.nio.file.Path


// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple partitions, sharding by key
// keeping track of consumer group ids
class DefaultLogTopicRepository(
    internal val topic: LogTopic
) : BaseRepository(
    object : Config {
        override val path: Path get() = topic.path
        override val filename: String get() = String.format("topic-%08d", topic.id.value.toLong())
        override val shard: Shard get() = topic.shard

    }
), LogTopicRepository {

    internal var activeLogShard: LogShard
    internal var activeLogShardRepository: LogShardRepository

    init {
        activeLogShard = LogShard(
            id = topic.shard,
            topicId = topic.id,
            path = topic.path.resolve(config.filename),
        )
        activeLogShardRepository = DefaultLogShardRepository(activeLogShard)
    }

    override fun setupConnection(connection: Connection) {}

    override fun setupSchema(connection: Connection) {}

    override fun append(cmdId: CmdId, bytes: ByteArray) {
        activeLogShardRepository.append(cmdId, bytes)
    }

    override fun read(firstId: LogChunkId, limit: Int): List<LogChunk> {
        return activeLogShardRepository.read(firstId, limit)
    }

    override fun count(): ULong {
        return activeLogShardRepository.count()
    }

    override fun clear() {
        activeLogShardRepository.clear()
    }

    override fun close() {
        activeLogShardRepository.close()
    }

}
