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

val functionTests: (TestContext) -> List<DynamicTest> = { ctx ->
    listOf(
        dynamicTest("Creates empty func") {

            //FIXME pass test id via inputs
            val topicId = ctx.createTopic(TopicName("test-001"))

            val res = ctx.sdk.adhocService().submit(
                InvokeAdhocReq(
                    inputs = InvocationInputs(),
                    code = Code(
                        """
                    local log = require('log')
                    local sys = require('sys')
                    
                    val func_id = sys.func.create({
                        name = 'empty-test-func',
                        run = <[]>
                    })

                    local funcs = sys.func.list()
                    log.info(funcs)

                    local func = funcs[1]
                    assert(func.id == func_id)
                    assert(func.name == 'empty-test-func')
                    
                    local topic_id = '${topicId.value.value}'
                    local emitter = sys.evt.emitter(topic_id)
                    emitter.emit({})

                                     
                """.trimIndent()
                    )
                )
            )

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
    )
}

