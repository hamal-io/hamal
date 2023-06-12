package io.hamal.lib.sdk.service

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.domain.ApiAgentExecRequests

interface ExecService {
    fun poll(): ApiAgentExecRequests

    //FIXME list of events to publish
    fun complete(execId: ExecId, stateAfterCompletion: StateAfterCompletion)

    fun fail(execId: ExecId)

    data class StateAfterCompletion(
        val contentType: String,
        val bytes: ByteArray
    )
}

data class DefaultExecService(val template: HttpTemplate) : ExecService {
    override fun poll(): ApiAgentExecRequests {
        return HttpTemplate("http://localhost:8084")
            .post("/v1/dequeue")
            .execute(ApiAgentExecRequests::class)
    }

    override fun complete(execId: ExecId, stateAfterCompletion: ExecService.StateAfterCompletion) {
        HttpTemplate("http://localhost:8084")
            .post("/v1/execs/${execId.value.value}/complete")
            .body(stateAfterCompletion.contentType, stateAfterCompletion.bytes)
            .execute()
    }

    override fun fail(execId: ExecId) {
        HttpTemplate("http://localhost:8084")
            .post("/v1/execs/${execId.value.value}/fail")
            .execute()
    }

}