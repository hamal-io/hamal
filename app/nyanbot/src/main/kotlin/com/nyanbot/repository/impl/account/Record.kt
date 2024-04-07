package com.nyanbot.repository.impl.account

import com.nyanbot.RecordAdapter
import com.nyanbot.repository.record.Record
import com.nyanbot.repository.record.RecordSequence
import com.nyanbot.repository.record.RecordedAt
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountType
import io.hamal.lib.domain.vo.PasswordSalt

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
        val salt: PasswordSalt,
        val type: AccountType
    ) : AccountRecord()

    data class Converted(
        override val entityId: AccountId,
    ) : AccountRecord()
}

