package io.hamal.repository.record.account

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountType
import io.hamal.lib.domain.vo.PasswordSalt
import io.hamal.repository.api.Account
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

data class AccountEntity(
    override val id: AccountId,
    override val cmdId: CmdId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,

    var type: AccountType?,
    var salt: PasswordSalt?

) : RecordEntity<AccountId, AccountRecord, Account> {

    override fun apply(rec: AccountRecord): AccountEntity {
        return when (rec) {
            is AccountRecord.Created -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                type = rec.type,
                salt = rec.salt,
                recordedAt = rec.recordedAt()
            )

            is AccountRecord.Converted -> copy(
                cmdId = rec.cmdId,
                type = AccountType.User,
                recordedAt = rec.recordedAt()
            )
        }
    }


    override fun toDomainObject(): Account {
        return Account(
            cmdId = cmdId,
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
        cmdId = firstRecord.cmdId,
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