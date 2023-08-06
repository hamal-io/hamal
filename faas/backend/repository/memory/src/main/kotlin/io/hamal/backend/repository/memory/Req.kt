package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.backend.repository.memory.record.CurrentExecProjection
import io.hamal.backend.repository.memory.record.QueueProjection
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedReq
import kotlinx.serialization.protobuf.ProtoBuf
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object MemoryReqRepository : ReqCmdRepository, ReqQueryRepository {

    val queue = mutableListOf<ReqId>()
    val store = mutableMapOf<ReqId, ByteArray>()
    val lock = ReentrantLock()

    override fun queue(req: SubmittedReq) {
        return lock.withLock {
            store[req.reqId] = ProtoBuf { }.encodeToByteArray(SubmittedReq.serializer(), req)
            queue.add(req.reqId)
        }
    }

    override fun next(limit: Int): List<SubmittedReq> {
        return lock.withLock {
            val result = mutableListOf<SubmittedReq>()

            repeat(limit) {
                val reqId = queue.removeFirstOrNull() ?: return result
                result.add(find(reqId)!!)
            }

            result
        }
    }

    override fun complete(reqId: ReqId) {
        val req = find(reqId) ?: return
        lock.withLock {
            store[req.reqId] =
                ProtoBuf { }.encodeToByteArray(SubmittedReq.serializer(), req.apply { status = ReqStatus.Completed })
        }
    }

    override fun fail(reqId: ReqId) {
        val req = find(reqId) ?: return
        lock.withLock {
            store[req.reqId] =
                ProtoBuf { }.encodeToByteArray(SubmittedReq.serializer(), req.apply { status = ReqStatus.Failed })
        }
    }

    override fun clear() {
        lock.withLock {
            store.clear()
            queue.clear()
            QueueProjection.clear()
            CurrentExecProjection.clear()
        }
    }


    override fun find(reqId: ReqId): SubmittedReq? {
        val result = lock.withLock { store[reqId] } ?: return null
        return ProtoBuf { }.decodeFromByteArray(SubmittedReq.serializer(), result)
    }

    override fun list(block: ReqQueryRepository.Query.() -> Unit): List<SubmittedReq> {
        val query = ReqQueryRepository.Query().also(block)
        return lock.withLock {
            store.keys.sorted()
                .dropWhile { it <= query.afterId }
                .take(query.limit.value)
                .mapNotNull { find(it) }
                .reversed()
        }
    }
}