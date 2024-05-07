package io.hamal.lib.domain.request

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestStatus
import io.hamal.lib.domain.vo.WorkspaceId

interface StateSetRequest {
    val correlation: Correlation
    val value: State
}

data class StateSetRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val workspaceId: WorkspaceId,
    val state: CorrelatedState
) : Requested()
