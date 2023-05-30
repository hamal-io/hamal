package io.hamal.backend.web

import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.domain.ReqPayload.InvokeAdhoc
import io.hamal.backend.service.cmd.ReqCmdService
import io.hamal.lib.domain.value.NumberValue
import io.hamal.lib.domain.value.StringValue
import io.hamal.lib.domain.value.to
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets
import io.hamal.lib.domain.vo.base.Secret
import io.hamal.lib.domain.vo.base.SecretKey
import io.hamal.lib.domain.vo.base.SecretStore
import io.hamal.lib.domain.vo.base.SecretStoreIdentifier
import io.hamal.lib.sdk.domain.ApiAdhocRequest
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
    val eventEmitter: EventEmitter,
    val reqCmdService: ReqCmdService
) {

    @PostMapping("/v1/adhoc")
    fun adhoc(
        @RequestBody script: String
    ): ResponseEntity<ApiReq> {
        val result = reqCmdService.request(
            InvokeAdhoc(
                inputs = InvocationInputs(
                    listOf(
                        StringValue("Hello") to StringValue("World"),
                        StringValue("Number") to NumberValue(42),
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
            ApiAdhocRequest(
                id = result.id,
                status = result.status,
                execId = null,
                execStatus = null
            ),
            HttpStatus.ACCEPTED
        )
    }
}