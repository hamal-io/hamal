package io.hamal.backend.instance.req.handler

import io.hamal.backend.repository.api.submitted_req.SubmittedReq
import io.hamal.lib.common.domain.CmdId

internal fun SubmittedReq.cmdId() = CmdId(reqId.value.value)