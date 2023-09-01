package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.submitted_req.SubmittedInvokeExecReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

internal class InvokeExecHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Invokes adhoc execution`() {
        testInstance(
            SubmittedInvokeExecReq(
                reqId = ReqId(1),
                status = Submitted,
                id = ExecId(3333),
                inputs = InvocationInputs(MapType(mutableMapOf("hamal" to StringType("justworks")))),
                code = CodeType("code"),
                funcId = null,
                correlationId = null,
                events = listOf()
            )
        )

        execQueryRepository.list { }.also {
            assertThat(it, hasSize(1))

            with(it.first()) {
                assertThat(id, equalTo(ExecId(3333)))
                assertThat(correlation, nullValue())
                assertThat(
                    inputs,
                    equalTo(ExecInputs(MapType(mutableMapOf("hamal" to StringType("justworks")))))
                )
                assertThat(code, equalTo(CodeType("code")))
            }
        }
    }

    @Test
    fun `Invokes event execution`() {
        createFunc(
            id = FuncId(4444), code = CodeType("SomeCode"), inputs = FuncInputs(
                MapType(
                    mutableMapOf(
                        "override" to StringType("false"),
                        "func" to StringType("func")
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
                inputs = InvocationInputs(
                    MapType(
                        mutableMapOf(
                            "override" to StringType("true"),
                            "invocation" to StringType("invocation")
                        )
                    )
                ),
                funcId = FuncId(4444),
                code = CodeType("some-code"),
                events = listOf()
            )
        )

        execQueryRepository.list { }.also {
            assertThat(it, hasSize(1))

            with(it.first()) {
                assertThat(id, equalTo(ExecId(3333)))
                assertThat(
                    correlation, equalTo(
                        Correlation(
                            funcId = FuncId(4444),
                            correlationId = CorrelationId("some-correlation")
                        )
                    )
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
                            ),
                        )
                    )
                )
                assertThat(code, equalTo(CodeType("SomeCode")))
            }
        }
    }

    @Test
    fun `Invokes func execution`() {
        createFunc(
            id = FuncId(4444),
            code = CodeType("SomeCode"),
            inputs = FuncInputs(
                MapType(
                    mutableMapOf(
                        "override" to StringType("false"),
                        "func" to StringType("func")
                    )
                )
            )
        )
        testInstance(
            SubmittedInvokeExecReq(
                reqId = ReqId(1),
                id = ExecId(3333),
                correlationId = CorrelationId("some-correlation"),
                status = Submitted,
                inputs = InvocationInputs(
                    MapType(
                        mutableMapOf(
                            "override" to StringType("true"),
                            "invocation" to StringType("invocation")
                        )
                    )
                ),
                funcId = FuncId(4444),
                code = CodeType("Some func code"),
                events = listOf()
            )
        )

        execQueryRepository.list { }.also {
            assertThat(it, hasSize(1))

            with(it.first()) {
                assertThat(id, equalTo(ExecId(3333)))
                assertThat(
                    correlation, equalTo(
                        Correlation(
                            funcId = FuncId(4444),
                            correlationId = CorrelationId("some-correlation")
                        )
                    )
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
                            ),
                        )
                    )
                )
                assertThat(code, equalTo(CodeType("SomeCode")))
            }
        }
    }

    @Test
    fun `Invokes fixed rate execution`() {
        createFunc(
            id = FuncId(4444),
            code = CodeType("SomeCode"),
            inputs = FuncInputs(
                MapType(
                    mutableMapOf(
                        "override" to StringType("false"),
                        "func" to StringType("func")
                    )
                )
            )
        )
        testInstance(submittedFixedRateInvocationReq)

        execQueryRepository.list { }.also {
            assertThat(it, hasSize(1))

            with(it.first()) {
                assertThat(id, equalTo(ExecId(3333)))
                assertThat(correlation, equalTo(Correlation(CorrelationId("some-correlation"), FuncId(4444))))
                assertThat(
                    inputs, equalTo(
                        ExecInputs(
                            MapType(
                                mutableMapOf(
                                    "func" to StringType("func"),
                                    "invocation" to StringType("invocation"),
                                    "override" to StringType("true"),
                                )
                            ),
                        )
                    )
                )
                assertThat(code, equalTo(CodeType("SomeCode")))
            }
        }
    }

    @Test
    fun `Tries to invoke exec but func does not exists`() {
        val exception = assertThrows<NoSuchElementException> { testInstance(submittedFixedRateInvocationReq) }
        assertThat(exception.message, equalTo("Func not found"))
        execQueryRepository.list { }.also {
            assertThat(it, empty())
        }
    }

    @Autowired
    private lateinit var testInstance: InvokeExecHandler

    //@formatter:off
    private val submittedFixedRateInvocationReq = SubmittedInvokeExecReq(
        reqId = ReqId(1),
        correlationId = CorrelationId("some-correlation"),
        status = Submitted,
        id = ExecId(3333),
        inputs = InvocationInputs(
            MapType(
                mutableMapOf(
                "override" to StringType("true"),
                "invocation" to StringType("invocation")
                )
            )
        ),
        funcId = FuncId(4444),
        code = CodeType(""),
        events = listOf()
    )
    //@formatter:on
}