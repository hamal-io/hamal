package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.Email
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.domain.vo.FeedbackMessage
import kotlinx.serialization.Serializable

@Serializable
data class Feedback(
    override val id: FeedbackId,
    override val updatedAt: UpdatedAt,
    val cmdId: CmdId,
    val mood: FeedbackMood,
    val message: FeedbackMessage,
    val email: Email,
    val accountId: AccountId
) : DomainObject<FeedbackId>

interface FeedbackRepository : FeedbackCmdRepository, FeedbackQueryRepository

interface FeedbackCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Feedback

    data class CreateCmd(
        val id: CmdId,
        val feedbackId: FeedbackId,
        val mood: FeedbackMood,
        val message: FeedbackMessage,
        val email: Email = Email(""),
        val accountId: AccountId
    )
}

interface FeedbackQueryRepository {
    fun get(feedbackId: FeedbackId) = find(feedbackId)
        ?: throw NoSuchElementException("Feedback not found")

    fun find(feedbackId: FeedbackId): Feedback?
    fun list(query: FeedbackQuery): List<Feedback>
    fun count(query: FeedbackQuery): ULong

    data class FeedbackQuery(
        var afterId: FeedbackId = FeedbackId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var feedbackIds: List<FeedbackId> = listOf()
    )
}
