package io.hamal.lib.sdk.service

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.State
import io.hamal.lib.domain.req.CompleteExecReq
import io.hamal.lib.domain.req.FailExecReq
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.domain.ApiUnitOfWorkList

interface ExecService {
    fun poll(): ApiUnitOfWorkList

    //FIXME list of events to publish
    fun complete(execId: ExecId, stateAfterCompletion: State, events: List<Event>)

    // able to emit events on failure
    fun fail(execId: ExecId, error: ErrorType)
}

class DefaultExecService(val template: HttpTemplate) : ExecService {
    override fun poll(): ApiUnitOfWorkList {
        return template.post("/v1/dequeue")
            .execute(ApiUnitOfWorkList::class)
    }

    override fun complete(execId: ExecId, stateAfterCompletion: State, events: List<Event>) {
        template.post("/v1/execs/{execId}/complete")
            .path("execId", execId)
            .body(CompleteExecReq(stateAfterCompletion, events))
            .execute()
    }

    override fun fail(execId: ExecId, error: ErrorType) {
        template.post("/v1/execs/{execId}/fail")
            .path("execId", execId)
            .body(FailExecReq(error))
            .execute()
    }
}