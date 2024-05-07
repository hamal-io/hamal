package io.hamal.runner.connector

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*

data class UnitOfWork(
    val id: ExecId,
    val namespaceId: NamespaceId,
    val workspaceId: WorkspaceId,
    val triggerId: TriggerId?,
    val inputs: ExecInputs,
    val state: State,
    val code: ValueCode,
    val codeType: CodeType,
    val execToken: ExecToken,
    val correlation: Correlation? = null
)

// FIXME ConnectorState, ConnectorEvent, ConnectorError
interface Connector {
    fun poll(): List<UnitOfWork>

    fun complete(
        execId: ExecId,
        statusCode: ExecStatusCode,
        result: ExecResult,
        state: ExecState,
        events: List<EventToSubmit>
    )

    fun fail(
        execId: ExecId,
        statusCode: ExecStatusCode,
        result: ExecResult
    )
}
