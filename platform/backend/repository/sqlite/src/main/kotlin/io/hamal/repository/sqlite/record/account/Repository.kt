package io.hamal.repository.sqlite.record.account

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountCmdRepository
import io.hamal.repository.api.AccountCmdRepository.CreateCmd
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import io.hamal.repository.api.AccountRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.account.AccountEntity
import io.hamal.repository.record.account.AccountRecord
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

internal object CreateAccount : CreateDomainObject<AccountId, AccountRecord, Account> {
    override fun invoke(recs: List<AccountRecord>): Account {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()

        check(firstRecord is AccountRecord.Created)

        var result = AccountEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            sequence = firstRecord.sequence(),
            type = firstRecord.type,
            salt = firstRecord.salt,
            recordedAt = firstRecord.recordedAt()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class AccountSqliteRepository(
    path: Path
) : RecordSqliteRepository<AccountId, AccountRecord, Account>(
    path = path,
    filename = "account.db",
    createDomainObject = CreateAccount,
    recordClass = AccountRecord::class,
    projections = listOf(ProjectionCurrent)
), AccountRepository {


    override fun create(cmd: CreateCmd): Account {
        val accountId = cmd.accountId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, accountId)) {
                versionOf(accountId, cmdId)
            } else {
                store(
                    AccountRecord.Created(
                        cmdId = cmdId,
                        entityId = accountId,
                        type = cmd.accountType,
                        salt = cmd.salt,
                    )
                )

                currentVersion(accountId)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun convert(cmd: AccountCmdRepository.ConvertCmd): Account {
        val accountId = cmd.accountId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, accountId)) {
                versionOf(accountId, cmdId)
            } else {
                store(
                    AccountRecord.Converted(
                        cmdId = cmdId,
                        entityId = accountId
                    )
                )
                currentVersion(accountId)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun find(accountId: AccountId): Account? {
        return ProjectionCurrent.find(connection, accountId)
    }

    override fun list(query: AccountQuery): List<Account> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: AccountQuery): Count {
        return ProjectionCurrent.count(connection, query)
    }
}