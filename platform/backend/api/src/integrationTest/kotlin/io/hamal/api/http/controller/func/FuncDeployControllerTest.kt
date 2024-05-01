package io.hamal.api.http.controller.func

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.DeployMessage
import io.hamal.lib.domain.vo.DeployMessage.Companion.DeployMessage
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName.Companion.FuncName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.*
import io.hamal.repository.api.Func
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class FuncDeployControllerTest : FuncBaseControllerTest() {

    @Nested
    inner class DeployVersionTest {

        @Test
        fun `Deploys higher version`() {
            val func = setup()
            repeat(20) { iter ->
                awaitCompleted(
                    deployFunc(
                        funcId = func.id,
                        req = ApiFuncDeployRequest(
                            version = CodeVersion(iter + 1),
                            null
                        )
                    )
                )
                assertThat(funcQueryRepository.get(func.id).deployment.version, equalTo(CodeVersion(iter + 1)))
            }

            with(funcQueryRepository.get(func.id)) {
                assertThat(name, equalTo(FuncName("test-func")))
                assertThat(code.version, equalTo(CodeVersion(20)))
                assertThat(codeQueryRepository.get(code.id, deployment.version).value, equalTo(ValueCode("code-19")))
            }

        }

        @Test
        fun `Deploys same version`() {
            val func = setup()

            repeat(20) {
                awaitCompleted(
                    deployFunc(
                        funcId = func.id,
                        req = ApiFuncDeployRequest(
                            version = CodeVersion(10),
                            null
                        )
                    )
                )
                assertThat(funcQueryRepository.get(func.id).deployment.version, equalTo(CodeVersion(10)))
            }

            with(funcQueryRepository.get(func.id)) {
                assertThat(name, equalTo(FuncName("test-func")))
                assertThat(code.version, equalTo(CodeVersion(20)))
                assertThat(codeQueryRepository.get(code.id, deployment.version).value, equalTo(ValueCode("code-9")))
            }
        }

        @Test
        fun `Deploys prior version`() {
            val func = setup()

            repeat(20) { iter ->
                awaitCompleted(
                    deployFunc(
                        funcId = func.id,
                        req = ApiFuncDeployRequest(
                            version = CodeVersion(20 - iter),
                            null
                        )
                    )
                )
                assertThat(funcQueryRepository.get(func.id).deployment.version, equalTo(CodeVersion(20 - iter)))
            }

            with(funcQueryRepository.get(func.id)) {
                assertThat(name, equalTo(FuncName("test-func")))
                assertThat(code.version, equalTo(CodeVersion(20)))
                assertThat(codeQueryRepository.get(code.id, deployment.version).value, equalTo(ValueCode("13 + 37")))
            }
        }

        @Test
        fun `Tries to deploy to func that does not exist`() {
            val deployResponse = httpTemplate.post("/v1/funcs/{funcId}/deploy")
                .path("funcId", FuncId(23))
                .body(ApiFuncDeployRequest(CodeVersion(1), null))
                .execute()

            assertThat(deployResponse.statusCode, equalTo(HttpStatusCode.NotFound))
            require(deployResponse is HttpErrorResponse) { "request was successful" }

            val error = deployResponse.error(ApiError::class)
            assertThat(error.message, equalTo("Func not found"))
        }

        @Test
        fun `Tries to deploy version that does not exist`() {
            val func: ApiFuncCreateRequested = createFunc(
                ApiFuncCreateRequest(
                    name = FuncName("Func-base"),
                    inputs = FuncInputs(),
                    code = ValueCode("40 + 2"),
                    codeType = CodeType.Lua54
                )
            )

            val deployResponse = httpTemplate.post("/v1/funcs/{funcId}/deploy")
                .path("funcId", func.id)
                .body(ApiFuncDeployRequest(CodeVersion(23), null))
                .execute()

            assertThat(deployResponse.statusCode, equalTo(HttpStatusCode.NotFound))
            require(deployResponse is HttpErrorResponse) { "request was successful" }

            val error = deployResponse.error(ApiError::class)
            assertThat(error.message, equalTo("Code not found"))
        }

        @Nested
        inner class DeployLatestTest {

            @Test
            fun `Deploys latest version`() {
                val funcId = setup().id
                awaitCompleted(
                    deployFunc(
                        funcId = funcId,
                        req = ApiFuncDeployRequest(
                            null,
                            null
                        )
                    )
                )

                with(funcQueryRepository.get(funcId))
                {
                    assertThat(name, equalTo(FuncName("test-func")))
                    assertThat(
                        codeQueryRepository.get(code.id, deployment.version).value, equalTo(ValueCode("code-19"))
                    )
                    assertThat(deployment.version, equalTo(code.version))
                    assertThat(deployment.message, equalTo(DeployMessage.empty))
                }
            }

            @Test
            fun `Deploys latest version with message`() {
                val funcId = setup().id

                awaitCompleted(
                    deployFunc(
                        funcId = funcId,
                        req = ApiFuncDeployRequest(
                            version = null,
                            DeployMessage("SuperFunc")
                        )
                    )
                )


                with(funcQueryRepository.get(funcId)) {
                    assertThat(name, equalTo(FuncName("test-func")))
                    assertThat(
                        codeQueryRepository.get(code.id, deployment.version).value, equalTo(ValueCode("code-19"))
                    )
                    assertThat(deployment.message, equalTo(DeployMessage("SuperFunc")))
                }
            }
        }

        private fun setup(): Func {
            val func = awaitCompleted(
                createFunc(
                    ApiFuncCreateRequest(
                        name = FuncName("test-func"),
                        inputs = FuncInputs(),
                        code = ValueCode("13 + 37"),
                        codeType = CodeType.Lua54
                    )
                )
            )

            for (i in 1..19) {
                awaitCompleted(
                    updateFunc(
                        func.id, ApiFuncUpdateRequest(
                            name = null,
                            inputs = null,
                            code = ValueCode("code-${i}")
                        )
                    )
                )
            }

            return funcQueryRepository.get(func.id)
        }
    }
}