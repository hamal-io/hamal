package io.hamal.api.http.controller.account

import io.hamal.lib.domain.vo.Email
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiAccountCreateRequest
import io.hamal.lib.sdk.api.ApiAccountPasswordChangeRequest
import io.hamal.lib.sdk.api.ApiAccountRequested
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class AccountUpdateControllerTest : AccountControllerBaseTest() {

    @Test
    fun `Creates account`() {
        val createAccountResponse = awaitCompleted(
            createAccount(
                ApiAccountCreateRequest(
                    email = Email(value = "test@hamal.io"),
                    password = Password(value = "secret")
                )
            )
        )
    }

    @Test
    fun `Updates password`() {
        val createAccountResponse = awaitCompleted(
            createAccount(
                ApiAccountCreateRequest(
                    email = Email(value = "test@hamal.io"),
                    password = Password(value = "secret")
                )
            )
        )

        val updateResponse = httpTemplate.patch("/v1/accounts/password")
            //.header("Bearer", testAuthToken.value)
            .body(
                ApiAccountPasswordChangeRequest(
                    currentPassword = Password(value = "secret"),
                    newPassword = Password(value = "changed-secret")
                )
            ).execute()

        assertThat(updateResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(updateResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateResponse.result(ApiAccountRequested::class)
        awaitCompleted(submittedReq)

        assertThat(submittedReq.id, equalTo(createAccountResponse.id))
    }


}