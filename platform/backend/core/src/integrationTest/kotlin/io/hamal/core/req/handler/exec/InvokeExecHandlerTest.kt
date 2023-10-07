package io.hamal.core.req.handler.exec

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.submitted_req.SubmittedInvokeExecReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

internal class InvokeExecHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Invokes execution with code`() {
        testInstance(
            SubmittedInvokeExecReq(
                reqId = ReqId(1),
                status = Submitted,
                id = ExecId(3333),
                groupId = testGroup.id,
                inputs = InvocationInputs(MapType(mutableMapOf("hamal" to StringType("justworks")))),
                code = CodeValue("code"),
                codeId = null,
                codeVersion = null,
                funcId = null,
                correlationId = null,
                events = listOf()
            )
        )

        execQueryRepository.list(ExecQuery(groupIds = listOf())).also {
            assertThat(it, hasSize(1))

            with(it.first()) {
                assertThat(id, equalTo(ExecId(3333)))
                assertThat(correlation, nullValue())
                assertThat(inputs, equalTo(ExecInputs(MapType(mutableMapOf("hamal" to StringType("justworks"))))))
                assertThat(code, equalTo(CodeValue("code")))
                assertThat(codeId, nullValue())
                assertThat(codeVersion, nullValue())
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
                MapType(
                    mutableMapOf(
                        "override" to StringType("false"), "func" to StringType("func")
                    )
                )
            )
        )
        testInstance(
            SubmittedInvokeExecReq(
                reqId = ReqId(1),
                correlationId = CorrelationId("some-correlation"),
                status = Submitted,
                id = ExecId(3333),
                groupId = testGroup.id,
                inputs = InvocationInputs(
                    MapType(
                        mutableMapOf(
                            "override" to StringType("true"), "invocation" to StringType("invocation")
                        )
                    )
                ),
                funcId = FuncId(4444),
                code = null,
                codeId = CodeId(4455),
                codeVersion = CodeVersion(5544),
                events = listOf()
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
                            MapType(
                                mutableMapOf(
                                    "func" to StringType("func"),
                                    "invocation" to StringType("invocation"),
                                    "override" to StringType("true"),
                                )
                            )
                        )
                    )
                )
                assertThat(code, nullValue())
                assertThat(codeId, equalTo(CodeId(4455)))
                assertThat(codeVersion, equalTo(CodeVersion(5544)))
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
    private lateinit var testInstance: InvokeExecHandler

    //    @formatter:off
    private val submittedFixedRateInvocationReq by lazy {
        SubmittedInvokeExecReq(
            reqId = ReqId(1),
            correlationId = CorrelationId("some-correlation"),
            status = Submitted,
            id = ExecId(3333),
            groupId = testGroup.id,
            inputs = InvocationInputs(MapType(mutableMapOf(
                    "override" to StringType("true"),
                    "invocation" to StringType("invocation")
                    ))),
            funcId = FuncId(4444),
            code = null,
            codeId = CodeId(5555),
            codeVersion = CodeVersion(6666),
            events = listOf()
        )
    }
    //@formatter:on
}