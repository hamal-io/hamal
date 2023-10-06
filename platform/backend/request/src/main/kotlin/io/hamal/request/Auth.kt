package io.hamal.request

import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.Password

interface SignInReq {
    val name: AccountName
    val password: Password
}
