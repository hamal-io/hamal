package io.hamal.bootstrap.suite.func

import io.hamal.bootstrap.TestContext
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.domain.ListEventsResponse
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import java.lang.Thread.sleep
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name

val functionTests: (TestContext) -> List<DynamicTest> = { ctx ->
    collectFiles().map { testFile ->
        dynamicTest("${testFile.parent.name}/${testFile.name}") {

            //FIXME pass test id via inputs
            val topicId = ctx.createTopic(TopicName("test-topic"))

            val code = Code(String(Files.readAllBytes(testFile)))

            ctx.sdk.adhocService().submit(
                InvokeAdhocReq(
                    inputs = InvocationInputs(),
                    code = code
                )
            )

            //
            while (true) {
                val events = HttpTemplate("http://localhost:8084")
                    .get("/v1/topics/${topicId.value.value}/events")
                    .execute(ListEventsResponse::class)
                    .events

                if (events.isNotEmpty()) {
                    break
                }

                sleep(1)
            }
        }
    }.toList()
}

val testPath = Paths.get("src", "integrationTest", "resources", "suite")

fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".hs") }

