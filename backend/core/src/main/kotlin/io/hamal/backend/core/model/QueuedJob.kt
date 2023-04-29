package io.hamal.backend.core.model

import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.vo.FlowId

sealed class QueuedJob(
    val id: FlowId,
    //queueId
) : DomainObject {
    class Enqueued(
        id: FlowId,
    ) : QueuedJob(id) {
        companion object {
        }
    }


    class Dequeued(
        id: FlowId
    ) : QueuedJob(id)
}