package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.ReqId

data class StateSetSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val state: CorrelatedState
) : Submitted
