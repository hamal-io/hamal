package io.hamal.core.adapter.auth

import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthQueryRepository
import io.hamal.repository.api.AuthQueryRepository.AuthQuery
import org.springframework.stereotype.Component

interface AuthListPort {
    operator fun invoke(query: AuthQuery): List<Auth>
    operator fun invoke(accountId: AccountId): List<Auth>
}

@Component
class AuthListAdapter(
    private val authQueryRepository: AuthQueryRepository
) : AuthListPort {
    override fun invoke(query: AuthQuery): List<Auth> = authQueryRepository.list(query)
    override fun invoke(accountId: AccountId): List<Auth> = authQueryRepository.list(accountId)
}