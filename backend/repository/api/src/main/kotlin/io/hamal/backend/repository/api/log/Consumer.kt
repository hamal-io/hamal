package io.hamal.backend.repository.api.log

interface LogConsumer<VALUE : Any> {
    val groupId: GroupId
    fun consume(limit: Int, fn: (LogChunkId, VALUE) -> Unit): Int {
        return consumeIndexed(limit) { _, chunkId, value -> fn(chunkId, value) }
    }

    fun consumeIndexed(limit: Int, fn: (Int, LogChunkId, VALUE) -> Unit): Int


}

@JvmInline
value class GroupId(val value: String) //FIXME become VO

interface BatchConsumer<VALUE : Any> {
    val groupId: GroupId

    // min batch size
    // max batch size

    fun consumeBatch(block: (List<VALUE>) -> Unit): Int

}
