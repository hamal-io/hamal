package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import kotlinx.serialization.Serializable


@Serializable
data class DequeueExecsResponse(
    val execs: List<Exec>
) {
    @Serializable
    data class Exec(
        val id: ExecId,
        val correlation: Correlation?,
        val inputs: ExecInputs,
        val state: State,
        val code: Code
    )
}
