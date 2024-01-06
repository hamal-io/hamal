package io.hamal.core.request.handler.exec

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.lib.domain.request.ExecInvokeRequested
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired


internal class ExecInvokeHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Invokes execution with code`() {
        testInstance(
            ExecInvokeRequested(
                id = RequestId(1),
                status = Submitted,
                execId = ExecId(3333),
                flowId = testFlow.id,
                groupId = testGroup.id,
                inputs = InvocationInputs(KuaMap(mutableMapOf("hamal" to KuaString("justworks")))),
                code = ExecCode(value = CodeValue("code")),
                funcId = null,
                correlationId = null,
                invocation = EventInvocation(listOf())
            )
        )

        execQueryRepository.list(ExecQuery(groupIds = listOf())).also {
            assertThat(it, hasSize(1))

            with(it.first()) {
                assertThat(id, equalTo(ExecId(3333)))
                assertThat(correlation, nullValue())
                assertThat(inputs, equalTo(ExecInputs(KuaMap(mutableMapOf("hamal" to KuaString("justworks"))))))
                assertThat(code, equalTo(ExecCode(value = CodeValue("code"))))
            }
        }
    }

    @Test
    fun `Invokes execution with codeId and codeVersion`() {
        createFunc(
            id = FuncId(4444),
            codeId = CodeId(4455),
            codeVersion = CodeVersion(5544),
            inputs = FuncInputs(
                KuaMap(
                    mutableMapOf(
                        "override" to KuaString("false"), "func" to KuaString("func")
                    )
                )
            )
        )
        testInstance(
            ExecInvokeRequested(
                id = RequestId(1),
                correlationId = CorrelationId("some-correlation"),
                status = Submitted,
                execId = ExecId(3333),
                flowId = testFlow.id,
                groupId = testGroup.id,
                inputs = InvocationInputs(
                    KuaMap(
                        mutableMapOf(
                            "override" to KuaString("true"), "invocation" to KuaString("invocation")
                        )
                    )
                ),
                funcId = FuncId(4444),
                code = ExecCode(
                    id = CodeId(4455),
                    version = CodeVersion(5544),
                ),
                invocation = EventInvocation(listOf())
            )
        )

        execQueryRepository.list(ExecQuery(groupIds = listOf())).also {
            assertThat(it, hasSize(1))

            with(it.first()) {
                assertThat(id, equalTo(ExecId(3333)))
                assertThat(
                    correlation,
                    equalTo(Correlation(funcId = FuncId(4444), correlationId = CorrelationId("some-correlation")))
                )
                assertThat(
                    inputs, equalTo(
                        ExecInputs(
                            KuaMap(
                                mutableMapOf(
                                    "func" to KuaString("func"),
                                    "invocation" to KuaString("invocation"),
                                    "override" to KuaString("true"),
                                )
                            )
                        )
                    )
                )
                assertThat(
                    code, equalTo(
                        ExecCode(
                            id = CodeId(4455),
                            version = CodeVersion(5544)
                        )
                    )
                )
            }
        }
    }

    @Test
    fun `Tries to invoke exec but func does not exists`() {
        val exception = assertThrows<NoSuchElementException> { testInstance(submittedFixedRateInvocationReq) }
        assertThat(exception.message, equalTo("Func not found"))
        execQueryRepository.list(ExecQuery(groupIds = listOf())).also {
            assertThat(it, empty())
        }
    }

    @Autowired
    private lateinit var testInstance: ExecInvokeHandler

    //    @formatter:off
    private val submittedFixedRateInvocationReq by lazy {
        ExecInvokeRequested(
            id = RequestId(1),
            correlationId = CorrelationId("some-correlation"),
            status = Submitted,
            execId = ExecId(3333),
            flowId = testFlow.id,
            groupId = testGroup.id,
            inputs = InvocationInputs(KuaMap(mutableMapOf(
                    "override" to KuaString("true"),
                    "invocation" to KuaString("invocation")
                    ))),
            funcId = FuncId(4444),
            code = ExecCode(
                id = CodeId(5555),
                version = CodeVersion(6666),
            ),
            invocation = EventInvocation(listOf())
        )
    }
    //@formatter:on
}