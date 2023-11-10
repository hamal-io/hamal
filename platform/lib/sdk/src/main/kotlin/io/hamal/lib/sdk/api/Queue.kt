package io.hamal.lib.sdk.api

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.Event
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable


@Serializable
data class ApiUnitOfWorkList(
    val work: List<UnitOfWork>
) {
    @Serializable
    data class UnitOfWork(
        val id: ExecId,
        val flowId: FlowId,
        val groupId: GroupId,
        val inputs: ExecInputs,
        val state: State,
        val code: CodeValue,
        val correlation: Correlation? = null,
        val events: List<Event>
    )
}
