package io.hamal.core.adapter.account

import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository
import org.springframework.stereotype.Component

fun interface AccountFindPort {
    operator fun invoke(accountId: AccountId): Account?
}

@Component
class AccountFindAdapter(
    private val accountQueryRepository: AccountQueryRepository
) : AccountFindPort {
    override fun invoke(accountId: AccountId): Account? = accountQueryRepository.find(accountId)
}