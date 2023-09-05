package io.hamal.api.req.handler.trigger

import io.hamal.api.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain._enum.TriggerType.FixedRate
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.submitted_req.SubmittedCreateTriggerReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import kotlin.time.Duration.Companion.seconds

internal class CreateTriggerHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Creates FixedRateTrigger`() {
        createFunc(FuncId(2222), FuncName("SomeFunc"))

        testInstance(submitCreateFixedRateTriggerReq)

        verifySingleFixedRateTriggerExists()
    }

    @Test
    fun `Tries to create FixedRateTrigger, but func does not exists`() {
        val exception = assertThrows<NoSuchElementException> {
            testInstance(submitCreateFixedRateTriggerReq)
        }
        assertThat(exception.message, equalTo("Func not found"))

        verifyNoTriggerExists()
    }

    @Test
    fun `Creates EventTrigger`() {
        createTopic(TopicId(1111), TopicName("SomeTopic"))
        createFunc(FuncId(2222), FuncName("SomeFunc"))

        testInstance(submitCreateEventTriggerReq)

        verifySingleEventTriggerExists()
    }

    @Test
    fun `Tries to create EventTrigger, but func does not exists`() {
        val exception = assertThrows<NoSuchElementException> {
            testInstance(submitCreateEventTriggerReq)
        }
        assertThat(exception.message, equalTo("Func not found"))

        verifyNoTriggerExists()
    }

    @Test
    fun `Tries to create EventTrigger, but topic does not exists`() {
        createFunc(FuncId(2222), FuncName("SomeFunc"))

        val exception = assertThrows<NoSuchElementException> {
            testInstance(submitCreateEventTriggerReq)
        }
        assertThat(exception.message, equalTo("Topic not found"))

        verifyNoTriggerExists()
    }

    private fun verifySingleFixedRateTriggerExists() {
        triggerQueryRepository.list { types = setOf(FixedRate) }.also { triggers ->
            assertThat(triggers, hasSize(1))

            with(triggers.first()) {
                require(this is FixedRateTrigger)
                assertThat(id, equalTo(TriggerId(1234)))
                assertThat(name, equalTo(TriggerName("FixedRateTrigger")))
                assertThat(funcId, equalTo(FuncId(2222)))
                assertThat(duration, equalTo(42.seconds))
                assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
            }
        }
    }

    private fun verifySingleEventTriggerExists() {
        triggerQueryRepository.list { types = setOf(Event) }.also { triggers ->
            assertThat(triggers, hasSize(1))

            with(triggers.first()) {
                require(this is EventTrigger)
                assertThat(id, equalTo(TriggerId(1234)))
                assertThat(name, equalTo(TriggerName("EventTrigger")))
                assertThat(funcId, equalTo(FuncId(2222)))
                assertThat(topicId, equalTo(TopicId(1111)))
                assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
            }
        }
    }

    private fun verifyNoTriggerExists() {
        triggerQueryRepository.list { }.also { triggers ->
            assertThat(triggers, empty())
        }
    }

    @Autowired
    private lateinit var testInstance: CreateTriggerHandler

    private val submitCreateFixedRateTriggerReq by lazy {
        SubmittedCreateTriggerReq(
            reqId = ReqId(1),
            status = Submitted,
            type = FixedRate,
            id = TriggerId(1234),
            groupId = testGroup.id,
            funcId = FuncId(2222),
            name = TriggerName("FixedRateTrigger"),
            duration = 42.seconds,
            inputs = TriggerInputs(
                MapType(mutableMapOf("hamal" to StringType("rocks")))
            ),
        )
    }

    private val submitCreateEventTriggerReq by lazy {
        SubmittedCreateTriggerReq(
            reqId = ReqId(1),
            status = Submitted,
            type = Event,
            id = TriggerId(1234),
            groupId = testGroup.id,
            funcId = FuncId(2222),
            topicId = TopicId(1111),
            name = TriggerName("EventTrigger"),
            inputs = TriggerInputs(
                MapType(mutableMapOf("hamal" to StringType("rocks"))),
            )
        )
    }
}