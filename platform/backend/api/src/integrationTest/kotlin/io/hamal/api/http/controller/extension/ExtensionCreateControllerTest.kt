package io.hamal.api.http.controller.extension

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.vo.CodeValue.Companion.CodeValue
import io.hamal.lib.domain.vo.ExtensionName.Companion.ExtensionName
import io.hamal.lib.sdk.api.ApiExtensionCreateRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ExtensionCreateControllerTest : ExtensionBaseControllerTest() {
    @Test
    fun `Creates extension`() {
        val res = awaitCompleted(
            createExtension(
                ApiExtensionCreateRequest(
                    name = ExtensionName("TestExtension"),
                    code = CodeValue("40 + 2")
                )
            )
        )
        val ext = extensionQueryRepository.get(res.id)

        assertThat(ext.name, equalTo(ExtensionName("TestExtension")))
        assertThat(codeQueryRepository.get(ext.code.id).value, equalTo(CodeValue("40 + 2")))

    }
}