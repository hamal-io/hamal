package io.hamal.core.adapter.account

import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository
import org.springframework.stereotype.Component

interface AccountGetPort {
    operator fun invoke(accountId: AccountId): Account
}

@Component
class AccountGetAdapter(
    private val accountQueryRepository: AccountQueryRepository
) : AccountGetPort {
    override fun invoke(accountId: AccountId): Account = accountQueryRepository.get(accountId)
}