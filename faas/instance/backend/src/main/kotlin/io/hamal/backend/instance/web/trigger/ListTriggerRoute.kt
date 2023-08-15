package io.hamal.backend.instance.web.trigger

import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.NamespaceQueryRepository
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.domain.ApiTriggerList
import io.hamal.lib.sdk.domain.ApiTriggerList.ApiSimpleTrigger.Func
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class ListTriggerRoute(
    private val triggerQueryRepository: TriggerQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val funcQueryRepository: FuncQueryRepository
) {
    @GetMapping("/v1/triggers")
    fun listTrigger(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") triggerId: TriggerId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiTriggerList> {
        val result = triggerQueryRepository.list {
            this.afterId = triggerId
            this.types = TriggerType.values().toSet()
            this.limit = limit
        }

        val namespaces = namespaceQueryRepository.list(result.map { it.namespaceId })
            .associateBy { it.id }

        val funcs = funcQueryRepository.list(result.map { it.funcId })
            .associateBy { it.id }

        return ResponseEntity.ok(
            ApiTriggerList(
                result.map {
                    ApiTriggerList.ApiSimpleTrigger(
                        id = it.id,
                        name = it.name,
                        func = Func(
                            id = it.funcId,
                            name = funcs[it.funcId]!!.name
                        ),
                        namespace = ApiTriggerList.ApiSimpleTrigger.Namespace(
                            id = it.namespaceId,
                            name = namespaces[it.namespaceId]!!.name
                        )
                    )
                }
            )
        )
    }
}
