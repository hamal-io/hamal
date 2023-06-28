package io.hamal.backend.instance.req.handler.trigger

import io.hamal.backend.instance.req.handler.BaseReqHandlerIT
import io.hamal.lib.domain.EventTrigger
import io.hamal.lib.domain.FixedRateTrigger
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedCreateTriggerReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.base.Secret
import io.hamal.lib.domain.vo.base.SecretKey
import io.hamal.lib.domain.vo.base.SecretStore
import io.hamal.lib.domain.vo.base.SecretStoreIdentifier
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.TableValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import kotlin.time.Duration.Companion.seconds

internal class CreateTriggerHandlerIT : BaseReqHandlerIT() {

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
        triggerQueryRepository.list { types = setOf(TriggerType.FixedRate) }.also { triggers ->
            assertThat(triggers, hasSize(1))

            with(triggers.first()) {
                require(this is FixedRateTrigger)
                assertThat(id, equalTo(TriggerId(1234)))
                assertThat(name, equalTo(TriggerName("FixedRateTrigger")))
                assertThat(funcId, equalTo(FuncId(2222)))
                assertThat(duration, equalTo(42.seconds))
                assertThat(inputs, equalTo(TriggerInputs(TableValue(StringValue("hamal") to StringValue("rocks")))))
                assertThat(
                    secrets, equalTo(
                        TriggerSecrets(
                            listOf(
                                Secret(
                                    key = SecretKey("key"),
                                    store = SecretStore("store"),
                                    storeIdentifier = SecretStoreIdentifier("identifier")
                                )
                            )
                        )
                    )
                )
            }
        }
    }

    private fun verifySingleEventTriggerExists() {
        triggerQueryRepository.list { types = setOf(TriggerType.Event) }.also { triggers ->
            assertThat(triggers, hasSize(1))

            with(triggers.first()) {
                require(this is EventTrigger)
                assertThat(id, equalTo(TriggerId(1234)))
                assertThat(name, equalTo(TriggerName("EventTrigger")))
                assertThat(funcId, equalTo(FuncId(2222)))
                assertThat(topicId, equalTo(TopicId(1111)))
                assertThat(inputs, equalTo(TriggerInputs(TableValue(StringValue("hamal") to StringValue("rocks")))))
                assertThat(
                    secrets, equalTo(
                        TriggerSecrets(
                            listOf(
                                Secret(
                                    key = SecretKey("key"),
                                    store = SecretStore("store"),
                                    storeIdentifier = SecretStoreIdentifier("identifier")
                                )
                            )
                        )
                    )
                )
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

    private val submitCreateFixedRateTriggerReq = SubmittedCreateTriggerReq(
        id = ReqId(1),
        status = ReqStatus.Submitted,
        type = TriggerType.FixedRate,
        funcId = FuncId(2222),
        triggerId = TriggerId(1234),
        triggerName = TriggerName("FixedRateTrigger"),
        duration = 42.seconds,
        inputs = TriggerInputs(TableValue(StringValue("hamal") to StringValue("rocks"))),
        secrets = TriggerSecrets(
            listOf(
                Secret(
                    key = SecretKey("key"),
                    store = SecretStore("store"),
                    storeIdentifier = SecretStoreIdentifier("identifier")
                )
            )
        ),
    )

    private val submitCreateEventTriggerReq = SubmittedCreateTriggerReq(
        id = ReqId(1),
        status = ReqStatus.Submitted,
        type = TriggerType.Event,
        funcId = FuncId(2222),
        topicId = TopicId(1111),
        triggerId = TriggerId(1234),
        triggerName = TriggerName("EventTrigger"),
        inputs = TriggerInputs(TableValue(StringValue("hamal") to StringValue("rocks"))),
        secrets = TriggerSecrets(
            listOf(
                Secret(
                    key = SecretKey("key"),
                    store = SecretStore("store"),
                    storeIdentifier = SecretStoreIdentifier("identifier")
                )
            )
        ),
    )
}