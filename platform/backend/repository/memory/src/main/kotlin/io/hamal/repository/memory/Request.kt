package io.hamal.repository.memory

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.RequestStatus.Processing
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.RequestQueryRepository
import io.hamal.repository.api.RequestRepository
import org.springframework.stereotype.Repository
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KClass

@Repository
class RequestMemoryRepository : RequestRepository {

    override fun queue(req: Requested) {
        return lock.withLock {
            store[req.requestId] = req
            queue.add(req.requestId)
        }
    }

    override fun next(limit: Limit): List<Requested> {
        return lock.withLock {
            val result = mutableListOf<Requested>()

            repeat(limit.intValue) {
                if (queue.isEmpty()) {
                    return result
                }

                val reqId = queue.remove()

                store[reqId]?.let { req ->
                    req.apply {
                        statusField(this::class).also { field -> field.set(this, Processing) }
                    }
                }

                result.add(store[reqId]!!)
            }

            result
        }
    }

    override fun complete(reqId: RequestId) {
        lock.withLock {
            val req = get(reqId)
            check(req.requestStatus == Processing) { "Request not processing" }
            store[req.requestId] = req.apply {
                statusField(this::class).also { field -> field.set(this, RequestStatus.Completed) }
            }
        }
    }

    override fun fail(reqId: RequestId) {
        lock.withLock {
            val req = get(reqId)
            check(req.requestStatus == Processing) { "Request not processing" }
            store[req.requestId] = req.apply {
                statusField(this::class).also { field -> field.set(this, RequestStatus.Failed) }
            }
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

    override fun list(query: RequestQueryRepository.RequestQuery): List<Requested> {
        return store.keys.sorted()
            .dropWhile { it <= query.afterId }
            .take(query.limit.intValue)
            .mapNotNull { find(it) }
            .reversed()
    }

    override fun count(query: RequestQueryRepository.RequestQuery): Count {
        return Count(
            store.keys.sorted()
                .dropWhile { it <= query.afterId }
                .count()
                .toLong()
        )
    }

    override fun close() {
    }

    private fun <REQUESTED_TYPE : Requested> statusField(klass: KClass<REQUESTED_TYPE>): Field =
        statusFieldCache(klass) { clazz ->
            clazz.java.getDeclaredField("requestStatus").also { field -> field.isAccessible = true }
        }

    private val queue = ConcurrentLinkedQueue<RequestId>()
    private val store = ConcurrentHashMap<RequestId, Requested>()
    private val lock = ReentrantLock()
    private val statusFieldCache = KeyedOnce.default<KClass<*>, Field>()

}