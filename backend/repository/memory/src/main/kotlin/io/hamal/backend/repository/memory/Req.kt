package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.req.Req
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object MemoryReqRepository : ReqCmdRepository, ReqQueryRepository {

    val queue = mutableListOf<ReqId>()
    val store = mutableMapOf<ReqId, Req>()
    val lock = ReentrantReadWriteLock()

    override fun queue(req: Req) {
        return lock.writeLock().withLock {
            store[req.id] = req
            queue.add(req.id)
        }
    }

    override fun next(limit: Int): List<Req> {
        return lock.writeLock().withLock {
            val result = mutableListOf<Req>()

            repeat(limit) {
                val reqId = queue.removeFirstOrNull() ?: return result
                result.add(find(reqId)!!)
            }

            result
        }
    }

    override fun complete(reqId: ReqId) {
        val req = find(reqId) ?: return
        lock.writeLock().withLock {
            store[reqId]!!.status = ReqStatus.Completed
        }
    }

    override fun fail(reqId: ReqId) {
        val req = find(reqId) ?: return
        lock.writeLock().withLock {
            store[reqId]!!.status = ReqStatus.Failed
        }
    }

    override fun find(reqId: ReqId) = lock.readLock().withLock { store[reqId] }

    override fun list(afterId: ReqId, limit: Int): List<Req> {
        return lock.readLock().withLock {
            store.keys.sorted()
                .dropWhile { it <= afterId }
                .take(limit)
                .mapNotNull { find(it) }
                .reversed()
        }
    }
}