package io.hamal.repository.record.feedback

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.domain.vo.FeedbackMessage
import io.hamal.repository.api.Feedback
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

data class FeedbackEntity(
    override val cmdId: CmdId,
    override val id: FeedbackId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,

    var mood: FeedbackMood? = null,
    var message: FeedbackMessage? = null,
    var accountId: AccountId? = null

) : RecordEntity<FeedbackId, FeedbackRecord, Feedback> {
    override fun apply(rec: FeedbackRecord): FeedbackEntity {
        return when (rec) {
            is FeedbackRecord.Created -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                sequence = rec.sequence(),
                mood = rec.mood,
                message = rec.message,
                accountId = rec.accountId,
                recordedAt = rec.recordedAt()
            )
        }
    }

    override fun toDomainObject(): Feedback {
        return Feedback(
            cmdId = cmdId,
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            mood = mood!!,
            message = message!!,
            accountId = accountId
        )
    }
}

fun List<FeedbackRecord>.createEntity(): FeedbackEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is FeedbackRecord.Created)

    var result = FeedbackEntity(
        id = firstRecord.entityId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence(),
        recordedAt = firstRecord.recordedAt(),
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateFeedbackFromRecords : CreateDomainObject<FeedbackId, FeedbackRecord, Feedback> {
    override fun invoke(recs: List<FeedbackRecord>): Feedback {
        return recs.createEntity().toDomainObject()
    }
}
