package io.hamal.bootstrap.suite

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.sdk.HamalSdk
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest

val functionTests: (sdk: HamalSdk) -> List<DynamicTest> = { sdk ->
    listOf(
        dynamicTest("Creates empty func") {
            val res = sdk.adhocService().submit(
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

                    log.info(func_id)

                    local funcs = sys.func.list()
                    log.info(funcs)

                    local func = funcs[1]
                    assert(func.id == func_id)
                    assert(func.name == 'empty-test-func')
                    
                """.trimIndent()
                    )
                )
            )
            println(res)
            Thread.sleep(1000)
        }
    )
}