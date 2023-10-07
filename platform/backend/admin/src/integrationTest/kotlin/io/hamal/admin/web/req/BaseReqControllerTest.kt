package io.hamal.admin.web.req

import io.hamal.admin.web.BaseControllerTest
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.admin.AdminInvokeAdhocReq
import io.hamal.lib.sdk.admin.AdminReqList
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseReqControllerTest : BaseControllerTest() {

    fun list(): AdminReqList {
        val listResponse = httpTemplate.get("/v1/reqs").execute()
        assertThat(listResponse.statusCode, equalTo(HttpStatusCode.Ok))

        require(listResponse is SuccessHttpResponse) { "request was not successful" }
        return listResponse.result(AdminReqList::class)
    }

    fun adhoc(code: CodeValue = CodeValue("")): AdminSubmittedReqWithId {
        return httpTemplate.post("/v1/groups/{groupId}/adhoc")
            .path("groupId", testGroup.id)
            .body(
                AdminInvokeAdhocReq(
                    inputs = InvocationInputs(),
                    code = code
                )
            ).execute(AdminSubmittedReqWithId::class)
    }

}