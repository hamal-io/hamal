package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.ExecLogCmdRepository
import io.hamal.backend.repository.api.ExecLogQueryRepository
import io.hamal.lib.domain.ExecLog
import io.hamal.lib.domain.RemoteAt
import io.hamal.lib.domain.vo.ExecId
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

object MemoryExecLogRepository : ExecLogCmdRepository, ExecLogQueryRepository {
    private val lock = ReentrantReadWriteLock()
    private val store = HashMap<ExecId, MutableList<ExecLog>>()

    override fun append(cmd: ExecLogCmdRepository.LogCmd): ExecLog {
        return lock.write {
            store.putIfAbsent(cmd.execId, mutableListOf())
            ExecLog(
                id = cmd.id,
                execId = cmd.execId,
                level = cmd.level,
                message = cmd.message,
                localAt = cmd.localAt,
                remoteAt = RemoteAt.now()
            ).also { store[it.execId]!!.add(it) }
        }
    }

    override fun clear() {
        lock.write { store.clear() }
    }

    override fun list(execId: ExecId, block: ExecLogQueryRepository.ExecLogQuery.() -> Unit): List<ExecLog> {
        val query = ExecLogQueryRepository.ExecLogQuery().also(block)
        return lock.read {
            store[execId]?.reversed()?.dropWhile { it.id >= query.afterId }
                ?.take(query.limit.value)
        } ?: listOf()
    }

}