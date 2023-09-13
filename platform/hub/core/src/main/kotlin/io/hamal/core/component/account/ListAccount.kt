package io.hamal.core.component.account

import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository
import org.springframework.stereotype.Component

@Component
class ListAccount(private val accountQueryRepository: AccountQueryRepository) {
    operator fun <T : Any> invoke(
        query: AccountQueryRepository.AccountQuery,
        responseHandler: (List<Account>) -> T
    ): T {
        val accounts = accountQueryRepository.list(query)
        return responseHandler(accounts)
    }
}