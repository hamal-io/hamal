package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedInvokeOneshotReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExecStatus.Planned
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

internal class InvokeOneshotHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Invokes one shot execution`() {
        createFunc(
            id = FuncId(4444), code = Code("SomeCode"), inputs = FuncInputs(
                TableValue(
                    StringValue("override") to StringValue("false"), StringValue("func") to StringValue("func")
                )
            ), secrets = FuncSecrets(
                listOf(
                    Secret(SecretKey("override"), SecretStore("override"), SecretStoreIdentifier("false")),
                    Secret(SecretKey("func"), SecretStore("func"), SecretStoreIdentifier("func"))
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
                assertThat(status, equalTo(Planned))
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
                assertThat(
                    secrets, equalTo(
                        ExecSecrets(
                            //@formatter:off
                            listOf(
                                Secret(SecretKey("func"), SecretStore("func"), SecretStoreIdentifier("func")),
                                Secret(SecretKey("override"), SecretStore("override"), SecretStoreIdentifier("true")),
                                Secret(SecretKey("invocation"), SecretStore("invocation"), SecretStoreIdentifier("invocation"))
                            )
                            //@formatter:on
                        )
                    )
                )
                assertThat(code, equalTo(Code("code")))
            }
        }
    }

    @Test
    fun `Tries to invoke one shot execution but func does not exists`() {
        val exception = assertThrows<NoSuchElementException> { testInstance(submittedInvocationReq) }
        assertThat(exception.message, equalTo("Func not found"))
        execQueryRepository.list { }.also {
            assertThat(it, empty())
        }
    }

    @Autowired
    private lateinit var testInstance: InvokeOneshotHandler

    //@formatter:off
    private val submittedInvocationReq = SubmittedInvokeOneshotReq(
        id = ReqId(1),
        correlationId = CorrelationId("some-correlation"),
        status = ReqStatus.Submitted,
        execId = ExecId(3333),
        inputs = InvocationInputs(
            TableValue(
                StringValue("override") to StringValue("true"), StringValue("invocation") to StringValue("invocation")
            )
        ),
        funcId = FuncId(4444),
        secrets = InvocationSecrets(
            listOf(
                Secret(SecretKey("override"), SecretStore("override"), SecretStoreIdentifier("true")),
                Secret(SecretKey("invocation"), SecretStore("invocation"), SecretStoreIdentifier("invocation"))
            )
        )
    )
    //@formatter:on
}