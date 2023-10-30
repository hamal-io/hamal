package io.hamal.request

import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.Password

interface LogInReq {
    val username: AccountName
    val password: Password
}
