package io.hamal.repository

import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.domain._enum.FeedbackMoods.Angry
import io.hamal.lib.domain.vo.AccountId.Companion.AccountId
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.domain.vo.FeedbackId.Companion.FeedbackId
import io.hamal.lib.domain.vo.FeedbackMessage.Companion.FeedbackMessage
import io.hamal.lib.domain.vo.FeedbackMood.Companion.FeedbackMood
import io.hamal.repository.api.Feedback
import io.hamal.repository.api.FeedbackCmdRepository.CreateCmd
import io.hamal.repository.api.FeedbackQueryRepository.FeedbackQuery
import io.hamal.repository.api.FeedbackRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class FeedbackTest : AbstractUnitTest() {

    @Nested
    inner class CreateTest {
        @TestFactory
        fun `Creates Feedback`() = runWith(FeedbackRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdGen(),
                    feedbackId = FeedbackId(1),
                    mood = FeedbackMood(Angry),
                    message = FeedbackMessage("Please let me pay for this"),
                    accountId = AccountId(2)
                )
            )

            with(result) {
                assertThat(id, equalTo(FeedbackId(1)))
                assertThat(mood, equalTo(Angry))
                assertThat(message, equalTo(FeedbackMessage("Please let me pay for this")))
                assertThat(accountId, equalTo(AccountId(2)))
            }
            verifyCount(1)
        }
    }


    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get feedback by id`() = runWith(FeedbackRepository::class) {
            createFeedback(FeedbackId(3))
            val result = get(FeedbackId(3))
            assertThat(result.id, equalTo(FeedbackId(3)))
            verifyFeedback(result)
        }

        @TestFactory
        fun `Tries to get feedback by id that does not exist`() = runWith(FeedbackRepository::class) {
            createFeedback(FeedbackId(1))
            val exception = assertThrows<NoSuchElementException> { get(FeedbackId(111111)) }
            assertThat(exception.message, equalTo("Feedback not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find feedback by id`() = runWith(FeedbackRepository::class) {
            createFeedback(FeedbackId(3))
            val result = find(FeedbackId(3))
            assertThat(result!!.id, equalTo(FeedbackId(3)))
            verifyFeedback(result)
        }

        @TestFactory
        fun `Tries to find feedback by id that does not exist`() = runWith(FeedbackRepository::class) {
            createFeedback(FeedbackId(1))
            val result = find(FeedbackId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ClearTest {
        @TestFactory
        fun `Nothing to clear`() = runWith(FeedbackRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(FeedbackRepository::class) {
            setup()
            clear()
            verifyCount(0)
        }
    }

    @Nested
    inner class ListAndCountTest {
        @TestFactory
        fun `By ids`() = runWith(FeedbackRepository::class) {
            setup()

            val result = list(
                FeedbackQuery(
                    feedbackIds = listOf(FeedbackId(111111), FeedbackId(3))
                )
            )

            assertThat(result, hasSize(1))
            verifyFeedback(result[0])
        }

        @TestFactory
        fun `Limit`() = runWith(FeedbackRepository::class) {
            setup()

            val query = FeedbackQuery(
                feedbackIds = listOf(),
                limit = Limit(3)
            )
            assertThat(count(query), equalTo(Count(4)))
            assertThat(list(query), hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(FeedbackRepository::class) {
            setup()

            val query = FeedbackQuery(
                afterId = FeedbackId(2),
                feedbackIds = listOf(),
                limit = Limit(1)
            )
            assertThat(count(query), equalTo(Count(1)))
            assertThat(list(query), hasSize(1))

        }
    }

    private fun FeedbackRepository.verifyCount(expected: Int) {
        verifyCount(expected) { }
    }

    private fun FeedbackRepository.verifyCount(expected: Int, block: FeedbackQuery.() -> Unit) {
        val counted = count(FeedbackQuery(feedbackIds = listOf()).also(block))
        assertThat("number of feedbacks expected", counted, equalTo(Count(expected)))
    }

    private fun FeedbackRepository.createFeedback(
        feedbackId: FeedbackId
    ) {
        create(
            CreateCmd(
                id = CmdGen(),
                feedbackId = feedbackId,
                mood = FeedbackMood(Angry),
                message = FeedbackMessage("feedback-message"),
                accountId = AccountId(2)
            )
        )

    }

    private fun verifyFeedback(feedback: Feedback) {
        with(feedback) {
            assertThat(mood, equalTo(Angry))
            assertThat(message, equalTo(FeedbackMessage("feedback-message")))
            assertThat(accountId, equalTo(AccountId(2)))
        }
    }

    private fun FeedbackRepository.setup() {
        createFeedback(feedbackId = FeedbackId(1))
        createFeedback(feedbackId = FeedbackId(2))
        createFeedback(feedbackId = FeedbackId(3))
        createFeedback(feedbackId = FeedbackId(4))
    }
}