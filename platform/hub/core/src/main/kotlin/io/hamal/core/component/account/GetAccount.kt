package io.hamal.core.component.account

import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository
import org.springframework.stereotype.Component

@Component
class GetAccount(private val accountQueryRepository: AccountQueryRepository) {
    operator fun <T : Any> invoke(
        accountId: AccountId,
        responseHandler: (Account) -> T
    ): T = responseHandler(accountQueryRepository.get(accountId))
}