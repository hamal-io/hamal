package io.hamal.testbed.admin

import io.hamal.lib.common.Logger
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Root
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.api.ApiInvokeAdhocReq
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

abstract class BaseTest {

    @Autowired
    lateinit var eventBrokerRepository: BrokerRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var authRepository: AuthRepository

    @Autowired
    lateinit var codeRepository: CodeRepository

    @Autowired
    lateinit var execRepository: ExecRepository

    @Autowired
    lateinit var extensionRepository: ExtensionRepository

    @Autowired
    lateinit var funcRepository: FuncRepository

    @Autowired
    lateinit var groupRepository: GroupRepository

    @Autowired
    lateinit var hookRepository: HookRepository

    @Autowired
    lateinit var namespaceRepository: NamespaceRepository

    @Autowired
    lateinit var reqRepository: ReqRepository

    @Autowired
    lateinit var snippetRepository: SnippetRepository

    @Autowired
    lateinit var triggerRepository: TriggerRepository

    @Autowired
    lateinit var generateDomainId: GenerateDomainId

    private lateinit var testAccount: Account
    private lateinit var testAccountAuthToken: AuthToken
    private lateinit var testGroup: Group
    private lateinit var testNamespace: Namespace

    @TestFactory
    fun run(): List<DynamicTest> {
        return testPaths().sorted().map { testPath ->
            val testName = generateTestName(testPath)
            dynamicTest(testName) {
                setupTestEnv()
                println("[$testName]")

                Files.walk(testPath)
                    .filter { f: Path -> f.name.endsWith(".lua") }
                    .sorted()
                    .forEach { file ->
                        println(">>>>>>>>>>>>>> ${file.fileName}")
                        val execReq = sdk.adhoc.invoke(
                            testNamespace.id,
                            ApiInvokeAdhocReq(
                                InvocationInputs(),
                                CodeValue(String(Files.readAllBytes(file)))
                            )
                        )

                        sdk.await(execReq)

                        var wait = true
                        val startedAt = TimeUtils.now()
                        while (wait) {
                            with(execRepository.get(execReq.id(::ExecId))) {
                                if (status == ExecStatus.Completed) {
                                    wait = false
                                }
                                if (status == ExecStatus.Failed) {
                                    check(this is FailedExec)
                                    fail { "Execution failed: ${this.result.value["message"]}" }
                                }

                                if (startedAt.plusSeconds(5).isBefore(TimeUtils.now())) {
                                    fail("Timeout")
                                }
                            }
                        }
                    }
            }
        }.toList()
    }

    private fun generateTestName(testPath: Path) = testPath.toAbsolutePath().toString().split("/")
        .dropWhile { it != "resources" }
        .drop(1)
        .joinToString("/")

    private fun setupTestEnv() {
        repeat(10) {
            eventBrokerRepository.clear()

            accountRepository.clear()
            authRepository.clear()
            codeRepository.clear()
            extensionRepository.clear()
            reqRepository.clear()
            execRepository.clear()
            funcRepository.clear()
            groupRepository.clear()
            hookRepository.clear()
            namespaceRepository.clear()
            snippetRepository.clear()
            triggerRepository.clear()
            Thread.sleep(10)
        }

        testAccount = accountRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(2),
                accountId = generateDomainId(::AccountId),
                accountType = Root,
                name = AccountName("root"),
                email = AccountEmail("root@hamal.io"),
                salt = PasswordSalt("root-salt")
            )
        )

        testAccountAuthToken = (authRepository.create(
            AuthCmdRepository.CreateTokenAuthCmd(
                id = CmdId(3),
                authId = generateDomainId(::AuthId),
                accountId = testAccount.id,
                token = AuthToken("root-token"),
                expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
            )
        ) as TokenAuth).token

        testGroup = groupRepository.create(
            GroupCmdRepository.CreateCmd(
                id = CmdId(4),
                groupId = generateDomainId(::GroupId),
                name = GroupName("root-group"),
                creatorId = testAccount.id
            )
        )

        testNamespace = namespaceRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(1),
                namespaceId = NamespaceId(1),
                groupId = testGroup.id,
                name = NamespaceName("hamal"),
                inputs = NamespaceInputs()
            )
        )
    }

    abstract val sdk: ApiSdk
    abstract val testPath: Path
    abstract val log: Logger

    private fun testPaths() = Files.walk(testPath)
        .filter { f: Path -> f.name.endsWith(".lua") }
        .map { it.parent }
        .distinct()
        .sorted()

    fun withApiSdk(serverPort: Number) = ApiSdkImpl(
        HttpTemplateImpl(
            baseUrl = "http://localhost:$serverPort",
            headerFactory = {
                set("authorization", "root-token")
            }
        )
    )
}
