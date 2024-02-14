//package io.hamal.testbed.root
//
//import io.hamal.lib.common.domain.CmdId
//import io.hamal.lib.common.util.TimeUtils
//import io.hamal.lib.domain.vo.*
//import io.hamal.lib.domain.vo.AccountType.Root
//import io.hamal.repository.api.*
//import io.hamal.testbed.BaseTest
//import java.time.temporal.ChronoUnit
//
//internal abstract class BaseRootTest : BaseTest() {
//
//    override fun setupTest() {
//        clearRepository()
//
//        testAccount = accountRepository.create(
//            AccountCmdRepository.CreateCmd(
//                id = CmdId(2),
//                accountId = AccountId.root,
//                accountType = Root,
//                name = AccountName("root"),
//                email = AccountEmail("root@hamal.io"),
//                salt = PasswordSalt("root-salt")
//            )
//        )
//
//        testAccountAuthToken = (authRepository.create(
//            AuthCmdRepository.CreateTokenAuthCmd(
//                id = CmdId(3),
//                authId = generateDomainId(::AuthId),
//                accountId = testAccount.id,
//                token = AuthToken("root-token"),
//                expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
//            )
//        ) as TokenAuth).token
//
//        testGroup = groupRepository.create(
//            GroupCmdRepository.CreateCmd(
//                id = CmdId(4),
//                groupId = GroupId.root,
//                name = GroupName("root-workspace"),
//                creatorId = testAccount.id
//            )
//        )
//
//        testNamespace = namespaceRepository.create(
//            NamespaceCmdRepository.CreateCmd(
//                id = CmdId(5),
//                namespaceId = NamespaceId.root,
//                groupId = testGroup.id,
//                name = NamespaceName("root-namespace"),
//                inputs = NamespaceInputs()
//            )
//        )
//    }
//
//}
