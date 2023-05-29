package io.hamal.backend.web

import io.hamal.backend.event.AdhocInvocationEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.lib.domain.ReqId
import io.hamal.lib.common.Shard
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
open class AdhocController(
    @Autowired val eventEmitter: EventEmitter
) {

    var counter: Int = 0

    @PostMapping("/v1/adhoc")
    fun adhoc(@RequestBody script: String): ResponseEntity<String> {
        eventEmitter.emit(
            AdhocInvocationEvent(
                reqId = ReqId(counter),
                shard = Shard(0),
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
                // FIXME invoked at
                // FIXME invoked by

            )
        )
        return ResponseEntity.ok("ok")
    }
}