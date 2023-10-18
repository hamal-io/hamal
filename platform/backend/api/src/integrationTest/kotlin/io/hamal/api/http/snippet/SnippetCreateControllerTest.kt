package io.hamal.api.http.snippet

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.SnippetId
import io.hamal.lib.domain.vo.SnippetInputs
import io.hamal.lib.domain.vo.SnippetName
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiCreateSnippetReq

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class SnippetCreateControllerTest : SnippetBaseControllerTest() {

    @Test
    fun `Creates snippet`() {
        val res = createSnippet(
            ApiCreateSnippetReq(
                name = SnippetName("TestSnippet"),
                inputs = SnippetInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
                value = CodeValue("13 + 37")
            )
        )

        awaitCompleted(res.reqId)
        val sn = snippetQueryRepository.get(res.id(::SnippetId))

        with(sn) {
            assertThat(name, equalTo(SnippetName("TestSnippet")))
        }
    }
}