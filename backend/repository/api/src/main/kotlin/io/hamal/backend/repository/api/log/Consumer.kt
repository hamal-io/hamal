package io.hamal.backend.repository.api.log

import java.util.concurrent.CompletableFuture

interface LogConsumer<VALUE : Any> {
    val groupId: GroupId
    fun consume(limit: Int, fn: (LogChunkId, VALUE) -> CompletableFuture<*>): Int {
        return consumeIndexed(limit) { _, chunkId, value -> fn(chunkId, value) }
    }

    fun consumeIndexed(limit: Int, fn: (Int, LogChunkId, VALUE) -> CompletableFuture<*>): Int


}

@JvmInline
value class GroupId(val value: String) //FIXME become VO

interface BatchConsumer<VALUE : Any> {
    val groupId: GroupId

    // min batch size
    // max batch size

    fun consumeBatch(block: (List<VALUE>) -> CompletableFuture<*>): Int

}
