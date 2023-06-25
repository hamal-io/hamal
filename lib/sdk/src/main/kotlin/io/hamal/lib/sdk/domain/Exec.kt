package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import kotlinx.serialization.Serializable

@Serializable
@Deprecated("do not have separate dto")
data class ApiSimpleExecutionModel(
    val id: ExecId,
    val state: ExecStatus
)

@Serializable
@Deprecated("do not have separate dto")
data class ApiSimpleExecutionModels(
    val execs: List<ApiSimpleExecutionModel>
)

@Serializable
data class ListExecsResponse(
    val execs: List<Exec>
) {
    @Serializable
    data class Exec(
        val id: ExecId,
        val status: ExecStatus
    )
}


