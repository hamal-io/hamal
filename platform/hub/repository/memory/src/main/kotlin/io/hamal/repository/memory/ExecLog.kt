package io.hamal.repository.memory

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.RemoteAt
import io.hamal.repository.api.ExecLog
import io.hamal.repository.api.ExecLogCmdRepository
import io.hamal.repository.api.ExecLogQueryRepository.ExecLogQuery
import io.hamal.repository.api.ExecLogRepository
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class MemoryExecLogRepository : ExecLogRepository {
    private val lock = ReentrantReadWriteLock()
    private val store = mutableListOf<ExecLog>()

    override fun append(cmd: ExecLogCmdRepository.LogCmd): ExecLog {
        return lock.write {
            ExecLog(
                id = cmd.id,
                execId = cmd.execId,
                level = cmd.level,
                message = cmd.message,
                localAt = cmd.localAt,
                remoteAt = RemoteAt.now()
            ).also { store.add(it) }
        }
    }

    override fun clear() {
        lock.write { store.clear() }
    }

    override fun list(execId: ExecId, query: ExecLogQuery): List<ExecLog> {
        return lock.read {
            store.reversed()
                .asSequence()
                .filter {
                    it.execId == execId
                }
                .dropWhile { it.id >= query.afterId }
                .take(query.limit.value)
                .toList()
        }
    }

    override fun list(query: ExecLogQuery): List<ExecLog> {
        return lock.read {
            store.reversed()
                .asSequence()
                .dropWhile { it.id >= query.afterId }
                .take(query.limit.value)
                .toList()
        }
    }
}