package io.hamal.core.request.handler.account

import io.hamal.core.component.GenerateToken
import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.AccountCreateRequested
import io.hamal.lib.domain.request.AccountPasswordChangeRequested
import io.hamal.lib.domain.vo.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class PasswordChangeHandlerTest : BaseRequestHandlerTest() {

    @Test
    fun `Updates Salt`() {
        createInstance(submitCreateEmailAccountReq)
        updateInstance(submitUpdateAccountReq)

        with(accountQueryRepository.get(AccountId(123))) {
            assertThat(type, equalTo(AccountType.User))
            assertThat(salt, equalTo(PasswordSalt("changed-salt")))
        }
    }

    private val submitUpdateAccountReq by lazy {
        AccountPasswordChangeRequested(
            requestId = RequestId(2),
            requestedBy = AuthId(3),
            requestStatus = RequestStatus.Submitted,
            id = AccountId(123),
            hash = PasswordHash("changed-secret"),
            salt = PasswordSalt(value = "changed-salt"),
            email = Email(value = "test@hamal.io")
        )
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
    private lateinit var createInstance: AccountCreateEmailHandler

    @Autowired
    private lateinit var updateInstance: AccountUpdateHandler

    @Autowired
    private lateinit var generateToken: GenerateToken


}