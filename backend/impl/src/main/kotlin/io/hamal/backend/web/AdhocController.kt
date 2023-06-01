package io.hamal.backend.web

import io.hamal.backend.req.InvokeAdhoc
import io.hamal.backend.req.Request
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.value.NumberValue
import io.hamal.lib.domain.value.StringValue
import io.hamal.lib.domain.value.to
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets
import io.hamal.lib.domain.vo.base.Secret
import io.hamal.lib.domain.vo.base.SecretKey
import io.hamal.lib.domain.vo.base.SecretStore
import io.hamal.lib.domain.vo.base.SecretStoreIdentifier
import io.hamal.lib.domain.vo.port.GenerateDomainId
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
    val request: Request,
    val generateDomainId: GenerateDomainId
) {

    @PostMapping("/v1/adhoc")
    fun adhoc(
        @RequestBody script: String
    ): ResponseEntity<ApiReq> {

        // reqCmdService - request
        // implementation
        // stores

//        reqCmdService.request()
//
//        eventEmitter.emit(
//            ComputeId(TimeUtils.now().toEpochMilli()), AdhocInvocationEvent(
////            computeId = computeId,
//                shard = Shard(1),
//                inputs = InvocationInputs(
//                    listOf(
//                        StringValue("Hello") to StringValue("World"),
//                        StringValue("Number") to NumberValue(42),
//                    )
//                ),
//                secrets = InvocationSecrets(
//                    listOf(
//                        Secret(
//                            SecretKey("Key"),
//                            SecretStore("store"),
//                            SecretStoreIdentifier("identifier")
//                        )
//                    )
//                ),
//                code = Code(script)
//            )
//        )

        val result = request(
            InvokeAdhoc(
                execId = generateDomainId(Shard(1), ::ExecId),
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
            ApiAdhocReq(
                id = result.id,
                status = result.status,
                execId = ExecId(0)
            ),
            HttpStatus.ACCEPTED
        )
    }
}