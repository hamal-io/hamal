package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountCmdRepository.CreateCmd
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import io.hamal.repository.api.AccountRepository
import io.hamal.repository.record.account.AccountCreationRecord
import io.hamal.repository.record.account.AccountRecord
import io.hamal.repository.record.account.CreateAccountFromRecords
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentAccountProjection {
    private val projection = mutableMapOf<AccountId, Account>()
    fun apply(account: Account) {

        require(projection.values.asSequence()
            .map { it.name }
            .none { it == account.name }
        ) { "${account.name} already exists" }

        if (account.email != null) {
            require(projection.values.asSequence()
                .map { it.email }
                .none { it == account.email }
            ) { "${account.email} already exists" }
        }


        projection[account.id] = account
    }

    fun find(accountId: AccountId): Account? = projection[accountId]

    fun find(accountName: AccountName): Account? = projection.values.find { it.name == accountName }

    fun list(query: AccountQuery): List<Account> {
        return projection.filter { query.accountIds.isEmpty() || it.key in query.accountIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: AccountQuery): ULong {
        return projection.filter { query.accountIds.isEmpty() || it.key in query.accountIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .dropWhile { it.id >= query.afterId }
            .count()
            .toULong()
    }

    fun clear() {
        projection.clear()
    }
}

class MemoryAccountRepository : MemoryRecordRepository<AccountId, AccountRecord, Account>(
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
                    AccountCreationRecord(
                        cmdId = cmd.id,
                        entityId = accountId,
                        type = cmd.accountType,
                        name = cmd.name,
                        email = cmd.email,
                        salt = cmd.salt
                    )
                )
                (currentVersion(accountId)).also(CurrentAccountProjection::apply)
            }
        }
    }

    override fun find(accountId: AccountId): Account? = lock.withLock { CurrentAccountProjection.find(accountId) }

    override fun find(accountName: AccountName): Account? = lock.withLock { CurrentAccountProjection.find(accountName) }

    override fun list(query: AccountQuery): List<Account> = lock.withLock { CurrentAccountProjection.list(query) }

    override fun count(query: AccountQuery): ULong = lock.withLock { CurrentAccountProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            CurrentAccountProjection.clear()
        }
    }

    override fun close() {
    }
}
