package io.hamal.runner.connector

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType

data class UnitOfWork(
    val id: ExecId,
    val inputs: ExecInputs,
    val state: State,
    val code: CodeType,
    val correlation: Correlation? = null,
    val events: List<Event> = listOf()
)

// FIXME ConnectorState, ConnectorEvent, ConnectorError
interface Connector {
    fun poll(): List<UnitOfWork>

    fun complete(execId: ExecId, state: State, events: List<EventToSubmit>)

    fun fail(execId: ExecId, error: ErrorType)
}
