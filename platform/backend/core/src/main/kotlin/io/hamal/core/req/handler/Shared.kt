package io.hamal.core.req.handler

import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.submitted_req.Submitted

internal fun Submitted.cmdId() = CmdId(id.value.value)