package io.hamal.core.adapter.account

import io.hamal.core.adapter.security.EnsureAccessPort
import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository
import org.springframework.stereotype.Component

fun interface AccountGetPort {
    operator fun invoke(accountId: AccountId): Account
}

@Component
class AccountGetAdapter(
    private val accountQueryRepository: AccountQueryRepository,
    private val ensureAccess: EnsureAccessPort
) : AccountGetPort {
    override fun invoke(accountId: AccountId): Account = ensureAccess(accountQueryRepository.get(accountId))
}