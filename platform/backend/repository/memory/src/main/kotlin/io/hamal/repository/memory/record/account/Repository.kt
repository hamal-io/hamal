package io.hamal.repository.memory.record.account

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountCmdRepository.*
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import io.hamal.repository.api.AccountRepository
import io.hamal.repository.memory.record.RecordMemoryRepository
import io.hamal.repository.record.account.AccountRecord
import io.hamal.repository.record.account.CreateAccountFromRecords
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class AccountMemoryRepository : RecordMemoryRepository<AccountId, AccountRecord, Account>(
    createDomainObject = CreateAccountFromRecords,
    recordClass = AccountRecord::class,
    projections = listOf(ProjectionCurrent())
), AccountRepository {

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
                (currentVersion(accountId)).also(currentProjection::upsert)
            }
        }
    }

    override fun convert(cmd: ConvertCmd): Account {
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
                (currentVersion(accountId)).also(currentProjection::upsert)
            }
        }
    }

    override fun changePassword(accountId: AccountId, cmd: PasswordChangeCmd): Account {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, accountId)) {
                versionOf(accountId, cmd.id)
            } else {
                store(
                    AccountRecord.Updated(
                        entityId = accountId,
                        cmdId = cmd.id,
                        salt = cmd.salt
                    )
                )
                (currentVersion(accountId)).also(currentProjection::upsert)
            }
        }
    }

    override fun find(accountId: AccountId): Account? = lock.withLock { currentProjection.find(accountId) }

    override fun list(query: AccountQuery): List<Account> =
        lock.withLock { currentProjection.list(query) }

    override fun count(query: AccountQuery): Count =
        lock.withLock { currentProjection.count(query) }

    override fun close() {}

    private val lock = ReentrantLock()
    private val currentProjection = getProjection<ProjectionCurrent>()
}
