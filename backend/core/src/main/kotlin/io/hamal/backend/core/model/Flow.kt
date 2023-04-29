package io.hamal.backend.core.model

import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.vo.FlowDefinitionId
import io.hamal.lib.vo.FlowId
import io.hamal.lib.vo.FlowState

sealed class Flow(
    val id: FlowId,
    val state: FlowState,
    val definitionId: FlowDefinitionId
) : DomainObject {

    class PlannedFlow(
        id: FlowId,
        definitionId: FlowDefinitionId
    ) : Flow(
        id = id,
        state = FlowState.Planned,
        definitionId = definitionId
    )

    class ScheduledFlow(
        id: FlowId,
        definitionId: FlowDefinitionId
    ) : Flow(
        id = id,
        state = FlowState.Scheduled,
        definitionId = definitionId
    )

    class StartedFlow(
        id: FlowId,
        definitionId: FlowDefinitionId
    ) : Flow(
        id = id,
        state = FlowState.Started,
        definitionId = definitionId
    )

    class CompletedFlow(
        id: FlowId,
        definitionId: FlowDefinitionId
    ) : Flow(
        id = id,
        state = FlowState.Completed,
        definitionId = definitionId
    )

    class FailedFlow(
        id: FlowId,
        definitionId: FlowDefinitionId
    ) : Flow(
        id = id,
        state = FlowState.Failed,
        definitionId = definitionId
    )

    class TerminalFailedFlow(
        id: FlowId,
        definitionId: FlowDefinitionId
    ) : Flow(
        id = id,
        state = FlowState.TerminalFailed,
        definitionId = definitionId
    )
}