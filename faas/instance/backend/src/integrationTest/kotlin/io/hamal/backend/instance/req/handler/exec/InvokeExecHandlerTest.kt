package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.req.handler.BaseReqHandlerTest
import io.hamal.backend.repository.api.submitted_req.SubmittedInvokeExecReq
import io.hamal.lib.domain.*
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.TableType
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
                inputs = InvocationInputs(TableType(StringType("hamal") to StringType("justworks"))),
                code = CodeType("code"),
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
                    equalTo(ExecInputs(TableType(StringType("hamal") to StringType("justworks"))))
                )
                assertThat(code, equalTo(CodeType("code")))
            }
        }
    }

    @Test
    fun `Invokes event execution`() {
        createFunc(
            id = FuncId(4444), code = CodeType("SomeCode"), inputs = FuncInputs(
                TableType(
                    StringType("override") to StringType("false"),
                    StringType("func") to StringType("func")
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
                    TableType(
                        StringType("override") to StringType("true"),
                        StringType("invocation") to StringType("invocation")
                    )
                ),
                funcId = FuncId(4444),
                invocation = EventInvocation(listOf()),
                code = CodeType("some-code")
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
                            TableType(
                                StringType("func") to StringType("func"),
                                StringType("invocation") to StringType("invocation"),
                                StringType("override") to StringType("true"),
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
                TableType(
                    StringType("override") to StringType("false"),
                    StringType("func") to StringType("func")
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
                    TableType(
                        StringType("override") to StringType("true"),
                        StringType("invocation") to StringType("invocation")
                    )
                ),
                funcId = FuncId(4444),
                code = CodeType("Some func code"),
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
                            TableType(
                                StringType("func") to StringType("func"),
                                StringType("invocation") to StringType("invocation"),
                                StringType("override") to StringType("true"),
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
                TableType(
                    StringType("override") to StringType("false"),
                    StringType("func") to StringType("func")
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
                            TableType(
                                StringType("func") to StringType("func"),
                                StringType("invocation") to StringType("invocation"),
                                StringType("override") to StringType("true"),
                            ),
                        )
                    )
                )
                assertThat(code, equalTo(CodeType("SomeCode")))
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
            TableType(
                StringType("override") to StringType("true"),
                StringType("invocation") to StringType("invocation")
            )
        ),
        funcId = FuncId(4444),
        invocation = FixedRateInvocation(),
        code = CodeType("")
    )
    //@formatter:on
}