package io.hamal.repository.sqlite.record.feedback

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.repository.api.Feedback
import io.hamal.repository.api.FeedbackCmdRepository
import io.hamal.repository.api.FeedbackQueryRepository.FeedbackQuery
import io.hamal.repository.api.FeedbackRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.feedback.FeedbackEntity
import io.hamal.repository.record.feedback.FeedbackRecord
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

internal object CreateFeedback : CreateDomainObject<FeedbackId, FeedbackRecord, Feedback> {
    override fun invoke(recs: List<FeedbackRecord>): Feedback {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is FeedbackRecord.Created)

        var result = FeedbackEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            sequence = firstRecord.sequence(),
            recordedAt = firstRecord.recordedAt()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class FeedbackSqliteRepository(
    path: Path
) : RecordSqliteRepository<FeedbackId, FeedbackRecord, Feedback>(
    path = path,
    filename = "feedback.db",
    createDomainObject = CreateFeedback,
    recordClass = FeedbackRecord::class,
    projections = listOf(
        ProjectionCurrent
    )
), FeedbackRepository {

    override fun create(cmd: FeedbackCmdRepository.CreateCmd): Feedback {
        val feedbackId = cmd.feedbackId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, feedbackId)) {
                versionOf(feedbackId, cmdId)
            } else {
                store(
                    FeedbackRecord.Created(
                        entityId = feedbackId,
                        cmdId = cmdId,
                        mood = cmd.mood,
                        message = cmd.message,
                        accountId = cmd.accountId
                    )
                )
                currentVersion(feedbackId).also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun find(feedbackId: FeedbackId): Feedback? {
        return ProjectionCurrent.find(connection, feedbackId)
    }

    override fun list(query: FeedbackQuery): List<Feedback> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: FeedbackQuery): Count {
        return ProjectionCurrent.count(connection, query)
    }
}