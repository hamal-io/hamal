package io.hamal.backend.infra.web

import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.InvokeUseCasePort
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.FuncId
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@Serializable
class SomeTest(val id: ExecId, val definitionId: FuncId, val shard: Shard)

@RestController
open class TriggerController @Autowired constructor(
    val request: InvokeUseCasePort,
) {

}