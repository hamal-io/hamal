package io.hamal.repository.memory

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.RequestQueryRepository
import io.hamal.repository.api.RequestRepository
import io.hamal.repository.record.json
import org.springframework.stereotype.Repository
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Repository
class ReqMemoryRepository : RequestRepository {

    val queue = mutableListOf<RequestId>()
    val store = mutableMapOf<RequestId, String>()
    val lock = ReentrantLock()

    override fun queue(req: Requested) {
        return lock.withLock {
            store[req.id] = json.serialize(req)
            queue.add(req.id)
        }
    }

    override fun next(limit: Int): List<Requested> {
        return lock.withLock {
            val result = mutableListOf<Requested>()

            repeat(limit) {
                val reqId = queue.removeFirstOrNull() ?: return result
                result.add(find(reqId)!!)
            }

            result
        }
    }

    override fun complete(reqId: RequestId) {
        val req = get(reqId)
        check(req.status == RequestStatus.Submitted) { "Req not submitted" }
        lock.withLock {
            store[req.id] = json.serialize(req.apply { status = RequestStatus.Completed })
        }
    }

    override fun fail(reqId: RequestId) {
        val req = get(reqId)
        check(req.status == RequestStatus.Submitted) { "Req not submitted" }
        lock.withLock {
            store[req.id] = json.serialize(req.apply { status = RequestStatus.Failed })
        }
    }

    override fun clear() {
        lock.withLock {
            store.clear()
            queue.clear()
        }
    }


    override fun find(reqId: RequestId): Requested? {
        val result = lock.withLock { store[reqId] } ?: return null
        return json.deserialize(Requested::class, result)

    }

    override fun list(query: RequestQueryRepository.ReqQuery): List<Requested> {

        return lock.withLock {

            store.keys.sorted()
                .dropWhile { it <= query.afterId }
                .take(query.limit.value)
                .mapNotNull { find(it) }
                .reversed()
        }
    }

    override fun count(query: RequestQueryRepository.ReqQuery): Count {
        return lock.withLock {
            Count(
                store.keys.sorted()
                    .dropWhile { it <= query.afterId }
                    .count()
                    .toLong()
            )
        }
    }

    override fun close() {
    }

}