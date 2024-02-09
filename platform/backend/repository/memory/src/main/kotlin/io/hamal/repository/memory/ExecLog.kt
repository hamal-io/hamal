package io.hamal.repository.memory

import io.hamal.lib.common.domain.Count
import io.hamal.repository.api.ExecLog
import io.hamal.repository.api.ExecLogCmdRepository.AppendCmd
import io.hamal.repository.api.ExecLogQueryRepository.ExecLogQuery
import io.hamal.repository.api.ExecLogRepository
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class ExecLogMemoryRepository : ExecLogRepository {
    private val lock = ReentrantReadWriteLock()
    private val store = mutableListOf<ExecLog>()

    override fun append(cmd: AppendCmd): ExecLog {
        return lock.write {
            store.removeIf { it.id == cmd.execLogId }
            ExecLog(
                id = cmd.execLogId,
                execId = cmd.execId,
                groupId = cmd.groupId,
                level = cmd.level,
                message = cmd.message,
                timestamp = cmd.timestamp
            ).also { store.add(it) }
        }
    }

    override fun clear() {
        lock.write { store.clear() }
    }

    override fun close() {}

    override fun list(query: ExecLogQuery): List<ExecLog> {
        return lock.read {
            store.reversed()
                .asSequence()
                .dropWhile { it.id >= query.afterId }
                .filter { query.groupIds.isEmpty() || query.groupIds.contains(it.groupId) }
                .filter { query.execIds.isEmpty() || query.execIds.contains(it.execId) }
                .filter { query.execLogIds.isEmpty() || query.execLogIds.contains(it.id) }
                .take(query.limit.value)
                .toList()
        }
    }

    override fun count(query: ExecLogQuery): Count {
        return lock.read {
            Count(
                store.reversed()
                    .asSequence()
                    .dropWhile { it.id >= query.afterId }
                    .filter { query.groupIds.isEmpty() || query.groupIds.contains(it.groupId) }
                    .filter { query.execIds.isEmpty() || query.execIds.contains(it.execId) }
                    .filter { query.execLogIds.isEmpty() || query.execLogIds.contains(it.id) }
                    .count()
                    .toLong()
            )
        }
    }
}