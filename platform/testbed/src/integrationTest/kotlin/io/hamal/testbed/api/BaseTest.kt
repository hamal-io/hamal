package io.hamal.testbed.api

import io.hamal.core.component.DelayRetry
import io.hamal.core.component.DelayRetryFixedTime
import io.hamal.core.config.BackendBasePath
import io.hamal.extension.net.http.ExtensionHttpFactory
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.api.ApiAdhocInvokeRequest
import io.hamal.plugin.net.http.PluginHttpFactory
import io.hamal.plugin.std.debug.PluginDebugFactory
import io.hamal.plugin.std.log.PluginLogFactory
import io.hamal.plugin.std.sys.PluginSysFactory
import io.hamal.repository.api.*
import io.hamal.runner.config.EnvFactory
import io.hamal.runner.config.SandboxFactory
import io.hamal.testbed.api.TestResult.*
import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Stream
import kotlin.io.path.name
import kotlin.time.Duration.Companion.milliseconds


@TestConfiguration
class TestSandboxConfig {
    @Bean
    fun sandboxFactory(@Value("\${io.hamal.runner.api.host}") apiHost: String): SandboxFactory =
        TestRunnerSandboxFactory(apiHost)

    @Bean
    fun envFactory(@Value("\${io.hamal.runner.api.host}") apiHost: String): EnvFactory =
        object : EnvFactory {
            override fun create() = RunnerEnv(
                HotObject.builder()
                    .set("api_host", apiHost)
                    .build()
            )
        }
}


class TestRunnerSandboxFactory(
    private val apiHost: String,
) : SandboxFactory {
    override fun create(ctx: SandboxContext): Sandbox {
        NativeLoader.load(NativeLoader.Preference.Jar)

        val sdk = ApiSdkImpl(
            apiHost,
            token = AuthToken("root-token")
        )

        return Sandbox(ctx)
            .registerPlugins(
                PluginLogFactory(sdk.execLog),
                PluginDebugFactory(),
                PluginSysFactory(sdk),
                PluginHttpFactory()
            )
            .registerExtensions(
                ExtensionHttpFactory
            )
    }
}


@TestConfiguration
class TestRetryConfig {
    @Bean
    fun backendBasePath() = BackendBasePath("/tmp/hamal/testbed/${UUID.randomUUID()}")

    @Bean
    fun delayRetry(): DelayRetry = DelayRetryFixedTime(1.milliseconds)
}


@RestController
class ClearController {

    @PostMapping("/v1/clear")
    fun clear() {
        accountRepository.clear()
        authRepository.clear()
        codeRepository.clear()
        endpointRepository.clear()
        extensionRepository.clear()
        requestRepository.clear()
        execRepository.clear()
        funcRepository.clear()
        workspaceRepository.clear()
        hookRepository.clear()

        namespaceRepository.clear()
        namespaceTreeRepository.clear()
        blueprintRepository.clear()
        triggerRepository.clear()

        testAccount = accountRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(2),
                accountId = AccountId.root,
                accountType = AccountType.Root,
                salt = PasswordSalt("root-salt")
            )
        )

        testAccountAuthToken = (authRepository.create(
            AuthCmdRepository.CreateTokenAuthCmd(
                id = CmdId(3),
                authId = AuthId.root,
                accountId = AccountId.root,
                token = AuthToken("root-token"),
                expiresAt = ExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
            )
        ) as Auth.Token).token

        testWorkspace = workspaceRepository.create(
            WorkspaceCmdRepository.CreateCmd(
                id = CmdId(4),
                workspaceId = WorkspaceId.root,
                name = WorkspaceName("root-workspace"),
                creatorId = AccountId.root
            )
        )

        testNamespace = namespaceRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(5),
                namespaceId = NamespaceId.root,
                workspaceId = WorkspaceId.root,
                name = NamespaceName("root-namespace"),
                features = NamespaceFeatures.default
            )
        )

        namespaceTreeRepository.create(
            NamespaceTreeCmdRepository.CreateCmd(
                id = CmdId(6),
                treeId = NamespaceTreeId.root,
                workspaceId = WorkspaceId.root,
                rootNodeId = NamespaceId.root
            )
        )
    }

    @Autowired
    lateinit var blueprintRepository: BlueprintRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var authRepository: AuthRepository

    @Autowired
    lateinit var codeRepository: CodeRepository

    @Autowired
    lateinit var endpointRepository: EndpointRepository

    @Autowired
    lateinit var execRepository: ExecRepository

    @Autowired
    lateinit var extensionRepository: ExtensionRepository

    @Autowired
    lateinit var funcRepository: FuncRepository

    @Autowired
    lateinit var workspaceRepository: WorkspaceRepository

    @Autowired
    lateinit var hookRepository: HookRepository

    @Autowired
    lateinit var namespaceRepository: NamespaceRepository

    @Autowired
    lateinit var namespaceTreeRepository: NamespaceTreeRepository

    @Autowired
    lateinit var requestRepository: RequestRepository

    @Autowired
    lateinit var triggerRepository: TriggerRepository

    private lateinit var testAccount: Account
    private lateinit var testAccountAuthToken: AuthToken
    private lateinit var testWorkspace: Workspace
    private lateinit var testNamespace: Namespace
}


sealed interface TestResult {
    object Success : TestResult
    data class Failed(val message: String) : TestResult
    object Timeout : TestResult
}


@TestConfiguration
class TestConfig {

    private lateinit var testAccount: Account
    private lateinit var testAccountAuthToken: AuthToken
    private lateinit var testWorkspace: Workspace
    private lateinit var testNamespace: Namespace

    @PostConstruct
    fun setup() {

        try {
            testAccount = accountRepository.create(
                AccountCmdRepository.CreateCmd(
                    id = CmdId(2),
                    accountId = AccountId.root,
                    accountType = AccountType.Root,
                    salt = PasswordSalt("root-salt")
                )
            )

            testAccountAuthToken = (authRepository.create(
                AuthCmdRepository.CreateTokenAuthCmd(
                    id = CmdId(3),
                    authId = AuthId.root,
                    accountId = AccountId.root,
                    token = AuthToken("root-token"),
                    expiresAt = ExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
                )
            ) as Auth.Token).token

            testWorkspace = workspaceRepository.create(
                WorkspaceCmdRepository.CreateCmd(
                    id = CmdId(4),
                    workspaceId = WorkspaceId.root,
                    name = WorkspaceName("root-workspace"),
                    creatorId = AccountId.root
                )
            )

            testNamespace = namespaceRepository.create(
                NamespaceCmdRepository.CreateCmd(
                    id = CmdId(5),
                    namespaceId = NamespaceId.root,
                    workspaceId = WorkspaceId.root,
                    name = NamespaceName("root-namespace"),
                    features = NamespaceFeatures.default
                )
            )
        } catch (ignored: Throwable) {
        }
    }

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var authRepository: AuthRepository

    @Autowired
    lateinit var workspaceRepository: WorkspaceRepository

    @Autowired
    lateinit var namespaceRepository: NamespaceRepository
}


abstract class BaseApiTest {

    abstract val sdk: ApiSdkImpl

    @TestFactory
    fun run(): List<DynamicTest> {
        return testPaths()
            .sorted()
            .map { testPath ->
                val testName = generateTestName(testPath)
                dynamicTest(testName) {

                    sdk.template.post("/v1/clear").execute()

                    var counter = 0
                    while (true) {
                        when (val result = runTest(testPath)) {
                            is Success -> break
                            is Failed -> {
                                if (counter++ >= 10) {
                                    fail { result.message }
                                }
                                sdk.template.post("/v1/clear").execute()
                            }

                            is Timeout -> fail("Timeout")
                        }
                    }
                }
            }.toList()
    }


    private fun runTest(testPath: Path): TestResult {

        val files = Files.walk(testPath)
            .filter { f: Path -> f.name.endsWith(".lua") }
            .sorted()

        for (file in files) {
            println(">>>>>>>>>>>>>> ${file.fileName}")

            val execReq = sdk.adhoc.invoke(
                NamespaceId.root,
                ApiAdhocInvokeRequest(InvocationInputs(), CodeValue(String(Files.readAllBytes(file))))
            )

            sdk.await(execReq)

            var wait = true
            val startedAt = TimeUtils.now()


            while (wait) {
                with(sdk.exec.get(execReq.id)) {
                    if (status == ExecStatus.Completed) {
                        wait = false
                    } else {
                        if (status == ExecStatus.Failed) {
                            return Failed(message = "Execution failed: ${this.result!!.value["message"]}")
                        } else if (startedAt.plusSeconds(1).isBefore(TimeUtils.now())) {
                            return Timeout
                        }
                    }
                }
            }
        }
        return Success
    }

    private fun generateTestName(testPath: Path) = testPath.toAbsolutePath().toString().split("/")
        .dropWhile { it != "resources" }
        .drop(1)
        .joinToString("/")

    private fun testPaths(): Stream<Path> = Files.walk(testPath)
        .filter { f: Path -> f.name.endsWith(".lua") }
        .map { it.parent }
        .distinct()
        .sorted()

    private val testPath: Path = Paths.get("src", "integrationTest", "resources", "api")
}