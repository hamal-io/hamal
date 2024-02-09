package io.hamal.repository.memory

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.RequestStatus.Processing
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.RequestQueryRepository
import io.hamal.repository.api.RequestRepository
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Repository
class RequestMemoryRepository : RequestRepository {

    val queue = ConcurrentLinkedQueue<RequestId>()
    val store = ConcurrentHashMap<RequestId, Requested>()
    val lock = ReentrantLock()

    override fun queue(req: Requested) {
        return lock.withLock {
            store[req.id] = req
            queue.add(req.id)
        }
    }

    override fun next(limit: Limit): List<Requested> {
        return lock.withLock {
            val result = mutableListOf<Requested>()

            repeat(limit.value) {
                if (queue.isEmpty()) {
                    return result
                }

                val reqId = queue.remove()
                store[reqId]?.status = Processing
                result.add(store[reqId]!!)
            }

            result
        }
    }

    override fun complete(reqId: RequestId) {
        lock.withLock {
            val req = get(reqId)
            check(req.status == Processing) { "Request not processing" }
            store[req.id] = req.apply { status = RequestStatus.Completed }
        }
    }

    override fun fail(reqId: RequestId) {
        lock.withLock {
            val req = get(reqId)
            check(req.status == Processing) { "Request not processing" }
            store[req.id] = req.apply { status = RequestStatus.Failed }
        }
    }

    override fun clear() {
        lock.withLock {
            store.clear()
            queue.clear()
        }
    }

    override fun find(reqId: RequestId): Requested? {
        return store[reqId]
    }

    override fun list(query: RequestQueryRepository.ReqQuery): List<Requested> {
        return store.keys.sorted()
            .dropWhile { it <= query.afterId }
            .take(query.limit.value)
            .mapNotNull { find(it) }
            .reversed()
    }

    override fun count(query: RequestQueryRepository.ReqQuery): Count {
        return Count(
            store.keys.sorted()
                .dropWhile { it <= query.afterId }
                .count()
                .toLong()
        )
    }

    override fun close() {
    }

}