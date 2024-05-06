package io.hamal.testbed.endpoint

import io.hamal.core.component.DelayRetry
import io.hamal.core.component.DelayRetryFixedTime
import io.hamal.core.component.SetupInternalTopics
import io.hamal.core.config.BackendBasePath
import io.hamal.core.service.InternalEventService
import io.hamal.extension.net.http.ExtensionNetHttpFactory
import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AuthId.Companion.AuthId
import io.hamal.lib.domain.vo.AuthToken.Companion.AuthToken
import io.hamal.lib.domain.vo.ExpiresAt.Companion.ExpiresAt
import io.hamal.lib.domain.vo.NamespaceName.Companion.NamespaceName
import io.hamal.lib.domain.vo.PasswordSalt.Companion.PasswordSalt
import io.hamal.lib.domain.vo.WorkspaceName.Companion.WorkspaceName
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.plugin.net.http.PluginHttpFactory
import io.hamal.plugin.std.debug.PluginDebugFactory
import io.hamal.plugin.std.log.PluginLogFactory
import io.hamal.plugin.std.sys.PluginSysFactory
import io.hamal.repository.api.*
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.runner.config.EnvFactory
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.test.RunnerFixture.createTestRunner
import io.hamal.runner.test.RunnerFixture.unitOfWork
import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
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
                ValueObject.builder()
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
        extensionRepository.clear()
        requestRepository.clear()
        execRepository.clear()
        funcRepository.clear()
        workspaceRepository.clear()

        namespaceRepository.clear()
        recipeRepository.clear()
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
                authId = generateDomainId(::AuthId),
                accountId = testAccount.id,
                token = AuthToken("root-token"),
                expiresAt = ExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
            )
        ) as Auth.Token).token

        testWorkspace = workspaceRepository.create(
            WorkspaceCmdRepository.CreateCmd(
                id = CmdId(4),
                workspaceId = WorkspaceId.root,
                name = WorkspaceName("root-workspace"),
                creatorId = testAccount.id
            )
        )

        testNamespace = namespaceRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(5),
                namespaceId = NamespaceId.root,
                workspaceId = testWorkspace.id,
                name = NamespaceName("root-namespace"),
                features = NamespaceFeatures.default
            )
        )
    }

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
    lateinit var workspaceRepository: WorkspaceRepository

    @Autowired
    lateinit var namespaceRepository: NamespaceRepository

    @Autowired
    lateinit var recipeRepository: RecipeRepository

    @Autowired
    lateinit var requestRepository: RequestRepository

    @Autowired
    lateinit var triggerRepository: TriggerRepository

    @Autowired
    lateinit var generateDomainId: GenerateDomainId

    private lateinit var testAccount: Account
    private lateinit var testAccountAuthToken: AuthToken
    private lateinit var testWorkspace: Workspace
    private lateinit var testNamespace: Namespace
}


@TestConfiguration
class TestConfig {

    private lateinit var testAccount: Account
    private lateinit var testAccountAuthToken: AuthToken
    private lateinit var testWorkspace: Workspace
    private lateinit var testNamespace: Namespace

    @PostConstruct
    fun setup() {
        topicRepository.clear()
        logBrokerRepository.clear()
        setupInternalTopics()

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
                    authId = AuthId(1),
                    accountId = testAccount.id,
                    token = AuthToken("root-token"),
                    expiresAt = ExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
                )
            ) as Auth.Token).token

            testWorkspace = workspaceRepository.create(
                WorkspaceCmdRepository.CreateCmd(
                    id = CmdId(4),
                    workspaceId = WorkspaceId.root,
                    name = WorkspaceName("root-workspace"),
                    creatorId = testAccount.id
                )
            )

            testNamespace = namespaceRepository.create(
                NamespaceCmdRepository.CreateCmd(
                    id = CmdId(5),
                    namespaceId = NamespaceId.root,
                    workspaceId = testWorkspace.id,
                    name = NamespaceName("root-namespace"),
                    features = NamespaceFeatures.default
                )
            )
        } catch (ignore: Throwable) {
        }
    }

    @Autowired
    lateinit var topicRepository: TopicRepository

    @Autowired
    lateinit var triggerRepository: TriggerRepository

    @Autowired
    lateinit var generateDomainId: GenerateDomainId

    @Autowired
    lateinit var internalEvenService: InternalEventService

    @Autowired
    lateinit var logBrokerRepository: LogBrokerRepository

    @Autowired
    lateinit var setupInternalTopics: SetupInternalTopics

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var authRepository: AuthRepository

    @Autowired
    lateinit var workspaceRepository: WorkspaceRepository

    @Autowired
    lateinit var namespaceRepository: NamespaceRepository
}


abstract class BaseEndpointTest {

    abstract val sdk: ApiSdkImpl

    @TestFactory
    fun run(): List<DynamicTest> {
        return testPaths()
            .sorted()
            .map { testPath ->
                val testName = generateTestName(testPath)
                dynamicTest(testName) {
                    sdk.template.post("/v1/clear").execute()

                    createTestRunner(
                        pluginFactories = listOf(
                            PluginSysFactory(sdk),
                            PluginHttpFactory()
                        ),
                        extensionFactories = listOf(
                            ExtensionNetHttpFactory
                        ),
                        env = RunnerEnv(
                            ValueObject.builder()
                                .set("api_host", sdk.template.baseUrl)
                                .build()
                        )
                    ).run(
                        unitOfWork(
                            code = String(Files.readAllBytes(testPath)),
                        )
                    )

                }
            }.toList()
    }


    private fun generateTestName(testPath: Path) = testPath.toAbsolutePath().toString().split("/")
        .dropWhile { it != "resources" }
        .drop(1)
        .joinToString("/")

    private fun testPaths(): Stream<Path> = Files.walk(testPath)
        .filter { f: Path -> f.name.endsWith(".lua") }
        .sorted()

    private val testPath: Path = Paths.get("src", "integrationTest", "resources", "endpoint")
}