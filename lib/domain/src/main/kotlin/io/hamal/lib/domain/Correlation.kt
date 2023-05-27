package io.hamal.lib.domain

import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import kotlinx.serialization.Serializable

@Serializable
data class Correlation(
    val correlationId: CorrelationId,
    val funcId: FuncId
)