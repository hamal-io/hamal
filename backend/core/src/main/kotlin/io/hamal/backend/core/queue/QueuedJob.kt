package io.hamal.backend.core.queue

import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.vo.JobId

interface QueuedJob : DomainObject<JobId> {
    override val id: JobId

    class Enqueued(override val id: JobId) : QueuedJob
    class Dequeued(override val id: JobId) : QueuedJob
}