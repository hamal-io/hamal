package io.hamal.mono.api

import io.hamal.lib.common.Logger
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Root
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.DefaultHubSdk
import io.hamal.lib.sdk.HubSdk
import io.hamal.lib.sdk.hub.HubInvokeAdhocReq
import io.hamal.repository.api.*
import io.hamal.repository.api.log.BrokerRepository
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Files
import java.nio.file.Path
import java.time.temporal.ChronoUnit
import kotlin.io.path.name

abstract class BaseApiTest {

    @Autowired
    lateinit var eventBrokerRepository: BrokerRepository

    @Autowired
    lateinit var accountCmdRepository: AccountCmdRepository

    @Autowired
    lateinit var authCmdRepository: AuthCmdRepository

    @Autowired
    lateinit var execCmdRepository: ExecCmdRepository

    @Autowired
    lateinit var execQueryRepository: ExecQueryRepository

    @Autowired
    lateinit var funcCmdRepository: FuncCmdRepository

    @Autowired
    lateinit var groupCmdRepository: GroupCmdRepository

    @Autowired
    lateinit var namespaceCmdRepository: NamespaceCmdRepository

    @Autowired
    lateinit var reqCmdRepository: ReqCmdRepository

    @Autowired
    lateinit var triggerCmdRepository: TriggerCmdRepository

    @Autowired
    lateinit var generateDomainId: GenerateDomainId

    private lateinit var testAccount: Account
    private lateinit var testAccountAuthToken: AuthToken
    private lateinit var testGroup: Group

    @TestFactory
    fun run(): List<DynamicTest> {
        return collectFiles().sorted().map { testFile ->
            val testFileWithPath = "${testFile.parent.parent.name}/${testFile.parent.name}/${testFile.name}"
            dynamicTest(testFileWithPath) {
                setupTestEnv()
                
                log.info("Start test $testFileWithPath")

                val execReq = rootHubSdk.adhoc.invoke(
                    testGroup.id,
                    HubInvokeAdhocReq(
                        InvocationInputs(),
                        CodeType(String(Files.readAllBytes(testFile)))
                    )
                )
                rootHubSdk.await(execReq)

                var wait = true
                val startedAt = TimeUtils.now()
                while (wait) {
                    Thread.sleep(1)
                    with(execQueryRepository.get(execReq.id(::ExecId))) {
                        if (status == ExecStatus.Completed) {
                            wait = false
                        }
                        if (status == ExecStatus.Failed) {
                            fail { "Execution failed" }
                        }

                        if (startedAt.plusSeconds(1).isBefore(TimeUtils.now())) {
                            fail("Timeout")
                        }
                    }
                }
            }
        }.toList()
    }

    private fun setupTestEnv() {
        eventBrokerRepository.clear()

        accountCmdRepository.clear()
        authCmdRepository.clear()
        reqCmdRepository.clear()
        execCmdRepository.clear()
        funcCmdRepository.clear()
        groupCmdRepository.clear()
        namespaceCmdRepository.clear()
        triggerCmdRepository.clear()

        testAccount = accountCmdRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(2),
                accountId = generateDomainId(::AccountId),
                accountType = Root,
                name = AccountName("test-account"),
                email = AccountEmail("test-account@hamal.io"),
                salt = PasswordSalt("test-salt")
            )
        )

        testAccountAuthToken = (authCmdRepository.create(
            AuthCmdRepository.CreateTokenAuthCmd(
                id = CmdId(3),
                authId = generateDomainId(::AuthId),
                accountId = testAccount.id,
                token = AuthToken("test-account-token"),
                expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
            )
        ) as TokenAuth).token

        testGroup = groupCmdRepository.create(
            GroupCmdRepository.CreateCmd(
                id = CmdId(4),
                groupId = generateDomainId(::GroupId),
                name = GroupName("test-group"),
                creatorId = testAccount.id
            )
        )

        namespaceCmdRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(1),
                namespaceId = generateDomainId(::NamespaceId),
                groupId = testGroup.id,
                name = NamespaceName("hamal"),
                inputs = NamespaceInputs()
            )
        )
    }

    abstract val rootHubSdk: HubSdk
    abstract val testPath: Path
    abstract val log: Logger

    private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".lua") }

    fun rootHubSdk(serverPort: Number) = DefaultHubSdk(
        HttpTemplate(
            baseUrl = "http://localhost:$serverPort",
            headerFactory = {
                set("x-hamal-token", "test-account-token")
            }
        )
    )
}
