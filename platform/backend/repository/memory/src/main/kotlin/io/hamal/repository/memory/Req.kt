package io.hamal.repository.memory

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.ReqQueryRepository
import io.hamal.repository.api.ReqRepository
import io.hamal.repository.api.submitted_req.Submitted
import kotlinx.serialization.protobuf.ProtoBuf
import org.springframework.stereotype.Repository
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Repository
class ReqMemoryRepository(
    val protobuf: ProtoBuf
) : ReqRepository {

    val queue = mutableListOf<ReqId>()
    val store = mutableMapOf<ReqId, ByteArray>()
    val lock = ReentrantLock()

    override fun queue(req: Submitted) {
        return lock.withLock {
            TODO()
//            store[req.id] = protobuf.encodeToByteArray(Submitted.serializer(), req)
//            queue.add(req.id)
        }
    }

    override fun next(limit: Int): List<Submitted> {
        return lock.withLock {
            val result = mutableListOf<Submitted>()

            repeat(limit) {
                val reqId = queue.removeFirstOrNull() ?: return result
                result.add(find(reqId)!!)
            }

            result
        }
    }

    override fun complete(reqId: ReqId) {
        val req = get(reqId)
        check(req.status == ReqStatus.Submitted) { "Req not submitted" }
        lock.withLock {
            TODO()
//            store[req.id] =
//                protobuf.encodeToByteArray(Submitted.serializer(), req.apply { status = ReqStatus.Completed })
        }
    }

    override fun fail(reqId: ReqId) {
        val req = get(reqId)
        check(req.status == ReqStatus.Submitted) { "Req not submitted" }
        lock.withLock {
            TODO()
//            store[req.id] =
//                protobuf.encodeToByteArray(Submitted.serializer(), req.apply { status = ReqStatus.Failed })
        }
    }

    override fun clear() {
        lock.withLock {
            store.clear()
            queue.clear()
        }
    }


    override fun find(reqId: ReqId): Submitted? {
        val result = lock.withLock { store[reqId] } ?: return null
//        return protobuf.decodeFromByteArray(Submitted.serializer(), result)
        TODO()
    }

    override fun list(query: ReqQueryRepository.ReqQuery): List<Submitted> {

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