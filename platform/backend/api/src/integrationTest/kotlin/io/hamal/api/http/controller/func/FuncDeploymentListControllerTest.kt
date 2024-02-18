package io.hamal.api.http.controller.func

import io.hamal.lib.domain.vo.*
import io.hamal.lib.sdk.api.ApiFuncCreateRequest
import io.hamal.lib.sdk.api.ApiFuncDeployRequest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class FuncDeploymentListControllerTest : FuncBaseControllerTest() {

    @Test
    fun `Get a list of deployments`() {
        val funcId = awaitCompleted(
            createFunc(
                ApiFuncCreateRequest(
                    name = FuncName("func-1"),
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
        ).funcId

        repeat(20) {
            awaitCompleted(deployFunc(funcId, ApiFuncDeployRequest(null, DeployMessage("deployed-${it}"))))
        }

        val deployments = listDeployments(funcId).deployments
        MatcherAssert.assertThat(deployments, Matchers.hasSize(20))
        MatcherAssert.assertThat(deployments[10].message, Matchers.equalTo(DeployMessage("deployed-10")))
        MatcherAssert.assertThat(deployments[10].version, Matchers.equalTo(CodeVersion(1)))
    }

}