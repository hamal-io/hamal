package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.ExecLog
import kotlinx.serialization.Serializable

@Serializable
data class ListExecLogsResponse(
    val logs: List<ExecLog>
)


