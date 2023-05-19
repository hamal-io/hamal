package io.hamal.backend.core.queue.port

import io.hamal.backend.core.queue.QueuedExec
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.ExecId

interface EnqueueExecPort {
    data class ExecToEnqueue(
        val execId: ExecId,
        val shard: Shard
    )

    operator fun invoke(execToEnqueue: ExecToEnqueue): QueuedExec.Enqueued
}