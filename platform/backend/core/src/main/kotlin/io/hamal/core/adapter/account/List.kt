package io.hamal.core.adapter.account

import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import org.springframework.stereotype.Component

interface AccountListPort {
    operator fun invoke(query: AccountQuery): List<Account>
}

@Component
class AccountListAdapter(
    private val accountQueryRepository: AccountQueryRepository
) : AccountListPort {
    override fun invoke(query: AccountQuery): List<Account> = accountQueryRepository.list(query)
}