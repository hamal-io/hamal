package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountCmdRepository
import io.hamal.repository.api.AccountCmdRepository.CreateCmd
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import io.hamal.repository.api.AccountRepository
import io.hamal.repository.record.account.AccountRecord
import io.hamal.repository.record.account.CreateAccountFromRecords
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object AccountCurrentProjection {
    private val projection = mutableMapOf<AccountId, Account>()
    fun apply(account: Account) {
        projection[account.id] = account
    }

    fun find(accountId: AccountId): Account? = projection[accountId]

    fun list(query: AccountQuery): List<Account> {
        return projection.filter { query.accountIds.isEmpty() || it.key in query.accountIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: AccountQuery): Count {
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

class AccountMemoryRepository : RecordMemoryRepository<AccountId, AccountRecord, Account>(
    createDomainObject = CreateAccountFromRecords,
    recordClass = AccountRecord::class
), AccountRepository {
    private val lock = ReentrantLock()

    override fun create(cmd: CreateCmd): Account {
        return lock.withLock {
            val accountId = cmd.accountId
            if (commandAlreadyApplied(cmd.id, accountId)) {
                versionOf(accountId, cmd.id)
            } else {
                store(
                    AccountRecord.Created(
                        cmdId = cmd.id,
                        entityId = accountId,
                        type = cmd.accountType,
                        salt = cmd.salt
                    )
                )
                (currentVersion(accountId)).also(AccountCurrentProjection::apply)
            }
        }
    }

    override fun convert(cmd: AccountCmdRepository.ConvertCmd): Account {
        return lock.withLock {
            val accountId = cmd.accountId
            if (commandAlreadyApplied(cmd.id, accountId)) {
                versionOf(accountId, cmd.id)
            } else {
                store(
                    AccountRecord.Converted(
                        cmdId = cmd.id,
                        entityId = accountId
                    )
                )
                (currentVersion(accountId)).also(AccountCurrentProjection::apply)
            }
        }
    }

    override fun find(accountId: AccountId): Account? = lock.withLock { AccountCurrentProjection.find(accountId) }

    override fun list(query: AccountQuery): List<Account> = lock.withLock { AccountCurrentProjection.list(query) }

    override fun count(query: AccountQuery): Count = lock.withLock { AccountCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            AccountCurrentProjection.clear()
        }
    }

    override fun close() {
    }
}
