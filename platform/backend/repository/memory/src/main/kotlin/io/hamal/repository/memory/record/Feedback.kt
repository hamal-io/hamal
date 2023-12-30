package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.repository.api.Feedback
import io.hamal.repository.api.FeedbackCmdRepository.CreateCmd
import io.hamal.repository.api.FeedbackQueryRepository.FeedbackQuery
import io.hamal.repository.api.FeedbackRepository
import io.hamal.repository.record.feedback.CreateFeedbackFromRecords
import io.hamal.repository.record.feedback.FeedbackCreatedRecord
import io.hamal.repository.record.feedback.FeedbackRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object FeedbackCurrentProjection {
    private val projection = mutableMapOf<FeedbackId, Feedback>()

    fun apply(feedback: Feedback) {
        projection[feedback.id] = feedback
    }

    fun find(feedbackId: FeedbackId): Feedback? = projection[feedbackId]

    fun list(query: FeedbackQuery): List<Feedback> {
        return projection.filter { query.feedbackIds.isEmpty() || it.key in query.feedbackIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: FeedbackQuery): ULong {
        return projection.filter { query.feedbackIds.isEmpty() || it.key in query.feedbackIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .dropWhile { it.id >= query.afterId }
            .count()
            .toULong()
    }

    fun clear() {
        projection.clear()
    }
}

class FeedbackMemoryRepository : RecordMemoryRepository<FeedbackId, FeedbackRecord, Feedback>(
    createDomainObject = CreateFeedbackFromRecords,
    recordClass = FeedbackRecord::class
), FeedbackRepository {
    private val lock = ReentrantLock()

    override fun create(cmd: CreateCmd): Feedback {
        return lock.withLock {
            val feedbackId = cmd.feedbackId
            val cmdId = cmd.id
            if (commandAlreadyApplied(cmdId, feedbackId)) {
                versionOf(feedbackId, cmdId)
            } else {
                store(
                    FeedbackCreatedRecord(
                        entityId = feedbackId,
                        cmdId = cmdId,
                        mood = cmd.mood,
                        message = cmd.message,
                        accountId = cmd.accountId
                    )
                )
            }
            (currentVersion(feedbackId)).also(FeedbackCurrentProjection::apply)
        }
    }

    override fun find(feedbackId: FeedbackId): Feedback? = lock.withLock { FeedbackCurrentProjection.find(feedbackId) }

    override fun list(query: FeedbackQuery): List<Feedback> = lock.withLock { FeedbackCurrentProjection.list(query) }

    override fun count(query: FeedbackQuery): ULong = lock.withLock { FeedbackCurrentProjection.count(query) }

    override fun clear() = FeedbackCurrentProjection.clear()

    override fun close() {}
}