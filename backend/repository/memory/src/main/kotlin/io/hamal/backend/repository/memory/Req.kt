package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.Req
import io.hamal.lib.domain.req.ReqStatus
import kotlinx.serialization.protobuf.ProtoBuf
import java.math.BigInteger
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object MemoryReqRepository : ReqCmdRepository, ReqQueryRepository {

    val queue = mutableListOf<ReqId>()
    val store = mutableMapOf<ReqId, ByteArray>()
    val lock = ReentrantReadWriteLock()

    override fun queue(req: Req) {
        return lock.writeLock().withLock {
            store[req.id] = ProtoBuf { }.encodeToByteArray(Req.serializer(), req)
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
            store[req.id] = ProtoBuf { }.encodeToByteArray(Req.serializer(), req.apply { status = ReqStatus.Completed })
        }
    }

    override fun fail(reqId: ReqId) {
        val req = find(reqId) ?: return
        lock.writeLock().withLock {
            store[req.id] = ProtoBuf { }.encodeToByteArray(Req.serializer(), req.apply { status = ReqStatus.Completed })
        }
    }

    override fun find(reqId: ReqId): Req? {
        val result = lock.readLock().withLock { store[reqId] } ?: return null
        return ProtoBuf { }.decodeFromByteArray(Req.serializer(), result)
    }

    override fun query(block: ReqQueryRepository.Query.() -> Unit): List<Req> {
        val query = ReqQueryRepository.Query(ReqId(BigInteger.ZERO), limit = 25)
        block(query)
        return lock.readLock().withLock {
            store.keys.sorted()
                .dropWhile { it <= query.afterId }
                .take(query.limit)
                .mapNotNull { find(it) }
                .reversed()
        }
    }
}