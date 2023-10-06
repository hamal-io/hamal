package io.hamal.bridge.web.exec

import io.hamal.bridge.web.BaseControllerTest
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.hub.HubInvokeAdhocReq
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseExecControllerTest : BaseControllerTest() {
    fun createAdhocExec(): HubSubmittedReqWithId {
        val createAdhocExecResponse = httpTemplate
            .post("/v1/groups/{groupId}/adhoc")
            .path("groupId", testGroup.id)
            .body(
                HubInvokeAdhocReq(
                    inputs = InvocationInputs(),
                    code = CodeType("40 + 2")
                )
            )
            .execute()

        assertThat(createAdhocExecResponse.statusCode, equalTo(Accepted))
        require(createAdhocExecResponse is SuccessHttpResponse) { "request was not successful" }

        return createAdhocExecResponse.result(HubSubmittedReqWithId::class)
    }
}