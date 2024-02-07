package io.hamal.repository.sqlite.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.FileUtils
import io.hamal.repository.api.log.*
import java.nio.file.Path
import kotlin.io.path.Path


//
//// FIXME just a pass through for now - replace with proper implementation,
//// like supporting multiple partitions, partitioning by key
//// keeping track of consumer group ids
//
//
//// FIXME just a pass through for now - replace with proper implementation,
//// like supporting multiple segments, roll over etc
//
class LogTopicSqliteRepository(
    internal val topic: LogTopic,
    val path: Path
) : LogTopicRepository {

    override fun append(cmdId: CmdId, bytes: ByteArray) {
        activeSegmentRepository.append(cmdId, bytes)
    }

    override fun read(firstId: LogEventId, limit: Limit): List<LogEvent> {
        return activeSegmentRepository.read(firstId, limit)
    }

    override fun countEvents(): Count {
        return activeSegmentRepository.count()
    }

    override fun clear() {
        return activeSegmentRepository.clear()
    }

    override fun close() {
        return activeSegmentRepository.close()
    }

    private var activeSegment = LogSegmentSqlite(
        id = LogSegmentId(SnowflakeId(0)),
        topicId = topic.id,
        path = FileUtils.createDirectories(
            path.resolve(Path(String.format("topics/%08d", topic.id.value.value)))
        )
    )

    private
    var activeSegmentRepository = LogSegmentSqliteRepository(activeSegment)
}