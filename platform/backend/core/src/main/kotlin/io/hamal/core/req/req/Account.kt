package io.hamal.core.req.req

import io.hamal.lib.domain.vo.Email
import io.hamal.lib.domain.vo.Password

data class CreateRootAccountReq(
    val email: Email,
    val password: Password
)