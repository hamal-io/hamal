package io.hamal.plugin.std.sys.recipe

import io.hamal.lib.domain.vo.RecipeId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.value.ValueString

class RecipeGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<ValueString, KuaError, KuaTable>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<KuaError?, KuaTable?> {
        return try {
            null to sdk.recipe.get(RecipeId(arg1.stringValue))
                .let { recipe ->
                    ctx.tableCreate(
                        "id" to ValueString(recipe.id.value.value.toString(16)),
                        "name" to ValueString(recipe.name.value),
                        "value" to KuaCode(recipe.value.value)
                    )
                }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}