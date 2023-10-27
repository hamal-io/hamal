package io.hamal.testbed.authorized

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Enjoyer
import io.hamal.repository.api.*
import io.hamal.testbed.BaseTest
import java.time.temporal.ChronoUnit

internal abstract class BaseAuthorizedTest : BaseTest(){

    override fun setupTest() {
        clearRepository()

        testAccount = accountRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(2),
                accountId = generateDomainId(::AccountId),
                accountType = Enjoyer,
                name = AccountName("group-admin"),
                email = AccountEmail("group-admin@hamal.io"),
                salt = PasswordSalt("group-admin-salt")
            )
        )

        testAccountAuthToken = (authRepository.create(
            AuthCmdRepository.CreateTokenAuthCmd(
                id = CmdId(3),
                authId = generateDomainId(::AuthId),
                accountId = testAccount.id,
                token = AuthToken("group-admin-token"),
                expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
            )
        ) as TokenAuth).token

        testGroup = groupRepository.create(
            GroupCmdRepository.CreateCmd(
                id = CmdId(4),
                groupId = generateDomainId(::GroupId),
                name = GroupName("group-admin-group"),
                creatorId = testAccount.id
            )
        )

        testNamespace = namespaceRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(1),
                namespaceId = generateDomainId(::NamespaceId),
                groupId = testGroup.id,
                name = NamespaceName("hamal"),
                inputs = NamespaceInputs()
            )
        )
    }
}
