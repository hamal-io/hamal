package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountCmdRepository
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import io.hamal.repository.api.AccountRepository
import io.hamal.repository.record.account.AccountCreationRecord
import io.hamal.repository.record.account.AccountRecord
import io.hamal.repository.record.account.createEntity
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentAccountProjection {
    private val projection = mutableMapOf<AccountId, Account>()
    fun apply(account: Account) {

        check(projection.values.asSequence()
            .map { it.name }
            .none { it == account.name }
        ) { "Account name ${account.name} not unique" }

        projection[account.id] = account
    }

    fun find(accountId: AccountId): Account? = projection[accountId]

    fun list(afterId: AccountId, limit: Limit): List<Account> {
        return projection.keys.sorted()
            .reversed()
            .dropWhile { it >= afterId }
            .take(limit.value)
            .mapNotNull { find(it) }
    }

    fun clear() {
        projection.clear()
    }
}

object MemoryAccountRepository : BaseRecordRepository<AccountId, AccountRecord>(), AccountRepository {
    private val lock = ReentrantLock()
    override fun create(cmd: AccountCmdRepository.CreateCmd): Account {
        return lock.withLock {
            val accountId = cmd.accountId
            if (contains(accountId)) {
                versionOf(accountId, cmd.id)
            } else {
                addRecord(
                    AccountCreationRecord(
                        entityId = accountId,
                        cmdId = cmd.id,
                        name = cmd.name,
                        email = cmd.email,
                    )
                )
                (currentVersion(accountId)).also(CurrentAccountProjection::apply)
            }
        }
    }

    override fun find(accountId: AccountId): Account? = CurrentAccountProjection.find(accountId)

    override fun list(block: AccountQuery.() -> Unit): List<Account> {
        val query = AccountQuery().also(block)
        return CurrentAccountProjection.list(query.afterId, query.limit)
    }

    override fun clear() {
        super.clear()
        CurrentAccountProjection.clear()
    }
}

private fun MemoryAccountRepository.currentVersion(id: AccountId): Account {
    return listRecords(id)
        .createEntity()
        .toDomainObject()
}

private fun MemoryAccountRepository.commandAlreadyApplied(id: AccountId, cmdId: CmdId) =
    listRecords(id).any { it.cmdId == cmdId }

private fun MemoryAccountRepository.versionOf(id: AccountId, cmdId: CmdId): Account {
    return listRecords(id).takeWhileInclusive { it.cmdId != cmdId }
        .createEntity()
        .toDomainObject()
}