package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
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
import org.springframework.beans.factory.annotation.Autowired

internal class InvokeAdhocHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Invokes adhoc execution`() {
        testInstance(submittedInvocationReq)

        execQueryRepository.list { }.also {
            assertThat(it, hasSize(1))

            with(it.first()) {
                assertThat(id, equalTo(ExecId(3333)))
                assertThat(correlation, nullValue())
                assertThat(status, equalTo(Planned))
                assertThat(inputs, equalTo(ExecInputs(TableValue(StringValue("hamal") to StringValue("justworks")))))
                assertThat(
                    secrets, equalTo(
                        ExecSecrets(
                            listOf(
                                Secret(
                                    key = SecretKey("key"),
                                    store = SecretStore("store"),
                                    storeIdentifier = SecretStoreIdentifier("ident")
                                )
                            )
                        )
                    )
                )
                assertThat(code, equalTo(Code("code")))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: InvokeAdhocHandler

    private val submittedInvocationReq = SubmittedInvokeAdhocReq(
        id = ReqId(1),
        status = ReqStatus.Submitted,
        execId = ExecId(3333),
        inputs = InvocationInputs(TableValue(StringValue("hamal") to StringValue("justworks"))),
        secrets = InvocationSecrets(
            listOf(
                Secret(
                    key = SecretKey("key"),
                    store = SecretStore("store"),
                    storeIdentifier = SecretStoreIdentifier("ident")
                )
            )
        ),
        code = Code("code")
    )
}