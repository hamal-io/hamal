package io.hamal.repository.memory.record.account

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.memory.record.ProjectionMemory

internal class ProjectionCurrent : ProjectionMemory.BaseImpl<AccountId, Account>() {

    fun find(accountId: AccountId): Account? = projection[accountId]

    fun list(query: AccountQueryRepository.AccountQuery): List<Account> {
        return projection.filter { query.accountIds.isEmpty() || it.key in query.accountIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.intValue)
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

}