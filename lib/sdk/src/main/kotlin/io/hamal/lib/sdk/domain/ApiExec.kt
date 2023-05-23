package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecState
import kotlinx.serialization.Serializable

@Serializable
data class ApiSimpleExecutionModel(
    val id: ExecId,
    val state: ExecState
)

@Serializable
data class ApiSimpleExecutionModels(
    val executions: List<ApiSimpleExecutionModel>
)


@Serializable
data class ApiDetailExecutionModel(
    val id: ExecId,
    val state: ExecState,
    val code: Code
)