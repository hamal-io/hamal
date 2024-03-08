package io.hamal.core.adapter.auth

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.ExecToken
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthQueryRepository
import org.springframework.stereotype.Component

fun interface AuthGetPort {
    operator fun invoke(authId: AuthId): Auth
}

@Component
class AuthGetAdapter(
    private val authQueryRepository: AuthQueryRepository
) : AuthGetPort {
    override fun invoke(authId: AuthId): Auth = when (authId) {
        AuthId.anonymous -> Auth.Anonymous
        AuthId.runner -> Auth.Runner(
            id = AuthId.root,
            cmdId = CmdId(1),
            accountId = AccountId.root,
            token = AuthToken("let_me_in"),
            execToken = ExecToken("ExecToken")
        )
        AuthId.system -> Auth.System
        else -> authQueryRepository.get(authId)
    }
}