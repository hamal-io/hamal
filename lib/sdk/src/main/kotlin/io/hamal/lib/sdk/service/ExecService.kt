package io.hamal.lib.sdk.service

import io.hamal.lib.domain.State
import io.hamal.lib.domain.req.CompleteExecReq
import io.hamal.lib.domain.req.FailExecReq
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.sdk.domain.DequeueExecsResponse

interface ExecService {
    fun poll(): DequeueExecsResponse

    //FIXME list of events to publish
    fun complete(execId: ExecId, stateAfterCompletion: State)

    fun fail(execId: ExecId, error: ErrorValue)
}

data class DefaultExecService(val template: HttpTemplate) : ExecService {
    override fun poll(): DequeueExecsResponse {
        return template
            .post("/v1/dequeue")
            .execute(DequeueExecsResponse::class)
    }

    override fun complete(execId: ExecId, stateAfterCompletion: State) {
        template
            .post("/v1/execs/${execId.value.value}/complete")
            .body(
                CompleteExecReq(
                    state = stateAfterCompletion,
                    events = listOf()
                )
            )
            .execute()
    }

    override fun fail(execId: ExecId, error: ErrorValue) {
        template
            .post("/v1/execs/${execId.value.value}/fail")
            .body(FailExecReq(reason = error))
            .execute()
    }
}