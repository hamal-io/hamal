package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
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
data class ApiExecList(
    val execs: List<SimpleExec>
) {
    @Serializable
    data class SimpleExec(
        val id: ExecId,
        val status: ExecStatus,
        val correlation: Correlation?,
        val func: Func?
    )

    @Serializable
    data class Func(
        val id: FuncId,
        val name: FuncName
    )
}

@Serializable
data class ApiExec(
    val id: ExecId,
    val status: ExecStatus,
    val correlation: Correlation?,
    val inputs: ExecInputs,
    val code: CodeType,
    val events: List<Event>
)
