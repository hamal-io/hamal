package io.hamal.mono

import io.hamal.backend.instance.BackendConfig
import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.mono.config.TestRunnerConfig
import io.hamal.runner.RunnerConfig
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.Thread.sleep
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name


@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        TestRunnerConfig::class,
        BackendConfig::class,
        RunnerConfig::class
    ]
)
@SpringBootTest(
    webEnvironment = WebEnvironment.DEFINED_PORT,
    properties = [
        "server.port=8042"
    ]
)
@ActiveProfiles("test")
abstract class BaseHamalTest {

    @Autowired
    lateinit var eventBrokerRepository: LogBrokerRepository

    @Autowired
    lateinit var execCmdRepository: ExecCmdRepository

    @Autowired
    lateinit var execQueryRepository: ExecQueryRepository

    @Autowired
    lateinit var funcCmdRepository: FuncCmdRepository

    @Autowired
    lateinit var namespaceCmdRepository: NamespaceCmdRepository

    @Autowired
    lateinit var reqCmdRepository: ReqCmdRepository

    @Autowired
    lateinit var triggerCmdRepository: TriggerCmdRepository

    @TestFactory
    fun run(): List<DynamicTest> {
        return collectFiles().map { testFile ->
            dynamicTest("${testFile.parent.name}/${testFile.name}") {
                setupTestEnv()

                val execId = sdk.adhocService().submit(
                    InvokeAdhocReq(
                        inputs = InvocationInputs(),
                        code = CodeType(String(Files.readAllBytes(testFile)))
                    )
                ).id(::ExecId)

                ActiveTest.awaitCompletion()

                // Waits until the test exec complete
                var wait = true
                val startedAt = TimeUtils.now()
                while (wait) {
                    sleep(1)
                    with(execQueryRepository.get(execId)) {
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
        reqCmdRepository.clear()
        execCmdRepository.clear()
        funcCmdRepository.clear()
        namespaceCmdRepository.clear()
        triggerCmdRepository.clear()
    }

    private val httpTemplate = HttpTemplate("http://localhost:8042")
    private val sdk = DefaultHamalSdk(httpTemplate)

}


private val testPath = Paths.get("src", "integrationTest", "resources", "suite")
private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".lua") }