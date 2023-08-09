package io.hamal.lib.domain.req

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import kotlinx.serialization.Serializable

@Serializable
data class SetStateReq(
    val correlation: Correlation,
    val value: State
)

