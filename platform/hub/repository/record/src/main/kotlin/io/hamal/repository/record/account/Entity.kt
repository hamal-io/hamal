package io.hamal.repository.record.account

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Account
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence

data class AccountEntity(
    override val id: AccountId,
    override val cmdId: CmdId,
    override val sequence: RecordSequence,

    var type: AccountType?,
    var name: AccountName?,
    var email: AccountEmail?,
    var salt: PasswordSalt?

) : RecordEntity<AccountId, AccountRecord, Account> {

    override fun apply(rec: AccountRecord): AccountEntity {
        return when (rec) {
            is AccountCreationRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                type = rec.type,
                name = rec.name,
                email = rec.email,
                salt = rec.salt
            )
        }
    }

    override fun toDomainObject(): Account {
        return Account(
            cmdId = cmdId,
            id = id,
            type = type!!,
            name = name!!,
            email = email,
            salt = salt!!
        )
    }
}

fun List<AccountRecord>.createEntity(): AccountEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is AccountCreationRecord)

    var result = AccountEntity(
        id = firstRecord.entityId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence(),
        type = firstRecord.type,
        name = firstRecord.name,
        email = firstRecord.email,
        salt = firstRecord.salt
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