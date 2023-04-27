package io.hamal.module.queue.core.job

import io.hamal.lib.domain.vo.JobId

sealed class Job(
    val id: JobId,
) {
    class Enqueued(
        id: JobId,
    ) : Job(id) {
        companion object {
        }
    }


    class Dequeued(
        id: JobId
    ) : Job(id)
}