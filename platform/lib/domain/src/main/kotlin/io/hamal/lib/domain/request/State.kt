package io.hamal.lib.domain.request

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.RequestId

interface StateSetRequest {
    val correlation: Correlation
    val value: State
}

data class StateSetRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val state: CorrelatedState
) : Requested()
