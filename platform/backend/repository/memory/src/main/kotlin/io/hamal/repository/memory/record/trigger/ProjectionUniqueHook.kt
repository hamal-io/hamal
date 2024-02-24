package io.hamal.repository.memory.record.trigger

import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.HookId
import io.hamal.repository.api.Trigger


internal class TriggerProjectionUniqueHook {

    fun create(trigger: Trigger.Hook) {
        if (!uniqueHooks.add(
                UniqueHook(
                    funcId = trigger.funcId,
                    hookId = trigger.hookId,
                    hookMethod = trigger.hookMethod
                )
            )
        ) {
            throw IllegalArgumentException("Trigger already exists")
        }
    }

    fun clear() {
        uniqueHooks.clear()
    }

    data class UniqueHook(
        val funcId: FuncId,
        val hookId: HookId,
        val hookMethod: HookMethod
    )

    private val uniqueHooks = mutableSetOf<UniqueHook>()
}