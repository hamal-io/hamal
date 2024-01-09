package io.hamal.repository.record.feedback

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.domain.vo.FeedbackMessage
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class FeedbackRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<FeedbackId>() {
    internal object Adapter : RecordAdapter<FeedbackRecord>(
        listOf(
            FeedbackCreatedRecord::class
        )
    )
}

data class FeedbackCreatedRecord(
    override val entityId: FeedbackId,
    override val cmdId: CmdId,
    val mood: FeedbackMood,
    val message: FeedbackMessage,
    val accountId: AccountId?
) : FeedbackRecord()