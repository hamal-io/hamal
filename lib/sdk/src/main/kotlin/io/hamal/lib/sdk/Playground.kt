package io.hamal.lib.sdk

import io.hamal.lib.domain.req.AdhocInvocationReq
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets
import io.hamal.lib.script.api.value.TableValue

fun main() {
    val sdk = DefaultHamalSdk

    val res = sdk.adhocService().submit(
        AdhocInvocationReq(
            inputs = InvocationInputs(TableValue()),
            secrets = InvocationSecrets(listOf()),
            code = Code(
                """
                local log = require('log')
                log.info('test 123')
            """.trimIndent()
            )
        )
    )

    println(res)
}