package io.hamal.api.http.controller.account

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiAccountCreateRequest
import io.hamal.lib.sdk.api.ApiAccountRequested
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class AccountControllerBaseTest : BaseControllerTest() {
    fun createAccount(req: ApiAccountCreateRequest): ApiAccountRequested {
        val createAccountResponse = httpTemplate.post("/v1/accounts")
            .body(req)
            .execute()

        assertThat(createAccountResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createAccountResponse is HttpSuccessResponse) { "request was not successful" }
        return createAccountResponse.result(ApiAccountRequested::class)
    }


}