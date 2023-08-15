package io.hamal.extension.std.sys.trigger

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableArray
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.domain.ApiSimpleEventTrigger
import io.hamal.lib.sdk.domain.ApiSimpleFixedRateTrigger
import io.hamal.lib.sdk.domain.ApiSimpleTrigger
import io.hamal.lib.sdk.domain.ApiTriggerList

class ListTriggerFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function0In2Out<ErrorType, TableArray>(
    FunctionOutput2Schema(ErrorType::class, TableArray::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, TableArray?> {
        val triggers = try {
            templateSupplier()
                .get("/v1/triggers")
                .execute(ApiTriggerList::class)
                .triggers
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf<ApiSimpleTrigger>()
        }

        return null to ctx.tableCreateArray().also { rs ->
            triggers.forEach { trigger ->

                when (val t = trigger) {
                    is ApiSimpleFixedRateTrigger -> {
                        val inner = ctx.tableCreateMap(6)
                        inner["id"] = t.id
                        inner["type"] = "FixedRate"
                        inner["name"] = t.name.value
                        inner["func"] = ctx.tableCreateMap(2).also { nt ->
                            nt["id"] = t.func.id
                            nt["name"] = t.func.name.value
                        }
                        inner["namespace"] = ctx.tableCreateMap(2).also { nt ->
                            nt["id"] = t.namespace.id
                            nt["name"] = t.namespace.name.value
                        }
                        inner["duration"] = t.duration.toIsoString()
                        rs.append(inner)
                    }

                    is ApiSimpleEventTrigger -> {
                        val inner = ctx.tableCreateMap(6)
                        inner["id"] = t.id
                        inner["type"] = "Event"
                        inner["name"] = t.name.value
                        inner["func"] = ctx.tableCreateMap(2).also { nt ->
                            nt["id"] = t.func.id
                            nt["name"] = t.func.name.value
                        }
                        inner["namespace"] = ctx.tableCreateMap(2).also { nt ->
                            nt["id"] = t.namespace.id
                            nt["name"] = t.namespace.name.value
                        }
                        inner["topic"] = ctx.tableCreateMap(2).also { nt ->
                            nt["id"] = t.topic.id
                            nt["name"] = t.topic.name.value
                        }
                        rs.append(inner)
                    }

                    else -> TODO()
                }
            }
        }
    }
}