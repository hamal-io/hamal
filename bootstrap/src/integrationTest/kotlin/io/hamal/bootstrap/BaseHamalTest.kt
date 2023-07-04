package io.hamal.bootstrap

import io.hamal.agent.AgentConfig
import io.hamal.backend.instance.BackendConfig
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.bootstrap.config.TestEnvConfig
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.lib.sdk.HamalSdk
import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.*
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.name


@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        TestEnvConfig::class,
        BackendConfig::class,
        AgentConfig::class
    ]
)
@SpringBootTest(
    webEnvironment = WebEnvironment.DEFINED_PORT,
    properties = ["server.port=8084"]
)
@ActiveProfiles("test")
abstract class BaseHamalTest {

    @LocalServerPort
    lateinit var localPort: Number

    @Autowired
    lateinit var eventBrokerRepository: LogBrokerRepository<*>

    @Autowired
    lateinit var execCmdRepository: ExecCmdRepository

    @Autowired
    lateinit var funcCmdRepository: FuncCmdRepository

    @Autowired
    lateinit var reqCmdRepository: ReqCmdRepository

    @Autowired
    lateinit var triggerCmdRepository: TriggerCmdRepository

    @TestFactory
    fun run(): List<DynamicTest> = collectFiles().map { testFile ->
        dynamicTest("${testFile.parent.name}/${testFile.name}") {
            sdk.adhocService().submit(
                InvokeAdhocReq(
                    inputs = InvocationInputs(),
                    code = Code(String(Files.readAllBytes(testFile)))
                )
            )
            ActiveTest.awaitCompletion()
        }
    }.toList()

    @BeforeEach
    fun before() {
        eventBrokerRepository.clear()
        reqCmdRepository.clear()
        execCmdRepository.clear()
        funcCmdRepository.clear()
        triggerCmdRepository.clear()
    }


    @PostConstruct
    fun setup() {
        sdk = DefaultHamalSdk("http://localhost:${localPort}")
    }

    private lateinit var sdk: HamalSdk


}

private val testPath = Paths.get("src", "integrationTest", "resources", "suite")
private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".hs") }