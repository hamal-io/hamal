package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.FuncName
import kotlinx.serialization.Serializable


@Serializable
data class ApiAgentExecRequests(
    val requests: List<ExecRequest>
) {

    @Serializable
    data class ExecRequest(
        val id: ExecId,
        val funcName: FuncName,
        val correlation: Correlation?,
        val state: State?,
        val code: Code
    )

    @Serializable
    data class State(
        val contentType: String,
        val bytes: ByteArray
    )
}