package io.hamal.backend.core.model

import io.hamal.backend.core.DomainObject
import io.hamal.lib.vo.JobId

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