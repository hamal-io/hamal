package io.hamal.api.http.extension

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
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
                    codeId = CodeId(1),
                    codeVersion = CodeVersion(1)
                )
            )
        )

        with(extensionQueryRepository.get(res.id(::ExtensionId))) {
            assertThat(name, equalTo(ExtensionName("TestExtension")))
            assertThat(code.id, equalTo(CodeId(1)))
            assertThat(code.version, equalTo(CodeVersion(1)))
        }
    }
}