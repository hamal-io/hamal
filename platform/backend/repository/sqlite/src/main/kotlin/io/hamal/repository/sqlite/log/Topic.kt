//package io.hamal.repository.sqlite.log
//
//import io.hamal.lib.common.domain.CmdId
//import io.hamal.lib.sqlite.Connection
//import io.hamal.lib.sqlite.SqliteBaseRepository
//import io.hamal.repository.api.log.*
//import java.nio.file.Path
//
//// FIXME just a pass through for now - replace with proper implementation,
//// like supporting multiple partitions, partitioning by key
//// keeping track of consumer group ids
//
//
//// FIXME just a pass through for now - replace with proper implementation,
//// like supporting multiple segments, roll over etc
//
//class TopicSqliteRepository(
//    internal val topic: DepTopic,
//    internal val path: Path
//) : SqliteBaseRepository(
//    object : Config {
//        override val path: Path get() = path
//        override val filename: String get() = String.format("topics/%08d", topic.id.value.value)
//
//    }
//), DepTopicRepository {
//
//    private var activeSegment: SegmentSqlite
//    private var activeSegmentRepository: SegmentSqliteRepository
//
//    init {
//        activeSegment = SegmentSqlite(
//            Segment.Id(0),
//            path = path.resolve(config.filename),
//            topicId = topic.id
//        )
//        activeSegmentRepository = SegmentSqliteRepository(activeSegment)
//    }
//
//    override fun append(cmdId: CmdId, bytes: ByteArray) {
//        activeSegmentRepository.append(cmdId, bytes)
//    }
//
//    override fun read(firstId: ChunkId, limit: Int): List<Chunk> {
//        return activeSegmentRepository.read(firstId, limit)
//    }
//
//    override fun count(): ULong {
//        return activeSegmentRepository.count()
//    }
//
//    override fun setupConnection(connection: Connection) {}
//
//    override fun setupSchema(connection: Connection) {}
//
//    override fun clear() {
//        activeSegmentRepository.clear()
//    }
//
//    override fun close() {
//        activeSegmentRepository.close()
//    }
//}
