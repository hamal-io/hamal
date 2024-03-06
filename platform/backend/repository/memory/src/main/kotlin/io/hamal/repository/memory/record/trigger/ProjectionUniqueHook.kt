package io.hamal.repository.memory.record.trigger

import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.Trigger
import io.hamal.repository.memory.record.ProjectionMemory


internal class ProjectionUniqueHook : ProjectionMemory<TriggerId, Trigger.Hook> {

    override fun upsert(obj: Trigger.Hook) {
        if (!uniqueHooks.add(
                UniqueHook(
                    funcId = obj.funcId,
                    hookId = obj.hookId,
                    hookMethod = obj.hookMethod
                )
            )
        ) {
            throw IllegalArgumentException("Trigger already exists")
        }
    }

    override fun clear() {
        uniqueHooks.clear()
    }

    data class UniqueHook(
        val funcId: FuncId,
        val hookId: HookId,
        val hookMethod: HookMethod
    )

    private val uniqueHooks = mutableSetOf<UniqueHook>()
}