package io.hamal.repository.record.account

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountEmail
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class AccountRecord(
    @Transient
    override var sequence: RecordSequence? = null
) : Record<AccountId>()

@Serializable
@SerialName("ACR")
data class AccountCreationRecord(
    override val entityId: AccountId,
    override val cmdId: CmdId,
    val name: AccountName,
    val email: AccountEmail?
) : AccountRecord()