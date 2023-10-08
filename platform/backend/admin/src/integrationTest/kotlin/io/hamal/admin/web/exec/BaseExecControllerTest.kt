package io.hamal.admin.web.exec

import io.hamal.admin.web.BaseControllerTest
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.admin.AdminInvokeAdhocReq
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseExecControllerTest : BaseControllerTest() {
    fun createAdhocExec(): AdminSubmittedReqWithId {
        val createAdhocExecResponse = httpTemplate
            .post("/v1/groups/{groupId}/adhoc")
            .path("groupId", testGroup.id)
            .body(
                AdminInvokeAdhocReq(
                    inputs = InvocationInputs(),
                    code = CodeValue("40 + 2")
                )
            )
            .execute()

        assertThat(createAdhocExecResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createAdhocExecResponse is SuccessHttpResponse) { "request was not successful" }

        return createAdhocExecResponse.result(AdminSubmittedReqWithId::class)
    }
}