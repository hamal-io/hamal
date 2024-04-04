package io.hamal.repository.record.account

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountType
import io.hamal.lib.domain.vo.PasswordSalt
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class AccountRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<AccountId>() {

    internal object Adapter : RecordAdapter<AccountRecord>(
        listOf(
            Created::class,
            Converted::class
        )
    )

    data class Created(
        override val entityId: AccountId,
        override val cmdId: CmdId,
        val salt: PasswordSalt,
        val type: AccountType
    ) : AccountRecord()

    data class Converted(
        override val entityId: AccountId,
        override val cmdId: CmdId,
    ) : AccountRecord()

    data class Updated(
        override val entityId: AccountId,
        override val cmdId: CmdId,
        val salt: PasswordSalt,
    ) : AccountRecord()
}

