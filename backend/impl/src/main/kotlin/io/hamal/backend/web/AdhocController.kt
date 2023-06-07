package io.hamal.backend.web

import io.hamal.backend.req.InvokeAdhoc
import io.hamal.backend.req.Request
import io.hamal.lib.common.value.NumberValue
import io.hamal.lib.common.value.StringValue
import io.hamal.lib.common.value.TableEntry
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets
import io.hamal.lib.domain.vo.base.Secret
import io.hamal.lib.domain.vo.base.SecretKey
import io.hamal.lib.domain.vo.base.SecretStore
import io.hamal.lib.domain.vo.base.SecretStoreIdentifier
import io.hamal.lib.sdk.domain.ApiAdhocReq
import io.hamal.lib.sdk.domain.ApiReq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
open class AdhocController
@Autowired constructor(
    val request: Request
) {

    @PostMapping("/v1/adhoc")
    fun adhoc(
        @RequestBody script: String
    ): ResponseEntity<ApiReq> {

        val result = request(
            InvokeAdhoc(
                inputs = InvocationInputs(
                    listOf(
                        TableEntry(StringValue("Hello"), StringValue("World")),
                        TableEntry(StringValue("Number"), NumberValue(42)),
                    )
                ),
                secrets = InvocationSecrets(
                    listOf(
                        Secret(
                            SecretKey("Key"),
                            SecretStore("store"),
                            SecretStoreIdentifier("identifier")
                        )
                    )
                ),
                code = Code(script)
            )
        )

        return ResponseEntity(
            ApiAdhocReq(
                id = result.id,
                status = result.status,
                execId = ExecId(0)
            ),
            HttpStatus.ACCEPTED
        )
    }
}