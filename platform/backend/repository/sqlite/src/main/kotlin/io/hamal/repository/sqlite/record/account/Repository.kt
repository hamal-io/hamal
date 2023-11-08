package io.hamal.repository.sqlite.record.account

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountCmdRepository
import io.hamal.repository.api.AccountCmdRepository.CreateCmd
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import io.hamal.repository.api.AccountRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.account.AccountConvertedRecord
import io.hamal.repository.record.account.AccountCreatedRecord
import io.hamal.repository.record.account.AccountEntity
import io.hamal.repository.record.account.AccountRecord
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import java.nio.file.Path

internal object CreateAccount : CreateDomainObject<AccountId, AccountRecord, Account> {
    override fun invoke(recs: List<AccountRecord>): Account {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is AccountCreatedRecord)

        var result = AccountEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            sequence = firstRecord.sequence(),
            type = firstRecord.type,
            name = firstRecord.name,
            email = firstRecord.email,
            salt = firstRecord.salt,
            recordedAt = firstRecord.recordedAt
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class SqliteAccountRepository(
    config: Config
) : SqliteRecordRepository<AccountId, AccountRecord, Account>(
    config = config,
    createDomainObject = CreateAccount,
    recordClass = AccountRecord::class,
    projections = listOf(ProjectionCurrent, ProjectionUniqueEmail, ProjectionUniqueName)
), AccountRepository {

    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "account.db"
    }

    override fun create(cmd: CreateCmd): Account {
        val accountId = cmd.accountId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, accountId)) {
                versionOf(accountId, cmdId)
            } else {
                store(
                    AccountCreatedRecord(
                        cmdId = cmdId,
                        entityId = accountId,
                        type = cmd.accountType,
                        name = cmd.name,
                        email = cmd.email,
                        salt = cmd.salt,
                    )
                )

                currentVersion(accountId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueEmail.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
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
                val currentVersion = currentVersion(accountId)
                store(
                    AccountConvertedRecord(
                        cmdId = cmdId,
                        entityId = accountId,
                        name = cmd.name ?: currentVersion.name,
                        email = cmd.email,
                    )
                )
                currentVersion(accountId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueEmail.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun find(accountId: AccountId): Account? {
        return ProjectionCurrent.find(connection, accountId)
    }

    override fun find(accountName: AccountName): Account? {
        return ProjectionUniqueName.find(connection, accountName)?.let {
            return ProjectionCurrent.find(connection, it)
        }
    }

    override fun list(query: AccountQuery): List<Account> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: AccountQuery): ULong {
        return ProjectionCurrent.count(connection, query)
    }
}