package io.hamal.core.req.handler.func

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.FuncCode
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.submitted_req.FuncCreateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CreateFuncHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Creates func`() {
        testInstance(submitCreateFuncReq)

        funcQueryRepository.list(FuncQuery(groupIds = listOf())).also { funcs ->
            assertThat(funcs, hasSize(1))
            with(funcs.first()) {
                assertThat(id, equalTo(FuncId(12345)))
                assertThat(name, equalTo(FuncName("awesome-func")))
                assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
                assertThat(
                    code, equalTo(
                        FuncCode(
                            id = CodeId(34567),
                            version = CodeVersion(1)
                        )
                    )
                )
            }
        }
    }

    @Autowired
    private lateinit var testInstance: CreateFuncHandler

    private val submitCreateFuncReq by lazy {
        FuncCreateSubmitted(
            reqId = ReqId(1),
            status = Submitted,
            groupId = testGroup.id,
            id = FuncId(12345),
            namespaceId = NamespaceId(23456),
            name = FuncName("awesome-func"),
            inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
            codeId = CodeId(34567),
            code = CodeValue("some code"),
        )
    }
}