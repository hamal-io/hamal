package io.hamal.repository.memory

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.repository.api.ReqQueryRepository
import io.hamal.repository.api.ReqRepository
import io.hamal.repository.api.submitted_req.SubmittedReq
import kotlinx.serialization.protobuf.ProtoBuf
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class MemoryReqRepository : ReqRepository {

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
        val req = get(reqId)
        check(req.status == Submitted) { "Req not submitted" }
        lock.withLock {
            store[req.reqId] =
                ProtoBuf { }.encodeToByteArray(SubmittedReq.serializer(), req.apply { status = ReqStatus.Completed })
        }
    }

    override fun fail(reqId: ReqId) {
        val req = get(reqId)
        check(req.status == Submitted) { "Req not submitted" }
        lock.withLock {
            store[req.reqId] =
                ProtoBuf { }.encodeToByteArray(SubmittedReq.serializer(), req.apply { status = ReqStatus.Failed })
        }
    }

    override fun clear() {
        lock.withLock {
            store.clear()
            queue.clear()
        }
    }


    override fun find(reqId: ReqId): SubmittedReq? {
        val result = lock.withLock { store[reqId] } ?: return null
        return ProtoBuf { }.decodeFromByteArray(SubmittedReq.serializer(), result)
    }

    override fun list(query: ReqQueryRepository.ReqQuery): List<SubmittedReq> {
        return lock.withLock {
            store.keys.sorted()
                .dropWhile { it <= query.afterId }
                .take(query.limit.value)
                .mapNotNull { find(it) }
                .reversed()
        }
    }

    override fun count(query: ReqQueryRepository.ReqQuery): ULong {
        return lock.withLock {
            store.keys.sorted()
                .dropWhile { it <= query.afterId }
                .count()
                .toULong()
        }
    }

    override fun close() {
    }

}