package io.hamal.lib.sdk

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.script.api.value.CodeValue
import io.hamal.lib.script.api.value.TableValue

fun main() {
    val sdk = DefaultHamalSdk("http://localhost:8084")

    val res = sdk.adhocService().submit(
        InvokeAdhocReq(
            inputs = InvocationInputs(TableValue()),
            code = CodeValue(
                """
                local log = require('log')
                log.info('test 123')
            """.trimIndent()
            )
        )
    )

    println(res)
}