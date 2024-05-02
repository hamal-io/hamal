package io.hamal.core.request.handler.func

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.FuncCreateRequested
import io.hamal.lib.domain.vo.AuthId.Companion.AuthId
import io.hamal.lib.domain.vo.CodeId.Companion.CodeId
import io.hamal.lib.domain.vo.CodeVersion.Companion.CodeVersion
import io.hamal.lib.domain.vo.DeployMessage.Companion.DeployMessage
import io.hamal.lib.domain.vo.DeployedAt.Companion.DeployedAt
import io.hamal.lib.domain.vo.FuncId.Companion.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName.Companion.FuncName
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.RequestId.Companion.RequestId
import io.hamal.repository.api.FuncCode
import io.hamal.repository.api.FuncDeployment
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant

internal class FuncCreateHandlerTest : BaseRequestHandlerTest() {

    @Test
    fun `Creates func`() {

        TimeUtils.withEpochMilli(123456789) {
            testInstance(submitCreateFuncReq)

            funcQueryRepository.list(FuncQuery(workspaceIds = listOf())).also { funcs ->
                assertThat(funcs, hasSize(1))
                with(funcs.first()) {
                    assertThat(id, equalTo(FuncId(12345)))
                    assertThat(name, equalTo(FuncName("awesome-func")))
                    assertThat(inputs, equalTo(FuncInputs(ValueObject.builder().set("hamal", "rocks").build())))
                    assertThat(deployment.version, equalTo(codeQueryRepository.get(CodeId(34567)).version))

                    assertThat(
                        code, equalTo(
                            FuncCode(
                                id = CodeId(34567),
                                version = CodeVersion(1)
                            )
                        )
                    )

                    assertThat(
                        deployment, equalTo(
                            FuncDeployment(
                                id = CodeId(34567),
                                version = CodeVersion(1),
                                message = DeployMessage("Initial version"),
                                deployedAt = DeployedAt(Instant.ofEpochMilli(123456789))
                            )
                        )
                    )
                }
            }
        }
    }

    @Autowired
    private lateinit var testInstance: FuncCreateHandler

    private val submitCreateFuncReq by lazy {
        FuncCreateRequested(
            requestId = RequestId(1),
            requestedBy = AuthId(2),
            requestStatus = Submitted,
            workspaceId = testWorkspace.id,
            id = FuncId(12345),
            namespaceId = NamespaceId(23456),
            name = FuncName("awesome-func"),
            inputs = FuncInputs(ValueObject.builder().set("hamal", "rocks").build()),
            codeId = CodeId(34567),
            code = ValueCode("some code"),
            codeType = CodeType.Lua54
        )
    }
}