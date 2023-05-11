package io.hamal.backend.core.queue

import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.JobId

interface QueuedJob : DomainObject<JobId> {
    override val id: JobId

    class Enqueued(override val id: JobId) : QueuedJob
    class Dequeued(override val id: JobId) : QueuedJob
}