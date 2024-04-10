package com.nyanbot.repository.impl.account

import com.nyanbot.repository.Account
import com.nyanbot.repository.AccountCmdRepository.CreateCmd
import com.nyanbot.repository.AccountQueryRepository.AccountQuery
import com.nyanbot.repository.AccountRepository
import com.nyanbot.repository.impl.RecordSqliteRepository
import com.nyanbot.repository.record.CreateDomainObject
import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.AccountId
import java.nio.file.Path

internal object CreateAccount : CreateDomainObject<AccountId, AccountRecord, Account> {
    override fun invoke(recs: List<AccountRecord>): Account {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()

        check(firstRecord is AccountRecord.Created)

        var result = AccountEntity(
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
        return tx {
            store(
                AccountRecord.Created(
                    entityId = accountId,
                    type = cmd.accountType,
                    salt = cmd.salt,
                )
            )

            currentVersion(accountId).also { ProjectionCurrent.upsert(this, it) }
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