package io.hamal.core.request.handler.account

import io.hamal.core.component.GenerateToken
import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.AccountCreateRequested
import io.hamal.lib.domain.vo.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CreateHandlerTest : BaseRequestHandlerTest() {

    @Test
    fun `Creates Email Account`() {
        testInstance(submitCreateEmailAccountReq)

        with(accountQueryRepository.get(AccountId(123))) {
            assertThat(type, equalTo(AccountType.User))
            assertThat(salt, equalTo(PasswordSalt("salt")))
        }
    }


    private val submitCreateEmailAccountReq by lazy {
        AccountCreateRequested(
            requestId = RequestId(1),
            requestedBy = AuthId(2),
            requestStatus = RequestStatus.Submitted,
            workspaceId = testWorkspace.id,
            accountId = AccountId(123),
            accountType = AccountType.User,
            emailAuthId = AuthId(3),
            tokenAuthId = AuthId(4),
            namespaceId = testNamespace.id,
            email = Email(value = "test@hamal.io"),
            salt = PasswordSalt(value = "salt"),
            hash = PasswordHash(value = "secret"),
            token = generateToken()
        )
    }

    @Autowired
    private lateinit var testInstance: AccountCreateEmailHandler

    @Autowired
    private lateinit var generateToken: GenerateToken
}