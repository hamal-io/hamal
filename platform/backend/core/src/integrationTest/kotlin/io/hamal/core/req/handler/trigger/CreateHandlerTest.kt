package io.hamal.core.request.handler.trigger

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.HookMethod.Get
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain._enum.TriggerType.*
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain.request.TriggerCreateRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.CronTrigger
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.HookTrigger
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import kotlin.time.Duration.Companion.seconds

internal class TriggerCreateHandlerTest : BaseReqHandlerTest() {

    @Nested
    inner class FixedRateTest {
        @Test
        fun `Creates trigger`() {
            createFunc(FuncId(2222), FuncName("SomeFunc"))

            testInstance(submitCreateFixedRateTriggerReq)

            verifySingleFixedRateTriggerExists()
        }

        @Test
        fun `Tries to create trigger, but func does not exists`() {
            val exception = assertThrows<NoSuchElementException> {
                testInstance(submitCreateFixedRateTriggerReq)
            }
            assertThat(exception.message, equalTo("Func not found"))

            verifyNoTriggerExists()
        }
    }

    @Nested
    inner class EventTriggerTest {
        @Test
        fun `Creates trigger`() {
            createTopic(TopicId(1111), TopicName("SomeTopic"))
            createFunc(FuncId(2222), FuncName("SomeFunc"))

            testInstance(submitCreateEventTriggerReq)

            verifySingleEventTriggerExists()
        }

        @Test
        fun `Tries to create trigger, but func does not exists`() {
            val exception = assertThrows<NoSuchElementException> {
                testInstance(submitCreateEventTriggerReq)
            }
            assertThat(exception.message, equalTo("Func not found"))

            verifyNoTriggerExists()
        }

        @Test
        fun `Tries to create trigger, but topic does not exists`() {
            createFunc(FuncId(2222), FuncName("SomeFunc"))

            val exception = assertThrows<NoSuchElementException> {
                testInstance(submitCreateEventTriggerReq)
            }
            assertThat(exception.message, equalTo("Topic not found"))

            verifyNoTriggerExists()
        }
    }

    @Nested
    inner class HookTriggerTest {

        @Test
        fun `Creates trigger`() {
            createHook(HookId(1111))
            createFunc(FuncId(2222), FuncName("SomeFunc"))

            testInstance(submitCreateHookTriggerReq)

            verifySingleHookTriggerExists()
        }

        @Test
        fun `Tries to create a trigger when hookId, funcId, hookMethod already exist`() {
            createHook(HookId(1111))
            createFunc(FuncId(2222), FuncName("SomeFunc"))
            testInstance(submitCreateHookTriggerReq)

            val exception = assertThrows<IllegalArgumentException> {
                testInstance(
                    TriggerCreateRequested(
                        id = RequestId(12345),
                        status = Submitted,
                        triggerType = Hook,
                        triggerId = TriggerId(2),
                        flowId = testFlow.id,
                        groupId = testGroup.id,
                        funcId = FuncId(2222),
                        hookId = HookId(1111),
                        name = TriggerName("HookTriggerInvalid"),
                        inputs = TriggerInputs(
                            HotObject.builder().set("hamal", "rocks").build(),
                        ),
                        hookMethod = Get
                    )
                )
            }
            assertThat(exception.message, equalTo("Trigger already exists"))

            verifySingleHookTriggerExists()
        }

        @Test
        fun `Tries to create trigger, but func does not exist`() {
            val exception = assertThrows<NoSuchElementException> {
                testInstance(submitCreateHookTriggerReq)
            }
            assertThat(exception.message, equalTo("Func not found"))

            verifyNoTriggerExists()
        }

        @Test
        fun `Tries to create trigger, but hook does not exist`() {
            createFunc(FuncId(2222), FuncName("SomeFunc"))

            val exception = assertThrows<NoSuchElementException> {
                testInstance(submitCreateHookTriggerReq)
            }
            assertThat(exception.message, equalTo("Hook not found"))

            verifyNoTriggerExists()
        }
    }

    @Nested
    inner class CronTriggerTest {
        @Test
        fun `Creates trigger`() {
            createFunc(FuncId(2222), FuncName("SomeFunc"))
            testInstance(submitCreateCronTriggerReq)
            verifySingleCronTriggerExists()
        }

        @Test
        fun `Tries to create trigger, but func does not exists`() {
            val exception = assertThrows<NoSuchElementException> {
                testInstance(submitCreateCronTriggerReq)
            }
            assertThat(exception.message, equalTo("Func not found"))

            verifyNoTriggerExists()
        }
    }

    private fun verifySingleFixedRateTriggerExists() {
        triggerQueryRepository.list(TriggerQuery(types = listOf(FixedRate), groupIds = listOf())).also { triggers ->
            assertThat(triggers, hasSize(1))

            with(triggers.first()) {
                require(this is FixedRateTrigger)
                assertThat(id, equalTo(TriggerId(1234)))
                assertThat(name, equalTo(TriggerName("FixedRateTrigger")))
                assertThat(funcId, equalTo(FuncId(2222)))
                assertThat(duration, equalTo(42.seconds))
                assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
            }
        }
    }

    private fun verifySingleEventTriggerExists() {
        triggerQueryRepository.list(TriggerQuery(types = listOf(Event), groupIds = listOf())).also { triggers ->
            assertThat(triggers, hasSize(1))

            with(triggers.first()) {
                require(this is EventTrigger)
                assertThat(id, equalTo(TriggerId(1234)))
                assertThat(name, equalTo(TriggerName("EventTrigger")))
                assertThat(funcId, equalTo(FuncId(2222)))
                assertThat(topicId, equalTo(TopicId(1111)))
                assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
            }
        }
    }

    private fun verifySingleHookTriggerExists() {
        triggerQueryRepository.list(TriggerQuery(types = listOf(Hook), groupIds = listOf())).also { triggers ->
            assertThat(triggers, hasSize(1))

            with(triggers.first()) {
                require(this is HookTrigger)
                assertThat(id, equalTo(TriggerId(1234)))
                assertThat(name, equalTo(TriggerName("HookTrigger")))
                assertThat(funcId, equalTo(FuncId(2222)))
                assertThat(hookId, equalTo(HookId(1111)))
                assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
            }
        }
    }

    private fun verifySingleCronTriggerExists() {
        triggerQueryRepository.list(TriggerQuery(types = listOf(Cron), groupIds = listOf())).also { triggers ->
            assertThat(triggers, hasSize(1))

            with(triggers.first()) {
                require(this is CronTrigger)
                assertThat(id, equalTo(TriggerId(1234)))
                assertThat(name, equalTo(TriggerName("CronTrigger")))
                assertThat(funcId, equalTo(FuncId(2222)))
                assertThat(cron, equalTo(CronPattern("0 0 * * * *")))
                assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
            }
        }
    }


    private fun verifyNoTriggerExists() {
        triggerQueryRepository.list(TriggerQuery(groupIds = listOf())).also { triggers ->
            assertThat(triggers, empty())
        }
    }

    @Autowired
    private lateinit var testInstance: TriggerCreateHandler

    private val submitCreateFixedRateTriggerReq by lazy {
        TriggerCreateRequested(
            id = RequestId(1),
            status = Submitted,
            triggerType = FixedRate,
            triggerId = TriggerId(1234),
            flowId = testFlow.id,
            groupId = testGroup.id,
            funcId = FuncId(2222),
            name = TriggerName("FixedRateTrigger"),
            duration = 42.seconds,
            inputs = TriggerInputs(
                HotObject.builder().set("hamal", "rocks").build()
            ),
        )
    }

    private val submitCreateEventTriggerReq by lazy {
        TriggerCreateRequested(
            id = RequestId(1),
            status = Submitted,
            triggerType = Event,
            triggerId = TriggerId(1234),
            flowId = testFlow.id,
            groupId = testGroup.id,
            funcId = FuncId(2222),
            topicId = TopicId(1111),
            name = TriggerName("EventTrigger"),
            inputs = TriggerInputs(
                HotObject.builder().set("hamal", "rocks").build(),
            )
        )
    }

    private val submitCreateHookTriggerReq by lazy {
        TriggerCreateRequested(
            id = RequestId(1),
            status = Submitted,
            triggerType = Hook,
            triggerId = TriggerId(1234),
            flowId = testFlow.id,
            groupId = testGroup.id,
            funcId = FuncId(2222),
            hookId = HookId(1111),
            name = TriggerName("HookTrigger"),
            inputs = TriggerInputs(
                HotObject.builder().set("hamal", "rocks").build(),
            ),
            hookMethod = Get
        )
    }

    private val submitCreateCronTriggerReq by lazy {
        TriggerCreateRequested(
            id = RequestId(1),
            status = Submitted,
            triggerType = Cron,
            triggerId = TriggerId(1234),
            flowId = testFlow.id,
            groupId = testGroup.id,
            funcId = FuncId(2222),
            name = TriggerName("CronTrigger"),
            cron = CronPattern("0 0 * * * *"),
            inputs = TriggerInputs(
                HotObject.builder().set("hamal", "rocks").build()
            ),
        )
    }
}