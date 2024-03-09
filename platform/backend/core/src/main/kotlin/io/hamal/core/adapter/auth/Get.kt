package io.hamal.core.adapter.auth

import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthQueryRepository
import org.springframework.stereotype.Component

fun interface AuthGetPort {
    operator fun invoke(authId: AuthId): Auth
}

fun interface AuthGetExecTokenPort {
    operator fun invoke(execId: ExecId): Auth.ExecToken
}


@Component
class AuthGetAdapter(
    private val authQueryRepository: AuthQueryRepository
) : AuthGetPort {
    override fun invoke(authId: AuthId): Auth = when (authId) {
        AuthId.anonymous -> Auth.Anonymous
        AuthId.runner -> Auth.Runner
        AuthId.system -> Auth.System
        else -> authQueryRepository.get(authId)
    }
}

@Component
class AuthGetExecTokenAdapter(
    private val authQueryRepository: AuthQueryRepository

) : AuthGetExecTokenPort {
    override fun invoke(execId: ExecId): Auth.ExecToken = authQueryRepository.get(execId)
}