package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class ApiSimpleExecutionModel(
    val id: ExecId,
    val ref: FuncRef,
    val state: ExecState
)

@Serializable
data class ApiSimpleExecutionModels(
    val execs: List<ApiSimpleExecutionModel>
)


@Serializable
data class ApiDetailExecutionModel(
    val id: ExecId,
    val ref: FuncRef,
    val state: ExecState,
    val func: FunctionModel,
    val cause: CauseModel
) {
    @Serializable
    data class FunctionModel(
        val id: FuncId,
        val code: Code
    )

    @Serializable
    data class CauseModel(
        val id: CauseId,
        val ref: TriggerRef
    )
}