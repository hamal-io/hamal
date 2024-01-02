package io.hamal.lib.domain.submitted

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ReqId

sealed class Submitted {
    abstract val id: ReqId
    abstract var status: ReqStatus
    val submissionType: String = this::class.java.simpleName
}