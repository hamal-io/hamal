package io.hamal.core.adapter.auth

import io.hamal.lib.domain.vo.Email
import io.hamal.lib.domain.vo.Web3Address
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthQueryRepository
import org.springframework.stereotype.Component

interface AuthFindPort {
    operator fun invoke(email: Email): Auth?
    operator fun invoke(address: Web3Address): Auth?
}

@Component
class AuthFindAdapter(
    private val authQueryRepository: AuthQueryRepository
) : AuthFindPort {
    override fun invoke(email: Email): Auth? = authQueryRepository.find(email)
    override fun invoke(address: Web3Address): Auth? = authQueryRepository.find(address)
}