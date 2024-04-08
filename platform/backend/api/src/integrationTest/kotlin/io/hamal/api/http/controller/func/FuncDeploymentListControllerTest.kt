package io.hamal.api.http.controller.func

import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.sdk.api.ApiFuncCreateRequest
import io.hamal.lib.sdk.api.ApiFuncDeployRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class FuncDeploymentListControllerTest : FuncBaseControllerTest() {

    @Test
    fun `Get a list of deployments`() {
        val funcId = awaitCompleted(
            createFunc(
                ApiFuncCreateRequest(
                    name = FuncName("func-1"),
                    inputs = FuncInputs(),
                    code = CodeValue(""),
                    codeType = CodeType.Lua54
                )
            )
        ).id

        repeat(20) {
            awaitCompleted(deployFunc(funcId, ApiFuncDeployRequest(null, DeployMessage("deployed-${it}"))))
        }

        val deployments = listDeployments(funcId).deployments
        assertThat(deployments, hasSize(20))
        assertThat(deployments[10].message, equalTo(DeployMessage("deployed-10")))
        assertThat(deployments[10].version, equalTo(CodeVersion(1)))
    }

}