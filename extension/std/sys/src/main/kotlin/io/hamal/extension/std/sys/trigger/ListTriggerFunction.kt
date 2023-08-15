package io.hamal.extension.std.sys.trigger

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableArray
import io.hamal.lib.kua.type.ErrorType
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
            listOf<ApiTriggerList.ApiSimpleTrigger>()
        }

        return null to ctx.tableCreateArray().also { rs ->
            triggers.forEach { trigger ->
                val inner = ctx.tableCreateMap(2)
                inner["id"] = trigger.id
                inner["name"] = trigger.name.value
                inner["func"] = ctx.tableCreateMap(2).also { nt ->
                    nt["id"] = trigger.func.id
                    nt["name"] = trigger.func.name.value
                }
                inner["namespace"] = ctx.tableCreateMap(2).also { nt ->
                    nt["id"] = trigger.namespace.id
                    nt["name"] = trigger.namespace.name.value
                }
                rs.append(inner)
            }
        }
    }
}