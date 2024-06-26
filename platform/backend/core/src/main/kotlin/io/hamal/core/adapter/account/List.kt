package io.hamal.core.adapter.account

import io.hamal.core.adapter.security.FilterAccessPort
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import org.springframework.stereotype.Component

fun interface AccountListPort {
    operator fun invoke(query: AccountQuery): List<Account>
}

@Component
class AccountListAdapter(
    private val accountQueryRepository: AccountQueryRepository,
    private val filterAccess: FilterAccessPort
) : AccountListPort {
    override fun invoke(query: AccountQuery): List<Account> = filterAccess(accountQueryRepository.list(query))
}