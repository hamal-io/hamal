package io.hamal.backend.core.queue

import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.ExecId

interface QueuedExec : DomainObject<ExecId> {
    override val id: ExecId

    class Enqueued(override val id: ExecId) : QueuedExec
    class Dequeued(override val id: ExecId) : QueuedExec
}