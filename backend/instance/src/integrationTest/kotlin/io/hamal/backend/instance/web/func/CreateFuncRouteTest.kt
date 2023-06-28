package io.hamal.backend.instance.web.func

import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.base.Secret
import io.hamal.lib.domain.vo.base.SecretKey
import io.hamal.lib.domain.vo.base.SecretStore
import io.hamal.lib.domain.vo.base.SecretStoreIdentifier
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.TableValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class CreateFuncRouteTest : BaseFuncRouteTest() {
    @Test
    fun `Create func`() {
        val result = createFunc(
            CreateFuncReq(
                name = FuncName("test-func"),
                inputs = FuncInputs(TableValue(StringValue("hamal") to StringValue("rocks"))),
                secrets = FuncSecrets(
                    listOf(
                        Secret(
                            key = SecretKey("some_secret"),
                            store = SecretStore("local"),
                            storeIdentifier = SecretStoreIdentifier("SOME_SECRET")
                        )
                    )
                ),
                code = Code("13 + 37")
            )
        )
        awaitCompleted(result.id)
        verifyFuncCreated(result.funcId)
    }
}

private fun CreateFuncRouteTest.verifyFuncCreated(funcId: FuncId) {
    with(funcQueryService.get(funcId)) {
        assertThat(id, equalTo(funcId))
        assertThat(name, equalTo(FuncName("test-func")))
        assertThat(inputs, equalTo(FuncInputs(TableValue(StringValue("hamal") to StringValue("rocks")))))
        assertThat(
            secrets, equalTo(
                FuncSecrets(
                    listOf(
                        Secret(
                            key = SecretKey("some_secret"),
                            store = SecretStore("local"),
                            storeIdentifier = SecretStoreIdentifier("SOME_SECRET")
                        )
                    )
                )
            )
        )
        assertThat(code, equalTo(Code("13 + 37")))
    }
}