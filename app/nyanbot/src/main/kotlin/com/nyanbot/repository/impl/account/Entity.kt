package com.nyanbot.repository.impl.account

import com.nyanbot.repository.Account
import com.nyanbot.repository.record.CreateDomainObject
import com.nyanbot.repository.record.RecordEntity
import com.nyanbot.repository.record.RecordSequence
import com.nyanbot.repository.record.RecordedAt
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountType
import io.hamal.lib.domain.vo.PasswordSalt


data class AccountEntity(
    override val id: AccountId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,

    var type: AccountType?,
    var salt: PasswordSalt?

) : RecordEntity<AccountId, AccountRecord, Account> {

    override fun apply(rec: AccountRecord): AccountEntity {
        return when (rec) {
            is AccountRecord.Created -> copy(
                id = rec.entityId,
                sequence = rec.sequence(),
                type = rec.type,
                salt = rec.salt,
                recordedAt = rec.recordedAt()
            )

            is AccountRecord.Converted -> copy(
                type = AccountType.User,
                recordedAt = rec.recordedAt()
            )
        }
    }


    override fun toDomainObject(): Account {
        return Account(
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            type = type!!,
            salt = salt!!
        )
    }
}

fun List<AccountRecord>.createEntity(): AccountEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is AccountRecord.Created)

    var result = AccountEntity(
        id = firstRecord.entityId,
        sequence = firstRecord.sequence(),
        type = firstRecord.type,
        salt = firstRecord.salt,
        recordedAt = firstRecord.recordedAt()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateAccountFromRecords : CreateDomainObject<AccountId, AccountRecord, Account> {
    override fun invoke(recs: List<AccountRecord>): Account {
        return recs.createEntity().toDomainObject()
    }
}