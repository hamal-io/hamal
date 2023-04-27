package io.hamal.lib.domain

import io.hamal.lib.domain.vo.JobId

sealed class QueuedJob(
    val id: JobId,
    //queueId
) : DomainObject {
    class Enqueued(
        id: JobId,
    ) : QueuedJob(id) {
        companion object {
        }
    }


    class Dequeued(
        id: JobId
    ) : QueuedJob(id)
}