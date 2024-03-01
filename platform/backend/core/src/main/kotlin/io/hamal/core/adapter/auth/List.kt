package io.hamal.core.adapter.auth

import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthQueryRepository
import io.hamal.repository.api.AuthQueryRepository.AuthQuery
import org.springframework.stereotype.Component

fun interface AuthListPort {
    operator fun invoke(query: AuthQuery): List<Auth>
}

@Component
class AuthListAdapter(
    private val authQueryRepository: AuthQueryRepository
) : AuthListPort {
    override fun invoke(query: AuthQuery): List<Auth> = authQueryRepository.list(query)
}