package io.hamal.lib.sdk.api

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.GroupId
import kotlinx.serialization.Serializable


@Serializable
data class ApiUnitOfWorkList(
    val work: List<UnitOfWork>
) {
    @Serializable
    data class UnitOfWork(
        val id: ExecId,
        val groupId: GroupId,
        val inputs: ExecInputs,
        val state: State,
        val code: CodeValue,
        val correlation: Correlation? = null,
        val events: List<Event>
    )
}
