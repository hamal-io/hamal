package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.req.handler.BaseReqHandlerTest
import io.hamal.backend.repository.api.submitted_req.SubmittedInvokeExecReq
import io.hamal.lib.domain.*
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.TableValue
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
                inputs = InvocationInputs(TableValue(StringValue("hamal") to StringValue("justworks"))),
                code = CodeValue("code"),
                funcId = null,
                correlationId = null,
                invocation = AdhocInvocation()
            )
        )

        execQueryRepository.list { }.also {
            assertThat(it, hasSize(1))

            with(it.first()) {
                assertThat(id, equalTo(ExecId(3333)))
                assertThat(correlation, nullValue())
                assertThat(
                    inputs,
                    equalTo(ExecInputs(TableValue(StringValue("hamal") to StringValue("justworks"))))
                )
                assertThat(code, equalTo(CodeValue("code")))
            }
        }
    }

    @Test
    fun `Invokes event execution`() {
        createFunc(
            id = FuncId(4444), code = CodeValue("SomeCode"), inputs = FuncInputs(
                TableValue(
                    StringValue("override") to StringValue("false"),
                    StringValue("func") to StringValue("func")
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
                    TableValue(
                        StringValue("override") to StringValue("true"),
                        StringValue("invocation") to StringValue("invocation")
                    )
                ),
                funcId = FuncId(4444),
                invocation = EventInvocation(listOf()),
                code = CodeValue("some-code")
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
                            TableValue(
                                StringValue("func") to StringValue("func"),
                                StringValue("invocation") to StringValue("invocation"),
                                StringValue("override") to StringValue("true"),
                            ),
                        )
                    )
                )
                assertThat(code, equalTo(CodeValue("SomeCode")))
            }
        }
    }

    @Test
    fun `Invokes func execution`() {
        createFunc(
            id = FuncId(4444),
            code = CodeValue("SomeCode"),
            inputs = FuncInputs(
                TableValue(
                    StringValue("override") to StringValue("false"),
                    StringValue("func") to StringValue("func")
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
                    TableValue(
                        StringValue("override") to StringValue("true"),
                        StringValue("invocation") to StringValue("invocation")
                    )
                ),
                funcId = FuncId(4444),
                code = CodeValue("Some func code"),
                invocation = FuncInvocation()
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
                            TableValue(
                                StringValue("func") to StringValue("func"),
                                StringValue("invocation") to StringValue("invocation"),
                                StringValue("override") to StringValue("true"),
                            ),
                        )
                    )
                )
                assertThat(code, equalTo(CodeValue("SomeCode")))
            }
        }
    }

    @Test
    fun `Invokes fixed rate execution`() {
        createFunc(
            id = FuncId(4444),
            code = CodeValue("SomeCode"),
            inputs = FuncInputs(
                TableValue(
                    StringValue("override") to StringValue("false"),
                    StringValue("func") to StringValue("func")
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
                            TableValue(
                                StringValue("func") to StringValue("func"),
                                StringValue("invocation") to StringValue("invocation"),
                                StringValue("override") to StringValue("true"),
                            ),
                        )
                    )
                )
                assertThat(code, equalTo(CodeValue("SomeCode")))
                assertThat(invocation, instanceOf(FixedRateInvocation::class.java))
            }
        }
    }

    @Test
    fun `Tries to invoke execution but func does not exists`() {
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
            TableValue(
                StringValue("override") to StringValue("true"),
                StringValue("invocation") to StringValue("invocation")
            )
        ),
        funcId = FuncId(4444),
        invocation = FixedRateInvocation(),
        code = CodeValue("")
    )
    //@formatter:on
}