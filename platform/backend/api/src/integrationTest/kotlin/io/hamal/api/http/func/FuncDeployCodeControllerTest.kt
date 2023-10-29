package io.hamal.api.http.func

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiFuncCodeDeployReq
import io.hamal.lib.sdk.api.ApiFuncCreateReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class FuncDeployCodeControllerTest : FuncBaseControllerTest() {

    @Test
    fun `Deploys func code version`() {
        val func = awaitCompleted(
            createFunc(
                ApiFuncCreateReq(
                    name = FuncName("Func-base"),
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
        )

        assertThat(funcQueryRepository.get(func.funcId).code.deployedVersion, equalTo(CodeVersion(1)))

        val deployResponse = httpTemplate.patch("/v1/funcs/{funcId}/deploy/{codeVersion}")
            .path("funcId", func.funcId)
            .body(
                ApiFuncCodeDeployReq(
                    deployedVersion = CodeVersion(123)
                )
            )
            .execute()



        assertThat(funcQueryRepository.get(func.funcId).code.deployedVersion, equalTo(CodeVersion(123)))

    }

}