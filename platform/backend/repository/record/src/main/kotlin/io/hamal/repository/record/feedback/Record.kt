package io.hamal.repository.record.feedback

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class FeedbackRecord(
    @Transient
    override var sequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<FeedbackId>()

@Serializable
@SerialName("FeedbackCreatedRecord")
data class FeedbackCreatedRecord(
    override val entityId: FeedbackId,
    override val cmdId: CmdId,
    val mood: FeedbackMood,
    val message: FeedbackMessage,
    val email: Email,
    val accountId: AccountId
) : FeedbackRecord()