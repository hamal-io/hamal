package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.kua.type.CodeType
import kotlinx.serialization.Serializable


@Serializable
data class HubUnitOfWorkList(
    val work: List<UnitOfWork>
) {
    @Serializable
    data class UnitOfWork(
        val id: ExecId,
        val inputs: ExecInputs,
        val state: State,
        val code: CodeType,
        val correlation: Correlation? = null,
        val events: List<Event>
    )
}
