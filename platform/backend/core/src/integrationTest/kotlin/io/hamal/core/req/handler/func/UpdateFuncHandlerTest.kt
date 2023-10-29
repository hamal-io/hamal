package io.hamal.core.req.handler.func

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.submitted_req.FuncUpdateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class UpdateFuncHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Updates func`() {
        createFunc(
            codeId = CodeId(1),
            codeVersion = CodeVersion(1)
        )

        val res = updateHandler(submitUpdateFuncReq)

        with(funcQueryRepository.get(FuncId(12345))) {
            assertThat(name, equalTo(FuncName("awesome-update")))
            //assertThat(code.version)
        }

    }


    /*fun createFunc(
        id: FuncId = generateDomainId(::FuncId),
        name: FuncName = FuncName("SomeFuncName"),
        inputs: FuncInputs = FuncInputs(),
        codeId: CodeId = CodeId(2222),
        codeVersion: CodeVersion = CodeVersion(2233),
    ): Func {
        return funcCmdRepository.create(
            FuncCmdRepository.CreateCmd(
                id = NextCommandId(),
                funcId = id,
                namespaceId = testNamespace.id,
                groupId = testGroup.id,
                name = name,
                inputs = inputs,
                code = FuncCode(
                    id = codeId,
                    version = codeVersion,
                    deployedVersion = codeVersion

                )
            )
        )
    }
*/

    private val submitUpdateFuncReq by lazy {
        FuncUpdateSubmitted(
            reqId = ReqId(10),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            id = FuncId(12345),
            name = FuncName("awesome-update"),
            inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
            code = CodeValue("some code"),
        )
    }

    @Autowired
    private lateinit var updateHandler: UpdateFuncHandler
}