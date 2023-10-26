package io.hamal.api.http.extension

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.sdk.api.ApiCreateExtensionReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ExtensionCreateControllerTest : ExtensionBaseControllerTest() {
    @Test
    fun `Creates extension`() {
        val res = awaitCompleted(
            createExtension(
                ApiCreateExtensionReq(
                    name = ExtensionName("TestExtension"),
                    code = CodeValue("40 + 2")
                )
            )
        )
        val ext = extensionQueryRepository.get(res.id(::ExtensionId))
        with(ext) {
            assertThat(name, equalTo(ExtensionName("TestExtension")))
        }

        with(codeQueryRepository.get(ext.code.id)) {
            assertThat(value, equalTo(CodeValue("40 + 2")))
        }
    }
}