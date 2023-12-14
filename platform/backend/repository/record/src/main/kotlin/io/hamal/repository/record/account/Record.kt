package io.hamal.repository.record.account

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountType
import io.hamal.lib.domain.vo.PasswordSalt
import io.hamal.lib.domain.vo.RecordedAt
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class AccountRecord(
    @Transient
    override var sequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<AccountId>()

@Serializable
@SerialName("AccountCreatedRecord")
data class AccountCreatedRecord(
    override val entityId: AccountId,
    override val cmdId: CmdId,
    val salt: PasswordSalt,
    val type: AccountType
) : AccountRecord()

@Serializable
@SerialName("AccountConvertedRecord")
data class AccountConvertedRecord(
    override val entityId: AccountId,
    override val cmdId: CmdId,
) : AccountRecord()