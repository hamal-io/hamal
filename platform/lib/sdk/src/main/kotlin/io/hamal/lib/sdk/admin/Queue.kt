package io.hamal.lib.sdk.admin

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.kua.type.CodeType
import kotlinx.serialization.Serializable


@Serializable
data class AdminUnitOfWorkList(
    val work: List<UnitOfWork>
) {
    @Serializable
    data class UnitOfWork(
        val id: ExecId,
        val groupId: GroupId,
        val inputs: ExecInputs,
        val state: State,
        val code: CodeType,
        val correlation: Correlation? = null,
        val events: List<Event>
    )
}
