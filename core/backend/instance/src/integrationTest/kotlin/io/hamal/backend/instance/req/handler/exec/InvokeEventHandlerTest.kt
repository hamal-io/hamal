package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.EventInvocation
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedInvokeEventReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.TableValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

internal class InvokeEventHandlerTest : BaseReqHandlerTest() {
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
        testInstance(submittedInvocationReq)

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
    fun `Tries to invoke event execution but func does not exists`() {
        val exception = assertThrows<NoSuchElementException> { testInstance(submittedInvocationReq) }
        assertThat(exception.message, equalTo("Func not found"))
        execQueryRepository.list { }.also {
            assertThat(it, empty())
        }
    }

    @Autowired
    private lateinit var testInstance: InvokeEventHandler

    //@formatter:off
    private val submittedInvocationReq = SubmittedInvokeEventReq(
        id = ReqId(1),
        correlationId = CorrelationId("some-correlation"),
        status = ReqStatus.Submitted,
        execId = ExecId(3333),
        inputs = InvocationInputs(
            TableValue(
                StringValue("override") to StringValue("true"),
                StringValue("invocation") to StringValue("invocation")
            )
        ),
        funcId = FuncId(4444),
        invocation = EventInvocation(listOf())
    )
    //@formatter:on
}