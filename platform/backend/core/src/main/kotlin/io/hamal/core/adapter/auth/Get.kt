package io.hamal.core.adapter.auth

import io.hamal.lib.domain.vo.AuthId
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
        AuthId.runner -> Auth.Runner
        AuthId.system -> Auth.System
        else -> authQueryRepository.get(authId)
    }
}