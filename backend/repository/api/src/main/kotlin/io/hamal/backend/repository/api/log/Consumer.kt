package io.hamal.backend.repository.api.log

import java.util.concurrent.CompletableFuture

interface Consumer<VALUE : Any> {
    val groupId: GroupId
    fun consume(limit: Int, fn: (VALUE) -> CompletableFuture<*>): Int {
        return consumeIndexed(limit) { _, value -> fn(value) }
    }

    fun consumeIndexed(limit: Int, fn: (Int, VALUE) -> CompletableFuture<*>): Int

    @JvmInline
    value class GroupId(val value: String)
}
