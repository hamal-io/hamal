package io.hamal.repository.memory.record.account

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository

internal object AccountCurrentProjection {
    private val projection = mutableMapOf<AccountId, Account>()
    fun apply(account: Account) {
        projection[account.id] = account
    }

    fun find(accountId: AccountId): Account? = projection[accountId]

    fun list(query: AccountQueryRepository.AccountQuery): List<Account> {
        return projection.filter { query.accountIds.isEmpty() || it.key in query.accountIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: AccountQueryRepository.AccountQuery): Count {
        return Count(
            projection.filter { query.accountIds.isEmpty() || it.key in query.accountIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}